package charts.myChart;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import lists.MyChartPoint;

public class MyChartList extends ArrayList<MyChartPoint> {
	
    private ArrayList<Double> values = new ArrayList<>();
    private JSONArray jsonArray = new JSONArray();

    public MyChartList() {
    }

    @Override
    public boolean add(MyChartPoint myChartPoint) {
        values.add(myChartPoint.getY());
        jsonArray.put(new JSONObject().put("x", myChartPoint.getX()).put("y", myChartPoint.getY()));
        return super.add(myChartPoint);
    }

    public ArrayList<Double> getValues() {
        return values;
    }
    
    public MyChartPoint getLast() throws UnknownHostException {
    	if (size() == 0) {
			throw new IndexOutOfBoundsException();
    	}
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
