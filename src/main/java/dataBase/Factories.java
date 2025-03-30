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
        public static final String DF_9_CDF = "DF_9_CDF";

        public static final String CONTINUE_OP_AVG_WEEK_240 = "YESTERDAY_OP_AVG_WEEK_240";
        public static final String INDEX_WITH_BID_ASK = "INDEX_WITH_BID_ASK";
        public static final String FUTURE_WEEK = "FUTURE_WEEK";
        public static final String FUTURE_MONTH = "FUTURE_MONTH";
        public static final String ROLL_3600 = "ROLL_3600";
        public static final String ROLL_900 = "ROLL_900";
        public static final String INDEX_RACES_WI  = "INDEX_RACES_WI";
        public static final String WEEK_RACES_WI  = "WEEK_RACES_WI";
        public static final String R1_MINUS_R2_IQ = "R1_MINUS_R2_IQ";
        public static final String MONTH_RACES_WM = "MONTH_RACES_WM";
        public static final String WEEK_RACES_WM = "WEEK_RACES_WM";
        public static final String BID_RACES_BA = "BID_RACES_BA";
        public static final String ASK_RACES_BA = "ASK_RACES_BA";
        public static final String MID_RACES_ML = "MID_RACES_ML";
        public static final String LAST_RACES_ML = "LAST_RACES_ML";

        public static final String VICTOR_INDEX_RACES = "VICTOR_INDEX_RACES";
        public static final String VICTOR_FUTURE_RACES = "VICTOR_FUTURE_RACES";
        public static final String VICTOR_ROLL_RACES = "VICTOR_ROLL_RACES";
        public static final String VICTOR_INDEX_RACES_RATIO = "VICTOR_INDEX_RACES_RATIO";
        public static final String VICTOR_ROLL_RACES_RATIO = "VICTOR_ROLL_RACES_RATIO";
        public static final String TRADING_STATUS = "TRADING_STATUS";

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
        public static final int DF_9 = 9479;
        public static final int DF_4_old = 9770;
        public static final int DF_8_old = 9773;
        public static final int DF_5_old = 9491;
        public static final int DF_6_old = 9492;
//        public static final int DF_7 = 9478;
        public static final int ROLL_3600 = 9542;
        public static final int ROLL_900 = 9543;
        public static final int INDEX_RACES_WI = 9789;
        public static final int WEEK_RACES_WI = 9788;

        public static final int MONTH_RACES_WM = 9791;
        public static final int WEEK_RACES_WM = 9790;
        public static final int BID_RACES_BA = 9870;
        public static final int ASK_RACES_BA = 9871;
        public static final int MID_RACES_ML = 9872;
        public static final int LAST_RACES_ML = 9873;

        public static final int VICTOR_INDEX_RACES = 9800;
        public static final int VICTOR_FUTURE_RACES = 9801;
        public static final int VICTOR_ROLL_RACES = 9834;
        public static final int VICTOR_INDEX_RACES_RATIO = 9863;
        public static final int VICTOR_ROLL_RACES_RATIO = 9865;



//        public static final int OP_AVG_WEEK = ;
//        public static final int OP_AVG_MONTH = ;
    }

}