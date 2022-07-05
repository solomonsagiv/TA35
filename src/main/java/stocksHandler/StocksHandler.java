package stocksHandler;

import charts.myChart.MyChartList;
import locals.L;
import miniStocks.MiniStock;
import myJson.IJsonData;
import myJson.MyJson;

public class StocksHandler implements IJsonData {
	
	MiniStock[] stocks;
	

	private MyChartList indDeltaList = new MyChartList();
	private MyChartList indDeltaNoBasketsList = new MyChartList();
	
	public StocksHandler() {
		stocks = new MiniStock[35];
		for (int i = 0; i < stocks.length; i++) {
			stocks[i] = new MiniStock(this);
		}
	}
	
	public MyJson getData() {
		MyJson json = new MyJson();
		
		int i = 0;
		for (MiniStock miniStock : stocks) {
			json.put(L.str(i), miniStock.getAsJson());
			i++;
		}
		return json;
	}
	

	public MiniStock[] getStocks() {
		return stocks;
	}

	public MyChartList getIndDeltaList() {
		return indDeltaList;
	}
	
	public MyChartList getIndDeltaNoBasketsList() {
		return indDeltaNoBasketsList;
	}
	
	@Override
	public MyJson getAsJson() {
		MyJson json = new MyJson();
		return json;
	}
		
	@Override
	public void loadFromJson(MyJson json) {
	}

	@Override
	public MyJson getResetJson() {
		return new MyJson();
	}

	@Override
	public MyJson getFullResetJson() {
		return getResetJson();
	}

}
