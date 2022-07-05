package handlers;

import charts.myChart.MyTimeSeries;

import java.util.HashMap;
import java.util.Map;

public class TimeSeriesHandler {

    Map<String, MyTimeSeries> map;

    public TimeSeriesHandler() {
        this.map = new HashMap<>();
    }

    public MyTimeSeries get(String serie_type) {
        return map.get(serie_type);
    }

    public void put(String serie_type, MyTimeSeries serie) {
        this.map.put(serie_type, serie);
    }


}
