package stocksHandler;

import charts.myChart.MyChartList;
import locals.L;
import miniStocks.MiniStock;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;

public class StocksHandler implements IJsonData {
	
	MiniStock[] stocks;
	
	double delta = 0;
	double deltaNoBaskets = 0;
	
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
	
	public void minusNoBasketsDelta( double delta ) {
		this.deltaNoBaskets -= delta;
	}
	
	public MiniStock[] getStocks() {
		return stocks;
	}

	public double getDelta() {
		return delta;
	}

	public double getDeltaNoBaskets() {
		return deltaNoBaskets;
	}
	
	public void appendDelta(double delta) {
		this.delta += delta;
	}

	public void appendDeltaNoBasket(double delta) {
		this.deltaNoBaskets += delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public void setDeltaNoBaskets(double deltaNoBaskets) {
		this.deltaNoBaskets = deltaNoBaskets;
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
		json.put(JsonStrings.delta, delta);
		json.put(JsonStrings.deltaNoBaskets, deltaNoBaskets);
		return json;
	}
		
	@Override
	public void loadFromJson(MyJson json) {
		setDelta(json.getDouble(JsonStrings.delta));
		setDeltaNoBaskets(json.getDouble(JsonStrings.deltaNoBaskets));
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
