package api;

import charts.myChart.MyChartList;
import exp.Exps;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import org.json.JSONObject;
import service.MyServiceHandler;
import stocksHandler.StocksHandler;
import java.util.ArrayList;

public class ApiObject implements IJsonData {

    private static ApiObject apiObject;

    private String status = null;
    private String streamMarket = "stream";

    private double interest = 0.006;
    private boolean dbLoaded = false;
    private double rando = 0;

    private Exps exps;

    private MyServiceHandler serviceHandler;

    private int bigConBidAskCounter = 0;
    private double bigConBid = 0;
    private double bigConAsk = 0;

    private int v5 = 0;
    private int v6 = 0;
    private int pre_v5 = 0;
    private int pre_v6 = 0;
    private int v103 = 0;
    private int v107 = 0;
    private int pre_v103 = 0;
    private int pre_v107 = 0;
    private int v4 = 0;
    private int v8 = 0;
    private int pre_v4 = 0;
    private int pre_v8 = 0;
    private int v5_speed_300 = 0;
    private int v6_speed_300 = 0;
    private int pre_df_n_speed_300 = 0;
    private int pre_df_speed_300 = 0;
    private int df_7 = 0;

    private double bid_ask_counter_avg_60 = 0;
    private double bid_ask_counter_avg = 0;

    private StocksHandler stocksHandler;

    // Ticker
    private double futureOpen = 0;
    private double open = 0;
    private double high = 0;
    private double low = 0;
    private double last = 0;
    private double index = 0;
    private double base = 0;
    private double index_bid = 0;
    private double index_ask = 0;

    private double equalLiveMove = 0;
    private double equalMove = 0;

    private int indBidAskCounter = 0;
    private ArrayList<Double> futureRatioList = new ArrayList<>();
    private ArrayList<Integer> indexBidAskCounterList = new ArrayList<>();

    private int optimiTimer = 0;
    private int pesimiTimer = 0;

    private int basketUp = 0;
    private int basketDown = 0;

    // Rcaes
    private int conUp = 0;
    private int conDown = 0;
    private int indUp = 0;
    private int indDown = 0;

    // Exp
    private int futureExpRaces = 0;
    private int indexExpRaces = 0;
    private int expOptimiTimer = 0;
    private int expPesimiTimer = 0;
    private double daysToExp = 0;

    // Lists
    private MyChartList indexChartList = new MyChartList();
    private MyChartList indBasketsList = new MyChartList();

    private String export_dir = "C://Users/user/Desktop/Work/Data history/TA35/";

    private boolean started;
    private String name;

    // Private constructor
    private ApiObject() {
        this.exps = new Exps(this);
        stocksHandler = new StocksHandler();
        this.name = "ta35";
    }

    // Get instance
    public static ApiObject getInstance() {
        if (apiObject == null) {
            apiObject = new ApiObject();
        }
        return apiObject;
    }

    public double floor(double d) {
        return Math.floor(d * 100) / 100;
    }

    @SuppressWarnings("unchecked")
    public JSONObject getData() {

        JSONObject json = new JSONObject();
        json.put("open", open);
        json.put("high", high);
        json.put("low", low);
        json.put("last", last);
        json.put("base", base);
        json.put("index_bid", index_bid);
        json.put("index_ask", index_ask);

        JSONObject races = new JSONObject();
        races.put("future_up", conUp);
        races.put("future_down", conDown);
        races.put("index_up", indUp);
        races.put("index_down", indDown);
        json.put("races", races);

        return json;
    }

    public void start() {
        setStarted(true);
    }


    private void setStarted(boolean bool) {
        this.started = bool;
    }

    public boolean isStarted() {
        return started;
    }

