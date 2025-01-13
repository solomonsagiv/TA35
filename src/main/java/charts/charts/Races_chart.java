package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import dataBase.DataBaseHandler;
import dataBase.Factories;
import dataBase.mySql.Queries;
import locals.Themes;
import org.jfree.chart.plot.ValueMarker;

import java.awt.*;
import java.sql.ResultSet;

public class Races_chart extends MyChartCreator {

    public static void main(String[] args) {
        Races_chart fullCharts2 = new Races_chart(ApiObject.getInstance());
        fullCharts2.createChart();
    }

    // Constructor
    public Races_chart(ApiObject apiObject) {
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
        MyTimeSeries index_with_bid_ask_Serie = TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_WITH_BID_ASK);
        index_with_bid_ask_Serie.setColor(Color.BLACK);
        index_with_bid_ask_Serie.setStokeSize(1.2f);

        // Index avg 3600
        MyTimeSeries index_avg_3600_serie = TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_AVG_3600);
        index_avg_3600_serie.setColor(Themes.PURPLE);
        index_avg_3600_serie.setStokeSize(0.75f);

        // Index avg 900
        MyTimeSeries index_avg_900_serie = TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_AVG_900);
        index_avg_900_serie.setColor(Themes.RED);
        index_avg_900_serie.setStokeSize(0.75f);

        MyTimeSeries future_week_serie =  new MyTimeSeries(Factories.TimeSeries.FUTURE_WEEK) {

            @Override
            public double getValue() {
                return ApiObject.getInstance().getExps().getWeek().getOptions().getContract();
            }

            @Override
            public void load() {
                ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.FUT_WEEK, Queries.RAW);
                DataBaseHandler.loadSerieData(rs, this);
            }

            @Override
            public void updateData() {
            }
        };
        future_week_serie.setColor(Themes.GREEN);
        future_week_serie.setStokeSize(1.2f);

        MyTimeSeries future_month_serie =  new MyTimeSeries(Factories.TimeSeries.FUTURE_MONTH) {

            @Override
            public double getValue() {
                return ApiObject.getInstance().getExps().getMonth().getOptions().getContract();
            }

            @Override
            public void load() {
                ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.FUT_WEEK, Queries.RAW);
                DataBaseHandler.loadSerieData(rs, this);
            }

            @Override
            public void updateData() {
            }
        };
        future_month_serie.setColor(Themes.GREEN);
        future_month_serie.setStokeSize(1.2f);

        series = new MyTimeSeries[5];
        series[0] = index_with_bid_ask_Serie;
        series[1] = future_week_serie;
        series[2] = future_month_serie;
        series[3] = index_avg_3600_serie;
        series[4] = index_avg_900_serie;

        // Chart
        MyChart indexChart = new MyChart(series, props);

        // ----------------------------------------- Victor race ----------------------------------------- //

        MyTimeSeries victor_index_races = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_INDEX_RACES);
        victor_index_races.setColor(Themes.ORANGE);
        victor_index_races.setStokeSize(1.2f);

        MyTimeSeries victor_future_races = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_FUTURE_RACES);
        victor_future_races.setColor(Themes.PURPLE);
        victor_future_races.setStokeSize(1.2f);

        MyTimeSeries victore_roll_races = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_ROLL_RACES);
        victore_roll_races.setColor(Themes.RED);
        victore_roll_races.setStokeSize(1.2f);


        series = new MyTimeSeries[3];
        series[0] = victor_index_races;
        series[1] = victor_future_races;
        series[2] = victore_roll_races;

        MyChart victor_races_chart = new MyChart(series, props);

        // ----------------------------------------- Victor race ratio ----------------------------------------- //

        MyTimeSeries victor_index_races_ratio = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_INDEX_RACES_RATIO);
        victor_index_races_ratio.setColor(Themes.ORANGE);
        victor_index_races_ratio.setStokeSize(1.2f);

        MyTimeSeries victor_roll_races_ratio = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.VICTOR_ROLL_RACES_RATIO);
        victor_roll_races_ratio.setColor(Themes.RED);
        victor_roll_races_ratio.setStokeSize(1.2f);

        MyTimeSeries plus_03 = new MyTimeSeries("0.3") {
            @Override
            public void updateData() {
            }

            @Override
            public double getValue() {
                return 0.5;
            }

            @Override
            public void load() {

            }

        };
        plus_03.setColor(Color.BLACK);
        plus_03.setStokeSize(2f);
        plus_03.setVisible(false);

        MyTimeSeries minus_03 = new MyTimeSeries("-0.3") {
            @Override
            public void updateData() {
            }

            @Override
            public double getValue() {
                return -0.5;
            }

            @Override
            public void load() {

            }
        };
        minus_03.setColor(Color.BLACK);
        minus_03.setStokeSize(2f);
        minus_03.setVisible(false);


        series = new MyTimeSeries[4];
        series[0] = victor_index_races_ratio;
        series[1] = victor_roll_races_ratio;
        series[2] = plus_03;
        series[3] = minus_03;

        MyChart victor_races_ratio_chart = new MyChart(series, props);
        ValueMarker plus = new ValueMarker(0.3);
        plus.setStroke(new BasicStroke(2f));

        ValueMarker minus = new ValueMarker(-0.3);
        minus.setStroke(new BasicStroke(2f));

        victor_races_ratio_chart.add_marker(plus);
        victor_races_ratio_chart.add_marker(minus);

        // ----------------------------------------- Races ----------------------------------------- //

        // Index races wi
        MyTimeSeries index_races_wi = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.INDEX_RACES_WI);
        index_races_wi.setColor(Themes.ORANGE);
        index_races_wi.setStokeSize(1.2f);

        // Week races wi
        MyTimeSeries week_races_wi = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.WEEK_RACES_WI);
        week_races_wi.setColor(Themes.PURPLE);
        week_races_wi.setStokeSize(1.2f);

        // Index races wi
        MyTimeSeries month_races_wm = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.MONTH_RACES_WM);
        month_races_wm.setColor(Themes.RED);
        month_races_wm.setStokeSize(1.2f);


        series = new MyTimeSeries[3];
        series[0] = index_races_wi;
        series[1] = week_races_wi;
        series[2] = month_races_wm;

        MyChart races_chart = new MyChart(series, props);

        // ----------------------------------------- BID ASK RACES ----------------------------------------- //

        // Index races wi
        MyTimeSeries bid_races = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.BID_RACES_BA);
        bid_races.setColor(Themes.BLUE);
        bid_races.setStokeSize(1.2f);

        // Week races wi
        MyTimeSeries ask_races = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.ASK_RACES_BA);
        ask_races.setColor(Themes.RED);
        ask_races.setStokeSize(1.2f);

        series = new MyTimeSeries[2];
        series[0] = bid_races;
        series[1] = ask_races;

        MyChart bid_ask_races_chart = new MyChart(series, props);

        // ----------------------------------------- Chart ----------------------------------------- //

        // ----- Charts ----- //
        MyChart[] charts = {indexChart, victor_races_chart, victor_races_ratio_chart, races_chart, bid_ask_races_chart};

        // ----------------------------------------- Container ----------------------------------------- //
        MyChartContainer chartContainer = new MyChartContainer(charts, "Races chart");
        chartContainer.create();

    }
}