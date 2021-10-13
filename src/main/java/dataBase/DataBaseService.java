package dataBase;

import api.Manifest;
import arik.Arik;
import counter.BackGroundRunner;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import exp.ExpMonth;
import exp.ExpWeek;
import service.MyBaseService;

import java.time.Instant;
import java.util.ArrayList;

public class DataBaseService extends MyBaseService {

    double bid_ask_counter_week_0 = 0;
    double bid_ask_counter_month_0 = 0;
    double delta_week_0 = 0;
    double delta_month_0 = 0;
    double ind_delta_0 = 0;
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
        if (Manifest.DB_UPLOAD) {
            //	 Day
            append_changed_data_to_lists();
        }
    }

    private void append_changed_data_to_lists() {

        double delta_week = apiObject.getExpWeek().getOptions().getTotal_delta();
        double delta_month = apiObject.getExpMonth().getOptions().getTotal_delta();
        double bid_ask_counter_week = apiObject.getExpWeek().getOptions().getConBidAskCounter();
        double bid_ask_counter_month = apiObject.getExpMonth().getOptions().getConBidAskCounter();
        double ind_delta = apiObject.getStocksHandler().getDelta();
        double index = apiObject.getIndex();
        double fut_week = apiObject.getExpWeek().getOptions().getContract();
        double fut_month = apiObject.getExpMonth().getOptions().getContract();
        double baskets = apiObject.getBasketUp() - apiObject.getBasketDown();

        // Delta week
        double change = delta_week - delta_week_0;
        if (change != 0) {
            if (change < 10000 && change > -10000) {
                delta_week_timestamp.add(new MyTimeStampObject(Instant.now(), change));
            }
            delta_week_0 = delta_week;
        }

        // Delta month
        change = delta_month - delta_month_0;
        if (change != 0) {
            if (change < 10000 && change > -10000) {
                delta_month_timestamp.add(new MyTimeStampObject(Instant.now(), change));
            }
            delta_month_0 = delta_month;
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

        // Baskets
        change = baskets - baskets_0;
        if (change != 0) {
            baskets_0 = baskets;
            baskets_timestamp.add(new MyTimeStampObject(Instant.now(), change));
        }

        // Op avg week
        if (sleepCount % 1000 == 0) {
            Instant instant = Instant.now();

            if (BackGroundRunner.streamMarketBool) {
                index_timestamp.add(new MyTimeStampObject(instant, index));
            }
            fut_week_timestamp.add(new MyTimeStampObject(instant, fut_week));
            fut_month_timestamp.add(new MyTimeStampObject(instant, fut_month));
        }

        // Grabb data and insert data
        if (sleepCount % 15000 == 0) {
            insert_data();
            grab_data();
        }

        // Options status
        if (sleepCount % 60000 == 0) {
            update_options_status();
        }
    }

    private void update_options_status() {
        new Thread(() -> {
            ExpWeek expWeek = apiObject.getExpWeek();
            ExpMonth expMonth = apiObject.getExpMonth();

            Queries.update_options_status(expWeek.getAsJson().toString(), DataBaseHandler.EXP_MONTH);
            Queries.update_options_status(expMonth.getAsJson().toString(), DataBaseHandler.EXP_WEEK);
        }).start();
    }

    private void insert_data() {
        new Thread(() -> {
            insert_data_retro(delta_week_timestamp, Factories.Tables.SAGIV_DELTA_WEEK_TABLE);
            insert_data_retro(delta_month_timestamp, Factories.Tables.SAGIV_DELTA_MONTH_TABLE);
            insert_data_retro(bid_ask_counter_week_timestamp, Factories.Tables.BID_ASK_COUNTER_WEEK_TABLE);
            insert_data_retro(bid_ask_counter_month_timestamp, Factories.Tables.BID_ASK_COUNTER_MONTH_TABLE);
            insert_data_retro(ind_delta_timestamp, Factories.Tables.INDEX_DELTA_TABLE);
            insert_data_retro(fut_week_timestamp, Factories.Tables.SAGIV_FUT_WEEK_TABLE);
            insert_data_retro(fut_month_timestamp, Factories.Tables.SAGIV_FUT_MONTH_TABLE);
            insert_data_retro(baskets_timestamp, Factories.Tables.BASKETS_TABLE);
            insert_data_retro(index_timestamp, Factories.Tables.SAGIV_INDEX_TABLE);
        }).start();
    }

    private void grab_data() {
        new Thread(() -> {
            try {
                int v5 = (int) Queries.handle_rs(Queries.get_last_record_from_decision_func(Factories.Tables.RESEARCH_TABLE, 2, 5));
                int v6 = (int) Queries.handle_rs(Queries.get_last_record_from_decision_func(Factories.Tables.RESEARCH_TABLE, 2, 6));
                double op_avg_week_30 = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.SAGIV_FUT_WEEK_TABLE, 30));
                double op_avg_month_30 = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.SAGIV_FUT_WEEK_TABLE, 30));

                // V5 V6
                apiObject.setV5(v5);
                apiObject.setV6(v6);

                // Op avg
                apiObject.getExpWeek().setOp_avg_30(op_avg_week_30);
                apiObject.getExpMonth().setOp_avg_30(op_avg_month_30);
                apiObject.setDbLoaded(true);

            } catch (Exception e ) {
                e.printStackTrace();
            }
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
}
