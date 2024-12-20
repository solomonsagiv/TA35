package api;

import charts.myChart.MyChartList;
import charts.myChart.TimeSeriesFactory;
import dataBase.DataBaseService;
import dataBase.Factories;
import exp.Exps;
import handlers.TimeSeriesHandler;
import locals.L;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import org.json.JSONObject;
import races.RacesService;
import service.BasketFinder_by_stocks;
import service.IndDeltaService;
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

    private double bid_ask_counter_avg_60 = 0;
    private double bid_ask_counter_avg = 0;

    private StocksHandler stocksHandler;
    private TimeSeriesHandler timeSeriesHandler;

    // Ticker
    private double futureOpen = 0;
    private double open = 0;
    private double high = 0;
    private double low = 0;
    private double last = 0;
    private double index = 0;
    private double index_avg_3600 = 0;
    private double index_avg_900 = 0;

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
    private final double RACE_MARGIN = 0;

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

    // Services
    private BasketFinder_by_stocks basketFinder_by_stocks;
    private DataBaseService dataBaseService;
    private IndDeltaService indDeltaService;
    private RacesService racesService;

    // Private constructor
    private ApiObject() {
        this.exps = new Exps(this);
        stocksHandler = new StocksHandler();
        this.name = "ta35";

        init_time_series();
    }

    private void init_time_series() {
        timeSeriesHandler = new TimeSeriesHandler();

        timeSeriesHandler.put(Factories.TimeSeries.INDEX_AVG_3600, TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_AVG_3600));
        timeSeriesHandler.put(Factories.TimeSeries.INDEX_AVG_900, TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_AVG_900));

        timeSeriesHandler.put(Factories.TimeSeries.DF_4_CDF_OLD, TimeSeriesFactory.get_serie(Factories.TimeSeries.DF_4_CDF_OLD));
        timeSeriesHandler.put(Factories.TimeSeries.DF_5_CDF_OLD, TimeSeriesFactory.get_serie(Factories.TimeSeries.DF_5_CDF_OLD));
        timeSeriesHandler.put(Factories.TimeSeries.DF_6_CDF_OLD, TimeSeriesFactory.get_serie(Factories.TimeSeries.DF_6_CDF_OLD));
        timeSeriesHandler.put(Factories.TimeSeries.DF_8_CDF_OLD, TimeSeriesFactory.get_serie(Factories.TimeSeries.DF_8_CDF_OLD));

        timeSeriesHandler.put(Factories.TimeSeries.ROLL_900, TimeSeriesFactory.get_serie(Factories.TimeSeries.ROLL_900));
        timeSeriesHandler.put(Factories.TimeSeries.ROLL_3600, TimeSeriesFactory.get_serie(Factories.TimeSeries.ROLL_3600));

        timeSeriesHandler.put(Factories.TimeSeries.DF_9_CDF, TimeSeriesFactory.get_serie(Factories.TimeSeries.DF_9_CDF));

        timeSeriesHandler.put(Factories.TimeSeries.OP_AVG_WEEK_5, TimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_WEEK_5));
        timeSeriesHandler.put(Factories.TimeSeries.OP_AVG_WEEK_60, TimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_WEEK_60));
        timeSeriesHandler.put(Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240, TimeSeriesFactory.get_serie(Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240));

        timeSeriesHandler.put(Factories.TimeSeries.FUTURE_WEEK, TimeSeriesFactory.get_serie(Factories.TimeSeries.FUTURE_WEEK));
        timeSeriesHandler.put(Factories.TimeSeries.FUTURE_MONTH, TimeSeriesFactory.get_serie(Factories.TimeSeries.FUTURE_MONTH));

        timeSeriesHandler.put(Factories.TimeSeries.INDEX_RACES_WI, TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_RACES_WI));
        timeSeriesHandler.put(Factories.TimeSeries.WEEK_RACES_WI, TimeSeriesFactory.get_serie(Factories.TimeSeries.WEEK_RACES_WI));
        timeSeriesHandler.put(Factories.TimeSeries.R1_MINUS_R2_IQ, TimeSeriesFactory.get_serie(Factories.TimeSeries.R1_MINUS_R2_IQ));

        timeSeriesHandler.put(Factories.TimeSeries.WEEK_RACES_WM, TimeSeriesFactory.get_serie(Factories.TimeSeries.WEEK_RACES_WM));
        timeSeriesHandler.put(Factories.TimeSeries.MONTH_RACES_WM, TimeSeriesFactory.get_serie(Factories.TimeSeries.MONTH_RACES_WM));

        timeSeriesHandler.put(Factories.TimeSeries.BID_RACES_BA, TimeSeriesFactory.get_serie(Factories.TimeSeries.BID_RACES_BA));
        timeSeriesHandler.put(Factories.TimeSeries.ASK_RACES_BA, TimeSeriesFactory.get_serie(Factories.TimeSeries.ASK_RACES_BA));

        timeSeriesHandler.put(Factories.TimeSeries.VICTOR_INDEX_RACES, TimeSeriesFactory.get_serie(Factories.TimeSeries.VICTOR_INDEX_RACES));
        timeSeriesHandler.put(Factories.TimeSeries.VICTOR_FUTURE_RACES, TimeSeriesFactory.get_serie(Factories.TimeSeries.VICTOR_FUTURE_RACES));
        timeSeriesHandler.put(Factories.TimeSeries.VICTOR_ROLL_RACES, TimeSeriesFactory.get_serie(Factories.TimeSeries.VICTOR_ROLL_RACES));

        timeSeriesHandler.put(Factories.TimeSeries.VICTOR_INDEX_RACES_RATIO, TimeSeriesFactory.get_serie(Factories.TimeSeries.VICTOR_INDEX_RACES_RATIO));
        timeSeriesHandler.put(Factories.TimeSeries.VICTOR_ROLL_RACES_RATIO, TimeSeriesFactory.get_serie(Factories.TimeSeries.VICTOR_ROLL_RACES_RATIO));

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


    public double get_index_mid() {
        return (index_bid + index_ask) / 2;
    }

    public double get_bid_last_margin() {
        return L.abs(index - index_bid);
    }

    public double get_ask_last_margin() {
        return L.abs(index - index_ask);
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

    public TimeSeriesHandler getTimeSeriesHandler() {
        return timeSeriesHandler;
    }

    public void setTimeSeriesHandler(TimeSeriesHandler timeSeriesHandler) {
        this.timeSeriesHandler = timeSeriesHandler;
    }

    public Exps getExps() {
        return exps;
    }

    public double getIndex_avg_3600() {
        return index_avg_3600;
    }

    public void setIndex_avg_3600(double index_avg_3600) {
        this.index_avg_3600 = index_avg_3600;
    }

    public double getIndex_avg_900() {
        return index_avg_900;
    }

    public void setIndex_avg_900(double index_avg_900) {
        this.index_avg_900 = index_avg_900;
    }

    public double getRACE_MARGIN() {
        return RACE_MARGIN;
    }

    public BasketFinder_by_stocks getBasketFinder_by_stocks() {
        return basketFinder_by_stocks;
    }

    public void setBasketFinder_by_stocks(BasketFinder_by_stocks basketFinder_by_stocks) {
        this.basketFinder_by_stocks = basketFinder_by_stocks;
    }

    public DataBaseService getDataBaseService() {
        return dataBaseService;
    }

    public void setDataBaseService(DataBaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }

    public IndDeltaService getIndDeltaService() {
        return indDeltaService;
    }

    public void setIndDeltaService(IndDeltaService indDeltaService) {
        this.indDeltaService = indDeltaService;
    }

    public RacesService getRacesService() {
        return racesService;
    }

    public void setRacesService(RacesService racesService) {
        this.racesService = racesService;
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
