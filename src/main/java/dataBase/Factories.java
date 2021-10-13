package dataBase;

public class Factories {

    public static class Tables {
        public static final String SAGIV_INDEX_TABLE = "sagiv.ta35_index";
        public static final String SAGIV_DELTA_WEEK_TABLE = "sagiv.ta35_delta_week_2_cdf";
        public static final String SAGIV_DELTA_MONTH_TABLE = "sagiv.ta35_delta_month_2_cdf";
        public static final String BID_ASK_COUNTER_WEEK_TABLE = "sagiv.ta35_bid_ask_counter_fut_week_cdf";
        public static final String BID_ASK_COUNTER_MONTH_TABLE = "sagiv.ta35_bid_ask_counter_fut_month_cdf";
        public static final String INDEX_DELTA_TABLE = "sagiv.ta35_ind_delta_cdf";
        public static final String BASKETS_TABLE = "sagiv.ta35_baskets_cdf";
        public static final String EXPS_TABLE = "sagiv.ta35_exps";
        public static final String SAGIV_FUT_WEEK_TABLE = "sagiv.ta35_future_week";
        public static final String SAGIV_FUT_MONTH_TABLE = "sagiv.ta35_future_month";
        public static final String RESEARCH_TABLE = "data.ta35_decision_func";
        public static final String SAGIV_OPTIONS_STATUS_TABLE = "sagiv.ta35_options_status";
    }

    public static class TimeSeries {
        public static final String INDEX_SERIE = "INDEX_SERIE";
        public static final String DELTA_WEEK_SERIE = "DELTA_WEEK_SERIE";
        public static final String DELTA_MONTH_SERIE = "DELTA_MONTH_SERIE";
        public static final String DELTA_WEEK_AVG_SERIE = "DELTA_WEEK_AVG";
        public static final String DELTA_MONTH_AVG_SERIE = "DELTA_MONTH_AVG";
        public static final String DELTA_WEEK_AVG_60_SERIE = "DELTA_WEEK_AVG_60";
        public static final String DELTA_MONTH_AVG_60_SERIE = "DELTA_MONTH_AVG_60";
        public static final String OP_AVG_WEEK_SERIE = "OP_AVG_WEEK";
        public static final String OP_AVG_MONTH_SERIE = "OP_AVG_MONTH";
        public static final String OP_AVG_WEEK_60_SERIE = "OP_AVG_WEEK_60";
        public static final String OP_AVG_MONTH_60_SERIE = "OP_AVG_MONTH_60";
        public static final String OP_AVG_WEEK_30_SERIE = "OP_AVG_WEEK_30";
        public static final String OP_AVG_MONTH_30_SERIE = "OP_AVG_MONTH_30";
        public static final String BID_ASK_COUNTER_WEEK_SERIE = "BID_ASK_COUNTER_WEEK";
        public static final String BID_ASK_COUNTER_MONTH_SERIE = "BID_ASK_COUNTER_MONTH";
        public static final String BID_ASK_COUNTER_WEEK_AVG_60_SERIE = "BID_ASK_COUNTER_WEEK_AVG_60";
        public static final String BID_ASK_COUNTER_MONTH_AVG_60_SERIE = "BID_ASK_COUNTER_MONTH_AVG_60";
    }

}