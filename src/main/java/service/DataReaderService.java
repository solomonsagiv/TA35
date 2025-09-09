package service;

import api.TA35;
import api.dde.DDE.DDEConnection;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import miniStocks.MiniStock;
import miniStocks.MiniStockDDECells;
import options.Options;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// השארתי את הייבוא של options כדי לא לשבור build, לא נעשה בהם שימוש בפועל

public class DataReaderService extends MyBaseService {

    // Cells
    String futureCell = "R2C1";
    String indexCell = "R2C2";
    String indexBidCell = "R2C3";
    String indexAskCell = "R2C4";
    String openCell = "R2C5";
    String highCell = "R2C6";
    String lowCell = "R2C7";
    String baseCell = "R2C8";
    String lastCell = "R2C9";
    String futureWeekBidCell = "R9C2";
    String futureWeekAskCell = "R9C3";
    String futureWeekCell = "R9C4";
    String statusCell = "R7C1";
    String futureBidCell = "R7C2";
    String futureAskCell = "R7C3";
    String trading_status_cell = "RC3";
    String roll_interest_cell = "R5C5";
    String op_interest_month_cell = "R5C6";
    String op_week_interest_cell = "R5C7";
    String index_mid_cell = "R5C8";

    public static boolean initStocksCells = false;

    DDEClientConversation conversation;
    TA35 ta35;

    // Columns (לא בשימוש כרגע – נשמרו כדי לא לשבור קוד)
    final int CALL_BID = 2;
    final int CALL_LAST = 3;
    final int CALL_ASK = 4;
    final int CALL_VOLUME = 5;
    final int STRIKE = 6;
    final int PUT_BID = 7;
    final int PUT_LAST = 8;
    final int PUT_ASK = 7;
    final int PUT_VOLUME = 9;

    // Rows (לא בשימוש כרגע – נשמרו כדי לא לשבור קוד)
    final int START_ROW_MONTH = 44;
    final int END_ROW_MONTH = 57;
    final int START_ROW_WEEK = 66;
    final int END_ROW_WEEK = 86;

    private Options optionsWeek, optionsMonth;

    boolean set_options = false;

    // טווח המניות
    private final int STOCKS_START_ROW = 2;   // כולל
    private final int STOCKS_END_ROW_EXC = 37;  // בלעדי (2..36)
    private final int NAME_COL_DEFAULT = 21;  // אם שם המניה בעמודה אחרת – עדכן כאן

    public DataReaderService(TA35 ta35, String excel_path) {
        super(ta35);
        this.ta35 = ta35;
        this.conversation = new DDEConnection().createNewConversation(excel_path);
    }

