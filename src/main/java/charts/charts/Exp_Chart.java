package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import dataBase.DataBaseHandler;
import dataBase.Factories;
import dataBase.mySql.Queries;
import locals.Themes;
import java.awt.*;
import java.sql.ResultSet;

public class Exp_Chart extends MyChartCreator {

	public static void main(String[] args) {
		Exp_Chart exp_chart = new Exp_Chart(ApiObject.getInstance());
		exp_chart.createChart();
	}

	// Constructor
	public Exp_Chart(ApiObject apiObject) {
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
		props.setProp(ChartPropsEnum.SLEEP, 10000);
		props.setProp(ChartPropsEnum.IS_RANGE_GRID_VISIBLE, -1);
		props.setProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, (double) INFINITE);
		props.setProp(ChartPropsEnum.SECONDS_ON_MESS, 10);
		props.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, 1);
		props.setProp(ChartPropsEnum.IS_DOMAIN_GRID_VISIBLE, 1);
		props.setProp(ChartPropsEnum.MARKER, 0);

		// ----------------------------------------- Index ----------------------------------------- //
		// Index
		MyTimeSeries indexSerie = new MyTimeSeries("Index") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getIndex();
			}

			@Override
			public void load() {
				ResultSet rs = Queries.get_exp_serie(Factories.Tables.INDEX_TABLE, DataBaseHandler.EXP_WEEK, 120);
				DataBaseHandler.loadSerieData(rs, this);
			}
		};
		indexSerie.setColor(Color.BLACK);
		indexSerie.setStokeSize(1.2f);

		series = new MyTimeSeries[1];
		series[0] = indexSerie;
		
		// Chart
		MyChart indexChart = new MyChart(series, props);

		// ----------------------------------------- Deltas ----------------------------------------- //
		// V4
		MyTimeSeries v4_serie = new MyTimeSeries("V4") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getExps().getWeek().getExpData().getV4() + apiObject.getPre_v4();
			}

			@Override
			public void load() {
				ResultSet rs = Queries.get_exp_decision_function(2, 4, DataBaseHandler.EXP_WEEK, 120);
				DataBaseHandler.loadSerieData(rs, this);
			}
		};
		v4_serie.setColor(Themes.BLUE_2);
		v4_serie.setStokeSize(1.2f);

		// V8
		// V4
		MyTimeSeries v8_serie = new MyTimeSeries("V8") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getExps().getWeek().getExpData().getV8() + apiObject.getPre_v8();
			}

			@Override
			public void load() {
				ResultSet rs = Queries.get_exp_decision_function(2, 8, DataBaseHandler.EXP_WEEK, 120);
				DataBaseHandler.loadSerieData(rs, this);
			}
		};
		v8_serie.setColor(Themes.GREEN_5);
		v8_serie.setStokeSize(1.2f);

		series = new MyTimeSeries[2];
		series[0] = v4_serie;
		series[1] = v8_serie;

		// Chart
		MyChart deltaChart = new MyChart(series, props);

		// ----------------------------------------- Chart ----------------------------------------- //

		// ----- Charts ----- //
		MyChart[] charts = { indexChart, deltaChart };

		// ----------------------------------------- Container ----------------------------------------- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Full chart");
		chartContainer.create();

	}
}