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

		series = new MyTimeSeries[2];
		series[0] = delta_week_serie;
		series[1] = delta_month_serie;

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

		// ----------------------------------------- OP AVG ----------------------------------------- //
		//  Week op avg 60
		MyTimeSeries op_avg_week_30 = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_WEEK_30_SERIE);
		op_avg_week_30.setColor(Themes.GREEN_6);
		op_avg_week_30.setStokeSize(2.5f);

		// Month op avg 60
		MyTimeSeries op_avg_month_30 = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_MONTH_30_SERIE);
		op_avg_month_30.setColor(Themes.GREEN_5);
		op_avg_month_30.setStokeSize(2.5f);

		series = new MyTimeSeries[2];
		series[0] = op_avg_week_30;
		series[1] = op_avg_month_30;

		// Chart
		MyChart op_avg_chart = new MyChart(series, props_2);

		// ----------------------------------------- Chart ----------------------------------------- //

		// ----- Charts ----- //
		MyChart[] charts = { indexChart, deltaChart, bid_ask_counter_chart, op_avg_chart };

		// ----------------------------------------- Container ----------------------------------------- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Delta chart");
		chartContainer.create();

	}
}