package api;

import api.deltaTest.CalcsService;
import charts.myChart.TimeSeriesFactory;
import counter.BackGroundRunner;
import dataBase.DataBaseHandler_TA35;
import dataBase.DataBaseService;
import dataBase.Factories;
import exp.Exps;
import handlers.TimeSeriesHandler;
import locals.L;
import locals.Themes;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import options.OptionsMonth;
import options.OptionsWeek;
import org.json.JSONObject;
import races.Race_Logic;
import races.RacesService;
import races.Stocks_Race_Service;
import service.BasketFinder_by_stocks;
import service.MyServiceHandler;
import service.StocksReaderService;
import stocksHandler.StocksHandler;
import java.awt.*;
import java.util.HashMap;

public class TA35 extends INDEX_OBJECT implements IJsonData {

    private static TA35 TA35;
    private int status = 999;
    private OptionsWeek optionsWeek;
    private OptionsMonth optionsMonth;

    private Stocks_Race_Service stocks_race_service;

    // Private constructor
    private TA35() {
        super();
        this.optionsWeek = new OptionsWeek(this);
    }

    @Override
    protected void init_data_base_service() {
        setDataBaseService(new DataBaseService(this, new DataBaseHandler_TA35(this)));
    }

    @Override
    protected void init_stocks_reader_service() {
        setStocksReaderService(new StocksReaderService(this, BackGroundRunner.excelPath));
    }

    @Override
    protected void init_calcs_service() {
        setCalcsService(new CalcsService(this));
    }

    @Override
    protected void init_race_service() {
        HashMap<Race_Logic.RACE_RUNNER_ENUM, Race_Logic> map = new HashMap<>();
        map.put(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, new Race_Logic(this, Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, L.RACE_MARGIN));
        map.put(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH, new Race_Logic(this, Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH, L.RACE_MARGIN));
        setRacesService(new RacesService(this, map));
    }

    @Override
    protected void init_baskets_service() {
        setBasketFinder_by_stocks(new BasketFinder_by_stocks(this, 28, 3));
    }

    @Override
    protected void init_stocks_handler() {
        setStocksHandler(new StocksHandler());
    }


    @Override
    protected void init_index_delta_service() {
    }

    @Override
    protected void init_exps() {
        setExps(new Exps(this));
    }

    @Override
    protected void init_name() {
        setName("ta35");
    }

