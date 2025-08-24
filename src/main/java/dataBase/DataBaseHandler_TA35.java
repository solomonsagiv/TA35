package dataBase;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import api.deltaTest.Calculator;
import arik.Arik;
import charts.myChart.MyTimeSeries;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import locals.L;
import options.Options;
import races.Race_Logic;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataBaseHandler_TA35 extends IDataBaseHandler {

    ArrayList<MyTimeStampObject> last_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> bid_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> ask_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> mid_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> mid_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> month_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> op_week_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> op_month_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> roll_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> baskets_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> month_counter_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> week_counter_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> trading_status_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> stocks_counter_change_timestamp = new ArrayList<>();

    ArrayList<MyTimeSeries> timeSeries;
    Race_Logic wi_race, wm_race;
    Options week_options, month_options;

    double bid_0 = 0,
            ask_0 = 0,
            last_0 = 0,
            mid_0 = 0,
            mid_races_0 = 0,
            op_week_interest_0 = 0,
            op_month_interest_0 = 0,
            roll_interest_0 = 0,
            baskets_0 = 0,
            month_races_0 = 0,
            month_counter_0 = 0,
            week_counter_0 = 0,
            trading_status_0 = 0,
            stocks_counter_change_0 = 0;

    public DataBaseHandler_TA35(BASE_CLIENT_OBJECT client) {
        super(client);
        init_timeseries_to_updater();
        this.wi_race = client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX);
        this.wm_race = client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH);
        this.week_options = client.getExps().getWeek().getOptions();
        this.month_options = client.getExps().getMonth().getOptions();
    }

    int sleep_count = 100;

    int stocks_sleep_count = 0;

    int load_stocks_data_count = 0;

    @Override
    public void insert_data(int sleep) {
        // Update count
        sleep_count += sleep;
        stocks_sleep_count += sleep;

        if (this.exps == null) {
            this.exps = client.getExps();
        }

        // Update lists retro
        if (sleep_count % 15000 == 0) {
            updateListsRetro();
            update_data();

            sleep_count = 0;

            // Try load stocks data
            load_stocks_data();
        }

        // Insert stocks
        if (stocks_sleep_count % 60000 == 0) {
            stocks_sleep_count = 0;
            insert_stocks();
            System.out.println("Insert");
        }

        // On changed da
        // ta
        on_change_data();
    }

    private void insert_stocks() {
        try {
            Queries.insertStocksSnapshot(TA35.getInstance().getStocksHandler().getStocks(), MySql.JIBE_DEV_CONNECTION);
        } catch (Exception e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage("TA35 Index Insert stocks Failed ");
            Arik.getInstance().sendErrorMessage(e);
        }
    }

    private void update_data() {
        new Thread(() -> {
            for (MyTimeSeries ts : timeSeries) {
                try {
                    System.out.println(ts.getName() + " " + ts.getValue());
                    System.out.println(ts.getName());
                    ts.updateData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void on_change_data() {

        // Is live db
        if (client.isStarted()) {

            // Status
            if (client.getTrading_status() != trading_status_0) {
                trading_status_0 = client.getTrading_status();
                trading_status_timestamp.add(new MyTimeStampObject(Instant.now(), trading_status_0));
            }

            // Index
            if (client.getLast_price() != last_0) {
                last_0 = client.getLast_price();
                last_timestamp.add(new MyTimeStampObject(Instant.now(), last_0));
            }

            // Index weighted
            if (client.getMid() != mid_0) {
                mid_0 = client.getMid();
                mid_timestamp.add(new MyTimeStampObject(Instant.now(), mid_0));
            }

            // Index bid synthetic
            if (client.getBid() != bid_0) {
                bid_0 = client.getBid();
                bid_timestamp.add(new MyTimeStampObject(Instant.now(), bid_0));
            }

            // Index ask synthetic
            if (client.getAsk() != ask_0) {
                ask_0 = client.getAsk();
                ask_timestamp.add(new MyTimeStampObject(Instant.now(), ask_0));
            }

            // Buy sell counter
            double stocks_counter_change = client.getStocks_counter_change();

            if (stocks_counter_change != stocks_counter_change_0) {
                double last_count = stocks_counter_change - stocks_counter_change_0;
                stocks_counter_change_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                stocks_counter_change_0 = stocks_counter_change;
            }

            // Baskets
            double baskets = ((TA35) client).getBasketFinder_by_stocks().get_baskets();

            if (baskets != baskets_0) {
                double last_count = baskets - baskets_0;
                if (L.abs(last_count) < 5) {
                    baskets_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                baskets_0 = baskets;
            }

            // Index races
            double index_races = wi_race.get_r_one_points();

            if (index_races != mid_races_0) {
                double last_count = index_races - mid_races_0;
                if (L.abs(last_count) < 5) {
                    mid_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                mid_races_0 = index_races;
            }

            // Month
            double month_races = wm_race.get_r_one_points();

            if (month_races != month_races_0) {
                double last_count = month_races - month_races_0;
                if (L.abs(last_count) < 5) {
                    month_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                month_races_0 = month_races;
            }

            // OP week interest
            double week_interest = client.getOp_week_interest();
            if (week_interest != op_week_interest_0) {
                if (L.abs(week_interest) < 10) {
                    op_week_interest_0 = week_interest;
                    op_week_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), op_week_interest_0));
                }
            }

            // OP month interest
            double month_interest = client.getOp_month_interest();
            if (month_interest != op_month_interest_0) {
                if (L.abs(month_interest) < 10) {
                    op_month_interest_0 = month_interest;
                    op_month_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), op_month_interest_0));
                }
            }

            // Roll interest
            double roll_interest = client.getRoll_interest();
            if (roll_interest != roll_interest_0) {
                if (L.abs(roll_interest) < 10) {
                    roll_interest_0 = roll_interest;
                    roll_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), roll_interest_0));
                }
            }

            // Month bid ask counter
            double week_counter = week_options.getBid_ask_counter();

            if (week_counter != week_counter_0) {
                double last_count = week_counter - week_counter_0;
                if (L.abs(last_count) < 5) {
                    week_counter_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                week_counter_0 = week_counter;
            }

            // Month bid ask counter
            double month_counter = month_options.getBid_ask_counter();

            if (month_counter != month_counter_0) {
                double last_count = month_counter - month_counter_0;
                if (L.abs(last_count) < 5) {
                    month_counter_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                month_counter_0 = month_counter;
            }
        }
    }

    @Override
    public void loadData() {
        try {
            // Load props
            load_properties();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Exp
//        load_exp_data();

        // Baskets
        load_baskets();

        // Load races
        load_all_races();

        // Load counters
        load_bid_ask_counter();

        // Load positive tracker
        load_positive_tracker();


        // Set load
        client.setDb_loaded(true);
    }


    private void load_stocks_data() {

        if (load_stocks_data_count > 3 ) return;

        if (TA35.getInstance().getStocksHandler().getStocks().size() == 0) return;

        try {
            load_stocks_data_count++;
            Queries.loadLastSnapshotStocksData(TA35.getInstance().getStocksHandler().getStocks(), MySql.JIBE_DEV_CONNECTION);
            load_stocks_data_count = 10;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Arik.getInstance().sendMessage("TA35 load stocks data");
            Arik.getInstance().sendErrorMessage(throwables);
        }

    }



    private void load_stocks_data_and_counter() {
        try {
            Queries.loadLastSnapshotStocksData(TA35.getInstance().getStocksHandler().getStocks(), MySql.JIBE_DEV_CONNECTION);

            int counter_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.STOCKS_WEIGHTED_CHNGE_PROD);
            int counter = (int) Queries.handle_rs(Objects.requireNonNull(Queries.get_last_record_mega(counter_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION)));
            client.setStocks_weighted_counter(counter);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void load_positive_tracker() {

        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.STOCKS_WEIGHTED_CHNGE_PROD);
        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW_NO_MODULU, MySql.JIBE_PROD_CONNECTION);

        for (Map<String, Object> row : rs) {
            try {

                Object value = row.get("value");
                if (value == null) {
                    continue;
                }

                BigDecimal bigDecimalValue = (BigDecimal) value;

                Calculator.PositiveTracker.update(bigDecimalValue.doubleValue());
            } catch (ClassCastException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void load_all_races() {
        int index_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX_RACES_WI);
        int week_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_RACES_WI);
        int month_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_RACES_WM);

        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, index_races_id, true);
        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, week_races_id, false);
        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH, month_races_id, true);
    }

    private void load_bid_ask_counter() {
        int week_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD);
        int week_counter = (int) Queries.handle_rs(Objects.requireNonNull(Queries.get_last_record_mega(week_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION)));

        int month_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD);
        int month_counter = (int) Queries.handle_rs(Objects.requireNonNull(Queries.get_last_record_mega(month_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION)));

        client.getExps().getWeek().getOptions().setBid_ask_counter(week_counter);
        client.getExps().getMonth().getOptions().setBid_ask_counter(month_counter);

        System.out.println("---------------------- Counterss ----------------------");
        System.out.println(week_counter + " " + month_counter);
    }

    @Override
    public void init_timeseries_to_updater() {

        timeSeries = new ArrayList<>();
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_4_CDF_OLD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF_OLD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_5_CDF_OLD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_6_CDF_OLD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_15));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_60));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_240_CONTINUE));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.ROLL_INTEREST_AVG_PROD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_MONTH_INTEREST_AVG_PROD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.ROLL_900));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.ROLL_3600));

    }

    private void updateListsRetro() {

        System.out.println("Insert !!!!! -------------------------- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + sleep_count);

        // Interest
        int dev_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_WEEK_INTEREST_DEV);
        int prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_WEEK_INTEREST_PROD);
        insert_dev_prod(op_week_interest_timeStamp, dev_id, prod_id);

        dev_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_MONTH_INTEREST_DEV);
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_MONTH_INTEREST_PROD);
        insert_dev_prod(op_month_interest_timeStamp, dev_id, prod_id);

        dev_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_INTEREST_DEV);
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_INTEREST_PROD);
        insert_dev_prod(roll_interest_timeStamp, dev_id, prod_id);

        dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.BASKETS);
        insert_dev_prod(baskets_timestamp, dev_id, prod_id);

        // Last
        dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.LAST_PRICE);
        insert_dev_prod(last_timestamp, dev_id, prod_id);

        // Mid
        dev_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MID_DEV);
        prod_id = 0;
        insert_dev_prod(mid_timestamp, dev_id, prod_id);

        // Bid
        dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.BID);
        insert_dev_prod(bid_timestamp, dev_id, prod_id);

        // Ask
        dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ASK);
        insert_dev_prod(ask_timestamp, dev_id, prod_id);

        // Index race
        dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX_RACES_WI);
        insert_dev_prod(mid_races_timeStamp, dev_id, prod_id);

        // Month race
        dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_RACES_WM);
        insert_dev_prod(month_races_timeStamp, dev_id, prod_id);

        // Month counter
        dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD);
        insert_dev_prod(month_counter_timestamp, dev_id, prod_id);

        // Week counter
        dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD);
        insert_dev_prod(week_counter_timestamp, dev_id, prod_id);

        // Trading status
        dev_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.TRADING_STATUS_DEV);
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.TRADING_STATUS);
        insert_dev_prod(trading_status_timestamp, dev_id, prod_id);

        // Buy sell counter
//        dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.STOCKS_WEIGHTED_CHNGE_PROD);
        insert_dev_prod(stocks_counter_change_timestamp, dev_id, prod_id);
    }
}