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
    
    // Tracking op_avg_60 zero crossing and TOTAL_DELTA accumulation
    private boolean op_avg_60_crossed_zero = false;
    private double previous_op_avg_60 = 0.0;
    private int total_delta_since_cross = 0;
    private int total_delta_at_cross = 0; // TOTAL_DELTA value at the moment of zero crossing
    private java.time.LocalDateTime op_avg_60_reset_timestamp = null; // Timestamp of last zero crossing
    
    // Tracking zero crossing for index_races_iw (only previous value needed for detection)
    private double previous_index_races_iw = 0.0;
    
    // Tracking zero crossing for week_races_wi (only previous value needed for detection)
    private double previous_week_races_wi = 0.0;
    
    // Tracking zero crossing for month_races_wm (only previous value needed for detection)
    private double previous_month_races_wm = 0.0;
    
    // Tracking zero crossing for week_bid_ask_counter (only previous value needed for detection)
    private int previous_week_bid_ask_counter = 0;
    
    // Tracking zero crossing for month_bid_ask_counter (only previous value needed for detection)
    private int previous_month_bid_ask_counter = 0;
    
    // Values at the moment op_avg_60 crossed zero (for displaying changes)
    private double index_races_iw_at_op_avg_60_cross = 0.0;
    private double week_races_wi_at_op_avg_60_cross = 0.0;
    private double month_races_wm_at_op_avg_60_cross = 0.0;
    private int week_bid_ask_counter_at_op_avg_60_cross = 0;
    private int month_bid_ask_counter_at_op_avg_60_cross = 0;
    
    // Tracking op_avg_15 zero crossing
    private boolean op_avg_15_crossed_zero = false;
    private double previous_op_avg_15 = 0.0;
    
    // Values at the moment op_avg_15 crossed zero (for displaying changes)
    private double index_races_iw_at_op_avg_15_cross = 0.0;
    private double week_races_wi_at_op_avg_15_cross = 0.0;
    private double month_races_wm_at_op_avg_15_cross = 0.0;
    private int week_bid_ask_counter_at_op_avg_15_cross = 0;
    private int month_bid_ask_counter_at_op_avg_15_cross = 0;
    private int weight_counter1_at_op_avg_15_cross = 0;
    private int weight_counter2_at_op_avg_15_cross = 0;
    private int weight_delta_at_op_avg_15_cross = 0;

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

        timeSeriesHandler.put_id(Factories.TimeSeries.COUNTER_2_TABLE_AVG, 10179);

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

    public double get_index_races_iw() {
        double value = get_main_race().get_r_one_points();
        previous_index_races_iw = value;
        return value;
    }

    public double get_week_races_wi() {
        double value = get_main_race().get_r_two_points();
        previous_week_races_wi = value;
        return value;
    }

    public double get_month_races_wm() {
        double value = racesService.get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_one_points();
        previous_month_races_wm = value;
        return value;
    }

    public int get_week_bid_ask_counter() {
        int value = getExps().getWeek().getOptions().getBidAskCounter();
        previous_week_bid_ask_counter = value;
        return value;
    }

    public int get_month_bid_ask_counter() {
        int value = getExps().getMonth().getOptions().getBidAskCounter();
        previous_month_bid_ask_counter = value;
        return value;
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

    /**
     * Checks if op_avg_60 crossed zero and updates tracking
     * Should be called whenever op_avg_60 is updated
     * When op_avg_60 crosses zero, it resets ALL other crosses as well
     */
    public void checkOpAvg60Cross(double current_op_avg_60) {
        // Check if crossed zero (from negative to positive or positive to negative)
        if (previous_op_avg_60 != 0.0 && 
            ((previous_op_avg_60 < 0 && current_op_avg_60 >= 0) || 
             (previous_op_avg_60 > 0 && current_op_avg_60 <= 0))) {
            // Zero crossing detected - start tracking from current TOTAL_DELTA
            int current_total_delta = (int) getTotal_delta();
            
            // Save current values of all tracked metrics at the moment of op_avg_60 cross
            index_races_iw_at_op_avg_60_cross = get_main_race().get_r_one_points();
            week_races_wi_at_op_avg_60_cross = get_main_race().get_r_two_points();
            month_races_wm_at_op_avg_60_cross = racesService.get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_one_points();
            week_bid_ask_counter_at_op_avg_60_cross = getExps().getWeek().getOptions().getBidAskCounter();
            month_bid_ask_counter_at_op_avg_60_cross = getExps().getMonth().getOptions().getBidAskCounter();
            
            // Reset op_avg_60 tracking
            op_avg_60_crossed_zero = true;
            total_delta_at_cross = current_total_delta;
            total_delta_since_cross = 0;
            op_avg_60_reset_timestamp = java.time.LocalDateTime.now(); // Update reset timestamp
        }
        previous_op_avg_60 = current_op_avg_60;
    }

    /**
     * Updates total_delta_since_cross if we're tracking after a zero cross
     * Calculates the difference from TOTAL_DELTA at the moment of crossing
     */
    public void updateTotalDeltaSinceCross(double total_delta) {
        if (op_avg_60_crossed_zero) {
            total_delta_since_cross = (int) (total_delta - total_delta_at_cross);
        }
    }

    public boolean isOp_avg_60_crossed_zero() {
        return op_avg_60_crossed_zero;
    }

    public void setOp_avg_60_crossed_zero(boolean op_avg_60_crossed_zero) {
        this.op_avg_60_crossed_zero = op_avg_60_crossed_zero;
    }

    public int getTotal_delta_since_cross() {
        return total_delta_since_cross;
    }

    public java.time.LocalDateTime getOp_avg_60_reset_timestamp() {
        return op_avg_60_reset_timestamp;
    }

    public void setTotal_delta_since_cross(int total_delta_since_cross) {
        this.total_delta_since_cross = total_delta_since_cross;
    }

    // Getters for value changes since op_avg_60 crossed zero
    public double getIndex_races_iw_change_since_op_avg_60_cross() {
        if (!op_avg_60_crossed_zero) {
            return 0.0;
        }
        return get_index_races_iw() - index_races_iw_at_op_avg_60_cross;
    }

    public double getWeek_races_wi_change_since_op_avg_60_cross() {
        if (!op_avg_60_crossed_zero) {
            return 0.0;
        }
        return get_week_races_wi() - week_races_wi_at_op_avg_60_cross;
    }

    public double getMonth_races_wm_change_since_op_avg_60_cross() {
        if (!op_avg_60_crossed_zero) {
            return 0.0;
        }
        return get_month_races_wm() - month_races_wm_at_op_avg_60_cross;
    }

    public int getWeek_bid_ask_counter_change_since_op_avg_60_cross() {
        if (!op_avg_60_crossed_zero) {
            return 0;
        }
        return get_week_bid_ask_counter() - week_bid_ask_counter_at_op_avg_60_cross;
    }

    public int getMonth_bid_ask_counter_change_since_op_avg_60_cross() {
        if (!op_avg_60_crossed_zero) {
            return 0;
        }
        return get_month_bid_ask_counter() - month_bid_ask_counter_at_op_avg_60_cross;
    }

    /**
     * Checks if op_avg_15 crossed zero and updates tracking
     * Should be called whenever op_avg_15 is updated
     */
    public void checkOpAvg15Cross(double current_op_avg_15) {
        // Check if crossed zero (from negative to positive or positive to negative)
        if (previous_op_avg_15 != 0.0 && 
            ((previous_op_avg_15 < 0 && current_op_avg_15 >= 0) || 
             (previous_op_avg_15 > 0 && current_op_avg_15 <= 0))) {
            // Zero crossing detected
            
            // Save current values of all tracked metrics at the moment of op_avg_15 cross
            index_races_iw_at_op_avg_15_cross = get_main_race().get_r_one_points();
            week_races_wi_at_op_avg_15_cross = get_main_race().get_r_two_points();
            month_races_wm_at_op_avg_15_cross = racesService.get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_one_points();
            week_bid_ask_counter_at_op_avg_15_cross = getExps().getWeek().getOptions().getBidAskCounter();
            month_bid_ask_counter_at_op_avg_15_cross = getExps().getMonth().getOptions().getBidAskCounter();
            weight_counter1_at_op_avg_15_cross = (int) getCounter1_weight();
            weight_counter2_at_op_avg_15_cross = (int) getCounter2_weight();
            weight_delta_at_op_avg_15_cross = (int) getDelta_weight();
            
            // Reset op_avg_15 tracking
            op_avg_15_crossed_zero = true;
        }
        previous_op_avg_15 = current_op_avg_15;
    }

    public boolean isOp_avg_15_crossed_zero() {
        return op_avg_15_crossed_zero;
    }

    public void setOp_avg_15_crossed_zero(boolean op_avg_15_crossed_zero) {
        this.op_avg_15_crossed_zero = op_avg_15_crossed_zero;
    }

    // Getters for value changes since op_avg_15 crossed zero
    public double getIndex_races_iw_change_since_op_avg_15_cross() {
        if (!op_avg_15_crossed_zero) {
            return 0.0;
        }
        return get_index_races_iw() - index_races_iw_at_op_avg_15_cross;
    }

    public double getWeek_races_wi_change_since_op_avg_15_cross() {
        if (!op_avg_15_crossed_zero) {
            return 0.0;
        }
        return get_week_races_wi() - week_races_wi_at_op_avg_15_cross;
    }

    public double getMonth_races_wm_change_since_op_avg_15_cross() {
        if (!op_avg_15_crossed_zero) {
            return 0.0;
        }
        return get_month_races_wm() - month_races_wm_at_op_avg_15_cross;
    }

    public int getWeek_bid_ask_counter_change_since_op_avg_15_cross() {
        if (!op_avg_15_crossed_zero) {
            return 0;
        }
        return get_week_bid_ask_counter() - week_bid_ask_counter_at_op_avg_15_cross;
    }

    public int getMonth_bid_ask_counter_change_since_op_avg_15_cross() {
        if (!op_avg_15_crossed_zero) {
            return 0;
        }
        return get_month_bid_ask_counter() - month_bid_ask_counter_at_op_avg_15_cross;
    }

    public int getWeight_counter1_change_since_op_avg_15_cross() {
        if (!op_avg_15_crossed_zero) {
            return 0;
        }
        return (int) getCounter1_weight() - weight_counter1_at_op_avg_15_cross;
    }

    public int getWeight_counter2_change_since_op_avg_15_cross() {
        if (!op_avg_15_crossed_zero) {
            return 0;
        }
        return (int) getCounter2_weight() - weight_counter2_at_op_avg_15_cross;
    }

    public int getWeight_delta_change_since_op_avg_15_cross() {
        if (!op_avg_15_crossed_zero) {
            return 0;
        }
        return (int) getDelta_weight() - weight_delta_at_op_avg_15_cross;
    }


}
