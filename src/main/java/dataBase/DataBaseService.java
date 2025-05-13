package dataBase;

import api.BASE_CLIENT_OBJECT;
import api.Manifest;
import api.TA35;
import arik.Arik;
import charts.myChart.MyTimeSeries;
import counter.BackGroundRunner;
import dataBase.mySql.MySql;
import exp.ExpMonth;
import exp.ExpWeek;
import races.Race_Logic;
import java.time.Instant;
import java.util.ArrayList;

public class DataBaseService extends IDataBaseHandler {

    double baskets_0 = 0;
    double index_races_0 = 0;
    double week_races_0 = 0;
    double month_races_wm_0 = 0;
    double week_races_wm_0 = 0;
    double op_week_interest_0 = 0;
    double op_month_interest_0 = 0;
    double op_roll_interest_0 = 0;

    ArrayList<MyTimeStampObject> baskets_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> index_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> week_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> month_races_wm_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> week_races_wm_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> op_week_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> op_month_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> op_roll_interest_timeStamp = new ArrayList<>();

    ExpWeek week;
    ExpMonth month;

    ArrayList<MyTimeSeries> timeSeriesList;

    TA35 client;

    int sleep_count = 100;

    public DataBaseService(BASE_CLIENT_OBJECT client) {
        super(client);
        this.client = (TA35) client;
        week = this.client.getExps().getWeek();
        month = this.client.getExps().getMonth();
        timeSeriesList = new ArrayList<>();

        // Append timeseries
        add_timeseries();
    }

    @Override
    public void insertData(int sleep) {

        if (Manifest.DB_UPLOAD && BackGroundRunner.streamMarketBool) {

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

    }

    @Override
    public void loadData() {

    }

    @Override
    public void initTablesNames() {

    }

    private void add_timeseries() {
        // OP AVG
//        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.INDEX_AVG_3600));
//        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.INDEX_AVG_900));
        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_15));
        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_60));
        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_240_CONTINUE));
        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.ROLL_900));
        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.ROLL_3600));

        // DF OLD CDF
        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_5_CDF_OLD));
        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_6_CDF_OLD));
        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_4_CDF_OLD));
        timeSeriesList.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF_OLD));

    }

    private void on_change_data() {

        double baskets = client.getBasketFinder_by_stocks().getBasket_up() - client.getBasketFinder_by_stocks().getBasket_down();

        // Baskets
        double change = baskets - baskets_0;
        if (change != 0) {
            baskets_0 = baskets;
            baskets_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Index races
        double index_races = client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX).get_r_one_points();

        if (index_races != index_races_0) {
            double last_count = index_races - index_races_0;
            if (last_count == 1 || last_count == -1) {
                index_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            index_races_0 = index_races;
        }

        // Week races
        double week_races = client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX).get_r_two_points();

        if (week_races != week_races_0) {
            double last_count = week_races - week_races_0;
            if (last_count == 1 || last_count == -1) {
                week_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            week_races_0 = week_races;
        }

        // Month races WM
        double month_races_wm = client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_one_points();

        if (month_races_wm != month_races_wm_0) {
            double last_count = month_races_wm - month_races_wm_0;
            if (last_count == 1 || last_count == -1) {
                month_races_wm_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            month_races_wm_0 = month_races_wm;
        }

        // Month races WM
        double week_races_wm = client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_two_points();

        if (week_races_wm != week_races_wm_0) {
            double last_count = week_races_wm - week_races_wm_0;
            if (last_count == 1 || last_count == -1) {
                week_races_wm_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            week_races_wm_0 = week_races_wm;
        }

        // Month interest
        double month_interest = client.getOp_month_interest();

        if (month_interest != op_month_interest_0) {
            double last_count = month_interest - op_month_interest_0;
            if (last_count == 1 || last_count == -1) {
                op_month_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            op_month_interest_0 = month_interest;
        }

        // Week interest
        double week_interest = client.getOp_week_interest();

        if (week_interest != op_week_interest_0) {
            double last_count = week_interest - op_week_interest_0;
            if (last_count == 1 || last_count == -1) {
                op_week_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            op_week_interest_0 = week_interest;
        }

        // Roll interest
        double roll_interest = client.getRoll_interest();

        if (roll_interest != op_roll_interest_0) {
            double last_count = roll_interest - op_roll_interest_0;
            if (last_count == 1 || last_count == -1) {
                op_roll_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
            }
            op_roll_interest_0 = roll_interest;
        }



        // Grabb data and insert data
        if (sleep_count % 10000 == 0) {
            updateListsRetro();
            grab_data();
        }
    }

    private void grab_data() {
        new Thread(() -> {
            try {
                client.setDb_loaded(true);

                // Update data
                update_series();

                System.out.println("Grabbed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void update_series() {
        for (MyTimeSeries serie : timeSeriesList) {
            serie.updateData();
        }
    }

    protected void insert_dev_prod(ArrayList<MyTimeStampObject> list, int dev_id, int prod_id) {
        System.out.println("------------------------ Insert start ----------------------------");
        if (dev_id != 0) {
            insertListRetro(list, dev_id, MySql.JIBE_DEV_CONNECTION);
        }
        if (prod_id != 0) {
            insertListRetro(list, prod_id, MySql.JIBE_PROD_CONNECTION);
        }
        System.out.println("------------------------ Insert End ----------------------------");
        list.clear();
    }

    private void updateListsRetro() {
        // Dev and Prod
        insert_data_retro_mega(baskets_timestamp, Factories.TimeSeries.BASKETS);
        insert_data_retro_mega(index_races_timeStamp, Factories.TimeSeries.INDEX_RACES_WI);
        insert_data_retro_mega(week_races_timeStamp, Factories.TimeSeries.WEEK_RACES_WI);

        insert_data_retro_mega(week_races_wm_timeStamp, Factories.TimeSeries.WEEK_RACES_WM);
        insert_data_retro_mega(month_races_wm_timeStamp, Factories.TimeSeries.MONTH_RACES_WM);

        // Interest dev
        insert_data_retro_mega(op_week_interest_timeStamp, Factories.TimeSeries.OP_WEEK_INTEREST_DEV);
        insert_data_retro_mega(op_month_interest_timeStamp, Factories.TimeSeries.OP_MONTH_INTEREST_DEV);
        insert_data_retro_mega(op_roll_interest_timeStamp, Factories.TimeSeries.ROLL_INTEREST_DEV);

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
