package miniStocks;

import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import stocksHandler.StocksHandler;

public class MiniStock implements IJsonData {
	
	// Variables
	StocksHandler handler;
	
	private double bid = 0;
	private double ask = 0;
	private double last = 0;
	private int volume = 0;
	private int preVolume = 0;
	private double delta = 0;
	private double weight = 0;
	private String name = "";
	
	// Constructor 
	public MiniStock(StocksHandler handler) {
		this.handler = handler;
	}
	
	// Getters and Setters	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getBid() {
		return bid;
	}
	public void setBid(double bid) {
		this.bid = bid;
	}
	public double getAsk() {
		return ask;
	}
	public void setAsk(double ask) {
		this.ask = ask;
	}
	public double getLast() {
		return last;
	}
	public void setLast(double last) {
		this.last = last;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public double getDelta() {
		return delta;
	}
	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	public int getPreVolume() {
		return preVolume;
	}
	
	public void setPreVolume(int preVolume) {
		this.preVolume = preVolume;
	}
	
	public void appendDelta(double newDelta) {
		this.delta += newDelta;
		
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	@Override
	public MyJson getAsJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.name, name);
		json.put(JsonStrings.last, last);
		json.put(JsonStrings.bid, bid);
		json.put(JsonStrings.ask, ask);
		json.put(JsonStrings.delta, delta);
		json.put(JsonStrings.volume, volume);
		json.put(JsonStrings.weight, weight);
		return json;
	}

	@Override
	public void loadFromJson(MyJson json) {
		setDelta(json.getDouble(JsonStrings.delta));
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

