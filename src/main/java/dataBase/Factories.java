package dataBase;

public class Factories {

    public static class Tables {
        public static final String SAGIV_INDEX_TABLE = "sagiv.ta35_index";
        public static final String INDEX_TABLE = "data.ta35_index";
        public static final String BASKETS_TABLE = "sagiv.ta35_baskets_cdf";
        public static final String EXPS_TABLE = "sagiv.ta35_exps";
        public static final String SAGIV_FUT_WEEK_TABLE = "sagiv.ta35_future_week";
        public static final String SAGIV_FUT_MONTH_TABLE = "sagiv.ta35_future_month";
        public static final String FUT_WEEK_TABLE = "data.ta35_futures_week";
        public static final String FUT_MONTH_TABLE = "data.ta35_futures";
        public static final String RESEARCH_TABLE = "data.ta35_decision_func";
        public static final String SAGIV_OPTIONS_STATUS_TABLE = "sagiv.ta35_options_status";
        public static final String DF_TABLE = "data.ta35_decision_func";
        public static final String INDEX_BID_ASK_COUNTER = "data.ta35_index_bid_ask_counter";
        public static final String OP_AVG_240_CONTINUE = "data.ta35_op_avg_week_240_continue";
        public static final String OP_AVG_15 = "data.ta35_op_avg_week_15";
        public static final String OP_AVG_60 = "data.ta35_op_avg_week_60";
        public static final String OP_AVG_5 = "data.ta35_op_avg_week_5";
    }

    public static class TimeSeries {
        public static final String INDEX = "INDEX";
        public static final String OP_AVG_WEEK = "OP_AVG";
        public static final String OP_AVG_MONTH = "OP_AVG_MONTH";

        public static final String OP_AVG_WEEK_5 = "OP_AVG_WEEK_5";
        public static final String OP_AVG_WEEK_60 = "OP_AVG_WEEK_60";
        public static final String DF_4_CDF = "DF_4_CDF";
        public static final String DF_8_CDF = "DF_8_CDF";
        public static final String DF_5_CDF = "DF_5_CDF";
        public static final String DF_6_CDF = "DF_6_CDF";
        public static final String DF_2_CDF = "DF_2_CDF";
        public static final String DF_7_CDF = "DF_7_CDF";
        public static final String DF_2_RAW = "DF_2_RAW";
        public static final String DF_7_RAW = "DF_7_RAW";

        public static final String CONTINUE_OP_AVG_WEEK_240 = "YESTERDAY_OP_AVG_WEEK_240";
        public static final String INDEX_WITH_BID_ASK = "INDEX_WITH_BID_ASK";
        public static final String FUTURE_WEEK = "FUTURE_WEEK";
        public static final String FUTURE_MONTH = "FUTURE_MONTH";
    }
    
    public static class IDs {
        public static final int INDEX_TABLE = 5;
        //     public static final int INDEX_DELTA_TABLE = "sagiv.ta35_ind_delta_cdf";
        public static final int BASKETS_TABLE = 1649;
        public static final int FUT_WEEK_TABLE = 23;
        public static final int FUT_MONTH_TABLE = 6;
        public static final int OP_AVG_240_CONTINUE = 1155;
        public static final int OP_AVG_60 = 1153;
        public static final int OP_AVG_5 = 110;
        public static final int DF_2 = 1145;
        public static final int DF_4 = 1147;
        public static final int DF_8 = 1151;
        public static final int DF_5 = 1148;
        public static final int DF_6 = 1149;
        public static final int DF_7 = 1150;
        public static final int OP_AVG_WEEK = 2034;
        public static final int OP_AVG_MONTH = 2035;

//        public static final int OP_AVG_WEEK = ;
//        public static final int OP_AVG_MONTH = ;
    }

}