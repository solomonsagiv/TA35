package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import locals.Themes;
import java.awt.*;
import java.sql.ResultSet;

public class FullChartsShort extends MyChartCreator {

    public static void main(String[] args) {
        FullChartsShort fullCharts2 = new FullChartsShort(ApiObject.getInstance());
        fullCharts2.createChart();
    }

    // Constructor
    public FullChartsShort(ApiObject apiObject) {
        super(apiObject);
    }

    @Override
    public void init() throws CloneNotSupportedException {

        MyTimeSeries[] series;

        // Props
        props = new MyProps();
        props.setProp(ChartPropsEnum.SECONDS, 1800);
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
        MyTimeSeries indexSerie = new MyTimeSeries("Index avg bid ask") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return (apiObject.getIndex_bid() + apiObject.getIndex_ask()) / 2;
            }

            @Override
            public void load() {

            }
        };
        indexSerie.setColor(Color.BLACK);
        indexSerie.setStokeSize(1.2f);

        series = new MyTimeSeries[1];
        series[0] = indexSerie;

        // Chart
        MyChart indexChart = new MyChart(series, props);

        // ----------------------------------------- Deltas ----------------------------------------- //
        // Delta week
        MyTimeSeries delta_week_serie = new MyTimeSeries("Delta week") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return apiObject.getExps().getWeek().getOptions().getTotal_delta();
            }

            @Override
            public void load() {
            }
        };
        delta_week_serie.setColor(Themes.GREEN_6);
        delta_week_serie.setStokeSize(1.2f);

        // Delta month
        MyTimeSeries delta_month_serie = new MyTimeSeries("Delta month") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return apiObject.getExps().getMonth().getOptions().getTotal_delta();
            }

            @Override
            public void load() {

            }
        };
        delta_month_serie.setColor(Themes.GREEN_5);
        delta_month_serie.setVisible(false);
        delta_month_serie.setStokeSize(1.2f);

        series = new MyTimeSeries[2];
        series[0] = delta_week_serie;
        series[1] = delta_month_serie;

        // Chart
        MyChart deltaChart = new MyChart(series, props);

        // ----------------------------------------- Bid ask counter ----------------------------------------- //
        // Counter week
        MyTimeSeries bid_ask_counter_week_serie = new MyTimeSeries("Bid ask counter week") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return apiObject.getExps().getWeek().getOptions().getConBidAskCounter();
            }

            @Override
            public void load() {

            }
        };
        bid_ask_counter_week_serie.setColor(Themes.GREEN_6);
        bid_ask_counter_week_serie.setStokeSize(1.2f);

        // Counter month
        MyTimeSeries bid_ask_counter_month_serie = new MyTimeSeries("Bid ask counter month") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return apiObject.getExps().getMonth().getOptions().getConBidAskCounter();
            }

            @Override
            public void load() {

            }
        };
        bid_ask_counter_month_serie.setVisible(false);
        bid_ask_counter_month_serie.setColor(Themes.GREEN_5);
        bid_ask_counter_month_serie.setStokeSize(1.2f);

        series = new MyTimeSeries[2];
        series[0] = bid_ask_counter_week_serie;
        series[1] = bid_ask_counter_month_serie;

        // Chart
        MyChart bid_ask_counter_chart = new MyChart(series, props);
        MyProps props_2 = (MyProps) props.clone();
        props_2.setProp(ChartPropsEnum.INCLUDE_DOMAIN_AXIS, 1);

        // ----------------------------------------- Op avg 1, 5 ----------------------------------------- //
        // Op avg 60
        MyTimeSeries opavg_1_week = new MyTimeSeries("Op avg week 1") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return apiObject.getExps().getWeek().getOp_avg_1();
            }

            @Override
            public void load() {

            }
        };
        opavg_1_week.setColor(Themes.BLUE_2);
        opavg_1_week.setStokeSize(1.2f);

        // Op avg 5
        MyTimeSeries opavg_5_week = new MyTimeSeries("Op avg week 5") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return apiObject.getExps().getWeek().getOp_avg_5();
            }

            @Override
            public void load() {

            }
        };
        opavg_5_week.setColor(Themes.GREEN);
        opavg_5_week.setStokeSize(1.2f);

        // Op avg 15
        MyTimeSeries opavg_15_week = new MyTimeSeries("Op avg week 15") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return apiObject.getExps().getMonth().getContinue_op_avg_15();
            }

            @Override
            public void load() {

            }
        };
        opavg_15_week.setColor(Themes.BLUE_2);
        opavg_15_week.setVisible(false);
        opavg_15_week.setStokeSize(1.2f);

        // Op avg 60
        MyTimeSeries opavg_60_week = new MyTimeSeries("Op avg week 60") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return apiObject.getExps().getMonth().getOp_avg_60();
            }

            @Override
            public void load() {

            }
        };
        opavg_60_week.setColor(Themes.GREEN);
        opavg_60_week.setVisible(false);
        opavg_60_week.setStokeSize(1.2f);


        // Op avg 15
        MyTimeSeries opavg_240_week = new MyTimeSeries("Op avg week 60") {
            @Override
            public ResultSet load_last_x_time(int minuts) {
                return null;
            }

            @Override
            public double getData() {
                return apiObject.getExps().getMonth().getContinue_op_avg_240();
            }

            @Override
            public void load() {

            }
        };
        opavg_240_week.setColor(Themes.ORANGE_2);
        opavg_240_week.setVisible(false);
        opavg_240_week.setStokeSize(1.2f);

        series = new MyTimeSeries[5];
        series[0] = opavg_1_week;
        series[1] = opavg_5_week;
        series[2] = opavg_15_week;
        series[3] = opavg_60_week;
        series[4] = opavg_240_week;

        // Chart
        MyChart opavg_chart = new MyChart(series, props);

        // ----------------------------------------- Chart ----------------------------------------- //

        // ----- Charts ----- //
        MyChart[] charts = {indexChart, opavg_chart, bid_ask_counter_chart, deltaChart};

        // ----------------------------------------- Container ----------------------------------------- //
        MyChartContainer chartContainer = new MyChartContainer(charts, "Full chart");
        chartContainer.create();

    }
}