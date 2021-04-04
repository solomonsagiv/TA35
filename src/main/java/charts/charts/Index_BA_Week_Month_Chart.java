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

public class Index_BA_Week_Month_Chart extends MyChartCreator {
	
	// Constructor
	public Index_BA_Week_Month_Chart(ApiObject apiObject) {
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

		// --------- Future BidAskCounter ---------- //

		// Index
		MyTimeSeries ConBidAskCounterWeek = new MyTimeSeries("ConBidAskCounterWeek", Themes.BLUE, 1.5f, props,
				apiObject.getExpWeek().getOptions().getConBidAskCounterList()) {
			/**
					 * 
					 */
			private static final long serialVersionUID = -1190459994466810207L;

			@Override
			public double getData() {
				return apiObject.getExpWeek().getOptions().getConBidAskCounter();
			}
		};
		
		series = new MyTimeSeries[1];
		series[0] = ConBidAskCounterWeek;

		// Chart
		MyChart conBidAskCounterChart = new MyChart(series, props);
		
		
		// --------- Future BidAskCounter ---------- //
		MyTimeSeries conBidAskCounterMonth = new MyTimeSeries("ConBidAskCounterMonth", Themes.BLUE, 1.5f, bottomChartProps,
				apiObject.getExpMonth().getOptions().getConBidAskCounterList()) {
			/**
					 * 
					 */
			private static final long serialVersionUID = -1190459994466810207L;

			@Override
			public double getData() {
				return apiObject.getExpMonth().getOptions().getConBidAskCounter();
			}
		};
		
		series = new MyTimeSeries[1];
		series[0] = conBidAskCounterMonth;

		// Chart
		MyChart conBidAskCounterMonthChart = new MyChart(series, props);
		
		// -------------------- Chart -------------------- //
		
		// ----- Charts ----- //
		MyChart[] charts = { indexChart, conBidAskCounterChart, conBidAskCounterMonthChart };

		// ----- Container ----- //
		MyChartContainer chartContainer = new MyChartContainer(charts, "Index week Month BA Chart");
		chartContainer.create();

	}

}
