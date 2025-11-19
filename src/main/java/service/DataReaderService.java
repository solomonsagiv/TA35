package service;

import api.TA35;
import api.dde.DDE.DDEConnection;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import locals.L;
import options.Option;
import options.Options;
import options.Strike;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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
    String statusCell = "R2C21";
    String futureBidCell = "R7C2";
    String futureAskCell = "R7C3";
    String trading_status_cell = "R2C21";
    String roll_interest_cell = "R5C5";
    String op_interest_month_cell = "R5C6";
    String op_week_interest_cell = "R5C7";
    String index_mid_cell = "R5C8";

    DDEClientConversation conversation;        // נשאר ל-HEAD/אופציות (כמו שהיה)

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
    private final int STOCKS_START_ROW   = 2;   // כולל
    private final int STOCKS_END_ROW_EXC = 37;  // בלעדי (2..36)
    private final int NAME_COL_DEFAULT   = 21;  // אם שם המניה בעמודה אחרת – עדכן כאן

    // Thread ייעודי למניות
    private final ExecutorService stocksExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "stocks-batch-worker");
        t.setDaemon(true);
        return t;
    });
    private final AtomicBoolean stocksInFlight = new AtomicBoolean(false);

    public DataReaderService(TA35 ta35, String excel_path) {
        super(ta35);
        this.ta35 = ta35;
        this.conversation = new DDEConnection().createNewConversation(excel_path);
    }

    public void update() {
        try {
            int status = Integer.parseInt(conversation.request(statusCell));
            ta35.setStatus(status);

            if (ta35.getStatus() == 0) {  // לא משנים את התנאי כדי לא לשבור לוגיקה קיימת

                sleepCount += getSleep();

//                 קישור אופציות (נשמר 1:1)
                if (ta35.getExps().getMonth().getOptions() != null) {
                    this.optionsWeek = ta35.getExps().getWeek().getOptions();
                    this.optionsMonth = ta35.getExps().getMonth().getOptions();
                    set_options = true;
                }

                // Futures (נשמר)
                optionsMonth.setContract(L.dbl(conversation.request(futureCell)));
                optionsMonth.setContractBid(L.dbl(conversation.request(futureBidCell)));
                optionsMonth.setContractAsk(L.dbl(conversation.request(futureAskCell)));

                // Week (נשמר)
                optionsWeek.setContract(L.dbl(conversation.request(futureWeekCell)));
                optionsWeek.setContractBid(L.dbl(conversation.request(futureWeekBidCell)));
                optionsWeek.setContractAsk(L.dbl(conversation.request(futureWeekAskCell)));

                // Big (נשמר)
                ta35.setMid(L.dbl(conversation.request(index_mid_cell)));
                ta35.setIndex(L.dbl(conversation.request(indexCell)));
                ta35.setBid(L.dbl(conversation.request(indexBidCell)));
                ta35.setAsk(L.dbl(conversation.request(indexAskCell)));
                ta35.setHigh(L.dbl(conversation.request(highCell)));
                ta35.setLow(L.dbl(conversation.request(lowCell)));
                ta35.setBase(L.dbl(conversation.request(baseCell)));
                ta35.setOpen(L.dbl(conversation.request(openCell)));
                ta35.setLast_price(L.dbl(conversation.request(lastCell)));

                // Read stocks – עכשיו ב-Thread נפרד עם Batch
//                if (sleepCount % 600 == 0) {
//                    read_stocks();
//                }

                // Reset sleep count
                if (sleepCount == 10000)  {
                    sleepCount = 0;
                }

                // Read options – נשמר כתגובה (מושבת)
                // handle_read_options();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean init_options = false;

    private void handle_read_options() throws DDEException {
        if (optionsMonth == null){
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
        int count = Math.max(0, end_row - start_row);
        if (count == 0) return;

        double[] strikes      = readColumnDoubles(STRIKE,     start_row, end_row);
        double[] callBids     = readColumnDoubles(CALL_BID,   start_row, end_row);
        double[] callAsks     = readColumnDoubles(CALL_ASK,   start_row, end_row);
        double[] callVolumes  = readColumnDoubles(CALL_VOLUME,start_row, end_row);
        double[] callLasts    = readColumnDoubles(CALL_LAST,  start_row, end_row);

        double[] putBids      = readColumnDoubles(PUT_BID,    start_row, end_row);
        double[] putAsks      = readColumnDoubles(PUT_ASK,    start_row, end_row);
        double[] putVolumes   = readColumnDoubles(PUT_VOLUME, start_row, end_row);
        double[] putLasts     = readColumnDoubles(PUT_LAST,   start_row, end_row);

        for (int i = 0; i < count; i++) {
            double strike = getValue(strikes, i);
            if (strike <= 0) {
                continue;
            }

            Option call = options.getOption(Option.Side.CALL, strike);
            if (call != null) {
                call.setBid((int) Math.round(getValue(callBids, i)));
                call.setAsk((int) Math.round(getValue(callAsks, i)));
                call.setVolume((int) Math.round(getValue(callVolumes, i)));
                call.setLast((int) Math.round(getValue(callLasts, i)));
            }

            Option put = options.getOption(Option.Side.PUT, strike);
            if (put != null) {
                put.setBid((int) Math.round(getValue(putBids, i)));
                put.setAsk((int) Math.round(getValue(putAsks, i)));
                put.setVolume((int) Math.round(getValue(putVolumes, i)));
                put.setLast((int) Math.round(getValue(putLasts, i)));
            }
        }
    }

    private double read_double_from_dde(String cell) throws DDEException {
        return L.dbl(conversation.request(cell));
    }

    private double[] readColumnDoubles(int column, int startRow, int endRow) throws DDEException {
        String raw = conversation.request(range(column, startRow, endRow));
        return parseDoubles(raw);
    }

    private double getValue(double[] values, int index) {
        if (values == null || index < 0 || index >= values.length) {
            return 0.0;
        }
        return values[index];
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

    // ==== Helpers ====
    private static String safeTrim(String s) { return s == null ? "" : s.trim(); }

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
            try { out[i] = (int) Math.round(parseDoubleFast(ls[i])); } catch (Exception e) { out[i] = 0; }
        }
        return out;
    }

    private static double parseDoubleFast(String s) {
        if (s == null) return 0.0;
        s = s.trim();
        if (s.isEmpty()) return 0.0;
        try { return Double.parseDouble(s.replace(",", "")); } catch (Exception e) { return 0.0; }
    }

    private static int maxLen(int... arr) {
        int m = 0; for (int v : arr) if (v > m) m = v; return m;
    }

    // === API קיים נשמר ===
    @Override
    public void go() {
        update();
    }

    @Override
    public String getName() {
        return "Data sheet service";
    }

    @Override
    public int getSleep() {
        return 200;
    }
}