    public void close() {
        getServiceHandler().getHandler().close();
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getIndex_bid() {
        return index_bid;
    }

    public void setIndBid(double newBid) {

        if (index_bid != 0 && newBid > index_bid && getStatus().contains(getStreamMarket())) {
            indBidAskCounter++;
        }

        this.index_bid = newBid;
    }

    public double getIndex_ask() {
        return index_ask;
    }

    public void setIndAsk(double newAsk) {

        if (index_ask != 0 && newAsk < index_ask && getStatus().contains(getStreamMarket())) {
            indBidAskCounter--;
        }

        this.index_ask = newAsk;
    }

    public int getConUp() {
        return conUp;
    }

    public void setConUp(int conUp) {
        this.conUp = conUp;
    }

    public int getConDown() {
        return conDown;
    }

    public void setConDown(int future_down) {
        this.conDown = future_down;
    }

    public int getIndUp() {
        return indUp;
    }

    public void setIndUp(int index_up) {
        this.indUp = index_up;
    }

    public int getIndDown() {
        return indDown;
    }

    public void setIndDown(int index_down) {
        this.indDown = index_down;
    }

    public double getIndex() {
        return index;
    }

    public void setIndex(double index) {
        this.index = index;
    }

    public int getFutureExpRaces() {
        return futureExpRaces;
    }

    public void setFutureExpRaces(int futureExpRaces) {
        this.futureExpRaces = futureExpRaces;
    }

    public int getIndexExpRaces() {
        return indexExpRaces;
    }

    public void setIndexExpRaces(int indexExpRaces) {
        this.indexExpRaces = indexExpRaces;
    }

    public String getExport_dir() {
        return export_dir;
    }

    public void setExport_dir(String export_dir) {
        this.export_dir = export_dir;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBasketUp() {
        return basketUp;
    }

    public void setBasketUp(int basketUp) {
        this.basketUp = basketUp;
    }

    public int getBasketDown() {
        return basketDown;
    }

    public void incrementBasketUp() {
        this.basketUp += 1;
    }

    public void incrementBasketDown() {
        this.basketDown += 1;
    }

    public void setBasketDown(int basketDown) {
        this.basketDown = basketDown;
    }

    public int getOptimiTimer() {
        return optimiTimer;
    }

    public void setOptimiTimer(long optimiTimer) {
        this.optimiTimer = (int) (optimiTimer / 1000);
    }

    public int getPesimiTimer() {
        return pesimiTimer;
    }

    public void setPesimiTimer(long pesimiTimer) {
        this.pesimiTimer = (int) (pesimiTimer / 1000);
    }

    public int getExpOptimiTimer() {
        return expOptimiTimer;
    }

    public void setExpOptimiTimer(int expOptimiTimer) {
        this.expOptimiTimer = expOptimiTimer;
    }

    public int getExpPesimiTimer() {
        return expPesimiTimer;
    }

    public void setExpPesimiTimer(int expPesimiTimer) {
        this.expPesimiTimer = expPesimiTimer;
    }

    public void setOptimiTimer(int optimiTimer) {
        this.optimiTimer = optimiTimer;
    }

    public void setPesimiTimer(int pesimiTimer) {
        this.pesimiTimer = pesimiTimer;
    }

    public double getDaysToExp() {
        return daysToExp;
    }

    public void setDaysToExp(double daysToExp) {
        this.daysToExp = daysToExp;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public int getIndBidAskCounter() {
        return indBidAskCounter;
    }

    public void setIndBidAskCounter(int indBidAskCounter) {
        this.indBidAskCounter = indBidAskCounter;
    }

    public String getStreamMarket() {
        return streamMarket;
    }

    public void setStreamMarket(String streamMarket) {
        this.streamMarket = streamMarket;
    }

    public ArrayList<Integer> getIndexBidAskCounterList() {
        return indexBidAskCounterList;
    }

    public void setIndexBidAskCounterList(ArrayList<Integer> indexBidAskCounterList) {
        this.indexBidAskCounterList = indexBidAskCounterList;
    }

    public boolean isDbLoaded() {
        return dbLoaded;
    }

    public void setDbLoaded(boolean dbLoaded) {
        this.dbLoaded = dbLoaded;
    }

    public double getFutureOpen() {
        return futureOpen;
    }

    public void setFutureOpen(double futureOpen) {
        this.futureOpen = futureOpen;
    }

    public ArrayList<Double> getFutureRatioList() {
        return futureRatioList;
    }

    public void setFutureRatioList(ArrayList<Double> futureRatioList) {
        this.futureRatioList = futureRatioList;
    }

    public double getEqualLiveMove() {
        return equalLiveMove;
    }

    public void setEqualLiveMove(double equalLiveMove) {
        this.equalLiveMove = equalLiveMove;
    }

    public void appendEqualMove(double move) {
        setEqualMove(getEqualMove() + move);
    }

    public double getEqualMove() {
        return equalMove;
    }

    public void setEqualMove(double equalMove) {
        this.equalMove = equalMove;
    }

    public MyChartList getIndexChartList() {
        return indexChartList;
    }

    public MyServiceHandler getServiceHandler() {
        if (serviceHandler == null) {
            serviceHandler = new MyServiceHandler();
        }
        return serviceHandler;
    }

    public int getPre_v5() {
        return pre_v5;
    }

    public void setPre_v5(int pre_v5) {
        this.pre_v5 = pre_v5;
    }

    public int getPre_v6() {
        return pre_v6;
    }

    public void setPre_v6(int pre_v6) {
        this.pre_v6 = pre_v6;
    }

    public synchronized void increasBigBidAskCounter() {
        this.bigConBidAskCounter++;
    }

    public synchronized void decreasBigBidAskCounter() {
        this.bigConBidAskCounter--;
    }

    public double getBigConBid() {
        return bigConBid;
    }

    public double getBigConAsk() {
        return bigConAsk;
    }

    public void setBigConBidAskCounter(int bigConBidAskCounter) {
        this.bigConBidAskCounter = bigConBidAskCounter;
    }

    public int getBigConBidAskCounter() {
        return bigConBidAskCounter;
    }

    public double getRando() {
        return rando;
    }

    public void setRando(double rando) {
        this.rando = rando;
    }

    public StocksHandler getStocksHandler() {
        return stocksHandler;
    }

    public MyChartList getIndBasketsList() {
        return indBasketsList;
    }

    public double getBid_ask_counter_avg() {
        return bid_ask_counter_avg;
    }

    public double getBid_ask_counter_avg_60() {
        return bid_ask_counter_avg_60;
    }

    public int getV5() {
        return v5;
    }

    public void setV5(int new_v5) {
        if (new_v5 != 0 && this.v5 != new_v5) {
            setPre_v5(this.v5);
            this.v5 = new_v5;
        }
    }

    public int getV103() {
        return v103;
    }

    public void setV103(int new_v103) {
        if (new_v103 != 0 && this.v103 != new_v103) {
            setPre_v103(this.v103);
            this.v103 = new_v103;
        }
    }

    public void setPre_v103(int pre_v103) {
        this.pre_v103 = pre_v103;
    }


    public int getV107() {
        return v107;
    }

    public void setV107(int new_v107) {
        if (new_v107 != 0 && this.v107 != new_v107) {
            setPre_v103(this.v107);
            this.v107 = new_v107;
        }
    }

    public void setPre_v107(int pre_v107) {
        this.pre_v107 = pre_v107;
    }

    public int getV6_speed_300() {
        return v6_speed_300;
    }

    public int getV5_speed_300() {
        return v5_speed_300;
    }

    public void setV5_speed_300(int new_val) {
        if (new_val != 0 && this.v5_speed_300 != new_val) {
            setPre_df_n_speed_300(this.v5_speed_300);
            this.v5_speed_300 = new_val;
        }
    }

    public void setV6_speed_300(int new_val) {
            if (new_val != 0 && this.v6_speed_300 != new_val) {
                setPre_df_speed_300(this.v6_speed_300);
                this.v6_speed_300 = new_val;
            }
    }

    public int getDf_7() {
        return df_7;
    }

    public void setDf_7(int df_7) {
        this.df_7 = df_7;
    }

    public int getPre_df_n_speed_300() {
        return pre_df_n_speed_300;
    }

    public int getPre_df_speed_300() {
        return pre_df_speed_300;
    }

    public void setPre_df_n_speed_300(int pre_df_n_speed_300) {
        this.pre_df_n_speed_300 = pre_df_n_speed_300;
    }

    public void setPre_df_speed_300(int pre_df_speed_300) {
        this.pre_df_speed_300 = pre_df_speed_300;
    }

    public int getV6() {
        return v6;
    }

    public void setV6(int new_v6) {
        if (new_v6 != 0 && this.v6 != new_v6) {
            setPre_v6(this.v6);
            this.v6 = new_v6;
        }
    }

    public int getPre_v4() {
        return pre_v4;
    }

    public void setPre_v4(int pre_v4) {
        this.pre_v4 = pre_v4;
    }

    public int getPre_v8() {
        return pre_v8;
    }

    public void setPre_v8(int pre_v8) {
        this.pre_v8 = pre_v8;
    }

    public int getV4() {
        return v4;
    }

    public void setV4(int new_v4) {
        if (new_v4 != 0 && this.v4 != new_v4) {
            setPre_v4(this.v4);
            this.v4 = new_v4;
        }
    }

    public int getV8() {
        return v8;
    }

    public void setV8(int new_v8) {
        if (new_v8 != 0 && this.v8 != new_v8) {
            setPre_v8(this.v8);
            this.v8 = new_v8;
        }
    }

    public Exps getExps() {
        return exps;
    }

    @Override
    public MyJson getAsJson() {
        MyJson json = new MyJson();
        json.put(JsonStrings.ind, apiObject.getIndex());
        json.put(JsonStrings.indBid, apiObject.getIndex_bid());
        json.put(JsonStrings.indAsk, apiObject.getIndex_ask());
        json.put(JsonStrings.conUp, apiObject.getConUp());
        json.put(JsonStrings.conDown, apiObject.getConDown());
        json.put(JsonStrings.indUp, apiObject.getIndUp());
        json.put(JsonStrings.indDown, apiObject.getIndDown());
        json.put(JsonStrings.basketUp, apiObject.getBasketUp());
        json.put(JsonStrings.basketDown, apiObject.getBasketDown());
        json.put(JsonStrings.indBidAskCounter, apiObject.getIndBidAskCounter());
        json.put(JsonStrings.base, apiObject.getBase());
        json.put(JsonStrings.v5, apiObject.getV5());
        json.put(JsonStrings.v6, apiObject.getV6());
        json.put(JsonStrings.stocks, apiObject.getStocksHandler().getAsJson());
        json.put(JsonStrings.expWeek, exps.getWeek().getAsJson());
        json.put(JsonStrings.expMonth, exps.getMonth().getAsJson());
        return json;
    }

    @Override
    public void loadFromJson(MyJson json) {
        apiObject.setConUp(json.getInt(JsonStrings.conUp));
        apiObject.setConDown(json.getInt(JsonStrings.conDown));
        apiObject.setIndUp(json.getInt(JsonStrings.indUp));
        apiObject.setIndDown(json.getInt(JsonStrings.indDown));
        apiObject.setBasketUp(json.getInt(JsonStrings.basketUp));
        apiObject.setBasketDown(json.getInt(JsonStrings.basketDown));
        apiObject.getStocksHandler().loadFromJson(new MyJson(json.getJSONObject(JsonStrings.stocks)));
        apiObject.setIndBidAskCounter(json.getInt(JsonStrings.indBidAskCounter));
        apiObject.exps.getWeek().loadFromJson(new MyJson(json.getJSONObject(JsonStrings.expWeek)));
        apiObject.exps.getMonth().loadFromJson(new MyJson(json.getJSONObject(JsonStrings.expMonth)));
    }

    @Override
    public MyJson getResetJson() {
        MyJson json = new MyJson();
        json.put(JsonStrings.expWeek, exps.getWeek().getResetJson());
        json.put(JsonStrings.expMonth, exps.getMonth().getResetJson());
        return json;
    }

    @Override
    public MyJson getFullResetJson() {
        return new MyJson();
    }

    public Object getName() {
        return name;
    }
}
