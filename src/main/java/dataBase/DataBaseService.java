package dataBase;

import api.Manifest;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import dataBase.mySql.tables.BoundsTable;
import exp.ExpMonth;
import exp.ExpWeek;
import service.MyBaseService;
import java.time.Instant;
import java.util.ArrayList;

public class DataBaseService extends MyBaseService {

    BoundsTable boundsTable;

    double bid_ask_counter_week_0 = 0;
    double bid_ask_counter_month_0 = 0;
    double delta_week_0 = 0;
    double delta_month_0 = 0;
    double ind_delta_0 = 0;

    ArrayList<MyTimeStampObject> bid_ask_counter_week_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> bid_ask_counter_month_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> delta_week_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> delta_month_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> ind_delta_timestamp = new ArrayList<>();

    public DataBaseService() {
        super();
        boundsTable = new BoundsTable("bounds");
    }

    @Override
    public void go() {
        if (Manifest.DB) {
            //	 Day
            append_changed_data_to_lists();
        }
    }

    private void append_changed_data_to_lists() {

        System.out.println("insert data is running");

        double delta_week = apiObject.getExpWeek().getOptions().getDelta();
        double delta_month = apiObject.getExpMonth().getOptions().getDelta();
        double bid_ask_counter_week = apiObject.getExpWeek().getOptions().getConBidAskCounter();
        double bid_ask_counter_month = apiObject.getExpMonth().getOptions().getConBidAskCounter();
        double ind_delta = apiObject.getStocksHandler().getDelta();

        // Delta week
        double change = delta_week - delta_week_0;
        if (change != 0) {
            delta_week_0 = delta_week;
            delta_week_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Delta month
        change = delta_month - delta_month_0;
        if (change != 0) {
            delta_month_0 = delta_month;
            delta_month_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Bid ask counter week
        change = bid_ask_counter_week - bid_ask_counter_week_0;
        if (change != 0) {
            bid_ask_counter_week_0 = bid_ask_counter_week;
            bid_ask_counter_week_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Delta month
        change = bid_ask_counter_month - bid_ask_counter_month_0;
        if (change != 0) {
            bid_ask_counter_month_0 = bid_ask_counter_month;
            bid_ask_counter_month_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Index delta
        change = ind_delta - ind_delta_0;
        if (change != 0) {
            ind_delta_0 = ind_delta;
            ind_delta_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Grabb data and insert data
        if (sleepCount % 15000 == 0) {
            insert_data();
            grab_data();
        }


    }

    private void insert_data() {
        new Thread(() -> {
            insert_data_retro(delta_week_timestamp, Factories.Tables.DELTA_WEEK_TABLE);
            insert_data_retro(delta_month_timestamp, Factories.Tables.DELTA_MONTH_TABLE);
            insert_data_retro(bid_ask_counter_week_timestamp, Factories.Tables.BID_ASK_COUNTER_WEEK_TABLE);
            insert_data_retro(bid_ask_counter_month_timestamp, Factories.Tables.BID_ASK_COUNTER_MONTH_TABLE);
            insert_data_retro(ind_delta_timestamp, Factories.Tables.INDEX_DELTA_TABLE);
        }).start();
    }

    private void grab_data() {
        new Thread(() -> {
            double op_avg_week = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_WEEK_TABLE));
            double op_avg_week_60 = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_WEEK_TABLE, 60));

            double op_avg_month = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_MONTH_TABLE));
            double op_avg_month_60 = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_MONTH_TABLE, 60));

            double delta_week_avg = Queries.handle_rs(Queries.get_serie_avg(Factories.Tables.DELTA_WEEK_TABLE));
            double delta_week_avg_60 = Queries.handle_rs(Queries.get_serie_avg(Factories.Tables.DELTA_WEEK_TABLE, 60));

            double delta_month_avg = Queries.handle_rs(Queries.get_serie_avg(Factories.Tables.DELTA_MONTH_TABLE));
            double delta_month_avg_60 = Queries.handle_rs(Queries.get_serie_avg(Factories.Tables.DELTA_MONTH_TABLE, 60));

            ExpWeek expWeek = apiObject.getExpWeek();
            expWeek.setOp_avg(op_avg_week);
            expWeek.setOp_avg_60(op_avg_week_60);
            expWeek.setDelta_avg(delta_week_avg);
            expWeek.setDelta_avg_60(delta_week_avg_60);

            ExpMonth expMonth = apiObject.getExpMonth();
            expMonth.setOp_avg(op_avg_month);
            expMonth.setOp_avg_60(op_avg_month_60);
            expMonth.setDelta_avg(delta_month_avg);
            expMonth.setDelta_avg_60(delta_month_avg_60);
        }).start();
    }

    @Override
    public String getName() {
        return "DataBaseService";
    }

    @Override
    public int getSleep() {
        return 1000;
    }

    public BoundsTable getBoundsTable() {
        return boundsTable;
    }



    void insert_data_retro(ArrayList<MyTimeStampObject> list, String table_location) {
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
    }
}
