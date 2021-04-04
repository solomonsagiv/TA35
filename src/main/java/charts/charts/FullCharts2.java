package charts.charts;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.plot.ValueMarker;

import api.ApiObject;
import charts.myChart.ChartPropsEnum;
import charts.myChart.MyChart;
import charts.myChart.MyChartContainer;
import charts.myChart.MyChartCreator;
import charts.myChart.MyProps;
import charts.myChart.MyTimeSeries;
import locals.Themes;

public class FullCharts2 extends MyChartCreator {

	// Constructor
	public FullCharts2(ApiObject apiObject) {
		super(apiObject);
	}

	@Override
	public void createChart() throws CloneNotSupportedException {

		MyTimeSeries[] series;
		ValueMarker marker = new ValueMarker(0);
		
		// Props
		props = new MyProps();
		props.setProp(ChartPropsEnum.SECONDS, INFINITE);
		props.setProp(ChartPropsEnum.IS_INCLUDE_TICKER, false);
		props.setProp(ChartPropsEnum.MARGIN, 0.005);
		props.setProp(ChartPropsEnum.RANGE_MARGIN, 0.0);
		props.setProp(ChartPropsEnum.IS_GRID_VISIBLE, true);
		props.setProp(ChartPropsEnum.IS_LOAD_DB, true);
		props.setProp(ChartPropsEnum.IS_LIVE, false);
		props.setProp(ChartPropsEnum.SLEEP, 1000);
		props.setProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, (double) INFINITE);
		props.setProp(ChartPropsEnum.SECONDS_ON_MESS, 10);
		props.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, false);
		props.setProp(ChartPropsEnum.MARKER, marker);
		
		// --------- Index ---------- //
		MyProps bottomChartProps = (MyProps) props.clone();
		
		marker.setPaint(Color.BLACK);
		marker.setStroke(new BasicStroke(1.0f));
		marker.setValue(0);
		bottomChartProps.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, true);
		
		// Index
		MyTimeSeries indexSerie = new MyTimeSeries("Index", Color.BLACK, 1.5f, props, apiObject.getIndexChartList()) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 3830013299128693882L;

			@Override
			public double getData() {
				try {
					return apiObject.getIndex();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		};

		series = new MyTimeSeries[1];
		series[0] = indexSerie;
		
		// Chart
		MyChart indexChart = new MyChart(series, props);

		// --------- Deltas ---------- //
		
		// Delta week
		MyTimeSeries deltaWeekSerie = new MyTimeSeries("Delta week", Themes.GREEN_LIGHT, 1.5f, props,
				apiObject.getExpWeek().getOptions().getDeltaChartList()) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 2974336648337921958L;

			@Override
			public double getData() {
				return apiObject.getExpWeek().getOptions().getDelta();
			}
		};
		
		// Delta month
		MyTimeSeries deltaMonthSerie = new MyTimeSeries("Delta month", Themes.GREEN, 1.5f, props,
				apiObject.getExpMonth().getOptions().getDeltaChartList()) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 2974336648337921958L;

			@Override
			public double getData() {
				return apiObject.getExpMonth().getOptions().getDelta();
			}
		};
		
		series = new MyTimeSeries[2];
		series[0] = deltaWeekSerie;
		series[1] = deltaMonthSerie;
			
		// Chart
		MyChart deltaWeekChart = new MyChart(series, props);
		
		// ---------- B / A counters ---------- //
		// Delta week
		MyTimeSeries conWeekBACounterSerie = new MyTimeSeries("B/A counter week", Themes.GREEN_LIGHT, 1.5f, props,
				apiObject.getExpWeek().getOptions().getConBidAskCounterList()) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 2974336648337921958L;

			@Override
			public double getData() {
				return apiObject.getExpWeek().getOptions().getConBidAskCounter();
			}
		};
		
		// Delta month
		MyTimeSeries conMonthBACounterSerie = new MyTimeSeries("B/A counter month", Themes.GREEN, 1.5f, props,
				apiObject.getExpMonth().getOptions().getConBidAskCounterList()) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 2974336648337921958L;

			@Override
			public double getData() {
				return apiObject.getExpMonth().getOptions().getConBidAskCounter();
			}
		};
		
		series = new MyTimeSeries[2];
		series[0] = conWeekBACounterSerie;
		series[1] = conMonthBACounterSerie;
			
		// Chart
		MyChart bidAskCounterChart = new MyChart(series, props);
		
		// --------- Ind delta ---------- //

		// Index
		MyTimeSeries indDeltaSerie = new MyTimeSeries("Ind delta", Themes.BLUE, 1.5f, bottomChartProps,
				apiObject.getStocksHandler().getIndDeltaList()) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 2974336648337921958L;

			@Override
			public double getData() {
				return apiObject.getStocksHandler().getDelta();
			}
		};

		series = new MyTimeSeries[1];
		series[0] = indDeltaSerie;

		// Chart
		MyChart indDeltaChart = new MyChart(series, bottomChartProps);

		// -------------------- Chart -------------------- //

		// ----- Charts ----- //
		MyChart[] charts = { indexChart, deltaWeekChart, bidAskCounterChart, indDeltaChart };

		// ----- Container ----- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Delta chart");
		chartContainer.create();

	}

}
