package options;

import api.ApiObject;
import charts.myChart.MyChartList;
import locals.L;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Options implements IJsonData {

    public static final int MONTH = 0;
    public static final int WEEK = 1;

    private double contract = 0;
    private double contractBid = 0;
    private double contractAsk = 0;
    private int conBidAskCounter = 0;
    private ArrayList<Double> op_list;

    private double delta_from_fix = 0;
    private double total_delta = 0;

    List<Strike> strikes;
    HashMap<Integer, Option> optionsMap;
    ArrayList<Option> options_list;

    private MyChartList deltaChartList = new MyChartList();
    private MyChartList conBidAskCounterList = new MyChartList();
    private MyChartList opChartList = new MyChartList();

    protected ApiObject apiObject;

    public Options(ApiObject apiObject) {
        this.apiObject = apiObject;
        strikes = new ArrayList<Strike>();
        optionsMap = new HashMap<Integer, Option>();
        this.op_list = new ArrayList<>();
        this.options_list = new ArrayList<>();
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

    public Option getOptionById(int id) {
        return optionsMap.get(id);
    }

    // Set option in strikes arr
    public void setOption(Option option) {
        // HashMap
        optionsMap.put(option.getId(), option);
        options_list.add(option);

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
                + conBidAskCounter + ", totalDelta=" + total_delta + ", contract=" + contract + "]";
    }


    public MyJson getData() {

        MyJson obj = new MyJson();
        MyJson strikeObj, callObj, putObj;

        for (Strike strike : strikes) {


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

    public void appendDelta(double delta) {
        this.total_delta += delta;
    }

    public void setDelta_from_fix(double delta_from_fix) {
        this.delta_from_fix = delta_from_fix;
        total_delta = delta_from_fix;
    }

    public double getTotal_delta() {
        return total_delta;
    }

    public double absolute(double d) {
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

    public ArrayList<Option> getOptions_list() {
        return options_list;
    }

    @Override
    public MyJson getAsJson() {
        MyJson json = new MyJson();
        json.put(JsonStrings.op, getOp());
        json.put(JsonStrings.conBidAskCounter, getConBidAskCounter());
        json.put(JsonStrings.delta, getTotal_delta());
        json.put(JsonStrings.con, getContract());
        return json;
    }

    @Override
    public void loadFromJson(MyJson json) {
        try {
            setConBidAskCounter(json.getInt(JsonStrings.conBidAskCounter));
            setDelta_from_fix(json.getDouble(JsonStrings.delta));
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

    public void load_op_avg(ArrayList<Double> op_list) {
        getOpChartList().getValues().addAll(op_list);
    }

    public void load_options_data(JSONObject json) {
        for (Option option : options_list) {
            JSONObject option_json = json.getJSONObject(option.getName());
            double delta = option_json.getDouble("delta");
            int open_pos = option_json.getInt("open_pos");
            option.appendDelta(delta);
            option.setOpen_pos(open_pos);
        }
    }
}

