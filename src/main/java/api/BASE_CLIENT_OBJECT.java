package api;

import dataBase.DataBaseService;
import dde.DDECells;
import exp.Exps;
import handlers.TimeSeriesHandler;
import locals.L;
import props.Props;
import races.Race_Logic;
import races.RacesService;
import service.MyServiceHandler;

import java.awt.*;

public abstract class BASE_CLIENT_OBJECT {

    private String name;
    protected double last_price, mid, bid, ask, open, high, low, base;
    protected double race_margin = 0.001;
    private String export_dir = "C://Users/user/Desktop/Work/Data history/TA35/";
    private boolean started, db_loaded;
    protected Exps exps;
    protected Props props;
    private int trading_status = 0;

    // Services
    public MyServiceHandler serviceHandler;
    public DataBaseService dataBaseService;
    protected TimeSeriesHandler timeSeriesHandler;
    public RacesService racesService;
    protected DDECells ddeCells;

    private double roll_interest = 0;
    private double op_month_interest = 0;
    private double roll_interest_avg = 0;
    private double op_month_interest_avg = 0;
    private double op_week_interest = 0;

    public BASE_CLIENT_OBJECT() {
        init_name();
        init_race_service();
        init_timeseries_handler();
        init_exps();
        init_data_base_service();
        init_dde_cells();
    }

    protected abstract void init_race_service();
    protected abstract void init_name();
    protected abstract void init_data_base_service();
    protected abstract void init_exps();
    public abstract Race_Logic get_main_race();
    protected abstract void init_timeseries_handler();
    protected abstract void init_dde_cells();
    public abstract Color get_index_race_serie_color();

    public double getOpenPresent() {
        return L.floor(((open - base) / base) * 100, 100);
    }
    public double getLastPresent() {
        return L.floor(((last_price - base) / base) * 100, 100);
    }
    public double getHighPresent() {
        return L.floor(((high - base) / base) * 100, 100);
    }
    public double getLowPresent() {
        return L.floor(((low - base) / base) * 100, 100);
    }

    public double get_ask_last_margin() {
        return L.abs(ask - last_price);
    }
    public double get_bid_last_margin() {
        return L.abs(last_price - bid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLast_price() {
        return last_price;
    }

    public void setLast_price(double last_price) {
        this.last_price = last_price;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
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

    public RacesService getRacesService() {
        return racesService;
    }

    public void setRacesService(RacesService racesService) {
        this.racesService = racesService;
    }

    public MyServiceHandler getServiceHandler() {
        return serviceHandler;
    }

    public void setServiceHandler(MyServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    public TimeSeriesHandler getTimeSeriesHandler() {
        return timeSeriesHandler;
    }

    public void setTimeSeriesHandler(TimeSeriesHandler timeSeriesHandler) {
        this.timeSeriesHandler = timeSeriesHandler;
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

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getRace_margin() {
        return race_margin;
    }

    public void setRace_margin(double race_margin) {
        this.race_margin = race_margin;
    }

    public String getExport_dir() {
        return export_dir;
    }

    public void setExport_dir(String export_dir) {
        this.export_dir = export_dir;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isDb_loaded() {
        return db_loaded;
    }

    public void setDb_loaded(boolean db_loaded) {
        this.db_loaded = db_loaded;
    }

    public DataBaseService getDataBaseService() {
        return dataBaseService;
    }

    public void setDataBaseService(DataBaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }

    public Exps getExps() {
        return exps;
    }

    public void setExps(Exps exps) {
        this.exps = exps;
    }

    public DDECells getDdeCells() {
        return ddeCells;
    }

    public void setDdeCells(DDECells ddeCells) {
        this.ddeCells = ddeCells;
    }

    public Props getProps() {
        return props;
    }

    public void setProps(Props props) {
        this.props = props;
    }

    public int getTrading_status() {
        return trading_status;
    }

    public void setTrading_status(int trading_status) {
        this.trading_status = trading_status;
    }

    public double getRoll_interest() {
        return roll_interest;
    }

    public void setRoll_interest(double roll_interest) {
        this.roll_interest = roll_interest;
    }

    public double getOp_month_interest() {
        return op_month_interest;
    }

    public void setOp_month_interest(double op_month_interest) {
        this.op_month_interest = op_month_interest;
    }

    public double getRoll_interest_avg() {
        return roll_interest_avg;
    }

    public void setRoll_interest_avg(double roll_interest_avg) {
        this.roll_interest_avg = roll_interest_avg;
    }

    public double getOp_month_interest_avg() {
        return op_month_interest_avg;
    }

    public void setOp_month_interest_avg(double op_month_interest_avg) {
        this.op_month_interest_avg = op_month_interest_avg;
    }

    public double getOp_week_interest() {
        return op_week_interest;
    }

    public void setOp_week_interest(double op_week_interest) {
        this.op_week_interest = op_week_interest;
    }
}
