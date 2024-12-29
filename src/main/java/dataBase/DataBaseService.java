package dataBase;

import api.Manifest;
import arik.Arik;
import charts.myChart.MyTimeSeries;
import counter.BackGroundRunner;
import dataBase.mySql.MySql;
import exp.ExpMonth;
import exp.ExpWeek;
import races.Race_Logic;
import service.MyBaseService;
import java.time.Instant;
import java.util.ArrayList;

public class DataBaseService extends MyBaseService {

    double baskets_0 = 0;
    double index_races_0 = 0;
    double week_races_0 = 0;
    double month_races_wm_0 = 0;
    double week_races_wm_0 = 0;
    double bid_races_ba_0 = 0;
    double ask_races_ba_0 = 0;

    ArrayList<MyTimeStampObject> baskets_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> index_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> week_races_timeStamp  = new ArrayList<>();
    ArrayList<MyTimeStampObject> month_races_wm_timeStamp  = new ArrayList<>();
    ArrayList<MyTimeStampObject> week_races_wm_timeStamp  = new ArrayList<>();
    ArrayList<MyTimeStampObject> bid_races_ba_timeStamp  = new ArrayList<>();
    ArrayList<MyTimeStampObject> ask_races_ba_timeStamp  = new ArrayList<>();

    ExpWeek week;
    ExpMonth month;

    ArrayList<MyTimeSeries> timeSeriesList = new ArrayList<>();

