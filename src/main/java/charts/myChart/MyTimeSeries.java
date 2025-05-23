package charts.myChart;

import api.BASE_CLIENT_OBJECT;
import lists.MyDoubleList;
import myJson.JsonStrings;
import myJson.MyJson;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDateTime;

interface ITimeSeries {
    double getValue();
    void load();
    void updateData();
}

public abstract class MyTimeSeries extends TimeSeries implements ITimeSeries {

    private int id = 0;
    public static final int TIME = 0;
    public static final int VALUE = 1;
    MyProps props;
    private String name;
    Second lastSeconde;
    private Color color;
    private float stokeSize;
    private boolean scaled = false;
    private boolean visible = true;
    MyDoubleList myValues;
    private String series_type;
    private boolean load = false;
//    private double data = 0;

    BASE_CLIENT_OBJECT client;

    double value = 0;

    // Constructor
    public MyTimeSeries(Comparable name, BASE_CLIENT_OBJECT client) {
        super(name);
        this.client = client;
        this.name = (String) name;
        this.series_type = (String) name;
        myValues = new MyDoubleList();
    }

    public MyProps getProps() {
        return props;
    }

    public String getSeries_type() {
        return series_type;
    }

    public TimeSeriesDataItem getLastItem() {
        return getDataItem(getItemCount() - 1);
    }

    public void load_data() {
        try {
            load();
            setLoad(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MyDoubleList getMyValues() {
        return myValues;
    }

    public MyJson getLastJson() throws ParseException {
        TimeSeriesDataItem item = getLastItem();
        LocalDateTime localDateTime = LocalDateTime.now();
        MyJson json = new MyJson();
        json.put(JsonStrings.x, localDateTime);
        json.put(JsonStrings.y, item.getValue());
        return json;
    }

    public void clear_data() {
        if (myValues.size() > 2) {
            data.clear();
            myValues.clear();
        }
    }

    public void add(LocalDateTime dateTime) {
        try {
            double value = getValue();
            if (value != 0) {
                base_add(dateTime, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(LocalDateTime dateTime, double value) {
        try {
            if (value != 0) {
                base_add(dateTime, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add() {
        double data = getValue();
        if (data != 0) {
            getMyValues().add(data);
            addOrUpdate(getLastSeconde(), data);
            lastSeconde = (Second) lastSeconde.next();
        }
    }

    private void base_add(LocalDateTime dateTime, double value) {
        if (value != 0) {
            lastSeconde = new Second(dateTime.getSecond(), dateTime.getMinute(), dateTime.getHour(), dateTime.getDayOfMonth(), dateTime.getMonthValue(), dateTime.getYear());
            myValues.add(value);
            addOrUpdate(lastSeconde, value);
        }
    }

    public void remove(int index) {
        delete(index, index);
        myValues.remove(index);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getStokeSize() {
        return stokeSize;
    }

    public void setStokeSize(float stokeSize) {
        this.stokeSize = stokeSize;
    }

    public boolean isScaled() {
        return scaled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Second getLastSeconde() {
        if (lastSeconde == null) {
            lastSeconde = new Second();
        }
        return lastSeconde;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public boolean isLoad() {
        return load;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}