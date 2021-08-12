package dataBase;

import api.Manifest;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import dataBase.mySql.tables.BoundsTable;
import service.MyBaseService;

public class DataBaseService extends MyBaseService {

    BoundsTable boundsTable;

    double pre_bid_ask_counter_week = 0;
    double pre_bid_ask_counter_month = 0;
    double pre_delta_week = 0;
    double pre_delta_month = 0;
    double pre_ind_delta = 0;

    public DataBaseService() {
        super();
        boundsTable = new BoundsTable("bounds");
    }

    @Override
    public void go() {
        if (Manifest.DB) {
            //	 Day
            insert_data();
        }
    }

    private void insert_data() {

        System.out.println("insert data is running");

        double last_delta_week = apiObject.getExpWeek().getOptions().getDelta();
        double last_delta_month = apiObject.getExpMonth().getOptions().getDelta();
        double last_bid_ask_counter_week = apiObject.getExpWeek().getOptions().getConBidAskCounter();
        double last_bid_ask_counter_month = apiObject.getExpMonth().getOptions().getConBidAskCounter();
        double last_ind_delta = apiObject.getStocksHandler().getDelta();


        // Delta week
        double change = last_delta_week - pre_delta_week;
        if (change != 0) {
            MySql.insert(Queries.insert(TablesFactory.DELTA_WEEK_TABLE, change));
            pre_delta_week = last_delta_week;
        }

        // Delta month
        change = last_delta_month - pre_delta_month;
        if (change != 0) {
            MySql.insert(Queries.insert(TablesFactory.DELTA_MONTH_TABLE, change));
            pre_delta_month = last_delta_month;
        }

        // Bid ask counter week
        change = last_bid_ask_counter_week - pre_bid_ask_counter_week;
        if (change != 0) {
            MySql.insert(Queries.insert(TablesFactory.BID_ASK_COUNTER_WEEK_TABLE, change));
            pre_bid_ask_counter_week = last_bid_ask_counter_week;
        }

        // Delta month
        change = last_bid_ask_counter_month - pre_bid_ask_counter_month;
        if (change != 0) {
            MySql.insert(Queries.insert(TablesFactory.BID_ASK_COUNTER_MONTH_TABLE, change));
            pre_bid_ask_counter_month = last_bid_ask_counter_month;
        }

        // Index delta
        change = last_ind_delta - pre_ind_delta;
        if (change != 0) {
            MySql.insert(Queries.insert(TablesFactory.INDEX_TABLE, change));
            pre_ind_delta = last_ind_delta;
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

    public BoundsTable getBoundsTable() {
        return boundsTable;
    }

}
