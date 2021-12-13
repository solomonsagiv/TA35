package service;

import counter.WindowTA35;
import locals.L;
import miniStocks.MiniStock;

public class BasketService extends MyBaseService {

    public BasketService() {
        super();
    }

    private void findBasket() {

        MiniStock[] stocks = apiObject.getStocksHandler().getStocks();
        int changesCount = 0;

        // For each ministock
        for (MiniStock stock : stocks) {

            int volume = stock.getVolume();

            // Volume changeed
            if (volume > stock.getPreVolume() && stock.getVolume() != 0 && stock.getPreVolume() != 0) {
                changesCount++;
            }

            // Update pre volume
            stock.setPreVolume(volume);
        }

        // More then 28 changes
        if (changesCount > 28) {

            double ind = apiObject.getIndex();
            double ind_bid = apiObject.getIndex_bid();
            double ind_ask = apiObject.getIndex_ask();

            double index_to_ask_margin = L.abs(ind - ind_ask);
            double index_to_bid_margin = L.abs(ind - ind_bid);

            // Up
            if (index_to_ask_margin < index_to_bid_margin) {
                apiObject.incrementBasketUp();
            // Down
            } else {
                apiObject.incrementBasketDown();
            }
        }
        try {
            WindowTA35.log.setText("Changed: " + changesCount + " \n ");
        } catch (Exception e) {

        }
    }

    // Basket class
    private class Basket {

        private boolean upDown;

        public Basket() {
        }

        public boolean isUpDown() {
            return upDown;
        }

        public void setUpDown(boolean upDown) {
            this.upDown = upDown;
        }
    }

    @Override
    public void go() {
        findBasket();
    }

    @Override
    public String getName() {
        return "Basket service";
    }

    @Override
    public int getSleep() {
        return 5000;
    }


}
