package dataBase;

import api.BASE_CLIENT_OBJECT;
import races.Race_Logic;

import java.time.Instant;
import java.util.ArrayList;

public class DataBaseHandler_Poli extends IDataBaseHandler {

    ArrayList<MyTimeStampObject> last_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> bid_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> ask_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> mid_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> mid_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> last_races_timeStamp = new ArrayList<>();
    
    double bid_0 = 0;
    double ask_0 = 0;
    double last_0 = 0;
    double mid_0 = 0;
    double mid_races_0 = 0;
    double last_races_0 = 0;

    public DataBaseHandler_Poli(BASE_CLIENT_OBJECT client) {
        super(client);
        initTablesNames();
    }

    int sleep_count = 100;

    public void insertData(int sleep) {

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

        // Load races
        load_races(Race_Logic.RACE_RUNNER_ENUM.LAST_MID, serie_ids.get(Factories.TimeSeries.INDEX), true);

        // Set load
        client.setDb_loaded(true);
    }

    @Override
    public void initTablesNames() {
    }

    @Override
    public void go(int sleep) {

    }

    private void updateListsRetro() {
        // Dev and Prod
//        insert_dev_prod(last_timestamp, serie_ids.get(Factories.TimeSeries.INDE), serie_ids.get(TimeSeriesHandler.INDEX_PROD));
//        insert_dev_prod(mid_races_timeStamp, serie_ids.get(TimeSeriesHandler.INDEX_WEIGHTED_RACES_DEV), serie_ids.get(TimeSeriesHandler.INDEX_RACES_PROD));
//        insert_dev_prod(last_races_timeStamp, serie_ids.get(TimeSeriesHandler.Q1_WEIGHTED_RACES_DEV), serie_ids.get(TimeSeriesHandler.Q1_RACES_PROD));
//
//        insert_dev_prod(bid_timestamp, serie_ids.get(TimeSeriesHandler.INDEX_BID_WEIGHTED_DEV), serie_ids.get(TimeSeriesHandler.INDEX_BID_WEIGHTED));
//        insert_dev_prod(ask_timestamp, serie_ids.get(TimeSeriesHandler.INDEX_ASK_WEIGHTED_DEV), serie_ids.get(TimeSeriesHandler.INDEX_ASK_WEIGHTED));
    }
}