package service;

import counter.WindowTA35;
import miniStocks.MiniStock;

public class BasketService extends MyBaseService {

    double ind_0 = 0;

    public BasketService() {
        super();
    }

    private void resetVariables() {
        ind_0 = apiObject.getIndex();
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

        // More then 30 changes
        if (changesCount > 28) {

            double ind = apiObject.getIndex();

            // Up
            if (ind > ind_0) {
                apiObject.incrementBasketUp();
            }

            // Down
            if (ind < ind_0) {
                apiObject.incrementBasketDown();
            }
        }
        try {
            WindowTA35.log.setText("Changed: " + changesCount + " \n " + ind_0);
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
        resetVariables();
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
