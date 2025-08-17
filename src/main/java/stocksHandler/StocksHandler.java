package stocksHandler;

import charts.myChart.MyChartList;
import locals.L;
import miniStocks.MiniStock;
import myJson.IJsonData;
import myJson.MyJson;

import java.util.ArrayList;

public class StocksHandler implements IJsonData {
	
	ArrayList<MiniStock> stocks;

	private MyChartList indDeltaList = new MyChartList();
	private MyChartList indDeltaNoBasketsList = new MyChartList();

	private double buy_sell_counter = 0;
	private double buy_sell_quantity_counter = 0;
	
	public StocksHandler() {
		this.stocks = new ArrayList<>();
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

	public ArrayList<MiniStock> getStocks() {
		return stocks;
	}

	public void setStocks(ArrayList<MiniStock> stocks) {
		this.stocks = stocks;
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
