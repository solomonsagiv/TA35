package options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import api.ApiObject;
import charts.myChart.MyChartList;
import locals.L;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;

public class Options implements IJsonData {
	
	public static final int MONTH = 0;
	public static final int WEEK = 1;
	
	private double contract = 0;
	private double contractBid = 0;
	private double contractAsk = 0;
	private int conBidAskCounter = 0;
	
	private double delta = 0;
	
	List<Strike> strikes;
	HashMap<Integer, Option> optionsMap;
	
	private MyChartList deltaChartList = new MyChartList();
	private MyChartList conBidAskCounterList = new MyChartList();
	private MyChartList opChartList = new MyChartList();
	
	protected ApiObject apiObject;
	
	public Options(ApiObject apiObject) {
		this.apiObject = apiObject;
		strikes = new ArrayList<Strike>();
		optionsMap = new HashMap<Integer, Option>();
	}
	
	public Option getOption(String name) {
		
		double targetStrike = Double.parseDouble(name.substring(1));
		
		for (Strike strike : strikes) {
			if (strike.getStrike() == targetStrike) {
				if (name.toLowerCase().contains("c")) {
					return strike.getCall();
				} else {
					return strike.getPut();
				}
			}
		}
		return null;
	}
	
	public Strike getStrikeInMoney(double index, int strikeFarLevel) {
		
		double margin = 100000;
		int returnIndex = 0;
		
		List<Strike> strikes = getStrikes();
		
		for (int i = 0; i < strikes.size(); i++) {
			
			Strike strike = strikes.get(i);
			double newMargin = absolute(strike.getStrike() - index);
			
			if (newMargin < margin) {
				margin = newMargin;
				returnIndex = i;
			} else {
				return strikes.get(returnIndex + strikeFarLevel);
			}
		}
		return null;
	}
	
	public ArrayList<Option> getOptionsList() {
		
		ArrayList<Option> optionsList = new ArrayList<>();
		
		for (Strike strike : strikes) {
			optionsList.add(strike.getCall());
			optionsList.add(strike.getPut());
		}
		
		return optionsList;
	}
	
	public Option getOption(String side, double targetStrike) {
		for (Strike strike : strikes) {
			if (strike.getStrike() == targetStrike) {
				if (side.toLowerCase().contains("c")) {
					return strike.getCall();
				} else {
					return strike.getPut();
				}
			}
		}
		return null;
	}

	public double getOp() {
		return getContract() - apiObject.getIndex();
	}
	
	// Return single strike by strike price (double)
	public Strike getStrike(double strikePrice) {
		for (Strike strike : strikes) {
			if (strikePrice == strike.getStrike()) {
				return strike;
			}
		}
		return null;
	}

	// Return list of strikes prices
	public ArrayList<Double> getStrikePricesList() {
		ArrayList<Double> list = new ArrayList<>();
		strikes.forEach(strike -> list.add(strike.getStrike()));
		return list;
	}

	// Remove strike from strikes arr by strike price (double)
	public void removeStrike(double strike) {
		int indexToRemove = 0;
		
		for (int i = 0; i < strikes.size(); i++) {
			if (strikes.get(i).getStrike() == strike) {
				indexToRemove = i;
			}
		}
		strikes.remove(indexToRemove);
	}

	// Remove strike from strikes arr by strike class
	public void removeStrike(Strike strike) {
		strikes.remove(strike);
	}

	// Add strike to strikes arr
	public void addStrike(Strike strike) {

		boolean contains = getStrikePricesList().contains(strike.getStrike());

		// Not inside
		if (!contains) {
			strikes.add(strike);
		}
	}
	
	public double getOp_avg() {

		if (opChartList.getValues().size() > 0) {

			double avg = 0;
			for (Double price : opChartList.getValues()) {
				avg += price;
			}
			return L.floor(avg / opChartList.getValues().size(), 100);
		} else {
			return 0;
		}

	}
	
	public Option getOptionById(int id) {
		return optionsMap.get(id);
	}
	
	// Set option in strikes arr
	public void setOption(Option option) {
		// HashMap
		optionsMap.put(option.getId(), option);
		
		// Strikes list 
		boolean callPut = option.getSide().toLowerCase().contains("c") ? true : false;

		Strike strike = getStrike(option.getStrike());

		if (strike != null) {

			if (callPut) {
				if (strike.getCall() == null) {
					strike.setCall(option);
				}
			} else {
				if (strike.getPut() == null) {
					strike.setPut(option);
				}
			}
		} else {
			
			// Create new if doesn't exist
			strike = new Strike();
			strike.setStrike(option.getStrike());

			if (callPut) {
				strike.setCall(option);
			} else {
				strike.setPut(option);
			}

			// Add strike
			addStrike(strike);
		}
	}
	
