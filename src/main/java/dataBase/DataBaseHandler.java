package dataBase;

import api.ApiObject;
import charts.myChart.MyTimeSeries;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import exp.ExpMonth;
import exp.ExpWeek;
import locals.L;
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
            int baskets_up = (int) L.abs(Queries.handle_rs(Queries.get_baskets_up_sum(Factories.Tables.BASKETS_TABLE)));
            int baskets_down = (int) L.abs(Queries.handle_rs(Queries.get_baskets_down_sum(Factories.Tables.BASKETS_TABLE)));
            int index_delta = (int) L.abs(Queries.handle_rs(Queries.get_serie_sum_today(Factories.Tables.INDEX_DELTA_TABLE)));

            apiObject.getStocksHandler().setDelta(index_delta);
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

            double baskets_exp_week = Queries.handle_rs(get_exp_data(Factories.Tables.BASKETS_TABLE, EXP_WEEK, SUM_RESULT_TYPE));
            double baskets_exp_month = Queries.handle_rs(get_exp_data(Factories.Tables.BASKETS_TABLE, EXP_MONTH, SUM_RESULT_TYPE));
            double start_exp_week = Queries.handle_rs(Queries.get_start_exp(EXP_WEEK));
            double start_exp_month = Queries.handle_rs(Queries.get_start_exp(EXP_MONTH));
            double ind_delta_week = Queries.handle_rs(get_exp_data(Factories.Tables.INDEX_DELTA_TABLE, EXP_WEEK, SUM_RESULT_TYPE));
            double ind_delta_month = Queries.handle_rs(get_exp_data(Factories.Tables.INDEX_DELTA_TABLE, EXP_MONTH, SUM_RESULT_TYPE));
            double v4_exp = Queries.handle_rs(Queries.get_decision_exp(EXP_WEEK, Factories.Tables.DF_TABLE, 2, 4));
            double v8_exp = Queries.handle_rs(Queries.get_decision_exp(EXP_WEEK, Factories.Tables.DF_TABLE, 2, 8));
            double v5_exp = Queries.handle_rs(Queries.get_decision_exp(EXP_MONTH, Factories.Tables.DF_TABLE, 2, 5));
            double v6_exp = Queries.handle_rs(Queries.get_decision_exp(EXP_MONTH, Factories.Tables.DF_TABLE, 2, 6));
            
            week.getExpData().setStart(start_exp_week);
            month.getExpData().setStart(start_exp_month);
            week.getExpData().setIndDelta(ind_delta_week);
            month.getExpData().setIndDelta(ind_delta_month);
            week.getExpData().setBaskets((int) baskets_exp_week);
            month.getExpData().setBaskets((int) baskets_exp_month);
            week.getExpData().setV4(v4_exp);
            week.getExpData().setV8(v8_exp);
            month.getExpData().setV5(v5_exp);
            month.getExpData().setV6(v6_exp);

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
