package options;

import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;

import java.util.ArrayList;
import java.util.HashMap;

public class Option implements IJsonData {

	private String name;
	private int strike;
	private String side;
	
	private int id = 0;
	private int cellRow = 0;
	private int bid = 0;
	private int ask = 0;
	private int last = 0;
	private int high = 0;
	private int low = 0;
	private int base = 0;
	private int prem = 10;
	private int pricing = -10;
	private int open_pos = 0;
	private double calcPrice = 0;
	private int volume = 0;
	private double stDev = 0;
	private double delta = 0;
	private ArrayList<Integer> cycleState = new ArrayList<>();
	
	private int bidAskCounter = 0;
	private int deltaCounter = 0;

	private HashMap<Integer, Level> levels;
	Options options;

	// Constructor
	public Option(String side, int strike, Options options) {
		this.side = side;
		this.strike = strike;
		this.name = side + String.valueOf(strike);
		this.options = options;

		levels = new HashMap<Integer, Level>();
		levels.put(1, new Level(1));
		levels.put(2, new Level(2));
		levels.put(3, new Level(3));
		levels.put(4, new Level(4));
		levels.put(5, new Level(5));
	}
	
	// Getters and Setters
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getStrike() {
		return strike;
	}

	public void setStrike(int strike) {
		this.strike = strike;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public int getLast() {
		return last;
	}

	public void setLast(int last) {
		this.last = last;
	}

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}

	public int getLow() {
		return low;
	}

	public void setLow(int low) {
		this.low = low;
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public int getCellRow() {
		return cellRow;
	}

	public void setCellRow(int cellRow) {
		this.cellRow = cellRow;
	}

	public HashMap<Integer, Level> getLevels() {
		return levels;
	}

	public void setLevels(HashMap<Integer, Level> levels) {
		this.levels = levels;
	}

	public int getBidAskCounter() {
		return bidAskCounter;
	}

	public void setBidAskCounter(int bidAskCounter) {
		this.bidAskCounter = bidAskCounter;
	}

	public int lastCycle() throws NullPointerException {
		return cycleState.get(1);
	}
	
	public int preCycle() throws NullPointerException {
		return cycleState.get(0);
	}
	
	public void appendCycleState(int cycle) {
		if (cycleState.size() < 2) {
			cycleState.add(cycle);
		} else {
			cycleState.remove(0);
			cycleState.add(cycle);
		}
	}

	public double getStDev() {
		return stDev;
	}

	public void setStDev(double stDev) {
		this.stDev = stDev;
	}

	public double getCalcPrice() {
		return calcPrice;
	}

	public void setCalcPrice(double calcPrice) {
		this.calcPrice = calcPrice;
	}

	@Override
	public String toString() {
		return "Option{" +
				"name='" + name + '\'' +
				", bid=" + bid +
				", ask=" + ask +
				", last=" + last +
				", volume=" + volume +
				", delta=" + delta +
				'}';
	}

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public int getAsk() {
		return ask;
	}

	public void setAsk(int ask) {
		this.ask = ask;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume( int volume ) {
		this.volume = volume;
	}

	public double getDelta() {
		return delta;
	}

	public void appendDelta( double delta ) {
		this.delta += delta;
	}

	public int getDeltaCounter() {
		return deltaCounter;
	}

	public void setDeltaCounter(int deltaCounter) {
		this.deltaCounter = deltaCounter;
	}

	public void increaseDeltaCounter() {
		deltaCounter++;
	}
	
	public void decreaseDeltaCounter() {
		deltaCounter--;
	}

	public int getPrem() {
		return prem;
	}

	public void setPrem(int prem) {
		this.prem = prem;
	}

	public void setPricing(int pricing) {
		this.pricing = pricing;
	}

	public void setOpen_pos(int open_pos) {
		this.open_pos = open_pos;
	}

	public int getPricing() {
		return pricing;
	}

	public int getOpen_pos() {
		return open_pos;
	}

	@Override
	public MyJson getAsJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.delta, delta);
		json.put(JsonStrings.open_pos, open_pos);
		json.put(JsonStrings.bid, bid);
		json.put(JsonStrings.ask, ask);
		return json;
	}

	@Override
	public void loadFromJson(MyJson json) {
		try {
			appendDelta(json.getDouble(JsonStrings.delta));
			setOpen_pos(json.getInt(JsonStrings.open_pos));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public MyJson getResetJson() {
		return new MyJson();
	}

	@Override
	public MyJson getFullResetJson() {
		return null;
	}
}
