package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import dataBase.Factories;
import locals.Themes;

public class Decision_Chart extends MyChartCreator {

	public static void main(String[] args) {
		Decision_Chart decision_chart = new Decision_Chart(ApiObject.getInstance());
		decision_chart.createChart();
	}

	// Constructor
	public Decision_Chart(ApiObject apiObject) {
		super(apiObject);
	}

	@SuppressWarnings("serial")
	@Override
	public void init() throws CloneNotSupportedException {

		// Props
		props = new MyProps();
		props.setProp(ChartPropsEnum.SECONDS, 36);
		props.setProp(ChartPropsEnum.IS_INCLUDE_TICKER, -1);
		props.setProp(ChartPropsEnum.IS_RANGE_GRID_VISIBLE, -1);
		props.setProp(ChartPropsEnum.IS_LOAD_DB, -1);
		props.setProp(ChartPropsEnum.IS_LIVE, -1);
		props.setProp(ChartPropsEnum.SLEEP, 15);
		props.setProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, INFINITE);
		props.setProp(ChartPropsEnum.SECONDS_ON_MESS, 10);
		props.setProp(ChartPropsEnum.MARKER, 0);
		props.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, 1);
		props.setProp(ChartPropsEnum.RETRO_MINS, 15);

		// ----- Chart 1 ----- //
		MyTimeSeries v4_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DF_V_4_SERIE);
		v4_serie.setStokeSize(2.25f);
		v4_serie.setColor(Themes.BLUE);

		MyTimeSeries v8_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DF_V_8_SERIE);
		v8_serie.setStokeSize(2.25f);
		v8_serie.setColor(Themes.GREEN_5);

		MyTimeSeries[] series = { v4_serie, v8_serie };

		// Chart
		MyChart chart = new MyChart(series, props);

		// ----- Charts ----- //
		MyChart[] charts = { chart };

		// ----- Container ----- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Decision v4, v8");
		chartContainer.create();
	}

}
