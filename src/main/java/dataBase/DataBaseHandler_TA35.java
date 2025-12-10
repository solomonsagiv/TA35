package dataBase;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import api.deltaTest.Calculator;
import arik.Arik;
import charts.myChart.MyTimeSeries;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import locals.L;
import miniStocks.MiniStock;
import options.Options;
import options.Strike;
import races.Race_Logic;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

public class DataBaseHandler_TA35 extends IDataBaseHandler {

    ArrayList<MyTimeStampObject> last_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> bid_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> ask_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> mid_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> mid_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> month_races_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> op_week_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> op_month_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> roll_interest_timeStamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> baskets_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> month_counter_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> week_counter_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> trading_status_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> ba_tot_pos_weight_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> delta_tot_pos_weight_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> counter_2_tot_weight_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> total_delta_timestamp = new ArrayList<>();
    ArrayList<MyTimeStampObject> counter2_table_avg_timestamp = new ArrayList<>();

    ArrayList<MyTimeSeries> timeSeries;
    Race_Logic wi_race, wm_race;
    Options week_options, month_options;

    double bid_0 = 0,
            ask_0 = 0,
            last_0 = 0,
            mid_0 = 0,
            mid_races_0 = 0,
            op_week_interest_0 = 0,
            op_month_interest_0 = 0,
            roll_interest_0 = 0,
            baskets_0 = 0,
            month_races_0 = 0,
            month_counter_0 = 0,
            week_counter_0 = 0,
            trading_status_0 = 0,
            ba_tot_pos_weight_0 = 0,
            delta_tot_pos_weight_0 = 0,
            counter_2_tot_weight_0 = 0,
            total_delta_0 = 0,
            counter2_table_avg_0 = 0;

