package handlers;

import charts.myChart.MyTimeSeries;

import java.util.HashMap;
import java.util.Map;

public class TimeSeriesHandler {

    Map<String, MyTimeSeries> map;
    Map<String, Integer> ids_map;


    public TimeSeriesHandler() {
        this.map = new HashMap<>();
        this.ids_map = new HashMap<>();
    }

    public MyTimeSeries get(String serie_type) {
        return map.get(serie_type);
    }

    public void put(String serie_type, MyTimeSeries serie) {
        this.map.put(serie_type, serie);
    }

    public int get_id(String serie_name) {
        return this.ids_map.get(serie_name);
    }

    public void put_id(String serie_name, int id) {
        ids_map.put(serie_name, id);
    }
}
