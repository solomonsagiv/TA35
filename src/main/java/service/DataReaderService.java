package service;

import api.dde.DDE.DDEConnection;
import com.pretty_tools.dde.client.DDEClientConversation;
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
    String daysToExpCell = "R4C1";

    DDEClientConversation conversation;

    public DataReaderService(String excel_path) {
        super();
        this.conversation = new DDEConnection(apiObject).createNewConversation(excel_path);
    }

    public void update() {
        try {
            String status = conversation.request(statusCell).replaceAll("\\s+", "");

            // Ticker datas
            apiObject.setStatus(status);

            if (apiObject.getStatus() != "preopen") {

                Options optionsWeek = apiObject.getExpWeek().getOptions();
                Options optionsMonth = apiObject.getExpMonth().getOptions();

                apiObject.setDaysToExp(L.dbl(conversation.request(daysToExpCell)));
                optionsMonth.setContract(L.dbl(conversation.request(futureCell)));

                // Week
                optionsWeek.setContract(L.dbl(conversation.request(futureWeekCell)));
                optionsWeek.setContractBid(L.dbl(conversation.request(futureWeekBidCell)));
                optionsWeek.setContractAsk(L.dbl(conversation.request(futureWeekAskCell)));

                // Big
                apiObject.setIndex(L.dbl(conversation.request(indexCell)));
                apiObject.setIndBid(L.dbl(conversation.request(indexBidCell)));
                apiObject.setIndAsk(L.dbl(conversation.request(indexAskCell)));
                apiObject.setHigh(L.dbl(conversation.request(highCell)));
                apiObject.setLow(L.dbl(conversation.request(lowCell)));
                apiObject.setBase(L.dbl(conversation.request(baseCell)));
                apiObject.setOpen(L.dbl(conversation.request(openCell)));
                apiObject.setLast(L.dbl(conversation.request(lastCell)));
                optionsMonth.setContractBid(L.dbl(conversation.request(futureBidCell)));
                optionsMonth.setContractAsk(L.dbl(conversation.request(futureAskCell)));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
