package api.deltaTest;

import api.TA35;
import miniStocks.MiniStock;
import options.Option;
import stocksHandler.StocksHandler;

public class Calculator {

    public static double calc(Option option, int newLast, int newVolume, double newDelta) {

        // Volume check
        if (newVolume > option.getVolume()) {

            double quantity = newVolume - option.getVolume();
            double delta = 0;

            // Buy ( Last == pre ask )
            if (newLast == option.getAsk()) {
                delta = quantity * newDelta;
            }

            // Buy ( Last == pre bid )
            if (newLast == option.getBid()) {
                delta = quantity * newDelta * -1;
            }

            // Append delta
            return delta;
        }
        return 0;
    }

    public static void calc_stocks_buy_sell_counters() {
        StocksHandler stocksHandler = TA35.getInstance().getStocksHandler();

        double buy_sell_counter = 0;

        for (MiniStock stock: stocksHandler.getStocks()) {
            double weight = stock.getWeight();
            double change = stock.getBuy_sell_counter() - stock.getBuy_sell_counter_0();

            if (change > 0) {
                buy_sell_counter++;
            } else {
                buy_sell_counter--;
            }

            // Append buy sell _0
            stock.setBuy_sell_counter_0(stock.getBuy_sell_counter());
        }

        TA35 client  = TA35.getInstance();
        client.setBuy_sell_counter(buy_sell_counter);
    }

    public static void calc_stock_buy_sell_counter(MiniStock miniStock, int quantity) {
	    // Buy
        if (miniStock.getLast() >= miniStock.getAsk() && miniStock.getLast() > miniStock.getPre_last()) {
        		miniStock.stock_buy(quantity);
        }
	    // Sell
	    if (miniStock.getLast() <= miniStock.getBid() && miniStock.getLast() < miniStock.getPre_last()) {
		    miniStock.stock_sell(quantity);
	    }
    }

}
