package dataBase.mySql;

import dataBase.Factories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Queries {

    public static ResultSet get_index_serie() {
        String q = "SELECT time, index as value FROM %s WHERE time::date = now()::date ORDER BY time;";
        String query = String.format(q, Factories.Tables.INDEX_TABLE);
        return MySql.select(query);
    }

    public static ResultSet get_serie(String table_location) {
        String q = "SELECT * FROM %s where time::date = now()::date ORDER BY time;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }

    public static ResultSet get_serie_cumulative(String table_location) {
        String q = "select time, sum(value) over (ORDER BY time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value " +
                "from %s where time::date = now()::date ORDER BY time;";
        String query = String.format(q, table_location);
        return MySql.select(query);
    }
    
    public static ResultSet get_op_avg(String fut_table_location) {
        String q = "select avg(f.futures - i.index) as value " +
                "from %s f " +
                "inner join %s i on f.time = i.time " +
                "where i.time::date = now()::date;";

        String query = String.format(q, fut_table_location, Factories.Tables.INDEX_TABLE);
        return MySql.select(query);
    }

    public static ResultSet get_op_avg(String fut_table_location, int min) {
        String q = "select avg(f.futures - i.index) over (ORDER BY i.time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value " +
                "from %s f " +
                "inner join %s i on f.time = i.time " +
                "where i.time::date = now()::date;";

        String query = String.format(q, fut_table_location, Factories.Tables.INDEX_TABLE);
        return MySql.select(query);
    }


    public static ResultSet get_serie_avg(String table_location) {
        String q = "select avg(value) as value " +
                "from %s;";
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

    public static String insert(String table_location, double value) {
        String q = "INSERT INTO %s (time, value) VALUES ('now()', %s)";
        String query = String.format(q, table_location, value);
        return query;
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



}