    public DataBaseHandler_TA35(BASE_CLIENT_OBJECT client) {
        super(client);
        init_timeseries_to_updater();
        this.wi_race = client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX);
        this.wm_race = client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH);
        this.week_options = client.getExps().getWeek().getOptions();
        this.month_options = client.getExps().getMonth().getOptions();
    }

    int sleep_count = 100;

    int stocks_sleep_count = 0;

    int load_stocks_data_count = 0;

    @Override
    public void insert_data(int sleep) {
        // Update count
        sleep_count += sleep;
        stocks_sleep_count += sleep;

        if (this.exps == null) {
            this.exps = client.getExps();
        }

        // Update lists retro
        if (sleep_count % 15000 == 0) {
            load_stocks_data();
            updateListsRetro();
            update_data();

            sleep_count = 0;
        }

        // Insert stocks
        if (stocks_sleep_count % 60000 == 0) {
            stocks_sleep_count = 0;
            insert_stocks();
            System.out.println("Insert");
        }

        // On changed da
        // ta
        on_change_data();
    }

    private void insert_stocks() {
        try {

            List<MiniStock> stocks = new ArrayList<>(TA35.getInstance().getStocksHandler().getStocks());

            Queries.insertStocksSnapshot(stocks, MySql.JIBE_PROD_CONNECTION);
            
            // Insert options snapshots for week and month
            insert_options_snapshots();
            
        } catch (Exception e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage("TA35 Index Insert stocks Failed ");
            Arik.getInstance().sendErrorMessage(e);
        }
    }

    private void insert_options_snapshots() {
        try {
            // Insert week options snapshot
            if (week_options != null && !week_options.getStrikes().isEmpty()) {
                Queries.insertOptionsSnapshot(week_options, "ta35w", MySql.JIBE_PROD_CONNECTION);
            }

            // Insert month options snapshot
            if (month_options != null && !month_options.getStrikes().isEmpty()) {
                Queries.insertOptionsSnapshot(month_options, "ta35m", MySql.JIBE_PROD_CONNECTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage("TA35 Index Insert options snapshots Failed ");
            Arik.getInstance().sendErrorMessage(e);
        }
    }

    private void update_data() {
        new Thread(() -> {
            for (MyTimeSeries ts : timeSeries) {
                try {
                    System.out.println(ts.getName() + " " + ts.getValue());
                    System.out.println(ts.getName());
                    ts.updateData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void update_options() {
        get_options_from_db("ta35w");
        get_options_from_db("ta35m");
    }

    /**
     * טוען volatility של אופציות מבסיס הנתונים לפי index_id
     * @param indexId 'ta35w' עבור Week או 'ta35m' עבור Month
     */
    private void get_options_from_db(String indexId) {
        // Determine which Options object to use based on index_id
        Options options;
        String errorMsgPrefix;
        if ("ta35w".equals(indexId)) {
            options = client.getExps().getWeek().getOptions();
            errorMsgPrefix = "TA35 Load week";
        } else if ("ta35m".equals(indexId)) {
            options = client.getExps().getMonth().getOptions();
            errorMsgPrefix = "TA35 Load month";
        } else {
            System.err.println("Unknown index_id: " + indexId);
            return;
        }
        
        try {
            // Load both CALL and PUT options
            String query = "SELECT strike, volatility, index_id, contract_type " +
                           "FROM ts.options_data " +
                           "WHERE index_id = '" + indexId + "' " +
                           "AND (contract_type = 'CALL' OR contract_type = 'PUT') " +
                           "AND time = (SELECT MAX(time) FROM ts.options_data) " +
                           "order by strike, contract_type";

            List<Map<String, Object>> rs = MySql.select(query, MySql.JIBE_PROD_CONNECTION);
            
            for (Map<String, Object> row : rs) {
                double strike = ((Number) row.get("strike")).doubleValue();
                double volatility = ((Number) row.get("volatility")).doubleValue();
                String contractType = (String) row.get("contract_type");
                
                // Get or create strike
                Strike s = options.getStrike(strike);
                if (s == null) {
                    s = new Strike(strike);
                    options.addStrike(s);
                }
                
                // Set IV on the appropriate option (CALL or PUT)
                if ("CALL".equalsIgnoreCase(contractType)) {
                    if (s.getCall() != null) {
                        s.getCall().setIv(volatility);
                    }
                } else if ("PUT".equalsIgnoreCase(contractType)) {
                    if (s.getPut() != null) {
                        s.getPut().setIv(volatility);
                    }
                }
                
                // Also update strike-level IV (for backward compatibility)
                s.setIv(volatility);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage(errorMsgPrefix + " options volatility failed");
            Arik.getInstance().sendErrorMessage(e);
        }
    }

    /**
     * טוען delta_counter ו-counter של אופציות Week מטבלת options_snapshots
     */
    private void load_options_week_counters_from_db() {
        try {
            List<Map<String, Object>> rs = Queries.getLastOptionsSnapshot("ta35w", MySql.JIBE_PROD_CONNECTION);
            
            if (rs == null || rs.isEmpty()) {
                return;
            }

            for (Map<String, Object> row : rs) {
                try {
                    Number strikeNum = (Number) row.get("strike");
                    if (strikeNum == null) continue;
                    
                    double strike = strikeNum.doubleValue();
                    Strike s = client.getExps().getWeek().getOptions().getStrike(strike);
                    
                    if (s == null) {
                        continue; // Strike doesn't exist, skip
                    }

                    // Get delta_counter and counter from database
                    Number deltaCounterNum = (Number) row.get("delta_counter");
                    Number counterNum = (Number) row.get("counter");

                    int deltaCounter = deltaCounterNum != null ? deltaCounterNum.intValue() : 0;
                    int counter = counterNum != null ? counterNum.intValue() : 0;

                    // Update Call option if exists
                    if (s.getCall() != null) {
                        s.getCall().setDeltaCounter(deltaCounter);
                        s.getCall().setBidAskCounter(counter);
                    }

                    // Update Put option if exists
                    if (s.getPut() != null) {
                        s.getPut().setDeltaCounter(deltaCounter);
                        s.getPut().setBidAskCounter(counter);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing row in load_options_week_counters_from_db: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Loaded week options counters from database: " + rs.size() + " strikes");
        } catch (Exception e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage("TA35 Load week options counters failed");
            Arik.getInstance().sendErrorMessage(e);
        }
    }

    /**
     * טוען delta_counter ו-counter של אופציות Month מטבלת options_snapshots
     */
    private void load_options_month_counters_from_db() {
        try {
            List<Map<String, Object>> rs = Queries.getLastOptionsSnapshot("ta35m", MySql.JIBE_PROD_CONNECTION);
            
            if (rs == null || rs.isEmpty()) {
                return;
            }

            for (Map<String, Object> row : rs) {
                try {
                    Number strikeNum = (Number) row.get("strike");
                    if (strikeNum == null) continue;
                    
                    double strike = strikeNum.doubleValue();
                    Strike s = client.getExps().getMonth().getOptions().getStrike(strike);
                    
                    if (s == null) {
                        continue; // Strike doesn't exist, skip
                    }

                    // Get delta_counter and counter from database
                    Number deltaCounterNum = (Number) row.get("delta_counter");
                    Number counterNum = (Number) row.get("counter");

                    int deltaCounter = deltaCounterNum != null ? deltaCounterNum.intValue() : 0;
                    int counter = counterNum != null ? counterNum.intValue() : 0;

                    // Update Call option if exists
                    if (s.getCall() != null) {
                        s.getCall().setDeltaCounter(deltaCounter);
                        s.getCall().setBidAskCounter(counter);
                    }

                    // Update Put option if exists
                    if (s.getPut() != null) {
                        s.getPut().setDeltaCounter(deltaCounter);
                        s.getPut().setBidAskCounter(counter);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing row in load_options_month_counters_from_db: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Loaded month options counters from database: " + rs.size() + " strikes");
        } catch (Exception e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage("TA35 Load month options counters failed");
            Arik.getInstance().sendErrorMessage(e);
        }
    }

    private void on_change_data() {

        // Is live db
        if (client.isStarted()) {

            // Status
            if (client.getTrading_status() != trading_status_0) {
                trading_status_0 = client.getTrading_status();
                trading_status_timestamp.add(new MyTimeStampObject(Instant.now(), trading_status_0));
            }

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

            // BA tot pos weight
            double ba_tot_pos_weight = client.getCounter1_weight();
            if (ba_tot_pos_weight != ba_tot_pos_weight_0) {
                ba_tot_pos_weight_timestamp.add(new MyTimeStampObject(Instant.now(), ba_tot_pos_weight));
                ba_tot_pos_weight_0 = ba_tot_pos_weight;
            }

            // BA tot pos weight
            double delta_tot_pos_weight = client.getDelta_weight();
            if (delta_tot_pos_weight != delta_tot_pos_weight_0) {
                delta_tot_pos_weight_timestamp.add(new MyTimeStampObject(Instant.now(), delta_tot_pos_weight));
                delta_tot_pos_weight_0 = delta_tot_pos_weight;
            }

            // Counter 2 tot weight
            double counter_2_tot_weight = client.getCounter2_weight();
            if (counter_2_tot_weight != counter_2_tot_weight_0) {
                counter_2_tot_weight_timestamp.add(new MyTimeStampObject(Instant.now(), counter_2_tot_weight));
                counter_2_tot_weight_0 = counter_2_tot_weight;
            }

            // Total delta
            double total_delta = client.getTotal_delta();
            if (total_delta != total_delta_0) {
                double last_count = total_delta - total_delta_0;
                total_delta_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                total_delta_0 = total_delta;
            }

            // Baskets
            double baskets = ((TA35) client).getBasketFinder_by_stocks().get_baskets();

            if (baskets != baskets_0) {
                double last_count = baskets - baskets_0;
                if (L.abs(last_count) < 5) {
                    baskets_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                baskets_0 = baskets;
            }

            // Index races
            double index_races = ((TA35) client).get_index_races_iw();

            if (index_races != mid_races_0) {
                double last_count = index_races - mid_races_0;
                if (L.abs(last_count) < 5) {
                    mid_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                mid_races_0 = index_races;
            }

            // Month
            double month_races = ((TA35) client).get_month_races_wm();

            if (month_races != month_races_0) {
                double last_count = month_races - month_races_0;
                if (L.abs(last_count) < 5) {
                    month_races_timeStamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                month_races_0 = month_races;
            }

            // OP week interest
            double week_interest = client.getOp_week_interest();
            if (week_interest != op_week_interest_0) {
                if (L.abs(week_interest) < 10) {
                    op_week_interest_0 = week_interest;
                    op_week_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), op_week_interest_0));
                }
            }

            // OP month interest
            double month_interest = client.getOp_month_interest();
            if (month_interest != op_month_interest_0) {
                if (L.abs(month_interest) < 10) {
                    op_month_interest_0 = month_interest;
                    op_month_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), op_month_interest_0));
                }
            }

            // Roll interest
            double roll_interest = client.getRoll_interest();
            if (roll_interest != roll_interest_0) {
                if (L.abs(roll_interest) < 10) {
                    roll_interest_0 = roll_interest;
                    roll_interest_timeStamp.add(new MyTimeStampObject(Instant.now(), roll_interest_0));
                }
            }

            // Month bid ask counter
            double week_counter = ((TA35) client).get_week_bid_ask_counter();

            if (week_counter != week_counter_0) {
                double last_count = week_counter - week_counter_0;
                if (L.abs(last_count) < 5) {
                    week_counter_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                week_counter_0 = week_counter;
            }

            // Month bid ask counter
            double month_counter = ((TA35) client).get_month_bid_ask_counter();

            if (month_counter != month_counter_0) {
                double last_count = month_counter - month_counter_0;
                if (L.abs(last_count) < 5) {
                    month_counter_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                }
                month_counter_0 = month_counter;
            }

            // Counter 2 table avg
            double counter2_table_avg = client.getCounter2_table_avg();
            if (counter2_table_avg != counter2_table_avg_0) {
                double last_count = counter2_table_avg - counter2_table_avg_0;
                counter2_table_avg_timestamp.add(new MyTimeStampObject(Instant.now(), last_count));
                counter2_table_avg_0 = counter2_table_avg;
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

        // Exp
        // load_exp_data();

        // Baskets
        load_baskets();

        // Load races
        load_all_races();

        // Load counters
        load_bid_ask_counter();

        // Load options counters (delta_counter, counter) from options_snapshots table
        load_options_week_counters_from_db();
        load_options_month_counters_from_db();

        // Load positive tracker
        load_positive_tracker();

        // Set load
        client.setDb_loaded(true);
    }

    private void load_stocks_data() {

        if (load_stocks_data_count > 3)
            return;

        if (TA35.getInstance().getStocksHandler().getStocks().size() == 0)
            return;

        try {
            load_stocks_data_count++;
            List<MiniStock> stocks = new ArrayList<>(TA35.getInstance().getStocksHandler().getStocks());

            Queries.loadLastSnapshotStocksData(stocks, MySql.JIBE_PROD_CONNECTION);
            load_stocks_data_count = 10;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Arik.getInstance().sendMessage("TA35 load stocks data");
            Arik.getInstance().sendErrorMessage(throwables);
        }

    }

    private void load_positive_tracker() {

        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.STOCKS_TOT_BA_WEIGHT_PROD);
        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW_NO_MODULU,
                MySql.JIBE_PROD_CONNECTION);

        for (Map<String, Object> row : rs) {
            try {

                Object value = row.get("value");
                if (value == null) {
                    continue;
                }

                BigDecimal bigDecimalValue = (BigDecimal) value;

                Calculator.PositiveTracker.update(bigDecimalValue.doubleValue());
            } catch (ClassCastException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void load_all_races() {
        int index_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX_RACES_WI);
        int week_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_RACES_WI);
        int month_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_RACES_WM);

        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, index_races_id, true);
        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, week_races_id, false);
        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH, month_races_id, true);
    }

    private void load_bid_ask_counter() {
        int week_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD);
        int week_counter = (int) Queries.handle_rs(
                Objects.requireNonNull(Queries.get_last_record_mega(week_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION)));

        int month_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD);
        int month_counter = (int) Queries.handle_rs(
                Objects.requireNonNull(Queries.get_last_record_mega(month_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION)));

        client.getExps().getWeek().getOptions().setBidAskCounter(week_counter);
        client.getExps().getMonth().getOptions().setBidAskCounter(month_counter);

        System.out.println("---------------------- Counterss ----------------------");
        System.out.println(week_counter + " " + month_counter);
    }

    @Override
    public void init_timeseries_to_updater() {
        timeSeries = new ArrayList<>();
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_4_CDF_OLD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_5_CDF_OLD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_6_CDF_OLD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF_OLD));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_240_CONTINUE));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_60));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_15));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.INDEX_AVG_3600));
        timeSeries.add(client.getTimeSeriesHandler().get(Factories.TimeSeries.INDEX_AVG_900));
    }

    private void updateListsRetro() {

        System.out.println(
                "Insert !!!!! -------------------------- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + sleep_count);

        // Interest
        int prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_WEEK_INTEREST_PROD);
        insert_dev_prod(op_week_interest_timeStamp, prod_id);

        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_MONTH_INTEREST_PROD);
        insert_dev_prod(op_month_interest_timeStamp, prod_id);

        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_INTEREST_PROD);
        insert_dev_prod(roll_interest_timeStamp, prod_id);

        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.BASKETS);
        insert_dev_prod(baskets_timestamp, prod_id);

        // Last
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.LAST_PRICE);
        insert_dev_prod(last_timestamp, prod_id);

        // Mid
        prod_id = 0;
        insert_dev_prod(mid_timestamp, prod_id);

        // Bid
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.BID);
        insert_dev_prod(bid_timestamp, prod_id);

        // Ask
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ASK);
        insert_dev_prod(ask_timestamp, prod_id);

        // Index race
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX_RACES_WI);
        insert_dev_prod(mid_races_timeStamp, prod_id);

        // Month race
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_RACES_WM);
        insert_dev_prod(month_races_timeStamp, prod_id);

        // Month counter
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD);
        insert_dev_prod(month_counter_timestamp, prod_id);

        // Week counter
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD);
        insert_dev_prod(week_counter_timestamp, prod_id);

        // Trading status
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.TRADING_STATUS);
        insert_dev_prod(trading_status_timestamp, prod_id);

        // Total weight BA
        // dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.STOCKS_TOT_BA_WEIGHT_PROD);
        insert_dev_prod(ba_tot_pos_weight_timestamp, prod_id);

        // Total weight Delta
        // dev_id = 0;
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.STOCKS_TOT_DELTA_WEIGHT_PROD);
        insert_dev_prod(delta_tot_pos_weight_timestamp, prod_id);

        // Counter 2 tot weight
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.COUNTER_2_TOT_WEIGHT_PROD);
        insert_dev_prod(counter_2_tot_weight_timestamp, prod_id);

        // Total delta
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.TOTAL_DELTA);
        insert_dev_prod(total_delta_timestamp, prod_id);

        // Counter 2 table avg
        prod_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.COUNTER_2_TABLE_AVG);
        insert_dev_prod(counter2_table_avg_timestamp, prod_id);
    }
}