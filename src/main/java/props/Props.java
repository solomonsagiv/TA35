package props;

import api.BASE_CLIENT_OBJECT;

import java.util.HashMap;
import java.util.Map;

public class Props {

    BASE_CLIENT_OBJECT client;
    private Map<String, Prop> map;
    Prop index_pre_start_time;
    Prop index_start_time;
    Prop index_end_time;
    Prop future_end_time;
    Prop excel_path;
    Prop main_exp;
    Prop sapi_excel_file;
    Prop stocks_excel_file;
    Prop chart_start_min;

    public static final String INDEX_PRE_START_TIME = "INDEX_PRE_START_TIME";
    public static final String INDEX_START_TIME = "INDEX_START_TIME";
    public static final String INDEX_END_TIME = "INDEX_END_TIME";
    public static final String FUT_END_TIME = "FUT_END_TIME";
    public static final String CHARTS = "CHARTS";
    public static final String EXCEL_FILE_LOCATION = "EXCEL_FILE_LOCATION";
    public static final String MAIN_EXP = "MAIN_EXP";
    public static final String SAPI_EXCEL_FILE_LOCATION = "SAPI_EXCEL_FILE_LOCATION";
    public static final String STOCKS_EXCEL_FILE_LOCATION = "STOCKS_EXCEL_FILE_LOCATION";
    public static final String EXP_Q1_START = "EXP_Q1_START";
    public static final String EXP_WEEK_START = "EXP_WEEK_START";
    public static final String EXP_MONTH_START = "EXP_MONTH_START";
    public static final String CHART_START_MIN = "CHART_START_MIN";

    public Props(BASE_CLIENT_OBJECT client) {
        this.client = client;
        map = new HashMap<>();
        init_props();
        init();
    }

    private void init_props() {
    }

    private void init() {
        map.put(INDEX_START_TIME, index_start_time);
        map.put(INDEX_END_TIME, index_end_time);
        map.put(FUT_END_TIME, future_end_time);
        map.put(EXCEL_FILE_LOCATION, excel_path);
        map.put(MAIN_EXP, main_exp);
        map.put(INDEX_PRE_START_TIME, index_pre_start_time);
        map.put(SAPI_EXCEL_FILE_LOCATION, sapi_excel_file);
        map.put(STOCKS_EXCEL_FILE_LOCATION, stocks_excel_file);
        map.put(CHART_START_MIN, chart_start_min);

    }

    public Map<String, Prop> getMap() {
        return map;
    }

}

