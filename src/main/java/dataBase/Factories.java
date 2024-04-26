package dataBase;

public class Factories {

    public static class Tables {
        public static final String EXPS_TABLE = "sagiv.ta35_exps";
        public static final String SAGIV_OPTIONS_STATUS_TABLE = "sagiv.ta35_options_status";
//        public static final String DF_TABLE = "data.ta35_decision_func";
    }

    public static class TimeSeries {
        public static final String INDEX = "INDEX";
        public static final String INDEX_AVG_3600 = "INDEX_AVG_3600";
        public static final String INDEX_AVG_900 = "INDEX_AVG_900";

        public static final String OP_AVG_WEEK_5 = "OP_AVG_WEEK_5";
        public static final String OP_AVG_WEEK_60 = "OP_AVG_WEEK_60";
        public static final String DF_4_CDF_OLD = "DF_4_CDF";
        public static final String DF_8_CDF_OLD = "DF_8_CDF";
        public static final String DF_5_CDF_OLD = "DF_5_CDF";
        public static final String DF_6_CDF_OLD = "DF_6_CDF";
        public static final String DF_8_CDF = "DF_8_DE_CORR_CDF";

        public static final String CONTINUE_OP_AVG_WEEK_240 = "YESTERDAY_OP_AVG_WEEK_240";
        public static final String INDEX_WITH_BID_ASK = "INDEX_WITH_BID_ASK";
        public static final String FUTURE_WEEK = "FUTURE_WEEK";
        public static final String FUTURE_MONTH = "FUTURE_MONTH";
        public static final String ROLL_3600 = "ROLL_3600";
        public static final String ROLL_900 = "ROLL_900";
    }

    public static class IDs {
//        public static final int INDEX_TABLE = 5404;
//        public static final int INDEX_BID = 22;
//        public static final int INDEX_ASK = 21;
//        //     public static final int INDEX_DELTA_TABLE = "sagiv.ta35_ind_delta_cdf";
//        public static final int BASKETS = 1649;
//        public static final int FUT_WEEK = 23;
//        public static final int FUT_MONTH = 6;
//        public static final int OP_AVG_240_CONTINUE = 5398;
//        public static final int OP_AVG_60 = 5393;
//        public static final int OP_AVG_15 = 5392;
//        public static final int DF_2 = 5385;
//        public static final int DF_8_DE_CORR = 5412;
//        public static final int DF_4_old = 5416;
//        public static final int DF_8_old = 5417;
//        public static final int DF_5_old = 5418;
//        public static final int DF_6_old = 5419;
//        public static final int DF_7 = 5420;
//        public static final int OP_AVG_WEEK = 2034;
//        public static final int OP_AVG_MONTH = 2035;


        public static final int INDEX = 5;
        public static final int INDEX_BID = 22;
        public static final int INDEX_ASK = 21;
        //     public static final int INDEX_DELTA_TABLE = "sagiv.ta35_ind_delta_cdf";
        public static final int BASKETS = 9513;
        public static final int FUT_WEEK = 23;
        public static final int FUT_MONTH = 6;
        public static final int OP_AVG_240_CONTINUE = 9486;
        public static final int OP_AVG_60 = 9484;
        public static final int OP_AVG_15 = 9485;
//        public static final int DF_2 = 9477;
        public static final int DF_8_DE_CORR = 9479;
        public static final int DF_4_old = 9770;
        public static final int DF_8_old = 9773;
        public static final int DF_5_old = 9491;
        public static final int DF_6_old = 9492;
//        public static final int DF_7 = 9478;
        public static final int ROLL_3600 = 9542;
        public static final int ROLL_900 = 9543;


//        public static final int OP_AVG_WEEK = ;
//        public static final int OP_AVG_MONTH = ;
    }

}