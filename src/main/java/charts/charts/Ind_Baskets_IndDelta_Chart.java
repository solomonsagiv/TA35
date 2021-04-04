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

public class Ind_Baskets_IndDelta_Chart extends MyChartCreator {
	
	// Constructor
	public Ind_Baskets_IndDelta_Chart(ApiObject apiObject) {
		super(apiObject);
	}
	
	@Override
	public void createChart() throws CloneNotSupportedException {
		
		MyTimeSeries[] series;
		
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
		
		// --------- Index ---------- //
		MyProps bottomChartProps = (MyProps) props.clone();
		ValueMarker marker = new ValueMarker(0);
		marker.setPaint(Color.BLACK);
		marker.setStroke(new BasicStroke(2f));
		bottomChartProps.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, true);
		
		// Index
		MyTimeSeries indexSerie = new MyTimeSeries("Index", Color.BLACK, 1.5f, props,
				apiObject.getIndexChartList()) {
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

		// --------- Baskets ---------- //
		
		// Index
		MyTimeSeries conBidAskCounter = new MyTimeSeries("Baskets", Themes.RED_2, 1.5f, props,
				apiObject.getIndBasketsList()) {
			/**
					 * 
					 */
			private static final long serialVersionUID = -1190459994466810207L;

			@Override
			public double getData() {
				return apiObject.getBasketUp() - apiObject.getBasketDown();
			}
		};
		
		series = new MyTimeSeries[1];
		series[0] = conBidAskCounter;

		// Chart
		MyChart conBidAskCounterChart = new MyChart(series, props);
		
		// --------- Ind Delta ---------- //
		
		// Index
		MyTimeSeries deltaSerie = new MyTimeSeries("Ind delta", Themes.LIGHT_BLUE_3, 1.5f, bottomChartProps,
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
		series[0] = deltaSerie;

		// Chart
		MyChart deltaChart = new MyChart(series, bottomChartProps);

		// -------------------- Chart -------------------- //

		// ----- Charts ----- //
		MyChart[] charts = { indexChart, conBidAskCounterChart, deltaChart };

		// ----- Container ----- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Ind baskets indDelta Chart");
		chartContainer.create();

	}

}
