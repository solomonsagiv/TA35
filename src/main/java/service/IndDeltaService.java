package service;

import api.dde.DDE.DDEConnection;
import api.deltaTest.Calculator;
import com.pretty_tools.dde.client.DDEClientConversation;
import locals.L;
import miniStocks.MiniStock;

public class IndDeltaService extends MyBaseService {

    DDEClientConversation conversation;

    public IndDeltaService(String excel_path) {
        super();
        DDEConnection ddeConnection = new DDEConnection(apiObject);
        this.conversation = ddeConnection.createNewConversation(excel_path);
        initStocksName();
    }

    private void calcStocksDelta() {

        MiniStock[] miniStocks = apiObject.getStocksHandler().getStocks();

        for (int i = 2; i < 37; i++) {
            try {
                MiniStock miniStock = miniStocks[i - 2];

                int newLast = (int) L.dbl(conversation.request(cell(i, 12)));
                int newVolume = (int) L.dbl(conversation.request(cell(i, 13)));
                double bid = L.dbl(conversation.request(cell(i, 14)));
                double ask = L.dbl(conversation.request(cell(i, 15)));
                double weight = L.dbl(conversation.request(cell(i, 16)));
                double delta = Calculator.calcMiniStockDelta(miniStock, newLast, newVolume);

                miniStock.setLast(newLast);
                miniStock.setBid(bid);
                miniStock.setAsk(ask);
                miniStock.setVolume(newVolume);
                miniStock.setWeight(weight);
                miniStock.appendDelta(delta);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initStocksName() {

        MiniStock[] miniStocks = apiObject.getStocksHandler().getStocks();

        for (int i = 2; i < 37; i++) {
            try {
                MiniStock miniStock = miniStocks[i - 2];

                String name = (L.str(conversation.request(cell(i, 17)))).replace("\r\n", "");
                miniStock.setName(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String cell(int row, int col) {
        return "R" + row + "C" + col;
    }

    @Override
    public void go() {
        calcStocksDelta();
    }

    @Override
    public String getName() {
        return "Ind delta service";
    }

    @Override
    public int getSleep() {
        return 1000;
    }
}
