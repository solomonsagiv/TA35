package dataBase;

import api.BASE_CLIENT_OBJECT;
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
    ArrayList<MyTimeStampObject> last_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> op_week_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> op_month_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> roll_interest_timeStamp = new ArrayList<>();

    double bid_0 = 0,
            ask_0 = 0,
            last_0 = 0,
            mid_0 = 0,
            mid_races_0 = 0,
            last_races_0 = 0,
            op_week_interest_0 = 0,
            op_month_interest_0 = 0,
            roll_interest_0 = 0;

    public DataBaseHandler_TA35(BASE_CLIENT_OBJECT client) {
        super(client);
        initTablesNames();
    }

    int sleep_count = 100;

    @Override
    public void go(int sleep) {
        if (this.exps == null) {
            this.exps = client.getExps();
        }

        // Update lists retro
        if (sleep_count % 15000 == 0) {
            updateListsRetro();
        }

        // On changed data
        on_change_data();

        // Update count
        sleep_count += sleep;
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

            // Index races
            double index_races = client.get_main_race().get_r_one_points();

            if (index_races != mid_races_0) {
                double last_count = index_races - mid_races_0;
                if (last_count == 1 || last_count == -1) {
                    mid_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                mid_races_0 = index_races;
            }

            // Q1 races
            double q1_races = client.get_main_race().get_r_two_points();

            if (q1_races != last_races_0) {
                double last_count = q1_races - last_races_0;
                if (last_count == 1 || last_count == -1) {
                    last_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                last_races_0 = q1_races;
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
        load_exp_data();

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
    public void initTablesNames() {
    }

    private void updateListsRetro() {

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

        // Last
        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.LAST_PRICE);
        insert_data_retro_mega(last_timestamp, id);

        // Mid
        id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MID);
        insert_data_retro_mega(last_timestamp, id);

        // Bid
        id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.BID);
        insert_data_retro_mega(last_timestamp, id);

        // Ask
        id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ASK);
        insert_data_retro_mega(last_timestamp, id);

        // Index race
        id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX_RACES_WI);
        insert_data_retro_mega(last_timestamp, id);

        // Month race
        id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_RACES_WM);
        insert_data_retro_mega(last_timestamp, id);

    }
}