package api;

import charts.myChart.TimeSeriesFactory;
import counter.BackGroundRunner;
import dataBase.DataBaseService;
import dataBase.Factories;
import exp.Exps;
import handlers.TimeSeriesHandler;
import locals.L;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import org.json.JSONObject;
import races.Race_Logic;
import races.RacesService;
import service.BasketFinder_by_stocks;
import service.IndDeltaService;
import service.MyServiceHandler;
import stocksHandler.StocksHandler;
import java.util.HashMap;

public class TA35 extends INDEX_OBJECT implements IJsonData {

    private static TA35 TA35;

    private String status;

    // Private constructor
    private TA35() {
        super();
        init_time_series();
    }

    @Override
    protected void init_data_base_service() {
        setDataBaseService(new DataBaseService(this));
    }

    @Override
    protected void init_race_service() {
        HashMap<Race_Logic.RACE_RUNNER_ENUM, Race_Logic> map = new HashMap<>();
        map.put(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, new Race_Logic(this, Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, L.RACE_MARGIN));
        map.put(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH, new Race_Logic(this, Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH, L.RACE_MARGIN));
        map.put(Race_Logic.RACE_RUNNER_ENUM.BID_ASK, new Race_Logic(this, Race_Logic.RACE_RUNNER_ENUM.BID_ASK, L.RACE_MARGIN));
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
        setIndexIndDeltaService(new IndDeltaService(this, BackGroundRunner.excelPath));
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
        return null;
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

        setTimeSeriesHandler(timeSeriesHandler);

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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
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

    @Override
    public MyJson getFullResetJson() {
        return new MyJson();
    }


}