	public List<Strike> getStrikes() {
		return strikes;
	}
	
	public void setStrikes(List<Strike> strikes) {
		this.strikes = strikes;
	}
	
	public String toStringVertical() {
		String string = "";
		for (Strike strike : strikes) {
			string += strike.toString() + "\n\n";
		}
		return string;
	}
	
	public JSONObject getOptionsWithDataAsJson() {
		JSONObject json = getAsJson();
		json.put(JsonStrings.data.toString(), getData());
		return json;
	}
	
	
	@Override
	public String toString() {
		return "Options [contractBid=" + contractBid + ", contractAsk=" + contractAsk + ", conBidAskCounter="
				+ conBidAskCounter + ", totalDelta=" + delta + ", contract=" + contract + "]";
	}

	
	public MyJson getData() {
		
		MyJson obj = new MyJson();
		MyJson strikeObj, callObj, putObj;
		
		for ( Strike strike : strikes ) {
			
			
			
			// Strike
			strikeObj = new MyJson();
			
			// Call 
			Option call = strike.getCall();
			callObj = new MyJson();
			callObj.put(JsonStrings.bid, call.getBid());
			callObj.put(JsonStrings.ask, call.getAsk());
			callObj.put(JsonStrings.last, call.getLast());
			callObj.put(JsonStrings.theoretic, call.getCalcPrice());
			callObj.put(JsonStrings.delta, call.getDelta());
			strikeObj.put(JsonStrings.call, callObj);
			
			// Put 
			Option put = strike.getPut();
			putObj = new MyJson();
			putObj.put(JsonStrings.bid, put.getBid());
			putObj.put(JsonStrings.ask, put.getAsk());
			putObj.put(JsonStrings.last, put.getLast());
			putObj.put(JsonStrings.theoretic, put.getCalcPrice());
			putObj.put(JsonStrings.delta, put.getDelta());
			strikeObj.put(JsonStrings.put, putObj);
			
			// Append to main obj
			obj.put(L.str(strike.getStrike()), strikeObj);
		}
		return obj;
	}
	
	public int getConBidAskCounter() {
		return conBidAskCounter;
	}
	
	public void setContractBid(double newBid) {
		if (contractBid != 0 && newBid > contractBid && apiObject.getStatus().contains(apiObject.getStreamMarket())) {
			increasFutureCounter();
		}
		this.contractBid = newBid;
	}
	
	public double getContractBid() {
		return contractBid;
	}
	
	public double getContractAsk() {
		return contractAsk;
	}
	
	public void setContractAsk(double newAsk) {
		if (contractAsk != 0 && newAsk < contractAsk && apiObject.getStatus().contains(apiObject.getStreamMarket())) {
			decreasFutureCounter();
		}
		this.contractAsk = newAsk;
	}
	
	public MyChartList getConBidAskCounterList() {
		return conBidAskCounterList;
	}
	
	public synchronized void increasFutureCounter() {
		this.conBidAskCounter++;
	}
	
	public synchronized void decreasFutureCounter() {
		this.conBidAskCounter--;
	}
	
	public String str(Object o) {
		return String.valueOf(o);
	}

	public double getDelta() {
		return delta;
	}

	public void appendDelta( double delta ) {
		this.delta += delta;
	}

	public void setDelta( double totalDelta ) {
		this.delta = totalDelta;
	}

	public double absolute( double d) {
		return Math.abs(d);
	}
	
	public MyChartList getDeltaChartList() {
		return deltaChartList;
	}
	
	public double getContract() {
		return contract;
	}
	
	public void setContract(double contract) {
		this.contract = contract;
	}
	
	public void setConBidAskCounter(int conBidAskCounter) {
		this.conBidAskCounter = conBidAskCounter;
	}
	
	public MyChartList getOpChartList() {
		return opChartList;
	}
	
	@Override
	public MyJson getAsJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.op, getOp());
		json.put(JsonStrings.opAvg, getOp_avg());
		json.put(JsonStrings.conBidAskCounter, getConBidAskCounter());
		json.put(JsonStrings.delta, getDelta());
		json.put(JsonStrings.con, getContract());
		return json;
	}
	
	@Override
	public void loadFromJson(MyJson json) {
		try {
			setConBidAskCounter(json.getInt(JsonStrings.conBidAskCounter));
			setDelta(json.getDouble(JsonStrings.delta));
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
		return new MyJson();
	}
	
}

