package dataBase;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import charts.myChart.MyTimeSeries;
import locals.L;
import races.Race_Logic;

import java.time.Instant;
import java.util.ArrayList;

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

    ArrayList<MyTimeSeries> timeSeries;

    double bid_0 = 0,
            ask_0 = 0,
            last_0 = 0,
            mid_0 = 0,
            mid_races_0 = 0,
            op_week_interest_0 = 0,
            op_month_interest_0 = 0,
            roll_interest_0 = 0,
            baskets_0 = 0,
            month_races_0;

    public DataBaseHandler_TA35(BASE_CLIENT_OBJECT client) {
        super(client);
        init_timeseries_to_updater();
    }

    int sleep_count = 100;

    @Override
    public void insert_data(int sleep) {
        // Update count
        sleep_count += sleep;

        if (this.exps == null) {
            this.exps = client.getExps();
        }

        // Update lists retro
        if (sleep_count % 15000 == 0) {
            updateListsRetro();
            update_data();
            sleep_count = 0;
        }

        // On changed da
        // ta
        on_change_data();
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
        if (client.isDb_loaded()) {

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

            // Baskets
            double baskets = ((TA35) client).getBasketFinder_by_stocks().get_baskets();

            if (baskets != baskets_0) {
                double last_count = baskets - baskets_0;
                if (last_count == 1 || last_count == -1) {
                    baskets_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                baskets_0 = baskets;
            }

            // Index races
            double index_races = client.get_main_race().get_r_one_points();

            if (index_races != mid_races_0) {
                double last_count = index_races - mid_races_0;
                if (last_count == 1 || last_count == -1) {
                    mid_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                mid_races_0 = index_races;
            }

            // Month
            double month_races = client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_one_points();

            if (month_races != month_races_0) {
                double last_count = month_races - month_races_0;
                if (last_count == 1 || last_count == -1) {
                    month_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                month_races_0 = month_races;
            }

            // OP week interest
            double week_interest = client.getOp_week_interest();
            if (week_interest != op_week_interest_0) {
                if (L.abs(week_interest - op_week_interest_0) < 0.1) {
                    op_week_interest_0 = week_interest;
                    op_week_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), op_week_interest_0));
                }
            }

            // OP month interest
            double month_interest = client.getOp_month_interest();
            if (month_interest != op_month_interest_0) {
                if (L.abs(month_interest - op_month_interest_0) < 0.1) {
                    op_month_interest_0 = month_interest;
                    op_month_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), op_month_interest_0));
                }
            }

            // Roll interest
            double roll_interest = client.getRoll_interest();
            if (roll_interest != roll_interest_0) {
                if (L.abs(roll_interest - roll_interest_0) < 0.1) {
                    roll_interest_0 = roll_interest;
                    roll_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), roll_interest_0));
                }
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

        // Set load
        client.setDb_loaded(true);
    }

    private void load_all_races() {
        int index_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX_RACES_WI);
        int week_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_RACES_WI);
        int month_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_RACES_WM);

        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, index_races_id, true);
        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, week_races_id, false);
        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH, month_races_id, true);
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

    }
}