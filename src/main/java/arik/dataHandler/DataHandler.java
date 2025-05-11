package arik.dataHandler;

import java.util.HashMap;
import java.util.Map;

public class DataHandler {

    public static final String TA35_INDEX = "TA35_INDEX";
    public static final String TA35_DF_5 = "TA35_DF_2";
    public static final String TA35_DF_6 = "TA35_DF_7";

    public static final String SPX_INDEX = "SPX_INDEX";
    public static final String SPX_DF_2 = "SPX_DF_2";
    public static final String SPX_DF_7 = "SPX_DF_7";
    public static final String SPX_DF_8 = "SPX_DF_8";

    public static final String NDX_INDEX = "NDX_INDEX";
    public static final String NDX_DF_2 = "NDX_DF_2";
    public static final String NDX_DF_7 = "NDX_DF_7";
    public static final String NDX_DF_8 = "NDX_DF_8";

    private Map<String, DataObject> map;

    public DataHandler() {
        map = new HashMap<>();

        put(TA35_INDEX);
        put(TA35_DF_5);
        put(TA35_DF_6);

        put(SPX_INDEX);
        put(SPX_DF_2);
        put(SPX_DF_7);
        put(SPX_DF_8);

        put(NDX_INDEX);
        put(NDX_DF_2);
        put(NDX_DF_7);
        put(NDX_DF_8);
    }

    private void put(String object_type) {
        map.put(object_type, new DataObject(object_type));
    }

    // Getters setters
    public DataObject get(String object_type) {
        return map.get(object_type);
    }

    public void put(String object_type, DataObject object) {
        map.put(object_type, object);
    }

}

