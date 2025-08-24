package dataBase.mySql;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import dataBase.IDataBaseHandler;
import miniStocks.MiniStock;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Queries {

    public static final int START_OF_THE_DAY_MIN = 600;
    public static final int step_second = 10;
    public static final String RAW = "RAW";
    public static final String AVG_TODAY = "AVG_TODAY";
    public static final String CDF = "CDF";
    public static final String RAW_NO_MODULU = "RAW_NO_MODULU";
    public static final String BY_ROWS = "BY_ROWS";
    public static final String BY_TIME = "BY_TIME";
    public static final String FROM_TODAY = "FROM_TODAY";

    public static List<Map<String, Object>> get_start_exp_mega(int index_id, TA35 TA35, String exp_prop_name, String connectionType) {
        String q = "WITH week_start_date AS (\n" +
                "  SELECT data::date AS start_date\n" +
                "  FROM sagiv.props\n" +
                "  WHERE stock_id = '%s'\n" +
                "    AND prop = '%s'\n" +
                ")\n" +
                "SELECT value\n" +
                "FROM ts.timeseries_data\n" +
                "WHERE timeseries_id = %s\n" +
                "  AND time >= (SELECT start_date FROM week_start_date)\n" +
                "  AND time < (SELECT start_date FROM week_start_date) + interval '1 day'\n" +
                "  AND value IS NOT NULL\n" +
                "ORDER BY time\n" +
                "LIMIT 10;\n";

        String query = String.format(q, TA35.getName(), exp_prop_name, index_id);
        return MySql.select(query, connectionType);
    }

    public static List<Map<String, Object>> get_exp_data_by_candle(TA35 client, int serie_id, String exp_prop_name, String connectionType) {
        String q = "select sum(sum) as value\n" +
                "from ts.ca_timeseries_1day_candle\n" +
                "where date_trunc('day', time) >= (select data::date as date\n" +
                "                                  from props\n" +
                "                                  where stock_id = '%s'\n" +
                "                                    and prop = '%s')\n" +
                " and date_trunc('day', time) < date_trunc('day', now())\n" +
                "  and timeseries_id = %s;";
        String query = String.format(q, client.getName(), exp_prop_name, serie_id);
        return MySql.select(query, connectionType);
    }

    public static List<Map<String, Object>> get_exp_data(TA35 client, int serie_id, String exp_prop_name, String connectionType) {
        String q = "select sum(sum) as value\n" +
                "from ts.ca_timeseries_1min_candle\n" +
                "where date_trunc('day', time) >= (select data::date as date\n" +
                "                                  from props\n" +
                "                                  where stock_id = '%s'\n" +
                "                                    and prop = '%s')\n" +
                " and date_trunc('day', time) < date_trunc('day', now())\n " +
                " and timeseries_id = %s;";
        String query = String.format(q, client.getName(), exp_prop_name, serie_id);
        return MySql.select(query, connectionType);
    }

    // -------------------------------------------- Mega tables -------------------------------------------- //

    public static List<Map<String, Object>> get_last_record_mega(int serie_id, String type, String connectionType) {
        switch (type) {
            case RAW:
                return get_last_raw_record_mega(serie_id, connectionType);
            case CDF:
                return get_last_cdf_record_mega(serie_id, connectionType);
        }
        return null;
    }

    private static List<Map<String, Object>> get_last_raw_record_mega(int serie_id, String connectionType) {
        String q = "select *\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "and %s %s;";

        String query = String.format(q, serie_id, Filters.TODAY, Filters.ORDER_BY_TIME_DESC_LIMIT_1);
        return MySql.select(query, connectionType);
    }

    private static List<Map<String, Object>> get_last_cdf_record_mega(int serie_id, String connectionType) {
        String q = "select sum(value) as value\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "and %s;";

        String query = String.format(q, serie_id, Filters.TODAY);
        return MySql.select(query, connectionType);
    }

    public static List<Map<String, Object>> get_serie_mega_table(int serie_id, String type, String connectionType) {
        switch (type) {
            case RAW:
                return get_serie_raw_mega_table(serie_id, connectionType);
            case CDF:
                return get_serie_cdf_mega_table(serie_id, connectionType);
            case RAW_NO_MODULU:
                return get_serie_raw_no_modulo_mega_table(serie_id, connectionType);
        }
        return null;
    }

    private static List<Map<String, Object>> get_serie_raw_mega_table(int serie_id, String connectionType) {

        String modulu = "%";

        String q = "select time, value\n" +
                "from (\n" +
                "         select time, value, row_number() over (order by time) as row\n" +
                "         from %s\n" +
                "         where timeseries_id = %s\n" +
                "           and %s) a\n" +
                "where row %s %s = 0;";

        String query = String.format(q, "ts.timeseries_data", serie_id, Filters.TODAY, modulu, step_second);
        return MySql.select(query, connectionType);
    }

    private static List<Map<String, Object>> get_serie_raw_no_modulo_mega_table(int serie_id, String connectionType) {

        String modulu = "%";

        String q = "select time, value\n" +
                "from (\n" +
                "         select time, value, row_number() over (order by time) as row\n" +
                "         from %s\n" +
                "         where timeseries_id = %s\n" +
                "           and %s) a";

        String query = String.format(q, "ts.timeseries_data", serie_id, Filters.TODAY);
        return MySql.select(query, connectionType);
    }


    public static List<Map<String, Object>> get_cumulative_avg_serie(int serie_id, int min, String connectionType) {

        String modulu = "%";

        String q = "select time, value\n" +
                "from (\n" +
                "         select time, avg(value) over (ORDER BY time RANGE BETWEEN '%s min' PRECEDING AND CURRENT ROW) as value, row_number() over (order by time) as row\n" +
                "         from ts.timeseries_data\n" +
                "         where timeseries_id = %s\n" +
                "           and %s) a\n" +
                "where row %s %s = 0;";

        String query = String.format(q, min, serie_id, Filters.TODAY, modulu, step_second);
        return MySql.select(query, connectionType);
    }

    public static List<Map<String, Object>> get_serie_moving_avg(int serie_id, int min, String connectionType) {
        String q = "select avg(value) as value\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "and time > now() - interval '%s min';";

        String query = String.format(q, serie_id, min);
        return MySql.select(query, connectionType);
    }

    private static List<Map<String, Object>> get_serie_cdf_mega_table(int serie_id, String connectionType) {
        String q = "select time, sum(sum) OVER (ORDER BY time RANGE BETWEEN INTERVAL '10 hours' PRECEDING AND CURRENT ROW) AS value\n" +
                "from %s\n" +
                "where timeseries_id = %s\n" +
                "  and %s;";

        String query = String.format(q, "ts.ca_timeseries_1min_candle", serie_id, Filters.TODAY);
        return MySql.select(query, connectionType);
    }

    public static double handle_rs(List<Map<String, Object>> rs) {
        for (Map<String, Object> row : rs) {
            try {
                Number value = (Number) row.get("value");
                if (value == null) {
                    return 0;
                }
                return value.doubleValue();
            } catch (ClassCastException throwables) {
                throwables.printStackTrace();
            }
        }
        return 0;
    }

    public static List<Map<String, Object>> get_arik_sessions(String connection_type) {
        String query = "select * \n" +
                "from sagiv.arik_sessions;";
        return MySql.select(query, connection_type);
    }

    public static List<Map<String, Object>> get_baskets_up_sum(int serie_id) {
        String q = "select sum(value) as value " +
                "from ts.timeseries_data where timeseries_id = %s and value = 1 and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);";
        String query = String.format(q, serie_id);
        return MySql.select(query, MySql.JIBE_PROD_CONNECTION);
    }

    public static List<Map<String, Object>> get_baskets_down_sum(int serie_id) {
        String q = "select sum(value) as value " +
                "from ts.timeseries_data where timeseries_id = %s and value = -1 and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day);";
        String query = String.format(q, serie_id);
        return MySql.select(query, MySql.JIBE_PROD_CONNECTION);
    }

    public static HashMap<String, Integer> get_bounds(BASE_CLIENT_OBJECT client, String title, String connectionType) {

        String query = String.format("SELECT * FROM sagiv.bounds WHERE stock_name = '%s' and item_name = '%s';", client.getName(), title);
        List<Map<String, Object>> rs = MySql.select(query, connectionType);

        Number x = 100, y = 100, width = 300, height = 300;

        for (Map<String, Object> row : rs) {
            try {
                x = (Number) row.get("x");
                y = (Number) row.get("y");
                width = (Number) row.get("width");
                height = (Number) row.get("height");

            } catch (ClassCastException throwables) {
                throwables.printStackTrace();
            }
        }

        HashMap<String, Integer> map = new HashMap<>();
        map.put(IDataBaseHandler.x, x.intValue());
        map.put(IDataBaseHandler.y, y.intValue());
        map.put(IDataBaseHandler.width, width.intValue());
        map.put(IDataBaseHandler.height, height.intValue());

        return map;
    }

    public static List<Map<String, Object>> get_index_with_bid_ask_series(int bid_id, int ask_id, String connectionType) {
        String modulu = "%";
        String q = "select time, value\n" +
                "from (\n" +
                "         select a.time, (a.value + b.value) / 2 as value, row_number() over (order by a.time) as row\n" +
                "         from (\n" +
                "                  select *\n" +
                "                  from ts.timeseries_data\n" +
                "                  where timeseries_id = %s\n" +
                "              ) a\n" +
                "                  inner join (select * from ts.timeseries_data where timeseries_id = %s) b on a.time = b.time\n" +
                "         where a.time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "         order by a.time) big\n" +
                "where row %s %s = 0;";
        String query = String.format(q, bid_id, ask_id, modulu, step_second);
        return MySql.select(query, connectionType);
    }

    public static List<Map<String, Object>> get_races_up_sum(int serie_id, String connection_type) {
        String q = "select sum(value) as value\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "  and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "and value > 0;";
        String query = String.format(q, serie_id);
        return MySql.select(query, connection_type);
    }

    public static List<Map<String, Object>> get_races_down_sum(int serie_id, String connection_type) {
        String q = "select sum(value) as value\n" +
                "from ts.timeseries_data\n" +
                "where timeseries_id = %s\n" +
                "  and time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "and value < 0;";
        String query = String.format(q, serie_id);
        return MySql.select(query, connection_type);
    }

    public static List<Map<String, Object>> get_races_margin_r1_minus_r2(int r_one_id, int r_two_id, String connectionType) {
        String q = "select a.time, sum(a.value) OVER (ORDER BY a.time RANGE BETWEEN INTERVAL '10 hours' PRECEDING AND CURRENT ROW) AS value\n" +
                "from (\n" +
                "with r_one_race as (\n" +
                "    select *\n" +
                "    from ts.ca_timeseries_1min_candle\n" +
                "    where timeseries_id = %s\n" +
                "      and date_trunc('day', time) between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "),\n" +
                "     r_two_race as (\n" +
                "         select *\n" +
                "         from ts.ca_timeseries_1min_candle\n" +
                "         where timeseries_id = %s\n" +
                "           and date_trunc('day', time) between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)\n" +
                "     )\n" +
                "select r_one_race.time as time, r_one_race.sum - r_two_race.sum as value\n" +
                "from r_one_race\n" +
                "         inner join r_two_race on r_one_race.time = r_two_race.time) a;";
        String query = String.format(q, r_one_id, r_two_id);
        return MySql.select(query, connectionType);
    }

    // Load the last snapshot of each stock into the list of stocks
    public static void loadLastSnapshotStocksData(List<MiniStock> stocks, String connectionType) throws SQLException {

        TA35 client  = TA35.getInstance();

        String sql = String.format("SELECT *\n" +
                "FROM sagiv.stocks_data\n" +
                "WHERE index_name = '%s'\n" +
                "  AND snapshot_time = (\n" +
                "    SELECT MAX(snapshot_time)\n" +
                "    FROM sagiv.stocks_data\n" +
                "    WHERE index_name = '%s'\n" +
                ");", client.getName().toUpperCase(), client.getName().toUpperCase());

        List<Map<String, Object>> rs = MySql.select(sql, connectionType);

        for (Map<String, Object> row : rs) {
            try {
                String name = (String) row.get("name");
                Number counter = (Number) row.get("counter");

                for (MiniStock stock : stocks) {
                    System.out.println("My stocks = " + stock.getName());
                    System.out.println("Database stock = " + name);

                    if (stock.getName().equals(name)) {
                        System.out.println("Found  -------------------- ");
                        stock.setBid_ask_counter((Integer) counter);
                        break;
                    }
                }
            } catch (ClassCastException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    // Insert all stocks (no id column) using a single multi-row INSERT string,
// same timestamp for all rows (captured once).
    public static void insertStocksSnapshot(List<MiniStock> stocks, String connection_type) {
        // Fixed timestamp for all rows (UTC ISO-8601)
        String ts = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).toString(); // e.g. 2025-08-11T12:34:56Z

        if (stocks == null || stocks.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO sagiv.stocks_data (name, price, weight, counter, snapshot_time, index_name) VALUES ");

        for (int i = 0; i < stocks.size(); i++) {
            MiniStock s = stocks.get(i);

            String nameLit = "'" + sqlEscape(s.getName()) + "'";
            String priceLit = formatNum(s.getLast());    // uses Locale.US, e.g. 1234.560000
            String weightLit = formatNum(s.getWeight());
            String counterLit = Integer.toString(s.getBid_ask_counter());
            String tsLit = "'" + sqlEscape(ts) + "'::timestamptz";

            // constant index name (change if you have per-stock index)
            String indexName = "TA35";
            String indexNameLit = "'" + sqlEscape(indexName) + "'";

            sb.append("(")
                    .append(nameLit).append(", ")
                    .append(priceLit).append(", ")
                    .append(weightLit).append(", ")
                    .append(counterLit).append(", ")
                    .append(tsLit).append(", ")
                    .append(indexNameLit)
                    .append(")");

            if (i < stocks.size() - 1) sb.append(", ");
        }
        sb.append(";");

        String sql = sb.toString();

        // Log + execute
        System.out.println("Executed SQL: " + sql);
        MySql.insert(sql, connection_type);

    }

    /**
     * Helpers
     **/
    private static String sqlEscape(String s) {
        if (s == null) return "";
        // Trim & remove hidden RTL marks that may arrive from Excel/JDDE
        s = s.trim().replaceAll("[\\u200F\\u200E\\u202A-\\u202E]", "");
        return s.replace("'", "''");
    }

    // Ensure dot-decimal regardless of locale
    private static String formatNum(double d) {
        return java.lang.String.format(java.util.Locale.US, "%.6f", d);
    }

    public static class Filters {
        public static final String TIME_BIGGER_THAN_10 = "time::time > time'10:00:00'";
        public static final String ONE_OR_MINUS_ONE = "(value = 1 or value = -1)";
        public static final String BIGGER_OR_SMALLER_10K = "(value < 10000 or value > -10000)";
        public static final String TODAY = "time between date_trunc('day', now()) and date_trunc('day', now() + interval '1' day)";
        public static final String ORDER_BY_TIME = "order by time";
        public static final String ORDER_BY_TIME_DESC = "order by time desc";
        public static final String ORDER_BY_TIME_DESC_LIMIT_1 = "order by time desc limit 1";
    }

}