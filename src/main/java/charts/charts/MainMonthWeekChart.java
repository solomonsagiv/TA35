package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import locals.Themes;

import java.awt.*;
import java.sql.ResultSet;

public class MainMonthWeekChart extends MyChartCreator {

	// Constructor
	public MainMonthWeekChart(ApiObject apiObject) {
		super(apiObject);
	}

	@SuppressWarnings("serial")
	@Override
	public void createChart() throws CloneNotSupportedException {
		
		// Props
		props = new MyProps();
		props.setProp(ChartPropsEnum.SECONDS, 900);
		props.setProp(ChartPropsEnum.IS_INCLUDE_TICKER, -1);
		props.setProp(ChartPropsEnum.MARGIN, .17);
		props.setProp(ChartPropsEnum.RANGE_MARGIN, 0.0);
		props.setProp(ChartPropsEnum.IS_GRID_VISIBLE, -1);
		props.setProp(ChartPropsEnum.IS_LOAD_DB, -1);
		props.setProp(ChartPropsEnum.IS_LIVE, 1);
		props.setProp(ChartPropsEnum.SLEEP, 200);
		props.setProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, (double) INFINITE);
		props.setProp(ChartPropsEnum.SECONDS_ON_MESS, 10);

		// ----- Chart 1 ----- //
		// Index
		MyTimeSeries index = new MyTimeSeries("Index") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getIndex();
			}

			@Override
			public void load_data() {

			}
		};
		index.setColor(Color.BLACK);
		index.setStokeSize(2.25f);

		// Bid
		MyTimeSeries bid = new MyTimeSeries("Bid") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getIndex_bid();
			}

			@Override
			public void load_data() {

			}
		};
		bid.setColor(Themes.BLUE);
		bid.setStokeSize(2.25f);

		// Ask
		MyTimeSeries ask = new MyTimeSeries("Ask") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getIndex_ask();
			}

			@Override
			public void load_data() {

			}
		};
		ask.setColor(Themes.RED);
		ask.setStokeSize(2.25f);

		// Future
		MyTimeSeries future = new MyTimeSeries("Future") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getExpMonth().getOptions().getContract();
			}

			@Override
			public void load_data() {

			}
		};
		future.setColor(Themes.GREEN);
		future.setStokeSize(2.25f);

		// Future
		MyTimeSeries futureWeek = new MyTimeSeries("Future week") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getExpWeek().getOptions().getContract();
			}

			@Override
			public void load_data() {

			}
		};
		futureWeek.setColor(Themes.GREEN_LIGHT);
		futureWeek.setStokeSize(2.25f);

		MyTimeSeries[] series = { index, bid, ask, future, futureWeek };

		// Chart
		MyChart chart = new MyChart(series, props);

		// ----- Charts ----- //
		MyChart[] charts = { chart };

		// ----- Container ----- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Month main chart");
		chartContainer.create();

	}

}
