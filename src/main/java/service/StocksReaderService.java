package service;

import api.TA35;
import api.dde.DDE.DDEConnection;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import locals.L;
import miniStocks.MiniStock;
import miniStocks.MiniStockDDECells;
import options.Option;
import options.Options;
import options.Strike;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class StocksReaderService extends MyBaseService {

    public static boolean initStocksCells = false;

    DDEClientConversation stocksConversation;  // חדש: שיחה נפרדת למניות (Thread-safe בפועל)

    TA35 ta35;

    // Columns (נשמרו כבקשתך)
    final int CALL_BID = 2;
    final int CALL_LAST = 3;
    final int CALL_ASK = 4;
    final int CALL_VOLUME = 5;
    final int STRIKE = 6;
    final int PUT_BID = 7;
    final int PUT_LAST = 8;
    final int PUT_ASK = 7;
    final int PUT_VOLUME = 9;

    // Rows (נשמרו כבקשתך)
    final int START_ROW_MONTH = 44;
    final int END_ROW_MONTH = 57;
    final int START_ROW_WEEK = 66;
    final int END_ROW_WEEK = 86;

    private Options optionsWeek;
    private Options optionsMonth;

    boolean set_options = false;

    // ==== הגדרות Batch למניות ====
    private final int STOCKS_START_ROW = 2;   // כולל
    private final int STOCKS_END_ROW_EXC = 37;  // בלעדי (2..36)
    private final int NAME_COL_DEFAULT = 2;  // אם שם המניה בעמודה אחרת – עדכן כאן

    // Thread ייעודי למניות
    private final ExecutorService stocksExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "stocks-batch-worker");
        t.setDaemon(true);
        return t;
    });
    private final AtomicBoolean stocksInFlight = new AtomicBoolean(false);

    public StocksReaderService(TA35 ta35, String excel_path) {
        super(ta35);
        this.ta35 = ta35;
        this.stocksConversation = new DDEConnection().createNewConversation(excel_path); // ערוץ נפרד למניות
    }

    public void update() {
        try {
            if (ta35.getStatus() == 0) {  // לא משנים את התנאי כדי לא לשבור לוגיקה קיימת
                sleepCount += getSleep();

                read_stocks();
                // Read options – נשמר כתגובה (מושבת)
                // handle_read_options();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean init_options = false;

    private void handle_read_options() throws DDEException {
        if (optionsMonth == null) {
            return;
        }
        if (init_options) {
//            read_options(optionsWeek, START_ROW_WEEK, END_ROW_WEEK);
            read_options(optionsMonth, START_ROW_MONTH, END_ROW_MONTH);
        } else {
            try {
                init_options = true;
//                init_options(optionsWeek, START_ROW_WEEK, END_ROW_WEEK);
                init_options(optionsMonth, START_ROW_MONTH, END_ROW_MONTH);
            } catch (DDEException e) {
                e.printStackTrace();
            }
        }
    }

    private void read_options(Options options, int start_row, int end_row) throws DDEException {
        Option call, put;
        double strike;

        for (int row = start_row; row < end_row; row++) {
            strike = L.dbl(stocksConversation.request(L.cell(row, STRIKE)));

            // Call
            call = options.getOption(Option.Side.CALL, strike);
            call.setBid((int) L.dbl(stocksConversation.request(L.cell(row, CALL_BID))));
            call.setAsk((int) L.dbl(stocksConversation.request(L.cell(row, CALL_ASK))));
            call.setVolume((int) L.dbl(stocksConversation.request(L.cell(row, CALL_VOLUME))));
            call.setLast((int) L.dbl(stocksConversation.request(L.cell(row, CALL_LAST))));

            // Put
            put = options.getOption(Option.Side.PUT, strike);
            put.setBid((int) L.dbl(stocksConversation.request(L.cell(row, PUT_BID))));
            put.setAsk((int) L.dbl(stocksConversation.request(L.cell(row, PUT_ASK))));
            put.setVolume((int) L.dbl(stocksConversation.request(L.cell(row, PUT_VOLUME))));
            put.setLast((int) L.dbl(stocksConversation.request(L.cell(row, PUT_LAST))));
        }
    }

    private double read_double_from_dde(String cell) throws DDEException {
        return L.dbl(stocksConversation.request(cell));
    }

    private void init_options(Options options, int start_row, int end_row) throws DDEException {
        for (int row = start_row; row < end_row; row++) {
            double strike = read_double_from_dde(L.cell(row, STRIKE));
            Option call = new Option(Option.Side.CALL, strike, options);
            Option put = new Option(Option.Side.PUT, strike, options);
            options.addStrike(new Strike(call, put, strike));
            System.out.println(strike);
        }
    }

    private void read_stocks() {
        if (!initStocksCells) {
            initStockCells(stocksConversation); // אתחול בבאטצ' דרך הערוץ של המניות
            initStocksCells = true;
        }
        
        // הגנה מפני concurrent reads - לא לקרוא אם יש קריאה בתהליך
        if (stocksInFlight.get()) {
            return; // יש קריאה בתהליך, דלג
        }
        
        // השתמש ב-ExecutorService הקיים במקום ליצור Thread חדש
        stocksInFlight.set(true);
        stocksExecutor.submit(() -> {
            try {
                batchReadStocks();
            } catch (DDEException e) {
                e.printStackTrace();
            } finally {
                stocksInFlight.set(false); // שחרר את הדגל בסיום
            }
        });
    }

    private void initStockCells(DDEClientConversation conv) {
        int nameCol = NAME_COL_DEFAULT;
        int start_row = STOCKS_START_ROW;
        int end_row = STOCKS_END_ROW_EXC;

        try {
            // בקשה אחת לכל שמות המניות (Range)
            String raw = conv.request(range(nameCol, start_row, end_row));
            String[] names = lines(raw);

            for (int i = 0; i < names.length; i++) {
                int row = start_row + i;
                String name = names[i];
                System.out.println("Name : " + name + " " + row);

                String nm = (name == null) ? "" : name.trim();
                if (nm.isEmpty()) continue;

                MiniStock stock = new MiniStock(ta35.getStocksHandler(), row);
                stock.setName(nm);
                ta35.getStocksHandler().addStock(stock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==== קריאת מניות בבאטצ' (Thread נפרד) ====
    private void batchReadStocks() throws DDEException {
        Set<MiniStock> stocks = ta35.getStocksHandler().getStocks();
        if (stocks == null || stocks.isEmpty()) return;

        // ניקח דוגמה כדי לגזור מספרי עמודות מתוך ה-cells (R{row}C{col})
        Iterator<MiniStock> it = stocks.iterator();
        if (!it.hasNext()) return;
        MiniStock sample = it.next();
        MiniStockDDECells c0 = sample.getDdeCells();

        int colName = colOf(c0.getNameCell());
        int colLast = colOf(c0.getLastPriceCell());
        int colBid = colOf(c0.getBidCell());
        int colAsk = colOf(c0.getAskCell());
        int colVol = colOf(c0.getVolumeCell());
        int colOpen = colOf(c0.getOpenCell());
        int colBase = colOf(c0.getBaseCell());
        int colWeight = colOf(c0.getWeightCell());
        
        // בקשות Range לעמודות דרך ערוץ המניות
        String[] names = lines(stocksConversation.request(range(colName, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] last = parseDoubles(stocksConversation.request(range(colLast, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] bid = parseDoubles(stocksConversation.request(range(colBid, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] ask = parseDoubles(stocksConversation.request(range(colAsk, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        int[] vol = parseInts(stocksConversation.request(range(colVol, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] open = parseDoubles(stocksConversation.request(range(colOpen, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] base = parseDoubles(stocksConversation.request(range(colBase, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] weight = parseDoubles(stocksConversation.request(range(colWeight, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));

        int rows = maxLen(names.length, last.length, bid.length, ask.length,
                vol.length, open.length, base.length, weight.length);

        for (MiniStock s : stocks) {
            int idx = s.getRow() - STOCKS_START_ROW;
            if (idx < 0 || idx >= rows) continue;

            if (idx < names.length) {
                String nm = safeTrim(names[idx]);
                if (!nm.isEmpty()) s.setName(nm);
            }
            if (idx < last.length) s.setLast(last[idx]);
            if (idx < bid.length) s.setBid(bid[idx]);
            if (idx < ask.length) s.setAsk(ask[idx]);
            if (idx < vol.length) s.setVolume(vol[idx]);
            if (idx < open.length) s.setOpen(open[idx]);
            if (idx < base.length) s.setBase(base[idx]);
            if (idx < weight.length) s.setWeight(weight[idx]);
        }
    }

    // ==== Helpers ====
    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String range(int col, int startRow, int endRowExc) {
        return "R" + startRow + "C" + col + ":R" + (endRowExc - 1) + "C" + col;
    }

    private static int colOf(String rc) {
        int cPos = rc.lastIndexOf('C');
        return Integer.parseInt(rc.substring(cPos + 1));
    }

    private static String[] lines(String raw) {
        return raw == null ? new String[0] : raw.split("\\r?\\n");
    }

    private static double[] parseDoubles(String raw) {
        String[] ls = lines(raw);
        double[] out = new double[ls.length];
        for (int i = 0; i < ls.length; i++) out[i] = parseDoubleFast(ls[i]);
        return out;
    }

    private static int[] parseInts(String raw) {
        String[] ls = lines(raw);
        int[] out = new int[ls.length];
        for (int i = 0; i < ls.length; i++) {
            try {
                out[i] = (int) Math.round(parseDoubleFast(ls[i]));
            } catch (Exception e) {
                out[i] = 0;
            }
        }
        return out;
    }

    private static double parseDoubleFast(String s) {
        if (s == null) return 0.0;
        s = s.trim();
        if (s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s.replace(",", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static int maxLen(int... arr) {
        int m = 0;
        for (int v : arr) if (v > m) m = v;
        return m;
    }

    // === API קיים נשמר ===
    @Override
    public void go() {
        update();
    }

    @Override
    public String getName() {
        return "Stocks sheet service";
    }

    @Override
    public int getSleep() {
        return 400;
    }
}
