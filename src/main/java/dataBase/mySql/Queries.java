package dataBase.mySql;

import api.ApiObject;
import dataBase.DataBaseHandler;
import dataBase.Factories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Queries {

    public static final int START_OF_THE_DAY_MIN = 600;

    public static ResultSet get_serie(String table_location) {
        String q = "SELECT * FROM %s where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) ORDER BY time;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_serie(String table_location, String filter) {
        String q = "SELECT * FROM %s where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) and %s ORDER BY time;";
        String query = String.format(q, table_location, filter);
        return MySql.select(query);
    }

    public static ResultSet get_serie(String table_location, int step_second) {
        String modulu = "%";
        String q = "select * " +
                "from ( " +
                "SELECT *, row_number() over (order by time) as row " +
                "FROM %s " +
                "where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) " +
                ") a " +
                "where row %s %s = 0;";
        String query = String.format(q, table_location, modulu, step_second);
        return MySql.select(query);
    }

    public static ResultSet get_serie_cumulative_sum(String table_location) {
        String q = "select time, sum(value) over (ORDER BY time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value " +
                "from %s where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) ORDER BY time;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_serie_cumulative_sum(String table_location, int step_second) {
        String modulu = "%";
        String q = "select * " +
                "from ( " +
                "select time, sum(value) over (ORDER BY time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value, row_number() over ( ORDER BY time ) as row " +
                "from %s " +
                "where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)) a " +
                "where row %s %s = 0;";
        String query = String.format(q, table_location, modulu, step_second);
        return MySql.select(query);
    }

    public static ResultSet ta35_op_avg_with_bid_ask(String fut_table, int min, int step_seconds) {
        String modulu = "%";
        String q = "select * " +
                "from ( " +
                "select i.time, " +
                "avg(f.futures - ((i.bid + i.ask) / 2)) " +
                "over (ORDER BY i.time RANGE BETWEEN INTERVAL '%s min' PRECEDING AND CURRENT ROW) as value, " +
                "row_number() over (order by i.time)                                              as row " +
                "from data.ta35_index i " +
                "inner join %s f on i.time = f.time " +
                "where bid is not null and ask is not null " +
                "and i.time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)) a " +
                "where row %s %s = 0;";
        String query = String.format(q, min, fut_table, modulu, step_seconds);
        return MySql.select(query);
    }

    public static ResultSet get_op_avg_last_x_rows(String table_location, int rows) {
        String q = "select avg(futures - ((bid + ask ) / 2)) as value\n" +
                "from (\n" +
                "select *\n" +
                "              from data.ta35_index i\n" +
                "                       inner join %s f on f.time = i.time\n" +
                "                where i.bid is not null and i.ask is not null\n" +
                "              order by i.time desc limit %s) a;";
        String query = String.format(q, table_location, rows);
        return MySql.select(query);
    }

    public static ResultSet op_avg_continue_serie(String table_location, int rows, int step_count) {
        String modulu = "%";
        String q = "select *\n" +
                "from (\n" +
                "         select time,\n" +
                "                avg(op) over (ORDER BY row RANGE BETWEEN %s PRECEDING AND CURRENT ROW) as value,\n" +
                "                row\n" +
                "         from (\n" +
                "                  select i.time                              as time,\n" +
                "                         f.futures - ((i.bid + i.ask) / 2)   as op,\n" +
                "                         row_number() over (order by i.time) as row\n" +
                "                  from data.ta35_index i\n" +
                "                           inner join %s f on i.time = f.time\n" +
                "                  where i.time >= (select date_trunc('day', time)\n" +
                "                                   from data.ta35_index\n" +
                "                                   where date_trunc('day', time) < date_trunc('day', now())\n" +
                "                                   group by date_trunc('day', time)\n" +
                "                                   order by date_trunc('day', time) desc\n" +
                "                                   limit 1)\n" +
                "                    and bid is not null\n" +
                "                    and ask is not null) a\n" +
                "         ) a\n" +
                "where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "and row %s %s = 0;";
        String query = String.format(q, rows, table_location, modulu, step_count);
        return MySql.select(query);
    }

    public static ResultSet get_serie_cumulative_avg(String table_location) {
        String q = "select time, avg(value) over (ORDER BY time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value " +
                "from %s where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) ORDER BY time;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_serie_cumulative_avg(String table_location, int min) {
        String q = "select time, avg(value) over (order by i.time range between '%s min' preceding and current row) as value " +
                "from %s where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) ORDER BY time;";
        String query = String.format(q, table_location, min);
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
        String q = "select avg(f.futures - ((i.ask + i.bid) / 2)) as value " +
                "from %s i " +
                "inner join %s f on i.time = f.time " +
                "where i.time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);";

        String query = String.format(q, Factories.Tables.INDEX_TABLE, fut_table_location);
        return MySql.select(query);
    }

    public static ResultSet op_avg_cumulative(String index_table, String fut_table, int min) {
        String query = String.format("select i.time as time, avg(f.value - i.value) over (order by i.time range between '%s min' preceding and current row ) as value " +
                "from %s i " +
                "inner join %s f on i.time = f.time " +
                "where i.time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);", min, index_table, fut_table);
        return MySql.select(query);
    }

    public static ResultSet get_decision_exp(String exp, String decisio_table_location, int session, int vesrion) {
        String q = "select sum(delta) as value " +
                "from %s " +
                "where session_id = %s " +
                "and version = %s " +
                "and time between date_trunc('day', (select date from sagiv.ta35_exps " +
                "where exp_type = '%s')) and date_trunc('day', now());";
        String query = String.format(q, decisio_table_location, session, vesrion, exp);

        return MySql.select(query);
    }

    public static ResultSet op_avg_cumulative(String index_table, String fut_table) {
        String query = String.format("select i.time as time, avg(f.value - i.value) over (ORDER BY time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value " +
                "from %s i " +
                "inner join %s f on i.time = f.time " +
                "where  i.time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);", index_table, fut_table);
        return MySql.select(query);
    }

    public static ResultSet get_last_record_from_cdf(String table_location) {
        String q = "select time, sum(value) over (order by time) as value " +
                "from %s " +
                "where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) order by time desc limit 1;";

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

    public static ResultSet get_last_record(String table_location) {
        String q = "select * from %s order by time desc limit 1";

        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_last_x_min_record_from_decision_func(String table_location, int session, int version, int min) {
        String q = "select time, sum(delta) over (ORDER BY time RANGE BETWEEN '%s min' PRECEDING AND CURRENT ROW) as value " +
                "from %s " +
                "where version = %s " +
                "and session_id = %s " +
                "and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);";

        String query = String.format(q, min, table_location, version, session);
        return MySql.select(query);
    }

    public static ResultSet get_op_avg(String fut_table_location, int min) {
        String q = "select avg(f.futures - ((i.ask + i.bid) / 2)) as value " +
                "from %s i " +
                "inner join %s f on i.time = f.time " +
                "where i.time > now() - interval '%s min' and bid is not null and ask is not null;";
        String query = String.format(q, Factories.Tables.INDEX_TABLE, fut_table_location, min);
        return MySql.select(query);
    }

    public static ResultSet get_serie_avg_today(String table_location) {
        String q = "select avg(value) as value " +
                "from %s where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_serie_sum_today(String table_location) {
        String q = "select sum(value) as value " +
                "from %s where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);";
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
                "where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) " +
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
                "where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) " +
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
                "from %s where value = 1 and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_baskets_down_sum(String table_location) {
        String q = "select sum(value) as value " +
                "from %s where value = -1 and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_exp_decision_function(int session, int version, String exp, int steps) {
        String modulu = "%";
        String q = "select * " +
                "from (select time, " +
                "sum(delta) over (ORDER BY time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value, " +
                "row_number() over (order by time) as row " +
                "from %s " +
                "where session_id = %s " +
                "and version = %s " +
                "and time between date_trunc('day', (select date " +
                "from sagiv.ta35_exps " +
                "where exp_type = '%s')) and date_trunc('day', now())) a " +
                "where row %s %s = 0;";
        String query = String.format(q, Factories.Tables.DF_TABLE, session, version, exp, modulu, steps);
        return MySql.select(query);
    }

    public static ResultSet get_exp_serie(String table_location, String exp, int steps) {
        String modulu = "%";
        String q = "select * " +
                "from ( " +
                "select time, index as value, row_number() over (order by time) as row " +
                "from %s " +
                "where time between date_trunc('day', (select date " +
                "from sagiv.ta35_exps " +
                "where exp_type = '%s')) and date_trunc('day', now())) a " +
                "where row %s %s = 0;";
        String query = String.format(q, table_location, exp, modulu, steps);
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
                "from ta35_options_status " +
                "where exp = '%s';";
        String query = String.format(q, Factories.Tables.SAGIV_OPTIONS_STATUS_TABLE, exp_name);
        return MySql.select(query);
    }

    public static ResultSet get_index_with_bid_ask_series(int step_second) {
        String modulu = "%";
        String q = "select * " +
                "from ( " +
                "select time, (bid + ask) / 2 as value, row_number() over (order by time) as row " +
                "from data.ta35_index " +
                "where time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day) " +
                "order by time) a " +
                "where row %s %s = 0;";
        String query = String.format(q, modulu, step_second);
        return MySql.select(query);
    }


    public static class Filters {
        public static final String TIME_BIGGER_THAN_10 = "time::time > time'10:00:00'";
    }

}