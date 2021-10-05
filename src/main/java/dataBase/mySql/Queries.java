package dataBase.mySql;

import api.ApiObject;
import dataBase.DataBaseHandler;
import dataBase.Factories;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Queries {

    public static ResultSet get_serie(String table_location) {
        String q = "SELECT * FROM %s where time::date = now()::date ORDER BY time;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_serie(String table_location, String filter) {
        String q = "SELECT * FROM %s where time::date = now()::date and %s ORDER BY time;";
        String query = String.format(q, table_location, filter);
        return MySql.select(query);
    }

    public static ResultSet get_serie_cumulative(String table_location) {
        String q = "select time, sum(value) over (ORDER BY time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value " +
                "from %s where time::date = now()::date ORDER BY time;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_start_exp(String exp) {
        String q = "select value " +
                "from %s " +
                "where (select date from sagiv.ta35_exps where exp_type = '%s') = time::date order by time limit 1;";
        String query = String.format(q, Factories.Tables.SAGIV_INDEX_TABLE, exp);
        return MySql.select(query);
    }

    public static ResultSet get_op_avg(String fut_table_location) {
        String q = "select avg(f.value - i.value) as value " +
                "from %s f " +
                "inner join %s i on f.time = i.time " +
                "where i.time::date = now()::date;";

        String query = String.format(q, fut_table_location, Factories.Tables.SAGIV_INDEX_TABLE);
        return MySql.select(query);
    }

    public static ResultSet get_last_record_from_cdf(String table_location) {
        String q = "select time, sum(value) over (order by time) as value " +
                "from %s " +
                "where time::date = now()::date order by time desc limit 1;";

        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_last_record_from_decision_func(String table_location, int session, int version) {
        String q = "select sum(delta) as value " +
                "from %s " +
                "where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) and session_id = %s and version = %s;";

        String query = String.format(q, table_location, session, version);
        return MySql.select(query);
    }

    public static ResultSet get_op_avg(String fut_table_location, int min) {
        String q = "select avg(f.value - i.value) as value " +
                "from %s i " +
                "inner join %s f on i.time = f.time " +
                "where i.time > i.time - interval '%s min';";

        String query = String.format(q, Factories.Tables.SAGIV_INDEX_TABLE, fut_table_location, min);
        return MySql.select(query);
    }

    public static ResultSet get_serie_avg_today(String table_location) {
        String q = "select avg(value) as value " +
                "from %s where time::date = now()::date;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_serie_sum_today(String table_location) {
        String q = "select sum(value) as value " +
                "from %s where time::date = now()::date;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_serie_avg(String table_location, int min) {
        String q = "select avg(value) as value " +
                "from %s " +
                "where time > now() - interval '%s min' order by time;";
        String query = String.format(q, table_location, min);
        return MySql.select(query);
    }

    public static ResultSet get_serie_avg_from_cdf(String table_location, int min) {
        String q = "select avg(value) as value " +
                "from ( " +
                "select time, sum(value) over (ORDER BY t.time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value " +
                "from %s t " +
                "where time::date = now()::date " +
                ") cumu " +
                "where time > now() - interval '%s min';";
        String query = String.format(q, table_location, min);
        return MySql.select(query);
    }

    public static ResultSet get_serie_avg_from_cdf(String table_location) {
        String q = "select avg(value) as value " +
                "from ( " +
                "select time, sum(value) over (ORDER BY t.time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value " +
                "from %s t " +
                "where time::date = now()::date " +
                ") cumu";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static String insert(String table_location, double value) {
        String q = "INSERT INTO %s (time, value) VALUES ('now()', %s)";
        String query = String.format(q, table_location, value);
        return query;
    }

    public static ResultSet get_baskets_up_sum(String table_location) {
        String q = "select sum(value) as value " +
                "from %s where value = 1 and time::date = now()::date;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_baskets_down_sum(String table_location) {
        String q = "select sum(value) as value " +
                "from %s where value = -1 and time::date = now()::date;";
        String query = String.format(q, table_location);
        return MySql.select(query);
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

    public static HashMap<String, Integer> get_bounds(String title) {

        ApiObject apiObject = ApiObject.getInstance();

        int width = 300, height = 300, x = 100, y = 100;

        String query = String.format("SELECT * FROM sagiv.bounds WHERE stock_name = '%s' and item_name = '%s';", apiObject.getName(), title);
        ResultSet rs = MySql.select(query);

        while (true) {
            try {
                if (!rs.next()) break;

                x = rs.getInt("x");
                y = rs.getInt("y");
                width = rs.getInt("width");
                height = rs.getInt("height");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            HashMap<String, Integer> map = new HashMap<>();
            map.put(DataBaseHandler.x, x);
            map.put(DataBaseHandler.y, y);
            map.put(DataBaseHandler.width, width);
            map.put(DataBaseHandler.height, height);

            return map;
        }
        return null;
    }

    public static ArrayList<Double> handle_rs_double_list(ResultSet rs) {
        ArrayList<Double> list = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                list.add(rs.getDouble("value"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return list;
    }

    public static void update_options_status(String value, String exp_name) {
        String q = "UPDATE %s SET value = '%s' " +
                "WHERE exp = '%s';";
        String query = String.format(q, Factories.Tables.SAGIV_OPTIONS_STATUS_TABLE, value, exp_name);
        MySql.update(query);
    }

    public static ResultSet get_options_status(String exp_name) {
        String q = "select value " +
                "from %s " +
                "where exp = '%s';";
        String query = String.format(q, Factories.Tables.SAGIV_OPTIONS_STATUS_TABLE, exp_name);
        return MySql.select(query);
    }

    public static class Filters {
        public static final String TIME_BIGGER_THAN_10 = "time::time > time'10:00:00'";
    }

}