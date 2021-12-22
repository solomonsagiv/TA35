package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import dataBase.DataBaseHandler;
import dataBase.Factories;
import dataBase.mySql.Queries;
import locals.Themes;

import java.sql.ResultSet;

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

		// ----- Chart 1 ----- //
		MyTimeSeries v5_serie = new MyTimeSeries("V5") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getV5();
			}

			@Override
			public void load() {
				ResultSet rs = Queries.get_last_x_min_record_from_decision_func(Factories.Tables.DF_TABLE, 2, 5, Queries.START_OF_THE_DAY_MIN);
				DataBaseHandler.loadSerieData(rs, this);
			}
		};
		v5_serie.setStokeSize(2.25f);
		v5_serie.setColor(Themes.BLUE);


		MyTimeSeries v6_serie = new MyTimeSeries("V6") {
			@Override
			public ResultSet load_last_x_time(int minuts) {
				return null;
			}

			@Override
			public double getData() {
				return apiObject.getV6();
			}

			@Override
			public void load() {
				ResultSet rs = Queries.get_last_x_min_record_from_decision_func(Factories.Tables.DF_TABLE, 2, 6, Queries.START_OF_THE_DAY_MIN);
				DataBaseHandler.loadSerieData(rs, this);
			}
		};
		v6_serie.setStokeSize(2.25f);
		v6_serie.setColor(Themes.GREEN_5);


		MyTimeSeries[] series = { v5_serie, v6_serie };

		// Chart
		MyChart chart = new MyChart(series, props);

		// ----- Charts ----- //
		MyChart[] charts = { chart };

		// ----- Container ----- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Decision v5, v6");
		chartContainer.create();
	}

}
