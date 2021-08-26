package dataBase;

import api.ApiObject;
import api.Manifest;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import exp.ExpMonth;
import exp.ExpWeek;
import service.MyBaseService;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;

public class DataBaseService extends MyBaseService {

    double bid_ask_counter_week_0 = 0;
    double bid_ask_counter_month_0 = 0;
    double delta_week_0 = 0;
    double delta_month_0 = 0;
    double ind_delta_0 = 0;
    double index_0 = 0;
    double fut_week_0 = 0;
    double fut_month_0 = 0;
    double baskets_0 = 0;

    ArrayList<MyTimeStampObject> bid_ask_counter_week_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> bid_ask_counter_month_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> delta_week_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> delta_month_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> ind_delta_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> index_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> fut_week_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> fut_month_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> baskets_timestamp = new ArrayList<>();

    public DataBaseService() {
        super();
    }

    @Override
    public void go() {
        if (Manifest.DB) {
            //	 Day
            append_changed_data_to_lists();
        }
    }

    private void append_changed_data_to_lists() {

        double delta_week = apiObject.getExpWeek().getOptions().getDelta();
        double delta_month = apiObject.getExpMonth().getOptions().getDelta();
        double bid_ask_counter_week = apiObject.getExpWeek().getOptions().getConBidAskCounter();
        double bid_ask_counter_month = apiObject.getExpMonth().getOptions().getConBidAskCounter();
        double ind_delta = apiObject.getStocksHandler().getDelta();
        double index = apiObject.getIndex();
        double fut_week = apiObject.getExpWeek().getOptions().getContract();
        double fut_month = apiObject.getExpMonth().getOptions().getContract();
        double baskets = apiObject.getBasketUp() - apiObject.getBasketDown();

        // Delta week
        double change = delta_week - delta_week_0;
        if (change != 0 && change < 10000 && change > -10000) {
            delta_week_0 = delta_week;
            delta_week_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Delta month
        change = delta_month - delta_month_0;
        if (change != 0 && change < 10000 && change > -10000) {
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

        // Index
        change = index - index_0;
        if (change != 0) {
            index_0 = index;
            index_timestamp.add(new MyTimeStampObject(Instant.now(), index));
        }

        // Fut week
        change = fut_week - fut_week_0;
        if (change != 0) {
            fut_week_0 = fut_week;
            fut_week_timestamp.add(new MyTimeStampObject(Instant.now(), fut_week));
        }

        // Fut month
        change = fut_month - fut_month_0;
        if (change != 0) {
            fut_month_0 = fut_month;
            fut_month_timestamp.add(new MyTimeStampObject(Instant.now(), fut_month));
        }

        // Baskets
        change = baskets - baskets_0;
        if (change != 0) {
            baskets_0 = baskets;
            baskets_timestamp.add(new MyTimeStampObject(Instant.now(), change));
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
            insert_data_retro(index_timestamp, Factories.Tables.INDEX_TABLE);
            insert_data_retro(fut_week_timestamp, Factories.Tables.FUT_WEEK_TABLE);
            insert_data_retro(fut_month_timestamp, Factories.Tables.FUT_MONTH_TABLE);
            insert_data_retro(baskets_timestamp, Factories.Tables.BASKETS_TABLE);
        }).start();
    }

    private void grab_data() {
        new Thread(() -> {

            double op_avg_week = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_WEEK_TABLE));
            double op_avg_week_60 = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_WEEK_TABLE, 60));

            double op_avg_month = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_MONTH_TABLE));
            double op_avg_month_60 = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_MONTH_TABLE, 60));

            double delta_week_avg = Queries.handle_rs(Queries.get_serie_avg_from_cdf(Factories.Tables.DELTA_WEEK_TABLE));
            double delta_week_avg_60 = Queries.handle_rs(Queries.get_serie_avg_from_cdf(Factories.Tables.DELTA_WEEK_TABLE, 60));

            double delta_month_avg = Queries.handle_rs(Queries.get_serie_avg_from_cdf(Factories.Tables.DELTA_MONTH_TABLE));
            double delta_month_avg_60 = Queries.handle_rs(Queries.get_serie_avg_from_cdf(Factories.Tables.DELTA_MONTH_TABLE, 60));

            double bid_ask_counter_week_avg_60 = Queries.handle_rs(Queries.get_serie_avg_from_cdf(Factories.Tables.BID_ASK_COUNTER_WEEK_TABLE, 60));
            double bid_ask_counter_month_avg_60 = Queries.handle_rs(Queries.get_serie_avg_from_cdf(Factories.Tables.BID_ASK_COUNTER_MONTH_TABLE, 60));

            ExpWeek expWeek = apiObject.getExpWeek();
            expWeek.setOp_avg(op_avg_week);
            expWeek.setOp_avg_60(op_avg_week_60);
            expWeek.setDelta_avg(delta_week_avg);
            expWeek.setDelta_avg_60(delta_week_avg_60);
            expWeek.setBid_ask_counter_avg_60(bid_ask_counter_week_avg_60);

            ExpMonth expMonth = apiObject.getExpMonth();
            expMonth.setOp_avg(op_avg_month);
            expMonth.setOp_avg_60(op_avg_month_60);
            expMonth.setDelta_avg(delta_month_avg);
            expMonth.setDelta_avg_60(delta_month_avg_60);
            expMonth.setBid_ask_counter_avg_60(bid_ask_counter_month_avg_60);

            apiObject.first_load = true;

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
