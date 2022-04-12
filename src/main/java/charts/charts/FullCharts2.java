package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import dataBase.Factories;
import locals.Themes;

import java.awt.*;

public class FullCharts2 extends MyChartCreator {

    public static void main(String[] args) {
        FullCharts2 fullCharts2 = new FullCharts2(ApiObject.getInstance());
        fullCharts2.createChart();
    }

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
        props.setProp(ChartPropsEnum.SLEEP, 5000);
        props.setProp(ChartPropsEnum.IS_RANGE_GRID_VISIBLE, -1);
        props.setProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, (double) INFINITE);
        props.setProp(ChartPropsEnum.SECONDS_ON_MESS, 10);
        props.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, 1);
        props.setProp(ChartPropsEnum.IS_DOMAIN_GRID_VISIBLE, 1);
        props.setProp(ChartPropsEnum.MARKER, 0);

        // ----------------------------------------- Index ----------------------------------------- //

        // Index
        MyTimeSeries index_with_bid_ask_Serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_WITH_BID_ASK_SERIE);
        index_with_bid_ask_Serie.setColor(Color.BLACK);
        index_with_bid_ask_Serie.setStokeSize(1.2f);

        MyTimeSeries future_week_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.FUTURE_WEEK_SERIE);
        future_week_serie.setColor(Themes.GREEN);
        future_week_serie.setStokeSize(1.2f);

        series = new MyTimeSeries[2];
        series[0] = index_with_bid_ask_Serie;
        series[1] = future_week_serie;

        // Chart
        MyChart indexChart = new MyChart(series, props);

        // ----------------------------------------- OP AVG 2 ----------------------------------------- //
        // --------------- WEEK --------------- //

        // Op avg 5
        MyTimeSeries opavg_5_week = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_WEEK_5_SERIE);
        opavg_5_week.setColor(Themes.RED);
        opavg_5_week.setStokeSize(1.2f);

        // Op avg 15
        MyTimeSeries opavg_15_week = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_WEEK_15_SERIE);
        opavg_15_week.setColor(Themes.GREEN);
        opavg_15_week.setStokeSize(1.2f);

        // Op avg 60
        MyTimeSeries opavg_60_week = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.OP_AVG_WEEK_60_SERIE);
        opavg_60_week.setColor(Themes.BLUE);
        opavg_60_week.setStokeSize(1.2f);

        // Op avg 240 yesterday
        MyTimeSeries continue_opavg_240_week = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240_SERIE);
        continue_opavg_240_week.setColor(Themes.ORANGE);
        continue_opavg_240_week.setStokeSize(1.2f);

        series = new MyTimeSeries[4];
        series[0] = opavg_5_week;
        series[1] = opavg_15_week;
        series[2] = opavg_60_week;
        series[3] = continue_opavg_240_week;


        MyChart op_avg_chart = new MyChart(series, props);

        // ----------------------------------------- Op avg 60 15 ----------------------------------------- //
        // --------------- WEEK --------------- //
        // Op avg 240 yesterday
        MyTimeSeries v_103_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.V_103_SERIE);
        v_103_serie.setColor(Themes.ORANGE);
        v_103_serie.setStokeSize(1.2f);

        MyTimeSeries v_107_serie = MyTimeSeriesFactory.get_serie(Factories.TimeSeries.V_107_SERIE);
        v_107_serie.setColor(Themes.PURPLE);
        v_107_serie.setStokeSize(1.2f);


        series = new MyTimeSeries[2];
        series[0] = v_103_serie;
        series[1] = v_107_serie;

        // Chart
        MyChart df_chart = new MyChart(series, props);

        // ----------------------------------------- Chart ----------------------------------------- //

        // ----- Charts ----- //
        MyChart[] charts = {indexChart, op_avg_chart, df_chart};

        // ----------------------------------------- Container ----------------------------------------- //
        MyChartContainer chartContainer = new MyChartContainer(charts, "Full chart");
        chartContainer.create();

    }
}