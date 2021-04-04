package lists;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyChartList extends ArrayList<MyChartPoint> {

    private ArrayList<Double> values = new ArrayList<>();
    private JSONArray jsonArray = new JSONArray();

    @Override
    public boolean add(MyChartPoint point) {
        values.add(point.getY());
        jsonArray.put(new JSONObject().put("x", point.getX()).put("y", point.getY()));
        return super.add(point);
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public MyChartPoint getLast() {
        return get(size() - 1);
    }

    public void setData(JSONArray jsonArray) {
        for (Object o : jsonArray) {
            JSONObject object = new JSONObject(o.toString());
            add(new MyChartPoint(object.getString("x"), object.getDouble("y")));
        }
    }

    @Override
    public String toString() {
        return jsonArray.toString();

    }
}
