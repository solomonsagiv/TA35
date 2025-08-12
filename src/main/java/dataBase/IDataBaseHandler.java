package dataBase;

import api.BASE_CLIENT_OBJECT;
import api.INDEX_OBJECT;
import api.TA35;
import arik.Arik;
import charts.myChart.MyTimeSeries;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import exp.ExpMonth;
import exp.ExpWeek;
import exp.Exps;
import locals.L;
import props.Prop;
import props.Props;
import races.Race_Logic;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IDataBaseHandler {


    public static final int SERIE_RESULT_TYPE = 0;
    public static final int SUM_RESULT_TYPE = 1;
    public static final int AVG_RESULT_TYPE = 2;
    public static final String EXP_WEEK = "WEEK";
    public static final String EXP_MONTH = "MONTH";
    public static final String x = "x";
    public static final String y = "y";
    public static final String width = "width";
    public static final String height = "height";

    public Map<Integer, Integer> serie_ids = new HashMap<>();

    private Map<String, MyTimeSeries> series = new HashMap<>();

    protected BASE_CLIENT_OBJECT client;
    protected Exps exps;

    public IDataBaseHandler(BASE_CLIENT_OBJECT client) {
        this.client = client;
        exps = client.getExps();
    }

    public abstract void loadData();

    public abstract void init_timeseries_to_updater();

    public abstract void insert_data(int sleep);

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

    void load_properties() {

        String q = "SELECT * FROM sagiv.props WHERE stock_id = '%s';";
        String query = String.format(q, client.getName());

        List<Map<String, Object>> rs = MySql.select(query, MySql.JIBE_PROD_CONNECTION);

        for (Map<String, Object> row: rs) {
            String props_name = "";
            Object data = null;

            try {
                props_name = (String) row.get("prop");
                data = row.get("data");

                System.out.println(props_name + "  " + data);

                client.getProps().getMap().get(props_name).setData(data);

            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println(data.toString() + " Prop name= " + props_name);
            }
        }
    }

//    void insert_properties() {
//        // Query
//        String q = "INSERT INTO sagiv.props (stock_id, prop, data) VALUES ('%s', '%s', '%s');";
//
//        // Get query map
//        HashMap<String, Prop> map = (HashMap<String, Prop>) client.getProps().getMap();
//
//        // For each prop
//        for (Map.Entry<String, Prop> entry : map.entrySet()) {
//            Prop prop = entry.getValue();
//            String query = String.format(q, client.getId_name(), prop.getName(), prop.getData());
//            System.out.println(query);
//            MySql.insert(query);
//        }
//    }

    void update_properties() {
        // Query
        String q = "UPDATE sagiv.props SET data = '%s' WHERE stock_id LIKE '%s' AND prop LIKE '%s';";

        // Get query map
        HashMap<String, Prop> map = (HashMap<String, Prop>) client.getProps().getMap();

        // For each prop
        for (Map.Entry<String, Prop> entry : map.entrySet()) {
            Prop prop = entry.getValue();
            String query = String.format(q, prop.getData(), client.getName(), prop.getName());
            MySql.update(query, MySql.JIBE_PROD_CONNECTION);
        }
    }

    public static void loadSerieData(List<Map<String, Object>> rs, MyTimeSeries timeSeries) {
        for (Map<String, Object> row: rs) {
            try {
                Timestamp timestamp = (Timestamp) row.get("time");

                Object value = row.get("value");
                if (value == null) {
                    continue;
                }

                BigDecimal bigDecimalValue = (BigDecimal) value;

                timeSeries.add(timestamp.toLocalDateTime(), bigDecimalValue.doubleValue());
            } catch (ClassCastException throwables) {
                throwables.printStackTrace();
            }
        }
        timeSeries.setLoad(true);

    }

    public static void insert_batch_data(ArrayList<MyTick> list, String table_location, String connection_type) {
        if (list.size() > 0) {

            // Create the query
            StringBuilder queryBuiler = new StringBuilder("INSERT INTO %s (time, value) VALUES ");
            int last_item_id = list.get(list.size() - 1).hashCode();
            for (MyTick row : list) {
                queryBuiler.append(String.format("(cast('%s' as timestamp with time zone), %s)", row.time, row.value));
                if (row.hashCode() != last_item_id) {
                    queryBuiler.append(",");
                }
            }
            queryBuiler.append(";");

            String q = String.format(queryBuiler.toString(), table_location);

            // Insert
            MySql.insert(q, true, connection_type);

            // Clear the list
            list.clear();
        }
    }


    void insertListRetro(ArrayList<MyTimeStampObject> list, int timseries_id, String connection_type) {
        if (list.size() > 0) {
            // Insert
            MySql.insert(convert_list_to_query(list, timseries_id), connection_type);
        }
    }

    public static double handle_rs(ResultSet rs) {
        while (true) {
            try {
                if (!rs.next()) break;
                return rs.getDouble("value");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return 0;
    }

    protected void load_races(Race_Logic.RACE_RUNNER_ENUM race_runner_enum, int serie_id, boolean r_one_or_two) {
        load_race_points(race_runner_enum, serie_id, true, r_one_or_two);
        load_race_points(race_runner_enum, serie_id, false, r_one_or_two);
    }

    private void load_race_points(Race_Logic.RACE_RUNNER_ENUM race_runner_enum, int serie_id, boolean up_down, boolean r_one_or_two) {
        List<Map<String, Object>> rs;
        if (up_down) {
            rs = Queries.get_races_up_sum(serie_id, MySql.JIBE_PROD_CONNECTION);
        } else {
            rs = Queries.get_races_down_sum(serie_id, MySql.JIBE_PROD_CONNECTION);
        }

        for (Map<String, Object> row: rs) {

            Object value_object = row.get("value");
            double value;

            if (value_object == null) {
                return;
            }

            if (value_object instanceof BigDecimal) {
                 value = ((BigDecimal) value_object).doubleValue();
                // Use doubleValue as needed
            } else {
                value = (double) value_object;
            }


            if (up_down) {
                if (r_one_or_two) {
                    client.getRacesService().get_race_logic(race_runner_enum).setR_one_up_points(value);
                } else {
                    client.getRacesService().get_race_logic(race_runner_enum).setR_two_up_points(value);
                }
            } else {
                if (r_one_or_two) {
                    client.getRacesService().get_race_logic(race_runner_enum).setR_one_down_points(L.abs(value));
                } else {
                    client.getRacesService().get_race_logic(race_runner_enum).setR_two_down_points(L.abs(value));
                }

            }
        }
    }

    public String convert_list_to_query(ArrayList<MyTimeStampObject> list, int timeseries_id) {
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
        return q;
    }

    private void insert_data(ArrayList<MyTick> myTicks, String speed_table_location, String connection_type) {
        IDataBaseHandler.insert_batch_data(myTicks, speed_table_location, connection_type);
    }

    public static ArrayList<MyTick> tick_logic(ArrayList<LocalDateTime> times) {
        ArrayList<MyTick> myTicks = new ArrayList<>();

        for (int i = 1; i < times.size(); i++) {

            long pre_time = Timestamp.valueOf(times.get(i - 1)).getTime();
            long curr_time = Timestamp.valueOf(times.get(i)).getTime();
            long mill = curr_time - pre_time;

            // Add tick to tick list
            myTicks.add(new MyTick(times.get(i), mill));
        }
        return myTicks;
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


    public void load_baskets() {
        try {

            if (client instanceof INDEX_OBJECT) {

                int baskets_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.BASKETS);

                int baskets_up = (int) L.abs(Queries.handle_rs(Queries.get_baskets_up_sum(baskets_id)));
                int baskets_down = (int) L.abs(Queries.handle_rs(Queries.get_baskets_down_sum(baskets_id)));

                ((INDEX_OBJECT)client).getBasketFinder_by_stocks().setBasket_up(baskets_up);
                ((INDEX_OBJECT)client).getBasketFinder_by_stocks().setBasket_down(baskets_down);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load_exp_data() {
        try {
            ExpWeek week = client.getExps().getWeek();
            ExpMonth month = client.getExps().getMonth();

            int baskets_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.BASKETS);
            int last_price_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.LAST_PRICE);
            int df_4_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_4_CDF_OLD);
            int df_8_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_8_CDF_OLD);
            int df_5_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_5_CDF_OLD);
            int df_6_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_6_CDF_OLD);

            double baskets_exp_week = Queries.handle_rs(Queries.get_exp_data(TA35.getInstance(), baskets_id, Props.EXP_WEEK_START, MySql.JIBE_PROD_CONNECTION));
            double baskets_exp_month = Queries.handle_rs(Queries.get_exp_data(TA35.getInstance(), baskets_id, Props.EXP_MONTH_START, MySql.JIBE_PROD_CONNECTION));
            double start_exp_week = Queries.handle_rs(Queries.get_start_exp_mega(last_price_id, TA35.getInstance(), Props.EXP_WEEK_START, MySql.JIBE_PROD_CONNECTION));
            double start_exp_month = Queries.handle_rs(Queries.get_start_exp_mega(last_price_id, TA35.getInstance(), Props.EXP_MONTH_START, MySql.JIBE_PROD_CONNECTION));

            double v4_week = Queries.handle_rs(Queries.get_exp_data_by_candle(TA35.getInstance(), df_4_id, Props.EXP_WEEK_START, MySql.JIBE_PROD_CONNECTION));
            double v8_week = Queries.handle_rs(Queries.get_exp_data_by_candle(TA35.getInstance(), df_8_id, Props.EXP_WEEK_START, MySql.JIBE_PROD_CONNECTION));

            double v5_month = Queries.handle_rs(Queries.get_exp_data_by_candle(TA35.getInstance(), df_5_id, Props.EXP_MONTH_START, MySql.JIBE_PROD_CONNECTION));
            double v6_month = Queries.handle_rs(Queries.get_exp_data_by_candle(TA35.getInstance(), df_6_id, Props.EXP_MONTH_START, MySql.JIBE_PROD_CONNECTION));

            // Start
            week.getExpData().setStart(start_exp_week);
            month.getExpData().setStart(start_exp_month);

            // Baskets
            week.getExpData().setBaskets((int) baskets_exp_week);
            month.getExpData().setBaskets((int) baskets_exp_month);

            // DF
            // Week
            week.getExpData().setV4(v4_week);
            week.getExpData().setV8(v8_week);

            // Month
            month.getExpData().setV5(v5_month);
            month.getExpData().setV6(v6_month);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> get_exp_data(String target_table_location, String exp, int result_type) {
        String q = "";

        // Serie
        if (result_type == SERIE_RESULT_TYPE) {
            q = "select * " +
                    "from %s where time::date >= (select date from %s where exp_type = '%s');";
        } else if (result_type == SUM_RESULT_TYPE) {
            // Sum
            q = "select sum(value) as value " +
                    "from %s where time::date >= (select date from %s where exp_type = '%s');";
        } else if (result_type == AVG_RESULT_TYPE) {
            q =  // Sum
                    q = "select avg(value) as value " +
                            "from %s where time::date >= (select date from %s where exp_type = '%s');";
        }

        String query = String.format(q, target_table_location, Factories.Tables.EXPS_TABLE, exp.toUpperCase());
        return MySql.select(query, MySql.JIBE_PROD_CONNECTION);
    }

    public Map<Integer, Integer> getSerie_ids() {
        return serie_ids;
    }

    public Map<String, MyTimeSeries> getSeries() {
        return series;
    }
}
