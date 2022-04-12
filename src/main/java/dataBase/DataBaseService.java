package dataBase;

import api.Manifest;
import arik.Arik;
import counter.BackGroundRunner;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import exp.Exp;
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
    double delta_mix_0 = 0;
    double ind_bid_ask_counter_0 = 0;
    
    ArrayList<MyTimeStampObject> bid_ask_counter_week_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> bid_ask_counter_month_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> delta_week_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> delta_month_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> delta_mix_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> ind_delta_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> index_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> fut_week_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> fut_month_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> baskets_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> ind_bid_ask_counter_timestamp = new ArrayList<>();

    ExpWeek week;
    ExpMonth month;

    public DataBaseService() {
        super();
        week = apiObject.getExps().getWeek();
        month = apiObject.getExps().getMonth();
    }

    @Override
    public void go() {
        if (Manifest.DB_UPLOAD && BackGroundRunner.streamMarketBool) {
            //	 Day
            append_changed_data_to_lists();
        }
    }

    private void append_changed_data_to_lists() {

        double delta_week = week.getOptions().getTotal_delta();
        double delta_month = month.getOptions().getTotal_delta();
        double delta_mix = month.getOptions().getTotal_delta() + week.getOptions().getTotal_delta();
        double bid_ask_counter_week = week.getOptions().getConBidAskCounter();
        double bid_ask_counter_month = month.getOptions().getConBidAskCounter();
        double ind_delta = apiObject.getStocksHandler().getDelta();
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

        // Delta week
        change = delta_week - delta_week_0;
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

        // Delta mix
        change = delta_mix - delta_mix_0;
        if (change != 0) {
            if (change < 10000 && change > -10000) {
                delta_mix_timestamp.add(new MyTimeStampObject(Instant.now(), change));
            }
            delta_mix_0 = delta_mix;
        }

        // Bid ask counter week
        change = bid_ask_counter_week - bid_ask_counter_week_0;
        if (change != 0) {
            if (change < 100 && change > -100) {
                bid_ask_counter_week_timestamp.add(new MyTimeStampObject(Instant.now(), change));
            }
            bid_ask_counter_week_0 = bid_ask_counter_week;
        }

        // Bid ask counter month
        change = bid_ask_counter_month - bid_ask_counter_month_0;
        if (change != 0) {
            if (change < 100 && change > -100) {
                bid_ask_counter_month_timestamp.add(new MyTimeStampObject(Instant.now(), change));
            }
            bid_ask_counter_month_0 = bid_ask_counter_month;
        }

        // Index delta
        change = ind_delta - ind_delta_0;
        if (change != 0) {
            if (change < 10000 && change > -10000) {
                ind_delta_timestamp.add(new MyTimeStampObject(Instant.now(), change));
            }
            ind_delta_0 = ind_delta;
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

    private void update_options_status() {
        new Thread(() -> {
            ExpWeek expWeek = apiObject.getExps().getWeek();
            ExpMonth expMonth = apiObject.getExps().getMonth();

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
            insert_data_retro(delta_mix_timestamp, Factories.Tables.DELTA_MIX_TABLE);
            insert_data_retro(ind_bid_ask_counter_timestamp, Factories.Tables.INDEX_BID_ASK_COUNTER);
        }).start();
    }
    
    private void grab_data() {
        new Thread(() -> {
            try {
                System.out.println("Grabbing...");
                int v5 = (int) Queries.handle_rs(Queries.get_last_record_from_decision_func(Factories.Tables.RESEARCH_TABLE, 2, 5));
                int v6 = (int) Queries.handle_rs(Queries.get_last_record_from_decision_func(Factories.Tables.RESEARCH_TABLE, 2, 6));
                int v4 = (int) Queries.handle_rs(Queries.get_last_record_from_decision_func(Factories.Tables.RESEARCH_TABLE, 2, 4));
                int v8 = (int) Queries.handle_rs(Queries.get_last_record_from_decision_func(Factories.Tables.RESEARCH_TABLE, 2, 8));

                int v103 = (int) Queries.handle_rs(Queries.get_last_record_from_decision_func(Factories.Tables.RESEARCH_TABLE, 1, 103));
                int v107 = (int) Queries.handle_rs(Queries.get_last_record_from_decision_func(Factories.Tables.RESEARCH_TABLE, 1, 107));
                
                double op_avg_week = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_WEEK_TABLE));
                double op_avg_week_5 =  Queries.handle_rs(Queries.get_last_record(Factories.Tables.OP_AVG_5));
                double op_avg_week_15 =  Queries.handle_rs(Queries.get_last_record(Factories.Tables.OP_AVG_15));
                double op_avg_week_60 =  Queries.handle_rs(Queries.get_last_record(Factories.Tables.OP_AVG_60));
                double continue_op_avg_week_240 = Queries.handle_rs(Queries.get_last_record(Factories.Tables.OP_AVG_240_CONTINUE));

                double op_avg_month = Queries.handle_rs(Queries.get_op_avg(Factories.Tables.FUT_MONTH_TABLE));

                // V5 V6 V4 V8 v103 v107
                apiObject.setV5(v5);
                apiObject.setV6(v6);
                apiObject.setV4(v4);
                apiObject.setV8(v8);
                apiObject.setV103(v103);
                apiObject.setV107(v107);

                Exp week = apiObject.getExps().getWeek();
                Exp month = apiObject.getExps().getMonth();

                // Op avg
                // Week
                week.setOp_avg(op_avg_week);
                week.setOp_avg_5(op_avg_week_5);
                week.setOp_avg_15(op_avg_week_15);
                week.setOp_avg_60(op_avg_week_60);
                week.setContinue_op_avg_240(continue_op_avg_week_240);

                // Month
                month.setOp_avg(op_avg_month);
                apiObject.setDbLoaded(true);

                System.out.println("Grabbed");

            } catch (Exception e) {
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
