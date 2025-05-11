package dataBase.mySql;

import arik.Arik;
import dataBase.Dev;
import dataBase.Prod;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySql {

    public static final String RAW = "RAW";
    public static final String AVG_TODAY = "AVG_TODAY";
    public static final String CDF = "CDF";
    public static final String BY_ROWS = "BY_ROWS";
    public static final String BY_TIME = "BY_TIME";
    public static final String FROM_TODAY = "FROM_TODAY";
    public static final String JIBE_PROD_CONNECTION = "JIBE_PROD_CONNECTION";
    public static final String JIBE_DEV_CONNECTION = "JIBE_DEV_CONNECTION";

    private static JibeConnectionPool pool;
    private static Statement stmt;

    public static final int step_second = 10;

    // Get connection pool
    public static Connection getConnection(String connection_type) {
        Connection conn = null;
        try {

            // Prod
            if (connection_type.equals(MySql.JIBE_PROD_CONNECTION)) {
                conn = Prod.getInstance().getConnection();
            } else if (connection_type.equals(MySql.JIBE_DEV_CONNECTION)) {
                conn = Dev.getInstance().getConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage(e.getMessage() + "\n" + e.getCause() + " \n" + "Trunticate");
        }
        return conn;
    }

    // Insert
    public static void insert(String query, String connection_type) {

        try (Connection conn = getConnection(connection_type);
             PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage(e.getMessage() + "\n" + e.getCause() + " \n" + "Insert");
        }
        System.out.println(LocalTime.now() + "  " + query);
    }


    // Update
    public static void update(String query, String connection_type) {
        try (Connection conn = getConnection(connection_type);
             PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage(e.getMessage() + "\n" + e.getCause() + " \n" + "Update");
        }
    }



    // Update
    public static List<Map<String, Object>> select(String query, String connection_type) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = getConnection(connection_type);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                // Convert each row into a map of column names to values
                Map<String, Object> row = new HashMap<>();
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    row.put(columnName, rs.getObject(i));
                }
                results.add(row);
            }

            return results;
        } catch (SQLException e) {
            e.printStackTrace();
            Arik.getInstance().sendMessage(e.getMessage() + "\n" + e.getCause() + " \n" + "Select");
        }

        return null;
    }


    public static List<Map<String, Object>> get_races_up_sum(int serie_id, String connectionType) {
        String q = "select sum(value) as value\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "  and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "and value > 0;";
        String query = String.format(q, serie_id);
        return MySql.select(query, connectionType);
    }

    public static List<Map<String, Object>> get_races_down_sum(int serie_id, String connectionType) {
        String q = "select sum(value) as value\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "  and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "and value < 0;";
        String query = String.format(q, serie_id);
        return MySql.select(query, connectionType);
    }


    public static String load_stocks_excel_file_location(String connection_type) {
        String query = "select data\n" +
                "from sagiv.props\n" +
                "where stock_id = 'stocks'\n" +
                "and prop = 'SAPI_EXCEL_FILE_LOCATION';";

        String res = "";

        List<Map<String, Object>> rs = MySql.select(query, connection_type);
        for (Map<String, Object> row : rs) {
            res = (String) row.get("data");
        }
        return res;
    }


    public static List<Map<String, Object>> get_timeseries(String stock_id, String connection_type) {
        String q = "select *\n" +
                "from sagiv.timeserie_table\n" +
                "where stock_id = '%s'";

        String query = String.format(q, stock_id);
        return MySql.select(query, connection_type);
    }

    public static List<Map<String, Object>> op_avg_mega_table(int index_id, int fut_id, String connection_type) {
        String q = "select avg(f.value - i.value) as value\n" +
                "from (\n" +
                "         select *\n" +
                "         from %s\n" +
                "         where timeseries_id = %s\n" +
                "     ) i\n" +
                "         inner join (select * from %s where timeseries_id = %s) f on i.time = f.time\n" +
                "where i.%s;";

        String query = String.format(q, "ts.timeseries_data", index_id, "ts.timeseries_data", fut_id, Filters.TODAY);
        System.out.println(query);
        return MySql.select(query, connection_type);
    }



    public static List<Map<String, Object>> get_serie_moving_avg(int serie_id, int min, String connection_type) {
        String q = "select avg(value) as value\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "and time > now() - interval '%s min';";

        String query = String.format(q, serie_id, min);
        return MySql.select(query, connection_type);
    }


    public static List<Map<String, Object>> get_last_record_mega(int serie_id, String type, String connection_type) {
        switch (type) {
            case RAW:
                return get_last_raw_record_mega(serie_id, connection_type);
            case CDF:
                return get_last_cdf_record_mega(serie_id, connection_type);
        }
        return null;
    }


    public static List<Map<String, Object>> get_transaction(int session_id, String connection_type) {
        String q = "select created_at, position_type, index_value_at_creation, index_value_at_close, close_reason, session_id\n" +
                "from ts.transactions\n" +
                "where session_id = %s\n" +
                "order by created_at desc limit 1;";

        String query = String.format(q, session_id);

        return MySql.select(query, connection_type);
    }

    private static List<Map<String, Object>> get_last_raw_record_mega(int serie_id, String connection_type) {
        String q = "select *\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "and %s %s;";

        String query = String.format(q, serie_id, Filters.TODAY, Filters.ORDER_BY_TIME_DESC_OFFSET_1_LIMIT_1);
        return MySql.select(query, connection_type);
    }

    private static List<Map<String, Object>> get_last_cdf_record_mega(int serie_id, String connection_type) {
        String q = "select sum(value) as value\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "and %s;";

        String query = String.format(q, serie_id, Filters.TODAY);
        return MySql.select(query, connection_type);
    }


    public static List<Map<String, Object>> get_serie_mega_table(int serie_id, String type, int min_from_start, String connection_type) {
        switch (type) {
            case RAW:
                return get_serie_raw_mega_table(serie_id, min_from_start, connection_type);
            case CDF:
                return get_serie_cdf_mega_table(serie_id, min_from_start, connection_type);
        }
        return null;
    }


    private static List<Map<String, Object>> get_serie_raw_mega_table(int serie_id, int min_from_start, String connection_type) {

        String modulu = "%";

        String query;

        String q = "with data as (\n" +
                "    select * , row_number() over (order by time) as row\n" +
                "    from %s\n" +
                "    where timeseries_id = %s\n" +
                "      and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "    order by time\n" +
                "),\n" +
                "     first as (\n" +
                "         select *\n" +
                "         from %s\n" +
                "         where timeseries_id = %s\n" +
                "           and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "         order by time\n" +
                "         limit 1\n" +
                "     )\n" +
                "select data.time as time, data.value as value\n" +
                "from data, first\n" +
                "where data.time > first.time + interval '%s min' and data.row %s %s = 0;\n";

        query = String.format(q, "ts.timeseries_data", serie_id, "ts.timeseries_data", serie_id, min_from_start, modulu, step_second);

        System.out.println(query);

        return MySql.select(query, connection_type);
    }


    private static List<Map<String, Object>> get_serie_cdf_mega_table(int serie_id, int min_from_start, String connection_type) {
        String modulu = "%";

        String query;

        String q = "\n" +
                "with data as (\n" +
                "    select time, sum(value) over (ORDER BY time RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as value, row_number() over (order by time) as row\n" +
                "    from %s\n" +
                "    where timeseries_id = %s\n" +
                "      and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "    order by time\n" +
                "),\n" +
                "     first as (\n" +
                "         select *\n" +
                "         from %s\n" +
                "         where timeseries_id = %s\n" +
                "           and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "         order by time\n" +
                "         limit 1\n" +
                "     )\n" +
                "select data.time as time, data.value as value\n" +
                "from data, first\n" +
                "where data.time > first.time + interval '%s min' and data.row %s %s = 0;\n";

// ts.ca_timeseries_1min_candle
        query = String.format(q, "ts.timeseries_data", serie_id, "ts.timeseries_data", serie_id, min_from_start, modulu, step_second);
        System.out.println(query);

        return MySql.select(query, connection_type);
    }

    public static List<Map<String, Object>> get_last_record(String table_location, String connection_type) {
        String q = "select * from %s %s";
        String query = String.format(q, table_location, Filters.ORDER_BY_TIME_DESC_LIMIT_1);
        return MySql.select(query, connection_type);
    }

    public static void update_prop(String client_name, String prop, String data, String connection_type) {
        String q = "update sagiv.props SET data = '%s' WHERE stock_id = '%s' AND prop = '%s';";
        String query = String.format(q, data, client_name, prop);
        MySql.update(query, connection_type);
    }

    public static List<Map<String, Object>> get_cumulative_avg_serie(int serie_id, int min, String connection_type) {

        String modulu = "%";

        String q = "select time, value\n" +
                "from (\n" +
                "         select time, avg(value) over (ORDER BY time RANGE BETWEEN '%s min' PRECEDING AND CURRENT ROW) as value, row_number() over (order by time) as row\n" +
                "         from ts.timeseries_data\n" +
                "         where timeseries_id = %s\n" +
                "           and %s) a\n" +
                "where row %s %s = 0;";

        String query = String.format(q, min, serie_id, Filters.TODAY, modulu, step_second);
        return MySql.select(query, connection_type);
    }


    public static void insert(String query, boolean thread, String connection_type) {
        if (thread) {
            new Thread(() -> {
                insert(query, connection_type);
            }).start();
        } else {
            insert(query, connection_type);
        }
    }

    // Insert
    public static void insert(String query) {
        new Thread(() -> {
            Connection conn = null;
            try {

                conn = getPool().getConnection();
                stmt = conn.createStatement();

                // Execute
                stmt.execute(query);
                System.out.println(query);
            } catch (Exception e) {
                e.printStackTrace();
                Arik.getInstance().sendMessage(e.getMessage() + "\n" + e.getCause());
            } finally {
                if (conn != null) {
                    // Return connection
                    getPool().releaseConnection(conn);
                }
            }
        }).start();
    }

    public static void insert(String query, boolean thread) {
        if (thread) {
            new Thread(() -> {
                insert(query);
            }).start();
        } else {
            insert(query);
        }
    }

    // Update
    public static void update(String query) {
        Connection conn = null;
        try {
            conn = getPool().getConnection();
            stmt = conn.createStatement();

            // Execute
            stmt.executeUpdate(query);

        } catch (Exception e) {
            Arik.getInstance().sendMessage(e.getMessage() + "\n" + e.getCause());
        } finally {
            if (conn != null) {
                // Return connection
                getPool().releaseConnection(conn);
            }
        }
    }

    public static void trunticate(String tableName) {

        String query = "TRUNCATE TABLE " + "stocks." + tableName;
        Statement st = null;
        Connection conn = null;
        try {

            conn = JibeConnectionPool.getConnectionsPoolInstance().getConnection();
            // create the java statement
            st = conn.createStatement();

            // execute the query, and get a java resultset
            st.executeUpdate(query);

            // Release connection
            getPool().releaseConnection(conn);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            Arik.getInstance().sendErrorMessage(e);
        } finally {
            if (conn != null) {
                // Return connection
                getPool().releaseConnection(conn);
            }
        }

    }

    // Get connection pool
    public static JibeConnectionPool getPool() {
        if (pool == null) {
            pool = JibeConnectionPool.getConnectionsPoolInstance();
        }
        return pool;
    }


    public static class Filters {
        public static final String ONE_OR_MINUS_ONE = "(value = 1 or value = -1)";
        public static final String BIGGER_OR_SMALLER_10K = "(value < 10000 or value > -10000)";
        public static final String TODAY = "time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)";
        public static final String ORDER_BY_TIME = "order by time";
        public static final String ORDER_BY_TIME_DESC = "order by time desc";
        public static final String ORDER_BY_TIME_DESC_LIMIT_1 = "order by time desc limit 1";
        public static final String ORDER_BY_TIME_DESC_OFFSET_1_LIMIT_1 = "order by time desc offset 1 limit 1";
    }

}
