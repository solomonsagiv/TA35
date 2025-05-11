package dataBase;

import api.TA35;
import charts.myChart.MyTimeSeries;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import exp.ExpMonth;
import exp.ExpWeek;
import locals.L;
import props.Props;
import races.Race_Logic;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class DataBaseHandler {

    public static final int SERIE_RESULT_TYPE = 0;
    public static final int SUM_RESULT_TYPE = 1;
    public static final int AVG_RESULT_TYPE = 2;
    public static final String EXP_WEEK = "WEEK";
    public static final String EXP_MONTH = "MONTH";
    public static final String x = "x";
    public static final String y = "y";
    public static final String width = "width";
    public static final String height = "height";

    TA35 client;

    public DataBaseHandler(TA35 client) {
        this.client = client;
    }

    public void load_data() {

        client = TA35.getInstance();

        // Exp
        load_exp_data();

        // Today
        load_today_data();

        // Load races
        load_all_races();

        // Set loaded
        client.setDb_loaded(true);
    }

    private void load_all_races() {
        int index_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX_RACES_WI);
        int week_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_RACES_WI);
        int month_races_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_RACES_WM);

        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, index_races_id, true);
        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX, week_races_id, false);
        load_races(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH, month_races_id, true);
    }

    public void load_today_data() {
        try {
            int baskets_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.BASKETS);

            int baskets_up = (int) L.abs(Queries.handle_rs(Queries.get_baskets_up_sum(baskets_id)));
            int baskets_down = (int) L.abs(Queries.handle_rs(Queries.get_baskets_down_sum(baskets_id)));

            client.getBasketFinder_by_stocks().setBasket_up(baskets_up);
            client.getBasketFinder_by_stocks().setBasket_down(baskets_down);
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

    public static void loadSerieData(List<Map<String, Object>> rs, MyTimeSeries timeSeries) {
        for (Map<String, Object> row: rs) {
            try {
                Timestamp timestamp = (Timestamp) row.get("time");

                Object value = row.get("value");
                if (value == null) {
                    continue;
                }

                BigDecimal bigDecimalValue = (BigDecimal) value;

                System.out.println(bigDecimalValue.doubleValue() + "  " + timeSeries.getName());
                timeSeries.add(timestamp.toLocalDateTime(), bigDecimalValue.doubleValue());
            } catch (ClassCastException throwables) {
                throwables.printStackTrace();
            }
        }
        timeSeries.setLoad(true);
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
}
