package api.deltaTest;

import api.TA35;
import miniStocks.MiniStock;
import options.Option;

public class Calculator {

	public static double  calc(Option option, int newLast, int newVolume, double newDelta) {

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
	
	public static double calcMiniStockDelta(MiniStock miniStock, int newLast, int newVolume) {
		
		TA35 client = TA35.getInstance();
		
		double delta = 0;
		
		// Bid, Ask, Last != 0
		if (miniStock.getLast() != 0 && miniStock.getBid() != 0 && miniStock.getAsk() != 0) {
			
			// Volume check
			if (newVolume > miniStock.getVolume()) {
				double quantity = newVolume - miniStock.getVolume();
				
				// Buy ( Last >= pre ask )
				if (newLast >= miniStock.getAsk()) {
					double stockWorth = client.getIndex() * 100 * miniStock.getWeight();
					double money = (quantity * miniStock.getLast());
					delta = (money / stockWorth) * miniStock.getWeight();
				}
				
				// Sell ( Last <= pre bid )
				if (newLast <= miniStock.getBid()) {
					double stockWorth = client.getIndex() * 100 * miniStock.getWeight();
					double money = (quantity * miniStock.getLast());
					delta = (money / stockWorth) * miniStock.getWeight();
					delta *= -1;
				}
			}
		}
		return delta;
	}

}
