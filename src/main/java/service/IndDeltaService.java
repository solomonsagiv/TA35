package service;

import api.INDEX_OBJECT;
import api.dde.DDE.DDEConnection;
import api.deltaTest.Calculator;
import com.pretty_tools.dde.client.DDEClientConversation;
import locals.L;
import miniStocks.MiniStock;
import miniStocks.MiniStockDDECells;

import java.util.ArrayList;

public class IndDeltaService extends MyBaseService {

    DDEClientConversation conversation;
    INDEX_OBJECT client;

    public IndDeltaService(INDEX_OBJECT client, String excel_path) {
        super(client);
        this.client = client;
        DDEConnection ddeConnection = new DDEConnection();
        this.conversation = ddeConnection.createNewConversation(excel_path);
        initStocksName();
    }

    private void calcStocksDelta() {

        ArrayList<MiniStock> miniStocks = client.getStocksHandler().getStocks();

        for (MiniStock miniStock : miniStocks) {
            try {
                MiniStockDDECells cells = miniStock.getDdeCells();

                int newLast = (int) L.dbl(cells.getLastPriceCell());
                int newVolume = (int) L.dbl(cells.getVolumeCell());
                double bid = L.dbl(cells.getBidCell());
                double ask = L.dbl(cells.getAskCell());
                double weight = L.dbl(cells.getWeightCell());
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

        ArrayList<MiniStock> miniStocks = client.getStocksHandler().getStocks();

        for (MiniStock miniStock: miniStocks) {
            try {
                MiniStockDDECells cells = miniStock.getDdeCells();
                String name = (L.str(cells.getNameCell())).replace("\r\n", "");
                miniStock.setName(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        for (int i = 2; i < 37; i++) {

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
