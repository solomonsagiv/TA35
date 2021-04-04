package charts.myChart;

import java.awt.Color;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import lists.MyChartPoint;

public abstract class MyTimeSeries extends TimeSeries implements ITimeSeries {

	public static final int TIME = 0;
	public static final int VALUE = 1;
	
	private Color color;
	private float stokeSize;
	private MyChartList myChartList;
	MyProps props;
	String name;
	Second lastSeconde;

	public MyTimeSeries(Comparable name, Color color, float strokeSize, MyProps props, MyChartList myChartList) {
		super(name);
		this.color = color;
		this.stokeSize = strokeSize;
		this.props = props;
		this.myChartList = myChartList;
		this.name = name.toString();
	}
	
	public void loadData(ArrayList<Double> dots) {
		try {
			
			LocalDateTime time = myChartList.get(0).getX();

			lastSeconde = new Second(time.getSecond(), time.getMinute(), time.getHour(), time.getDayOfMonth(),
					time.getMonth().getValue(), time.getYear());

			for (int i = 0; i < myChartList.size(); i++) {

				addOrUpdate(lastSeconde.next(), myChartList.get(i).getY());
				dots.add(myChartList.get(i).getY());

				lastSeconde = (Second) lastSeconde.next();

			}
			return;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		lastSeconde = new Second();
	}
	
	@Override
	public void delete(RegularTimePeriod period) {
		int index = getIndex(period);
		myChartList.getValues().remove(index);
		super.delete(period);
	}
	
	public double add() {
		double data = 0;
		// live data
		if (props.getBool(ChartPropsEnum.IS_LIVE)) {
			try {
				data = getData();
				addOrUpdate(getLastSeconde(), data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				MyChartPoint point = myChartList.getLast();
				data = point.getY();
				addOrUpdate(getLastSeconde(), data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		lastSeconde = (Second) lastSeconde.next();
		return data;
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

	public MyChartList getMyChartList() {
		return myChartList;
	}

	public void setMyChartList(MyChartList myChartList) {
		this.myChartList = myChartList;
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
}

interface ITimeSeries {
	double getData() throws UnknownHostException;
}