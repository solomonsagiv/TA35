package dataBase;

import api.ApiObject;
import charts.myChart.MyTimeSeries;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;

import java.sql.ResultSet;
import java.sql.SQLException;
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

        double exp_week_delta = Queries.handle_rs(get_exp_data(Factories.Tables.DELTA_WEEK_TABLE, EXP_WEEK, SUM_RESULT_TYPE));
        double exp_month_delta = Queries.handle_rs(get_exp_data(Factories.Tables.DELTA_MONTH_TABLE, EXP_MONTH, SUM_RESULT_TYPE));
        double ind_delta_week = Queries.handle_rs(get_exp_data(Factories.Tables.INDEX_DELTA_TABLE, EXP_WEEK, SUM_RESULT_TYPE));
        double ind_delta_month = Queries.handle_rs(get_exp_data(Factories.Tables.INDEX_DELTA_TABLE, EXP_MONTH, SUM_RESULT_TYPE));
        double baskets_exp_week = Queries.handle_rs(get_exp_data(Factories.Tables.BASKETS, EXP_WEEK, SUM_RESULT_TYPE));
        double baskets_exp_month = Queries.handle_rs(get_exp_data(Factories.Tables.BASKETS, EXP_MONTH, SUM_RESULT_TYPE));
        double start_exp_week = Queries.handle_rs(Queries.get_start_exp(EXP_WEEK));
        double start_exp_month = Queries.handle_rs(Queries.get_start_exp(EXP_MONTH));

        apiObject.getExpWeek().getExpData().setStart(start_exp_week);
        apiObject.getExpMonth().getExpData().setStart(start_exp_month);
        apiObject.getExpWeek().getExpData().setDelta(exp_week_delta);
        apiObject.getExpMonth().getExpData().setDelta(exp_month_delta);
        apiObject.getExpWeek().getExpData().setIndDelta(ind_delta_week);
        apiObject.getExpMonth().getExpData().setIndDelta(ind_delta_month);
        apiObject.getExpWeek().getExpData().setBaskets((int) baskets_exp_week);
        apiObject.getExpMonth().getExpData().setBaskets((int) baskets_exp_month);
        apiObject.getExpWeek().getOptions().load_op_avg(Queries.handle_rs_double_list(Queries.get_op_avg(Factories.Tables.FUT_WEEK_TABLE)));
        apiObject.getExpMonth().getOptions().load_op_avg(Queries.handle_rs_double_list(Queries.get_op_avg(Factories.Tables.FUT_MONTH_TABLE)));
    }

    public ResultSet get_exp_data(String target_table_location, String exp, int result_type) {
        String q = "";

        // Serie
        if (result_type == SERIE_RESULT_TYPE) {
            q = "select * " +
                    "from %s where time::date > (select date from %s where exp_type = '%s');";
        } else if (result_type == SUM_RESULT_TYPE) {
            // Sum
            q = "select sum(value) as value " +
                    "from %s where time::date > (select date from %s where exp_type = '%s');";
        } else if (result_type == AVG_RESULT_TYPE) {
            q =  // Sum
                    q = "select avg(value) as value " +
                            "from %s where time::date > (select date from %s where exp_type = '%s');";
        }

        String query = String.format(q, target_table_location, Factories.Tables.EXPS_TABLE, exp.toUpperCase());
        return MySql.select(query);
    }

    public static void loadSerieData(ResultSet rs, MyTimeSeries timeSeries) {
        while (true) {
            try {
                if (!rs.next()) break;
                Timestamp timestamp = rs.getTimestamp(1);
                double value = rs.getDouble("value");
                timeSeries.add(timestamp.toLocalDateTime(), value);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
