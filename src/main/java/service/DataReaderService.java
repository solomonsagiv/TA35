package service;

import api.TA35;
import api.dde.DDE.DDEConnection;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import locals.L;
import miniStocks.MiniStock;
import miniStocks.MiniStockDDECells;
import options.Options;

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

    boolean initStocksCells = false;

    DDEClientConversation conversation;

    TA35 ta35;

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

                Options optionsWeek = ta35.getExps().getWeek().getOptions();
                Options optionsMonth = ta35.getExps().getMonth().getOptions();

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
                ta35.setOp_week_interest(L.dbl(conversation.request(op_week_interest_cell)));
                ta35.setOp_month_interest(L.dbl(conversation.request(op_interest_month_cell)));
                ta35.setRoll_interest(L.dbl(conversation.request(roll_interest_cell)));

                // Read stocks
                read_stocks();

            }
        } catch (Exception e) {
            e.printStackTrace();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            initStockCells(conversation);
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

        int nameCol = 10;
        int row = 2;

        while (true) {
            try {
                String name = conversation.request(String.format("R%sC%s", row, nameCol));

//                 End
                if (row > 100) {
                    break;
                }

//                 End
                if (name.replaceAll("\\s+", "").equals("0")) {
                    break;
                }

                MiniStock stock = new MiniStock(ta35.getStocksHandler(), row);

//                 Add stock
                ta35.getStocksHandler().getStocks().add(stock);
                row++;

            } catch (DDEException e) {
                e.printStackTrace();
            }
        }

        initStocksCells = true;
    }
}
