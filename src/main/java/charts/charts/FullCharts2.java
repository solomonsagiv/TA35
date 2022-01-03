package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import dataBase.Factories;
import locals.Themes;
import java.awt.*;

public class FullCharts2 extends MyChartCreator {

	// Constructor
	public FullCharts2(ApiObject apiObject) {
		super(apiObject);
	}

	@Override
	public void init() throws CloneNotSupportedException {

		MyTimeSeries[] series;

		// Props
		props = new MyProps();
		props.setProp(ChartPropsEnum.SECONDS, INFINITE);
		props.setProp(ChartPropsEnum.IS_INCLUDE_TICKER, -1);
		props.setProp(ChartPropsEnum.IS_GRID_VISIBLE, 1);
		props.setProp(ChartPropsEnum.IS_LOAD_DB, 1);
		props.setProp(ChartPropsEnum.IS_LIVE, -1);
		props.setProp(ChartPropsEnum.SLEEP, 1000);
		props.setProp(ChartPropsEnum.IS_RANGE_GRID_VISIBLE, -1);
		props.setProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, (double) INFINITE);
		props.setProp(ChartPropsEnum.SECONDS_ON_MESS, 10);
		props.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, 1);
		props.setProp(ChartPropsEnum.IS_DOMAIN_GRID_VISIBLE, 1);
		props.setProp(ChartPropsEnum.MARKER, 0);

		// ----------------------------------------- Index ----------------------------------------- //
		// Index
		MyTimeSeries indexSerie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_SERIE);
		indexSerie.setColor(Color.BLACK);
		indexSerie.setStokeSize(1.2f);

		series = new MyTimeSeries[1];
		series[0] = indexSerie;
		
		// Chart
		MyChart indexChart = new MyChart(series, props);

		// ----------------------------------------- Deltas ----------------------------------------- //
		// Delta week
		MyTimeSeries delta_week_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DELTA_WEEK_SERIE);
		delta_week_serie.setColor(Themes.GREEN_6);
		delta_week_serie.setStokeSize(1.2f);

		// Delta month
		MyTimeSeries delta_month_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DELTA_MONTH_SERIE);
		delta_month_serie.setColor(Themes.GREEN_5);
		delta_month_serie.setStokeSize(1.2f);

		// Delta mix
		MyTimeSeries delta_mixserie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DELTA_MIX_SERIE);
		delta_mixserie.setColor(Themes.LIGHT_BLUE_3);
		delta_mixserie.setStokeSize(1.2f);

		series = new MyTimeSeries[3];
		series[0] = delta_week_serie;
		series[1] = delta_month_serie;
		series[2] = delta_mixserie;

		// Chart
		MyChart deltaChart = new MyChart(series, props);

		// ----------------------------------------- Bid ask counter ----------------------------------------- //
		// Counter week
		MyTimeSeries bid_ask_counter_week_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.BID_ASK_COUNTER_WEEK_SERIE);
		bid_ask_counter_week_serie.setColor(Themes.GREEN_6);
		bid_ask_counter_week_serie.setStokeSize(1.2f);

		// Counter month
		MyTimeSeries bid_ask_counter_month_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.BID_ASK_COUNTER_MONTH_SERIE);
		bid_ask_counter_month_serie.setColor(Themes.GREEN_5);
		bid_ask_counter_month_serie.setStokeSize(1.2f);

		series = new MyTimeSeries[2];
		series[0] = bid_ask_counter_week_serie;
		series[1] = bid_ask_counter_month_serie;

		// Chart
		MyChart bid_ask_counter_chart = new MyChart(series, props);
		MyProps props_2 = (MyProps) props.clone();
		props_2.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, 1);

		// ----------------------------------------- Chart ----------------------------------------- //

		// ----- Charts ----- //
		MyChart[] charts = { indexChart, deltaChart, bid_ask_counter_chart };

		// ----------------------------------------- Container ----------------------------------------- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Full chart");
		chartContainer.create();

	}
}