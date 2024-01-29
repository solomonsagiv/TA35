package dataBase;

import api.ApiObject;
import charts.myChart.MyTimeSeries;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import exp.ExpMonth;
import exp.ExpWeek;
import locals.L;
import props.Props;
import java.sql.ResultSet;
import java.sql.Timestamp;

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

    public void load_data() {

        ApiObject apiObject = ApiObject.getInstance();

        // Exp
        load_exp_data(apiObject);

        // Today
        load_today_data(apiObject);

        // Set loaded
        apiObject.setDbLoaded(true);
    }

    public void load_today_data(ApiObject apiObject) {
        try {
            int baskets_up = (int) L.abs(Queries.handle_rs(Queries.get_baskets_up_sum(Factories.IDs.BASKETS)));
            int baskets_down = (int) L.abs(Queries.handle_rs(Queries.get_baskets_down_sum(Factories.IDs.BASKETS)));

            apiObject.setBasketUp(baskets_up);
            apiObject.setBasketDown(baskets_down);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void load_exp_data(ApiObject apiObject) {
        try {
            ExpWeek week = apiObject.getExps().getWeek();
            ExpMonth month = apiObject.getExps().getMonth();

            double baskets_exp_week = Queries.handle_rs(Queries.get_exp_data(ApiObject.getInstance(), Factories.IDs.BASKETS, Props.EXP_WEEK_START));
            double baskets_exp_month = Queries.handle_rs(Queries.get_exp_data(ApiObject.getInstance(), Factories.IDs.BASKETS, Props.EXP_MONTH_START));
            double start_exp_week = Queries.handle_rs(Queries.get_start_exp_mega(Factories.IDs.INDEX, ApiObject.getInstance(), Props.EXP_WEEK_START));
            double start_exp_month =  Queries.handle_rs(Queries.get_start_exp_mega(Factories.IDs.INDEX, ApiObject.getInstance(), Props.EXP_MONTH_START));

            double v5_week = Queries.handle_rs(Queries.get_exp_data_by_candle(ApiObject.getInstance(), Factories.IDs.DF_5_old, Props.EXP_WEEK_START));
            double v6_week = Queries.handle_rs(Queries.get_exp_data_by_candle(ApiObject.getInstance(), Factories.IDs.DF_6_old, Props.EXP_WEEK_START));
            double v8_week = Queries.handle_rs(Queries.get_exp_data_by_candle(ApiObject.getInstance(), Factories.IDs.DF_8_DE_CORR, Props.EXP_WEEK_START));

            double v5_month = Queries.handle_rs(Queries.get_exp_data_by_candle(ApiObject.getInstance(), Factories.IDs.DF_5_old, Props.EXP_MONTH_START));
            double v6_month = Queries.handle_rs(Queries.get_exp_data_by_candle(ApiObject.getInstance(), Factories.IDs.DF_6_old, Props.EXP_MONTH_START));
            double v8_month = Queries.handle_rs(Queries.get_exp_data_by_candle(ApiObject.getInstance(), Factories.IDs.DF_8_DE_CORR, Props.EXP_MONTH_START));

            // Start
            week.getExpData().setStart(start_exp_week);
            month.getExpData().setStart(start_exp_month);

            // Baskets
            week.getExpData().setBaskets((int) baskets_exp_week);
            month.getExpData().setBaskets((int) baskets_exp_month);

            // DF
            // Week
            week.getExpData().setV5(v5_week);
            week.getExpData().setV6(v6_week);
            week.getExpData().setV8_de_corr(v8_week);

            // Month
            month.getExpData().setV5(v5_month);
            month.getExpData().setV6(v6_month);
            month.getExpData().setV8_de_corr(v8_month);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ResultSet get_exp_data(String target_table_location, String exp, int result_type) {
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
        return MySql.select(query);
    }

    public static void loadSerieData(ResultSet rs, MyTimeSeries timeSeries) {
        try {
            while (true) {
                if (!rs.next()) break;
                Timestamp timestamp = rs.getTimestamp(1);
                double value = rs.getDouble("value");
                timeSeries.add(timestamp.toLocalDateTime(), value);

//                System.out.println(timeSeries.getName() + " " + timestamp + " " + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
