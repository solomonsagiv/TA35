package lists;

import java.time.LocalDateTime;

import org.json.JSONObject;

public class MyChartPoint {

    private LocalDateTime x;
    private double y;

    public MyChartPoint( LocalDateTime x, double y ) {
        this.x = x;
        this.y = y;
    }

    public MyChartPoint( String x, double y ) {
        this.x = LocalDateTime.parse( x );
        this.y = y;
    }

    public MyChartPoint(JSONObject jsonObject) {
        this.x = LocalDateTime.parse(jsonObject.getString("x"));
        this.y = jsonObject.getDouble("y");
    }

    public LocalDateTime getX() {
        return x;
    }

    public void setX( LocalDateTime x ) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }

    public JSONObject getAsJson() {
        JSONObject object = new JSONObject( );
        object.put( "x", getX( ) );
        object.put( "y", getY( ) );
        return object;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
