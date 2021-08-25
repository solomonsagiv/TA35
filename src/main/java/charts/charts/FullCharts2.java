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
	public void createChart() throws CloneNotSupportedException {

		MyTimeSeries[] series;

		// Props
		props = new MyProps();
		props.setProp(ChartPropsEnum.SECONDS, INFINITE);
		props.setProp(ChartPropsEnum.IS_INCLUDE_TICKER, -1);
		props.setProp(ChartPropsEnum.MARGIN, 0.005);
		props.setProp(ChartPropsEnum.RANGE_MARGIN, 0.0);
		props.setProp(ChartPropsEnum.IS_GRID_VISIBLE, 1);
		props.setProp(ChartPropsEnum.IS_LOAD_DB, 1);
		props.setProp(ChartPropsEnum.IS_LIVE, -1);
		props.setProp(ChartPropsEnum.SLEEP, 1000);
		props.setProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, (double) INFINITE);
		props.setProp(ChartPropsEnum.SECONDS_ON_MESS, 10);
		props.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, -1);
		props.setProp(ChartPropsEnum.MARKER, 0);

		// --------- Index ---------- //
		// Index
		MyTimeSeries indexSerie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_SERIE);
		indexSerie.setColor(Color.BLACK);
		indexSerie.setStokeSize(1.5f);

		series = new MyTimeSeries[1];
		series[0] = indexSerie;
		
		// Chart
		MyChart indexChart = new MyChart(series, props);

		// --------- Deltas ---------- //
		// Delta week
		MyTimeSeries deltaWeekSerie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DELTA_WEEK_SERIE);
		deltaWeekSerie.setColor(Themes.GREEN_LIGHT);
		deltaWeekSerie.setStokeSize(1.5f);

		// Delta week avg 60
		MyTimeSeries delta_week_avg_60_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DELTA_WEEK_AVG_60_SERIE);
		delta_week_avg_60_serie.setColor(Themes.BLUE4);
		delta_week_avg_60_serie.setStokeSize(1.1f);
		
		// Delta month
		MyTimeSeries deltaMonthSerie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DELTA_MONTH_SERIE);
		deltaMonthSerie.setColor(Themes.GREEN);
		deltaMonthSerie.setStokeSize(1.5f);

		// Delta week
		MyTimeSeries delta_month_avg_60_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DELTA_MONTH_AVG_60_SERIE);
		delta_month_avg_60_serie.setColor(Themes.BLUE4);
		delta_month_avg_60_serie.setStokeSize(1.1f);

		series = new MyTimeSeries[4];
		series[0] = deltaWeekSerie;
		series[1] = delta_week_avg_60_serie;
		series[2] = deltaMonthSerie;
		series[3] = delta_month_avg_60_serie;

		// Chart
		MyChart deltaChart = new MyChart(series, props);

		// -------------------- Bid ask counter ------------------ //
		// Counter week
		MyTimeSeries bid_ask_counter_week = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.BID_ASK_COUNTER_WEEK_SERIE);
		bid_ask_counter_week.setColor(Themes.BINANCE_ORANGE);
		bid_ask_counter_week.setStokeSize(1.5f);

		// Counter week avg 60
		MyTimeSeries bid_ask_counter_week_avg_60 = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.BID_ASK_COUNTER_WEEK_AVG_60_SERIE);
		bid_ask_counter_week_avg_60.setColor(Themes.BLUE4);
		bid_ask_counter_week_avg_60.setStokeSize(1.1f);

		// Counter month
		MyTimeSeries bid_ask_counter_month = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.BID_ASK_COUNTER_MONTH_SERIE);
		bid_ask_counter_month.setColor(Themes.BINANCE_ORANGE_2);
		bid_ask_counter_month.setStokeSize(1.5f);

		// Counter month avg 60
		MyTimeSeries bid_ask_counter_month_avg_60 = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.BID_ASK_COUNTER_MONTH_AVG_60_SERIE);
		bid_ask_counter_month_avg_60.setColor(Themes.BLUE4);
		bid_ask_counter_month_avg_60.setStokeSize(1.1f);

		series = new MyTimeSeries[4];
		series[0] = bid_ask_counter_week;
		series[1] = bid_ask_counter_week_avg_60;
		series[2] = bid_ask_counter_month;
		series[3] = bid_ask_counter_month_avg_60;

		// Chart
		MyChart bid_ask_counter_chart = new MyChart(series, props);

		// -------------------- OP AVG ------------------ //
		//  Week 60
		MyTimeSeries op_avg_week_60 = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_WEEK_60_SERIE);
		op_avg_week_60.setColor(Themes.GREY_LIGHT);
		op_avg_week_60.setStokeSize(1.5f);

		// Month 60
		MyTimeSeries op_avg_month_60 = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_MONTH_60_SERIE);
		op_avg_month_60.setColor(Themes.BINANCE_RED_2);
		op_avg_month_60.setStokeSize(1.5f);

		series = new MyTimeSeries[2];
		series[0] = op_avg_week_60;
		series[1] = op_avg_month_60;

		// Chart
		MyChart op_avg_chart = new MyChart(series, props);

		// -------------------- Chart -------------------- //

		// ----- Charts ----- //
		MyChart[] charts = { indexChart, deltaChart, bid_ask_counter_chart, op_avg_chart };

		// ----- Container ----- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Delta chart");
		chartContainer.create();

	}

}
