package dataBase;

import api.Manifest;
import arik.Arik;
import charts.myChart.MyTimeSeries;
import counter.BackGroundRunner;
import dataBase.mySql.MySql;
import exp.ExpMonth;
import exp.ExpWeek;
import service.MyBaseService;
import java.time.Instant;
import java.util.ArrayList;

public class DataBaseService extends MyBaseService {

    double baskets_0 = 0;
    double ind_bid_ask_counter_0 = 0;
    
    ArrayList<MyTimeStampObject> index_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> fut_week_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> fut_month_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> baskets_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> ind_bid_ask_counter_timestamp = new ArrayList<>();

    ExpWeek week;
    ExpMonth month;

    ArrayList<MyTimeSeries> timeSeriesList = new ArrayList<>();

    public DataBaseService() {
        super();
        week = apiObject.getExps().getWeek();
        month = apiObject.getExps().getMonth();

        // OP AVG
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_5));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_60));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_MONTH));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240));

        // DF CDF
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_2_CDF));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_7_CDF));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_5_CDF));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_6_CDF));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_4_CDF));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF));

        // DF RAW
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_2_RAW));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_7_RAW));
        timeSeriesList.add(apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_DE_CORR_RAW));



    }

    @Override
    public void go() {
        if (Manifest.DB_UPLOAD && BackGroundRunner.streamMarketBool) {
            //	 Day
            append_changed_data_to_lists();
        }
    }

    private void append_changed_data_to_lists() {

        double index = apiObject.getIndex();
        double fut_week = week.getOptions().getContract();
        double fut_month = month.getOptions().getContract();
        double baskets = apiObject.getBasketUp() - apiObject.getBasketDown();
        double ind_bid_ask_counter = apiObject.getIndBidAskCounter();

        // Index bid ask counter
        double change = ind_bid_ask_counter - ind_bid_ask_counter_0;
        if (change != 0) {
            if (change < 10000 && change > -10000) {
                ind_bid_ask_counter_timestamp.add(new MyTimeStampObject(Instant.now(), change));
            }
            ind_bid_ask_counter_0 = ind_bid_ask_counter;
        }

        // Baskets
        change = baskets - baskets_0;
        if (change != 0) {
            baskets_0 = baskets;
            baskets_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Op avg week
        if (sleepCount % 1000 == 0) {
            Instant instant = Instant.now();
            index_timestamp.add(new MyTimeStampObject(instant, index));
            fut_week_timestamp.add(new MyTimeStampObject(instant, fut_week));
            fut_month_timestamp.add(new MyTimeStampObject(instant, fut_month));
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
            insert_data_retro_mega(fut_week_timestamp, Factories.Tables.SAGIV_FUT_WEEK_TABLE);
            insert_data_retro_mega(fut_month_timestamp, Factories.Tables.SAGIV_FUT_MONTH_TABLE);
            insert_data_retro_mega(baskets_timestamp, Factories.Tables.BASKETS_TABLE);
            insert_data_retro_mega(index_timestamp, Factories.Tables.SAGIV_INDEX_TABLE);
            insert_data_retro_mega(index_timestamp, Factories.IDs.BASKETS_TABLE);
            insert_data_retro_mega(ind_bid_ask_counter_timestamp, Factories.Tables.INDEX_BID_ASK_COUNTER);
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
