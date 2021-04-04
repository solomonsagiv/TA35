package charts.barChart.updater;

import org.jfree.data.category.DefaultCategoryDataset;

import locals.L;
import miniStocks.MiniStock;

public class StocksChartBarUpdater implements IBarChartUpdater {

	MiniStock[] stocks;

	public StocksChartBarUpdater(MiniStock[] stocks) {
		this.stocks = stocks;
	}
	
	@Override
	public void updateBasics(DefaultCategoryDataset dataset) {
		for (MiniStock stock : stocks) {
//			dataset.addValue(stock.getDelta(), stock.getName(), stock.getName());
			dataset.addValue(stock.getDelta(), L.str(stock.hashCode()), L.str(stock.hashCode()));
		}
	}
	
	@Override
	public void update(DefaultCategoryDataset dataset) {
		for (MiniStock stock : stocks) {
//			dataset.setValue(stock.getDelta(), stock.getName(), stock.getName());
			dataset.addValue(stock.getDelta(), L.str(stock.hashCode()), L.str(stock.hashCode()));
		}
	}

}
