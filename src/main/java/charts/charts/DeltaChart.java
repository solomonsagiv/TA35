package charts.charts;

import java.awt.Color;
import java.net.UnknownHostException;

import api.ApiObject;
import charts.myChart.ChartPropsEnum;
import charts.myChart.MyChart;
import charts.myChart.MyChartContainer;
import charts.myChart.MyChartCreator;
import charts.myChart.MyProps;
import charts.myChart.MyTimeSeries;
import locals.Themes;
import options.Options;

public class DeltaChart extends MyChartCreator {

	ApiObject apiObject = ApiObject.getInstance();

	// Constructor
	public DeltaChart(ApiObject client) {
		super(client);
	}

	@Override
    public void createChart() throws CloneNotSupportedException {

        MyTimeSeries[] series;
        
        // Props
        props = new MyProps();
        props.setProp( ChartPropsEnum.SECONDS, INFINITE );
        props.setProp( ChartPropsEnum.IS_INCLUDE_TICKER, false );
        props.setProp( ChartPropsEnum.MARGIN, .17 );
        props.setProp( ChartPropsEnum.RANGE_MARGIN, 0.0 );
        props.setProp( ChartPropsEnum.IS_GRID_VISIBLE, true );
        props.setProp( ChartPropsEnum.IS_LOAD_DB, true );
        props.setProp( ChartPropsEnum.IS_LIVE, false );
        props.setProp( ChartPropsEnum.SLEEP, 1000 );
        props.setProp( ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, (double) INFINITE);
        props.setProp( ChartPropsEnum.SECONDS_ON_MESS, 10 );
        props.setProp( ChartPropsEnum.INCLUDE_DOMAIN_AXIS, true );
        
        // --------- Chart 1 ---------- //
        MyProps newProps = ( MyProps ) props.clone();
        newProps.setProp( ChartPropsEnum.INCLUDE_DOMAIN_AXIS, false );

        // Index
        MyTimeSeries index = new MyTimeSeries( "Index", Color.BLACK, 1.5f, newProps, apiObject.getIndexChartList() ) {
            @Override
            public double getData() throws UnknownHostException {
                return apiObject.getIndexChartList().getLast().getY();
            }
        };
        
        series = new MyTimeSeries[1];
        series[0] = index;

        // Chart
        MyChart indexChart = new MyChart( series, newProps );
        
        // ---------- Chart 2 ---------- //

        Options optionsWeek = apiObject.getExpWeek().getOptions();
        
        // Month delta 
        MyTimeSeries weekDeltaSerie = new MyTimeSeries( "Delta week ", Themes.LIGHT_BLUE_3, 1.5f, newProps, optionsWeek.getDeltaChartList() ) {
            @Override
            public double getData() throws UnknownHostException {
                return optionsWeek.getDeltaChartList().getLast().getY();
            };
        };
        
        series = new MyTimeSeries[1];
        series[0] = weekDeltaSerie;
        
        MyChart deltaWeekChart = new MyChart( series, newProps );
        
        
        // ---------- Chart 3 ---------- //

        Options optionsMonth = apiObject.getExpMonth().getOptions();
        
        // Month delta 
        MyTimeSeries monthDeltaSerie = new MyTimeSeries( "Delta month", Themes.LIGHT_BLUE_4, 1.5f, newProps, optionsMonth.getDeltaChartList() ) {
            @Override
            public double getData() throws UnknownHostException {
                return optionsMonth.getDeltaChartList().getLast().getY();
            };
        };
        
        series = new MyTimeSeries[1];
        series[0] = monthDeltaSerie;

        MyChart deltaMonthChart = new MyChart( series, newProps );

        // -------------------- Chart -------------------- //

        // ----- Charts ----- //
        MyChart[] charts = { indexChart, deltaWeekChart, deltaMonthChart };

        // ----- Container ----- //
        MyChartContainer chartContainer = new MyChartContainer( charts, getClass().getName() );
        chartContainer.create();


    }

}
