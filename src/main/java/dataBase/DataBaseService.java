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

    ArrayList<MyTimeStampObject> baskets_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> index_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> week_races_timeStamp  = new ArrayList<>();

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
            index_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            index_races_0 = index_races;
        }

        // Week races
        double week_races = apiObject.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX).get_r_two_points();

        if (week_races != week_races_0) {
            double last_count = week_races - week_races_0;
            week_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            week_races_0 = week_races;
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
