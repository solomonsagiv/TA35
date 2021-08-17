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
		MyProps bottomChartProps = (MyProps) props.clone();
		bottomChartProps.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, 1);
		
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
		
		// Delta month
		MyTimeSeries deltaMonthSerie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.DELTA_MONTH_SERIE);
		deltaMonthSerie.setColor(Themes.GREEN);
		deltaMonthSerie.setStokeSize(1.5f);
		
		series = new MyTimeSeries[2];
		series[0] = deltaWeekSerie;
		series[1] = deltaMonthSerie;
			
		// Chart
		MyChart deltaWeekChart = new MyChart(series, props);

		// -------------------- Chart -------------------- //

		// ----- Charts ----- //
		MyChart[] charts = { indexChart, deltaWeekChart };

		// ----- Container ----- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Delta chart");
		chartContainer.create();

	}

}
