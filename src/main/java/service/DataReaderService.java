package service;

import api.TA35;
import api.dde.DDE.DDEConnection;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import locals.L;
import miniStocks.MiniStock;
import miniStocks.MiniStockDDECells;
import options.*;

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

    // Columns
    final int CALL_BID = 2;
    final int CALL_LAST = 3;
    final int CALL_ASK = 4;
    final int CALL_VOLUME = 5;
    final int STRIKE = 6;
    final int PUT_BID = 7;
    final int PUT_LAST = 8;
    final int PUT_ASK = 7;
    final int PUT_VOLUME = 9;

    // Rows
    final int START_ROW_MONTH = 44;
    final int END_ROW_MONTH = 57;
    final int START_ROW_WEEK = 66;
    final int END_ROW_WEEK = 86;

    private Options optionsWeek;
    private Options optionsMonth;

    boolean set_options = false;

    public DataReaderService(TA35 ta35, String excel_path) {
        super(ta35);
        this.ta35 = ta35;
        this.conversation = new DDEConnection().createNewConversation(excel_path);
    }

    public void update() {
        try {
            String status = conversation.request(statusCell).replaceAll("\\s+", "");

            // Ticker datas
            ta35.setStatus(status);


            if (ta35.getStatus() != "preopen") {

                if (ta35.getExps().getMonth().getOptions() != null) {
                    this.optionsWeek = ta35.getExps().getWeek().getOptions();
                    this.optionsMonth = ta35.getExps().getMonth().getOptions();
                    set_options = true;
                }

                optionsMonth.setContract(L.dbl(conversation.request(futureCell)));

                // Week
                optionsWeek.setContract(L.dbl(conversation.request(futureWeekCell)));
                optionsWeek.setContractBid(L.dbl(conversation.request(futureWeekBidCell)));
                optionsWeek.setContractAsk(L.dbl(conversation.request(futureWeekAskCell)));

                // Big
                ta35.setMid(L.dbl(conversation.request(index_mid_cell)));
                ta35.setIndex(L.dbl(conversation.request(indexCell)));
                ta35.setBid(L.dbl(conversation.request(indexBidCell)));
                ta35.setAsk(L.dbl(conversation.request(indexAskCell)));
                ta35.setHigh(L.dbl(conversation.request(highCell)));
                ta35.setLow(L.dbl(conversation.request(lowCell)));
                ta35.setBase(L.dbl(conversation.request(baseCell)));
                ta35.setOpen(L.dbl(conversation.request(openCell)));
                ta35.setLast_price(L.dbl(conversation.request(lastCell)));
                optionsMonth.setContractBid(L.dbl(conversation.request(futureBidCell)));
                optionsMonth.setContractAsk(L.dbl(conversation.request(futureAskCell)));

                // Interest
                ta35.setOp_week_interest(L.dbl(conversation.request(op_week_interest_cell)) * 100);
                ta35.setOp_month_interest(L.dbl(conversation.request(op_interest_month_cell)) * 100);
                ta35.setRoll_interest(L.dbl(conversation.request(roll_interest_cell)) * 100);

                // Append data to lists
                ta35.getRoll_interest_list().add(ta35.getRoll_interest());
                ta35.getSpot_interest_list().add(ta35.getOp_month_interest());

                // Read stocks
                read_stocks();

                // Read options
                handle_read_options();

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
        Option call, put;
        double strike;

        for (int row = start_row; row < end_row; row++) {
            strike = L.dbl(conversation.request(L.cell(row, STRIKE)));

            // Call
            call = options.getOption(Option.Side.CALL, strike);
            call.setBid((int) L.dbl(conversation.request(L.cell(row, CALL_BID))));
            call.setAsk((int) L.dbl(conversation.request(L.cell(row, CALL_ASK))));
            call.setVolume((int) L.dbl(conversation.request(L.cell(row, CALL_VOLUME))));
            call.setLast((int) L.dbl(conversation.request(L.cell(row, CALL_LAST))));

            // Put
            put = options.getOption(Option.Side.PUT, strike);
            put.setBid((int) L.dbl(conversation.request(L.cell(row, PUT_BID))));
            put.setAsk((int) L.dbl(conversation.request(L.cell(row, PUT_ASK))));
            put.setVolume((int) L.dbl(conversation.request(L.cell(row, PUT_VOLUME))));
            put.setLast((int) L.dbl(conversation.request(L.cell(row, PUT_LAST))));
        }
    }

    private double read_double_from_dde(String cell) throws DDEException {
        return L.dbl(conversation.request(cell));
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

        if (initStocksCells) {
            for (MiniStock stock : ta35.getStocksHandler().getStocks()) {
                try {
                    MiniStockDDECells ddeCells = stock.getDdeCells();

                    stock.setLast(L.dbl(conversation.request(ddeCells.getLastPriceCell())));
                    stock.setBid(L.dbl(conversation.request(ddeCells.getBidCell())));
                    stock.setAsk(L.dbl(conversation.request(ddeCells.getAskCell())));
                    stock.setVolume((int) L.dbl(conversation.request(ddeCells.getVolumeCell())));
                    stock.setOpen(L.dbl(conversation.request(ddeCells.getOpenCell())));
                    stock.setBase(L.dbl(conversation.request(ddeCells.getBaseCell())));
                    stock.setName(conversation.request(ddeCells.getNameCell()));
                    stock.setWeight(L.dbl(conversation.request(ddeCells.getWeightCell())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            initStockCells(conversation);
            initStocksCells = true;

        }
    }

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

    private void initStockCells(DDEClientConversation conversation) {

        int nameCol = 21;
        int start_row = 2;
        int end_row = 37;

        for (int row = start_row; row < end_row; row++) {
            try {
                String name = conversation.request(String.format("R%sC%s", row, nameCol));
                System.out.println("Name : " + name + " " + row);
//                 End
                MiniStock stock = new MiniStock(ta35.getStocksHandler(), row);
                stock.setName(name);
//                 Add stock
                ta35.getStocksHandler().addStock(stock);

            } catch (DDEException e) {
                e.printStackTrace();
            }
        }
        initStocksCells = true;
    }
}
