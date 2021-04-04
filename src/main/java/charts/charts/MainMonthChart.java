package charts.charts;

import java.awt.Color;

import api.ApiObject;
import charts.myChart.ChartPropsEnum;
import charts.myChart.MyChart;
import charts.myChart.MyChartContainer;
import charts.myChart.MyChartCreator;
import charts.myChart.MyProps;
import charts.myChart.MyTimeSeries;
import locals.Themes;

public class MainMonthChart extends MyChartCreator {

	// Constructor
	public MainMonthChart(ApiObject apiObject) {
		super(apiObject);
	}
	
	@SuppressWarnings("serial")
	@Override
	public void createChart() throws CloneNotSupportedException {
		
		// Props
		props = new MyProps();
		props.setProp(ChartPropsEnum.SECONDS, 900);
		props.setProp(ChartPropsEnum.IS_INCLUDE_TICKER, false);
		props.setProp(ChartPropsEnum.MARGIN, .17);
		props.setProp(ChartPropsEnum.RANGE_MARGIN, 0.0);
		props.setProp(ChartPropsEnum.IS_GRID_VISIBLE, false);
		props.setProp(ChartPropsEnum.IS_LOAD_DB, false);
		props.setProp(ChartPropsEnum.IS_LIVE, true);
		props.setProp(ChartPropsEnum.SLEEP, 200);
		props.setProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, (double) INFINITE);
		props.setProp(ChartPropsEnum.SECONDS_ON_MESS, 10);
		
		// ----- Chart 1 ----- //
		// Index
		MyTimeSeries index = new MyTimeSeries("Index", Color.BLACK, 2.25f, props, null) {
			@Override
			public double getData() {
				return apiObject.getIndex();
			}
		};

		// Bid
		MyTimeSeries bid = new MyTimeSeries("Bid", Themes.BLUE, 2.25f, props, null) {
			@Override
			public double getData() {
				return apiObject.getIndex_bid();
			}
		};
		
		// Ask
		MyTimeSeries ask = new MyTimeSeries("Ask", Themes.RED, 2.25f, props, null) {
			@Override
			public double getData() {
				return apiObject.getIndex_ask();
			}
		};

		// Future
		MyTimeSeries future = new MyTimeSeries("Index", Themes.GREEN, 2.25f, props, null) {
			@Override
			public double getData() {
				return apiObject.getExpMonth().getOptions().getContract();
			}
		};

		MyTimeSeries[] series = { index, bid, ask, future };

		// Chart
		MyChart chart = new MyChart(series, props);

		// ----- Charts ----- //
		MyChart[] charts = { chart };

		// ----- Container ----- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Month main chart");
		chartContainer.create();

	}

}
