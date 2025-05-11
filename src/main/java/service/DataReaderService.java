package service;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import api.dde.DDE.DDEConnection;
import com.pretty_tools.dde.client.DDEClientConversation;
import dde.DDECells;
import locals.L;
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
                ta35.setMid(L.dbl(conversation.request(indexCell)));
                ta35.setBid(L.dbl(conversation.request(indexBidCell)));
                ta35.setAsk(L.dbl(conversation.request(indexAskCell)));
                ta35.setHigh(L.dbl(conversation.request(highCell)));
                ta35.setLow(L.dbl(conversation.request(lowCell)));
                ta35.setBase(L.dbl(conversation.request(baseCell)));
                ta35.setOpen(L.dbl(conversation.request(openCell)));
                ta35.setLast_price(L.dbl(conversation.request(lastCell)));
                optionsMonth.setContractBid(L.dbl(conversation.request(futureBidCell)));
                optionsMonth.setContractAsk(L.dbl(conversation.request(futureAskCell)));

                // Read stocks
                read_stocks();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read_stocks() {

        for (BASE_CLIENT_OBJECT stock : L.stocks) {
            try {
                DDECells ddeCells = stock.getDdeCells();

                stock.setLast_price(L.dbl(conversation.request(ddeCells.getCell(DDECells.LAST_PRICE))));
                stock.setBid(L.dbl(conversation.request(ddeCells.getCell(DDECells.BID))));
                stock.setAsk(L.dbl(conversation.request(ddeCells.getCell(DDECells.ASK))));
                stock.setMid(L.dbl(conversation.request(ddeCells.getCell(DDECells.MID))));
                stock.setOpen(L.dbl(conversation.request(ddeCells.getCell(DDECells.OPEN))));
                stock.setBase(L.dbl(conversation.request(ddeCells.getCell(DDECells.BASE))));

            } catch (Exception e) {
                e.printStackTrace();
            }
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
}
