package charts.myChart;

import java.util.Properties;

import org.jfree.chart.plot.Marker;

public class MyProps implements Cloneable {

    private Properties properties = new Properties( );

    public void setProp( Enum e, Object o ) {
        properties.put( e, o );
    }

    public Object get( Enum e ) {
        return properties.get( e );
    }

    public double getDouble( Enum e ) {
        return ( double ) properties.get( e );
    }

    public int getInt( Enum e ) {
        return ( int ) properties.get( e );
    }

    public String getString( Enum e ) {
        return ( String ) properties.get( e );
    }

    public boolean getBool( Enum e ) {
        try {
            return ( boolean ) properties.get( e );
        } catch ( NullPointerException exception ) {
            return false;
        }
    }

    public float getFloat( Enum e ) {
        return ( float ) properties.get( e );
    }


    private void setProperties( Properties properties ) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "MyProps{" +
                "properties=" + properties +
                '}';
    }

    @Override
    public Object clone() {
        MyProps props = new MyProps();
        props.setProperties( ( Properties ) this.properties.clone() );
        return props;
    }
}

interface IChartProps {

    int getSeconds();

    boolean isIncludeTicker();

    double getMarginMaxMin();

    float getStrokeSize();

    double getRangeMargin();

    boolean isGridLineVisible();

    boolean isLoadFromDB();

    Marker getMarker();

    boolean isLive();

    int getSleep();

    double getChartHighInDots();

    int getSecondsOnMess();

}