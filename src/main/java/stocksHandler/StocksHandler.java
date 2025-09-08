package stocksHandler;

import locals.L;
import miniStocks.MiniStock;
import myJson.IJsonData;
import myJson.MyJson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StocksHandler implements IJsonData {
	
	private Set<MiniStock> stocks;

	private double buy_sell_counter = 0;
	private double buy_sell_quantity_counter = 0;
	
	public StocksHandler() {
		this.stocks = new HashSet<>();
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

	public Set<MiniStock> getStocks() {
		return stocks;
	}


	public void addStock(MiniStock stock) {
		for (MiniStock s : stocks) {
			if (stock.getName().equals(s.getName())) {
				System.out.println(stock.getName() + " Already inside ):");
				return;
			}
		}

		System.out.println(stock.getName() + " ADD :)");
		stocks.add(stock);
	}

	public void setStocks(Set<MiniStock> stocks) {
		this.stocks = stocks;
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
