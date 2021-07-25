package dataBase.mySql;

import java.sql.ResultSet;

public class Queries {

    public static ResultSet get_index_serie() {
        String query = "SELECT time, index as value FROM data.ta35_index WHERE time::date = now()::date ORDER BY time;";
        System.out.println(query);
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

}