    public void update() {
        try {
            String statusRaw = safeRequest(statusCell);
            if (statusRaw != null) {
                ta35.setStatus(statusRaw.replaceAll("\\s+", ""));
            }

            if (!"preopen".equalsIgnoreCase(ta35.getStatus())) {

                if (ta35.getExps().getMonth().getOptions() != null) {
                    this.optionsWeek = ta35.getExps().getWeek().getOptions();
                    this.optionsMonth = ta35.getExps().getMonth().getOptions();
                    set_options = true;
                }

                // --- HEAD (קריאות נקודתיות מהירות) ---
                ta35.setMid(fdbl(safeRequest(index_mid_cell)));
                ta35.setIndex(fdbl(safeRequest(indexCell)));
                ta35.setBid(fdbl(safeRequest(indexBidCell)));
                ta35.setAsk(fdbl(safeRequest(indexAskCell)));
                ta35.setHigh(fdbl(safeRequest(highCell)));
                ta35.setLow(fdbl(safeRequest(lowCell)));
                ta35.setBase(fdbl(safeRequest(baseCell)));
                ta35.setOpen(fdbl(safeRequest(openCell)));
                ta35.setLast_price(fdbl(safeRequest(lastCell)));

                // Week
                optionsWeek.setContract(fdbl(safeRequest(futureWeekCell)));
                optionsWeek.setContractBid(fdbl(safeRequest(futureWeekBidCell)));
                optionsWeek.setContractAsk(fdbl(safeRequest(futureWeekAskCell)));

                // Month
                optionsMonth.setContract(fdbl(safeRequest(futureCell)));
                optionsMonth.setContractBid(fdbl(safeRequest(futureBidCell)));
                optionsMonth.setContractAsk(fdbl(safeRequest(futureAskCell)));

                // Interest
                ta35.setOp_week_interest(fdbl(safeRequest(op_week_interest_cell)) * 100.0);
                ta35.setOp_month_interest(fdbl(safeRequest(op_interest_month_cell)) * 100.0);
                ta35.setRoll_interest(fdbl(safeRequest(roll_interest_cell)) * 100.0);

                // Append data to lists
                ta35.getRoll_interest_list().add(ta35.getRoll_interest());
                ta35.getSpot_interest_list().add(ta35.getOp_month_interest());

                // Read stocks (Batch)
                read_stocks();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======== Stocks ========

    private void read_stocks() {
        if (!initStocksCells) {
            initStockCells(conversation); // אתחול רשימת המניות בבאטצ' של שמות
            initStocksCells = true;
        }
        try {
            batchReadStocks();            // קריאה מרוכזת לכל העמודות הנדרשות
        } catch (DDEException e) {
            e.printStackTrace();
        }
    }

    private void batchReadStocks() throws DDEException {
        Set<MiniStock> stocks = ta35.getStocksHandler().getStocks();
        if (stocks == null || stocks.isEmpty()) return;

        // לוקחים מניה כדוגמה כדי לחלץ מספרי עמודות מתוך התאים בסגנון "R{row}C{col}"
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

        // בקשה אחת לכל עמודה על כל הטווח
        String[] names = lines(conversation.request(range(colName, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] last = parseDoubles(conversation.request(range(colLast, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] bid = parseDoubles(conversation.request(range(colBid, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] ask = parseDoubles(conversation.request(range(colAsk, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        int[] vol = parseInts(conversation.request(range(colVol, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] open = parseDoubles(conversation.request(range(colOpen, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] base = parseDoubles(conversation.request(range(colBase, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));
        double[] weight = parseDoubles(conversation.request(range(colWeight, STOCKS_START_ROW, STOCKS_END_ROW_EXC)));

        int rows = maxLen(names.length, last.length, bid.length, ask.length,
                vol.length, open.length, base.length, weight.length);

        for (MiniStock s : stocks) {
            int idx = s.getRow() - STOCKS_START_ROW;
            if (idx < 0 || idx >= rows) continue;

            if (idx < names.length) {
                String nm = names[idx].trim();
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

    private void initStockCells(DDEClientConversation conversation) {
        int nameCol = NAME_COL_DEFAULT;
        int start_row = STOCKS_START_ROW;
        int end_row = STOCKS_END_ROW_EXC;

        try {
            // בקשה אחת לכל שמות המניות
            String raw = conversation.request(range(nameCol, start_row, end_row));
            String[] names = lines(raw);

            List<MiniStock> created = new ArrayList<>(names.length);
            for (int i = 0; i < names.length; i++) {
                String name = names[i].trim();
                if (name.isEmpty()) continue;

                int row = start_row + i;
                MiniStock stock = new MiniStock(ta35.getStocksHandler(), row);
                stock.setName(name);
                ta35.getStocksHandler().addStock(stock);
                created.add(stock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======== Helpers ========

    private String safeRequest(String item) {
        try {
            return conversation.request(item);
        } catch (Exception e) {
            return null;
        }
    }

    private static String range(int col, int startRow, int endRowExc) {
        return "R" + startRow + "C" + col + ":R" + (endRowExc - 1) + "C" + col;
    }

    private static int colOf(String rc) {
        // "R12C21" -> 21
        int cPos = rc.lastIndexOf('C');
        return Integer.parseInt(rc.substring(cPos + 1));
    }

    private static String[] lines(String raw) {
        return raw == null ? new String[0] : raw.split("\\r?\\n");
    }

    private static double[] parseDoubles(String raw) {
        String[] ls = lines(raw);
        double[] out = new double[ls.length];
        for (int i = 0; i < ls.length; i++) out[i] = fdbl(ls[i]);
        return out;
    }

    private static int[] parseInts(String raw) {
        String[] ls = lines(raw);
        int[] out = new int[ls.length];
        for (int i = 0; i < ls.length; i++) {
            try {
                out[i] = (int) Math.round(fdbl(ls[i]));
            } catch (Exception e) {
                out[i] = 0;
            }
        }
        return out;
    }

    private static double fdbl(String s) {
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

    // ======== Service API ========

    @Override
    public void go() {
        update();
    }

    @Override
    public String getName() {
        return "Data sheet service (Batch stocks, no options)";
    }

    @Override
    public int getSleep() {
        return 200;
    }
}
