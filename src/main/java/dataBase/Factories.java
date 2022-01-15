package dataBase;

public class Factories {

    public static class Tables {
        public static final String SAGIV_INDEX_TABLE = "sagiv.ta35_index";
        public static final String INDEX_TABLE = "data.ta35_index";
        public static final String SAGIV_DELTA_WEEK_TABLE = "sagiv.ta35_delta_week_2_cdf";
        public static final String SAGIV_DELTA_MONTH_TABLE = "sagiv.ta35_delta_month_2_cdf";
        public static final String BID_ASK_COUNTER_WEEK_TABLE = "sagiv.ta35_bid_ask_counter_fut_week_cdf";
        public static final String BID_ASK_COUNTER_MONTH_TABLE = "sagiv.ta35_bid_ask_counter_fut_month_cdf";
        public static final String INDEX_DELTA_TABLE = "sagiv.ta35_ind_delta_cdf";
        public static final String BASKETS_TABLE = "sagiv.ta35_baskets_cdf";
        public static final String EXPS_TABLE = "sagiv.ta35_exps";
        public static final String SAGIV_FUT_WEEK_TABLE = "sagiv.ta35_future_week";
        public static final String SAGIV_FUT_MONTH_TABLE = "sagiv.ta35_future_month";
        public static final String FUT_WEEK_TABLE = "data.ta35_futures_week";
        public static final String FUT_MONTH_TABLE = "data.ta35_futures";
        public static final String RESEARCH_TABLE = "data.ta35_decision_func";
        public static final String SAGIV_OPTIONS_STATUS_TABLE = "sagiv.ta35_options_status";
        public static final String DELTA_MIX_TABLE = "data.ta35_delta_mix";
        public static final String DF_TABLE = "data.ta35_decision_func";
        public static final String DF_N_SPEED_300 = "data.research_ta35_df5_speed_300";
        public static final String DF_SPEED_300 = "data.research_ta35_df6_speed_300";
        public static final String INDEX_BID_ASK_COUNTER = "data.ta35_index_bid_ask_counter";
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
        public static final String OP_AVG_WEEK_15_SERIE = "OP_AVG_WEEK_15";
        public static final String OP_AVG_MONTH_15_SERIE = "OP_AVG_MONTH_15";
        public static final String BID_ASK_COUNTER_WEEK_SERIE = "BID_ASK_COUNTER_WEEK";
        public static final String BID_ASK_COUNTER_MONTH_SERIE = "BID_ASK_COUNTER_MONTH";
        public static final String BID_ASK_COUNTER_WEEK_AVG_60_SERIE = "BID_ASK_COUNTER_WEEK_AVG_60";
        public static final String BID_ASK_COUNTER_MONTH_AVG_60_SERIE = "BID_ASK_COUNTER_MONTH_AVG_60";
        public static final String DELTA_WEEK_MONTH_SERIE = "DELTA_WEEK_MONTH";
        public static final String DELTA_MIX_SERIE = "DELTA_MIX";
        public static final String DF_V_4_SERIE = "DF_V_4";
        public static final String DF_V_8_SERIE = "DF_V_8";
        public static final String DF_V_5_SERIE = "DF_V_5";
        public static final String DF_V_6_SERIE = "DF_V_6";
        public static final String INDEX_DELTA_SERIE = "INDEX_DELTA";
        public static final String CONTINUE_OP_AVG_WEEK_15_SERIE = "YESTERDAY_OP_AVG_WEEK_15_SERIE";
        public static final String CONTINUE_OP_AVG_MONTH_15_SERIE = "YESTERDAY_OP_AVG_MONTH_15_SERIE";
        public static final String CONTINUE_OP_AVG_WEEK_60_SERIE = "YESTERDAY_OP_AVG_WEEK_60_SERIE";
        public static final String CONTINUE_OP_AVG_WEEK_240_SERIE = "YESTERDAY_OP_AVG_WEEK_240_SERIE";
        public static final String CONTINUE_OP_AVG_MONTH_60_SERIE = "YESTERDAY_OP_AVG_MONTH_60_SERIE";
        public static final String CONTINUE_OP_AVG_MONTH_240_SERIE = "YESTERDAY_OP_AVG_MONTH_240_SERIE";
        public static final String INDEX_WITH_BID_ASK_SERIE = "INDEX_WITH_BID_ASK_SERIE";
    }

}