    @Override
    public Race_Logic get_main_race() {
        return getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX);
    }

    @Override
    protected void init_timeseries_handler() {
        timeSeriesHandler = new TimeSeriesHandler();
        
        timeSeriesHandler.put(Factories.TimeSeries.INDEX_AVG_3600, TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_AVG_3600, this));
        timeSeriesHandler.put(Factories.TimeSeries.INDEX_AVG_900, TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_AVG_900, this));

        timeSeriesHandler.put(Factories.TimeSeries.DF_4_CDF_OLD, TimeSeriesFactory.get_serie(Factories.TimeSeries.DF_4_CDF_OLD, this));
        timeSeriesHandler.put(Factories.TimeSeries.DF_5_CDF_OLD, TimeSeriesFactory.get_serie(Factories.TimeSeries.DF_5_CDF_OLD, this));
        timeSeriesHandler.put(Factories.TimeSeries.DF_6_CDF_OLD, TimeSeriesFactory.get_serie(Factories.TimeSeries.DF_6_CDF_OLD, this));
        timeSeriesHandler.put(Factories.TimeSeries.DF_8_CDF_OLD, TimeSeriesFactory.get_serie(Factories.TimeSeries.DF_8_CDF_OLD, this));

        timeSeriesHandler.put(Factories.TimeSeries.ROLL_900, TimeSeriesFactory.get_serie(Factories.TimeSeries.ROLL_900, this));
        timeSeriesHandler.put(Factories.TimeSeries.ROLL_3600, TimeSeriesFactory.get_serie(Factories.TimeSeries.ROLL_3600, this));

        timeSeriesHandler.put(Factories.TimeSeries.OP_AVG_WEEK_15, TimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_WEEK_15, this));
        timeSeriesHandler.put(Factories.TimeSeries.OP_AVG_WEEK_60, TimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_WEEK_60, this));
        timeSeriesHandler.put(Factories.TimeSeries.OP_AVG_240_CONTINUE, TimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_240_CONTINUE, this));

        timeSeriesHandler.put(Factories.TimeSeries.FUTURE_WEEK, TimeSeriesFactory.get_serie(Factories.TimeSeries.FUTURE_WEEK, this));
        timeSeriesHandler.put(Factories.TimeSeries.FUTURE_MONTH, TimeSeriesFactory.get_serie(Factories.TimeSeries.FUTURE_MONTH, this));

        timeSeriesHandler.put(Factories.TimeSeries.INDEX_RACES_WI, TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_RACES_WI, this));
        timeSeriesHandler.put(Factories.TimeSeries.WEEK_RACES_WI, TimeSeriesFactory.get_serie(Factories.TimeSeries.WEEK_RACES_WI, this));

        timeSeriesHandler.put(Factories.TimeSeries.WEEK_RACES_WM, TimeSeriesFactory.get_serie(Factories.TimeSeries.WEEK_RACES_WM, this));
        timeSeriesHandler.put(Factories.TimeSeries.MONTH_RACES_WM, TimeSeriesFactory.get_serie(Factories.TimeSeries.MONTH_RACES_WM, this));

        timeSeriesHandler.put(Factories.TimeSeries.MONTH_RACES_WM, TimeSeriesFactory.get_serie(Factories.TimeSeries.MONTH_RACES_WM, this));
        timeSeriesHandler.put(Factories.TimeSeries.MONTH_RACES_WM, TimeSeriesFactory.get_serie(Factories.TimeSeries.MONTH_RACES_WM, this));

        timeSeriesHandler.put(Factories.TimeSeries.OP_MONTH_INTEREST_AVG_PROD, TimeSeriesFactory.get_serie(Factories.TimeSeries.OP_MONTH_INTEREST_AVG_PROD, this));
        timeSeriesHandler.put(Factories.TimeSeries.ROLL_INTEREST_AVG_PROD, TimeSeriesFactory.get_serie(Factories.TimeSeries.ROLL_INTEREST_AVG_PROD, this));

        timeSeriesHandler.put(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD, TimeSeriesFactory.get_serie(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD, this));
        timeSeriesHandler.put(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD, TimeSeriesFactory.get_serie(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD, this));

        timeSeriesHandler.put(Factories.TimeSeries.STOCKS_TOT_BA_WEIGHT_PROD, TimeSeriesFactory.get_serie(Factories.TimeSeries.STOCKS_TOT_BA_WEIGHT_PROD, this));
        timeSeriesHandler.put(Factories.TimeSeries.STOCKS_TOT_DELTA_WEIGHT_PROD, TimeSeriesFactory.get_serie(Factories.TimeSeries.STOCKS_TOT_DELTA_WEIGHT_PROD, this));

        timeSeriesHandler.put(Factories.TimeSeries.COUNTER_2_TOT_WEIGHT_PROD, TimeSeriesFactory.get_serie(Factories.TimeSeries.COUNTER_2_TOT_WEIGHT_PROD, this));
        timeSeriesHandler.put(Factories.TimeSeries.TOTAL_DELTA, TimeSeriesFactory.get_serie(Factories.TimeSeries.TOTAL_DELTA, this));



        timeSeriesHandler.put(Factories.TimeSeries.TOTAL_DELTA, TimeSeriesFactory.get_serie(Factories.TimeSeries.TOTAL_DELTA, this));

        setTimeSeriesHandler(timeSeriesHandler);

        timeSeriesHandler.put_id(Factories.TimeSeries.INDEX, 5);
        timeSeriesHandler.put_id(Factories.TimeSeries.BID, 22);
        timeSeriesHandler.put_id(Factories.TimeSeries.ASK, 21);
        timeSeriesHandler.put_id(Factories.TimeSeries.MID_DEV, 11997);
        timeSeriesHandler.put_id(Factories.TimeSeries.LAST_PRICE, 5);
        timeSeriesHandler.put_id(Factories.TimeSeries.BASKETS, 9513);
        timeSeriesHandler.put_id(Factories.TimeSeries.FUTURE_WEEK, 23);
        timeSeriesHandler.put_id(Factories.TimeSeries.FUTURE_MONTH, 6);
        timeSeriesHandler.put_id(Factories.TimeSeries.OP_AVG_240_CONTINUE, 9486);
        timeSeriesHandler.put_id(Factories.TimeSeries.OP_AVG_WEEK_60, 9484);
        timeSeriesHandler.put_id(Factories.TimeSeries.OP_AVG_WEEK_15, 9485);
        timeSeriesHandler.put_id(Factories.TimeSeries.DF_4_CDF_OLD, 9770);
        timeSeriesHandler.put_id(Factories.TimeSeries.DF_8_CDF_OLD, 9773);
        timeSeriesHandler.put_id(Factories.TimeSeries.DF_5_CDF_OLD, 9491);
        timeSeriesHandler.put_id(Factories.TimeSeries.DF_6_CDF_OLD, 9492);
        timeSeriesHandler.put_id(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD, 10117);
        timeSeriesHandler.put_id(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD, 10118);
        timeSeriesHandler.put_id(Factories.TimeSeries.STOCKS_TOT_BA_WEIGHT_PROD,10126);
        timeSeriesHandler.put_id(Factories.TimeSeries.STOCKS_TOT_DELTA_WEIGHT_PROD, 10137);
        timeSeriesHandler.put_id(Factories.TimeSeries.COUNTER_2_TOT_WEIGHT_PROD, 10144);
        timeSeriesHandler.put_id(Factories.TimeSeries.TOTAL_DELTA, 10162);

        timeSeriesHandler.put_id(Factories.TimeSeries.INDEX_RACES_WI, 9789);
        timeSeriesHandler.put_id(Factories.TimeSeries.WEEK_RACES_WI, 9788);
        timeSeriesHandler.put_id(Factories.TimeSeries.MONTH_RACES_WM, 9791);
        timeSeriesHandler.put_id(Factories.TimeSeries.WEEK_RACES_WM, 9788);
        timeSeriesHandler.put_id(Factories.TimeSeries.ROLL_3600, 9542);
        timeSeriesHandler.put_id(Factories.TimeSeries.ROLL_900, 9543);

        timeSeriesHandler.put_id(Factories.TimeSeries.OP_MONTH_INTEREST_PROD, 10113);
        timeSeriesHandler.put_id(Factories.TimeSeries.OP_WEEK_INTEREST_PROD, 10116);
        timeSeriesHandler.put_id(Factories.TimeSeries.OP_MONTH_INTEREST_AVG_PROD, 10115);
        timeSeriesHandler.put_id(Factories.TimeSeries.ROLL_INTEREST_PROD, 10112);
        timeSeriesHandler.put_id(Factories.TimeSeries.ROLL_INTEREST_AVG_PROD, 10114);

        timeSeriesHandler.put_id(Factories.TimeSeries.OP_MONTH_INTEREST_DEV, 13002);
        timeSeriesHandler.put_id(Factories.TimeSeries.OP_MONTH_INTEREST_AVG_DEV, 13004);
        timeSeriesHandler.put_id(Factories.TimeSeries.OP_WEEK_INTEREST_DEV, 13005);
        timeSeriesHandler.put_id(Factories.TimeSeries.ROLL_INTEREST_DEV, 13001);
        timeSeriesHandler.put_id(Factories.TimeSeries.ROLL_INTEREST_AVG_DEV, 13003);
        timeSeriesHandler.put_id(Factories.TimeSeries.TRADING_STATUS, 10125);
        timeSeriesHandler.put_id(Factories.TimeSeries.TRADING_STATUS_DEV, 13046);
    }

    @Override
    protected void init_dde_cells() {
    }

    @Override
    public Color get_index_race_serie_color() {
        return Themes.ORANGE;
    }

    // Get instance
    public static TA35 getInstance() {
        if (TA35 == null) {
            TA35 = new TA35();
        }
        return TA35;
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
        json.put("last", last_price);
        json.put("base", base);
        json.put("index_bid", bid);
        json.put("index_ask", ask);
        return json;
    }

    public double get_index_mid() {
        return (bid + ask) / 2;
    }

    public void start() {
        setStarted(true);
    }

    public void close() {
        getServiceHandler().getHandler().close();
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public MyServiceHandler getServiceHandler() {
        if (serviceHandler == null) {
            serviceHandler = new MyServiceHandler();
        }
        return serviceHandler;
    }


    public void setTimeSeriesHandler(TimeSeriesHandler timeSeriesHandler) {
        this.timeSeriesHandler = timeSeriesHandler;
    }

    public Exps getExps() {
        return exps;
    }

    @Override
    public MyJson getAsJson() {
        MyJson json = new MyJson();
        json.put(JsonStrings.ind, TA35.getIndex());
        json.put(JsonStrings.base, TA35.getBase());
        json.put(JsonStrings.stocks, TA35.getStocksHandler().getAsJson());
        json.put(JsonStrings.expWeek, exps.getWeek().getAsJson());
        json.put(JsonStrings.expMonth, exps.getMonth().getAsJson());
        return json;
    }

    @Override
    public void loadFromJson(MyJson json) {
        TA35.getStocksHandler().loadFromJson(new MyJson(json.getJSONObject(JsonStrings.stocks)));
        TA35.exps.getWeek().loadFromJson(new MyJson(json.getJSONObject(JsonStrings.expWeek)));
        TA35.exps.getMonth().loadFromJson(new MyJson(json.getJSONObject(JsonStrings.expMonth)));
    }

    @Override
    public MyJson getResetJson() {
        MyJson json = new MyJson();
        json.put(JsonStrings.expWeek, exps.getWeek().getResetJson());
        json.put(JsonStrings.expMonth, exps.getMonth().getResetJson());
        return json;
    }

    public OptionsWeek getOptionsWeek() {
        return optionsWeek;
    }

    public void setOptionsWeek(OptionsWeek optionsWeek) {
        this.optionsWeek = optionsWeek;
    }

    public OptionsMonth getOptionsMonth() {
        return optionsMonth;
    }

    public void setOptionsMonth(OptionsMonth optionsMonth) {
        this.optionsMonth = optionsMonth;
    }

    @Override
    public MyJson getFullResetJson() {
        return new MyJson();
    }

    public Stocks_Race_Service getStocks_race_service() {
        return stocks_race_service;
    }

    public void setStocks_race_service(Stocks_Race_Service stocks_race_service) {
        this.stocks_race_service = stocks_race_service;
    }

    public double get_ask_last_margin() {
        return L.abs(ask - index);
    }
    public double get_bid_last_margin() {
        return L.abs(index - bid);
    }


}