    public DataBaseService() {
        super();
        week = apiObject.getExps().getWeek();
        month = apiObject.getExps().getMonth();

        // OP AVG
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.INDEX_AVG_3600));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.INDEX_AVG_900));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_5));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_60));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.ROLL_900));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.ROLL_3600));

        // DF OLD CDF
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_5_CDF_OLD));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_6_CDF_OLD));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_4_CDF_OLD));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF_OLD));

        // VICTOR RACES
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_INDEX_RACES));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_FUTURE_RACES));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_ROLL_RACES));

        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_INDEX_RACES_RATIO));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_ROLL_RACES_RATIO));

        // CDF
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_9_CDF));
    }

    @Override
    public void go() {
        if (Manifest.DB_UPLOAD && BackGroundRunner.streamMarketBool) {
            //	 Day
            append_changed_data_to_lists();
        }
    }

    private void append_changed_data_to_lists() {

        double baskets = apiObject.getBasketUp() - apiObject.getBasketDown();

        // Baskets
        double change = baskets - baskets_0;
        if (change != 0) {
            baskets_0 = baskets;
            baskets_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Index races
        double index_races = apiObject.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX).get_r_one_points();

        if (index_races != index_races_0) {
            double last_count = index_races - index_races_0;
            if (last_count == 1 || last_count == -1) {
                index_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            index_races_0 = index_races;
        }

        // Week races
        double week_races = apiObject.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX).get_r_two_points();

        if (week_races != week_races_0) {
            double last_count = week_races - week_races_0;
            if (last_count == 1 || last_count == -1) {
                week_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            week_races_0 = week_races;
        }

        // Month races WM
        double month_races_wm = apiObject.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_one_points();

        if (month_races_wm != month_races_wm_0) {
            double last_count = month_races_wm - month_races_wm_0;
            if (last_count == 1 || last_count == -1) {
                month_races_wm_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            month_races_wm_0 = month_races_wm;
        }

        // Month races WM
        double week_races_wm = apiObject.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_two_points();

        if (week_races_wm != week_races_wm_0) {
            double last_count = week_races_wm - week_races_wm_0;
            if (last_count == 1 || last_count == -1) {
                week_races_wm_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            week_races_wm_0 = week_races_wm;
        }

        // Bid races BA
        double bid_races_ba = apiObject.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.BID_ASK).get_r_one_points();

        if (bid_races_ba != bid_races_ba_0) {
            double last_count = bid_races_ba - bid_races_ba_0;
            if (last_count == 1 || last_count == -1) {
                bid_races_ba_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            bid_races_ba_0 = bid_races_ba;
        }

        // Ask races BA
        double ask_races_ba = apiObject.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.BID_ASK).get_r_two_points();

        if (ask_races_ba != ask_races_ba_0) {
            double last_count = ask_races_ba - ask_races_ba_0;
            if (last_count == 1 || last_count == -1) {
                ask_races_ba_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            ask_races_ba_0 = ask_races_ba;
        }

        // Op avg week
        if (sleepCount % 1000 == 0) {
            Instant instant = Instant.now();
        }

        System.out.println("Stream merket " + BackGroundRunner.streamMarketBool);

        // Grabb data and insert data
        if (sleepCount % 10000 == 0) {
            insert_data();
            grab_data();
        }

        // Options status
//        if (sleepCount % 60000 == 0) {
//            update_options_status();
//        }
    }

    private void insert_data() {
        new Thread(() -> {
            insert_data_retro_mega(baskets_timestamp, Factories.IDs.BASKETS);
            insert_data_retro_mega(index_races_timeStamp, Factories.IDs.INDEX_RACES_WI);
            insert_data_retro_mega(week_races_timeStamp, Factories.IDs.WEEK_RACES_WI);

            insert_data_retro_mega(week_races_wm_timeStamp, Factories.IDs.WEEK_RACES_WM);
            insert_data_retro_mega(month_races_wm_timeStamp, Factories.IDs.MONTH_RACES_WM);

            insert_data_retro_mega(bid_races_ba_timeStamp, Factories.IDs.BID_RACES_BA);
            insert_data_retro_mega(ask_races_ba_timeStamp, Factories.IDs.ASK_RACES_BA);
        }).start();
    }

    private void grab_data() {
        new Thread(() -> {
            try {
                apiObject.setDbLoaded(true);

                // Update data
                update_series();

                System.out.println("Grabbed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void update_series() {
        for (MyTimeSeries serie: timeSeriesList) {
            serie.updateData();
        }
    }

    @Override
    public String getName() {
        return "DataBaseService";
    }

    @Override
    public int getSleep() {
        return 1000;
    }

    void insert_data_retro_mega(ArrayList<MyTimeStampObject> list, String table_location) {
        try {
            if (list.size() > 0) {
                // Create the query
                StringBuilder queryBuiler = new StringBuilder("INSERT INTO %s (time, value) VALUES ");
                int last_item_id = list.get(list.size() - 1).hashCode();
                for (MyTimeStampObject row : list) {
                    queryBuiler.append(String.format("(cast('%s' as timestamp with time zone), %s)", row.getInstant(), row.getValue()));
                    if (row.hashCode() != last_item_id) {
                        queryBuiler.append(",");
                    }
                }
                queryBuiler.append(";");

                String q = String.format(queryBuiler.toString(), table_location);

                // Insert
                MySql.insert(q, true);

                // Clear the list
                list.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage("Insert data ta35 failed \n to table " + table_location + "\n \n " + e.getCause());
        }
    }

    void insert_data_retro_mega(ArrayList<MyTimeStampObject> list, int timeseries_id) {
        if (list.size() > 0) {

            // Create the query
            StringBuilder queryBuiler = new StringBuilder("INSERT INTO %s (time, value, timeseries_id) VALUES ");
            int last_item_id = list.get(list.size() - 1).hashCode();
            for (MyTimeStampObject row : list) {
                queryBuiler.append(String.format("(cast('%s' as timestamp with time zone), %s, %s)", row.getInstant(), row.getValue(), timeseries_id));
                if (row.hashCode() != last_item_id) {
                    queryBuiler.append(",");
                }
            }
            queryBuiler.append(";");

            String q = String.format(queryBuiler.toString(), "ts.timeseries_data");

            System.out.println(q);

            // Insert
            MySql.insert(q);

            // Clear the list
            list.clear();
        }
    }
}
