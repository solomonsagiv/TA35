package dataBase;

public class Factories {

    public static class Tables {
        public static final String EXPS_TABLE = "sagiv.ta35_exps";
        public static final String SAGIV_OPTIONS_STATUS_TABLE = "sagiv.ta35_options_status";
//        public static final String DF_TABLE = "data.ta35_decision_func";
    }

    public static class TimeSeries {
        public static final String INDEX = "INDEX";
        public static final String BID = "BID";
        public static final String ASK = "ASK";
        public static final String LAST_PRICE = "LAST_PRICE";
        public static final String BASKETS = "BASKETS";

        public static final String INDEX_AVG_3600 = "INDEX_AVG_3600";
        public static final String INDEX_AVG_900 = "INDEX_AVG_900";

        public static final String OP_AVG_WEEK_15 = "OP_AVG_WEEK_15";
        public static final String OP_AVG_WEEK_60 = "OP_AVG_WEEK_60";

        public static final String DF_4_CDF_OLD = "DF_4_CDF";
        public static final String DF_8_CDF_OLD = "DF_8_CDF";
        public static final String DF_5_CDF_OLD = "DF_5_CDF";
        public static final String DF_6_CDF_OLD = "DF_6_CDF";

        public static final String OP_AVG_240_CONTINUE = "OP_AVG_240_CONTINUE";
        public static final String MID_DEV = "MID";
        public static final String FUTURE_WEEK = "FUTURE_WEEK";
        public static final String FUTURE_MONTH = "FUTURE_MONTH";
        public static final String ROLL_3600 = "ROLL_3600";
        public static final String ROLL_900 = "ROLL_900";
        public static final String INDEX_RACES_WI  = "INDEX_RACES_WI";
        public static final String WEEK_RACES_WI  = "WEEK_RACES_WI";
        public static final String MONTH_RACES_WM = "MONTH_RACES_WM";
        public static final String WEEK_RACES_WM = "WEEK_RACES_WM";
        public static final String TRADING_STATUS = "TRADING_STATUS";
        public static final String TRADING_STATUS_DEV = "TRADING_STATUS_DEV";
        public static final String ROLL_INTEREST_PROD = "ROLL_INTEREST_PROD";
        public static final String OP_MONTH_INTEREST_PROD = "OP_INTEREST_PROD";
        public static final String ROLL_INTEREST_AVG_PROD = "ROLL_INTEREST_AVG_PROD";
        public static final String OP_MONTH_INTEREST_AVG_PROD = "OP_INTEREST_AVG_PROD";
        public static final String OP_WEEK_INTEREST_PROD = "OP_WEEK_INTEREST_PROD";

        public static final String ROLL_INTEREST_DEV = "ROLL_INTEREST_DEV";
        public static final String OP_MONTH_INTEREST_DEV = "OP_INTEREST_DEV";
        public static final String ROLL_INTEREST_AVG_DEV = "ROLL_INTEREST_AVG_DEV";
        public static final String OP_MONTH_INTEREST_AVG_DEV = "OP_INTEREST_AVG_DEV";
        public static final String OP_WEEK_INTEREST_DEV = "OP_WEEK_INTEREST_DEV";
        public static final String MONTH_BID_ASK_COUNTER_PROD = "MONTH_BID_ASK_COUNTER_PROD";
        public static final String WEEK_BID_ASK_COUNTER_PROD = "WEEK_BID_ASK_COUNTER_PROD";

        public static final String STOCKS_TOT_BA_WEIGHT_PROD = "STOCKS_TOT_BA_WEIGHT_PROD";
        public static final String STOCKS_TOT_DELTA_WEIGHT_PROD = "STOCKS_TOT_DELTA_WEIGHT_PROD";
        public static final String COUNTER_2_TOT_WEIGHT_PROD = "COUNTER_2_TOT_WEIGHT_PROD";

        public static final String MONTH_OBI_PROD = "MONTH_OBI_PROD";
        public static final String WEEK_OBI_PROD = "WEEK_OBI_PROD";
        public static final String MONTH__MPO_PROD = "MONTH__MPO_PROD";
        public static final String WEEK__MPO_PROD = "WEEK__MPO_PROD";
    }
}