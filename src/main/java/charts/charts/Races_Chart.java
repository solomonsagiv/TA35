package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import dataBase.DataBaseHandler;
import dataBase.Factories;
import dataBase.mySql.Queries;
import locals.Themes;

import java.awt.*;
import java.sql.ResultSet;

public class Races_Chart extends MyChartCreator {

    public static void main(String[] args) {
        Races_Chart fullCharts2 = new Races_Chart(ApiObject.getInstance());
        fullCharts2.createChart();
    }

    // Constructor
    public Races_Chart(ApiObject apiObject) {
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

        // ----------------------------------------- Races ----------------------------------------- //

        // Index races wi
        MyTimeSeries index_races_wi = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.INDEX_RACES_WI);
        index_races_wi.setColor(Themes.ORANGE);
        index_races_wi.setStokeSize(1.2f);

        // Week races wi
        MyTimeSeries week_races_wi = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.WEEK_RACES_WI);
        week_races_wi.setColor(Themes.PURPLE);
        week_races_wi.setStokeSize(1.2f);

        series = new MyTimeSeries[2];
        series[0] = index_races_wi;
        series[1] = week_races_wi;


        MyChart races_chart = new MyChart(series, props);

        // ----------------------------------------- Races ----------------------------------------- //

        // Index races wi
        MyTimeSeries r1_minus_r2 = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.R1_MINUS_R2_IQ);
        r1_minus_r2.setColor(Themes.RED);
        r1_minus_r2.setStokeSize(1.2f);

        // Week races wi
        series = new MyTimeSeries[1];
        series[0] = r1_minus_r2;


        MyChart minus_chart = new MyChart(series, props);


        // ----------------------------------------- Chart ----------------------------------------- //

        // ----- Charts ----- //
        MyChart[] charts = {indexChart, races_chart, minus_chart};

        // ----------------------------------------- Container ----------------------------------------- //
        MyChartContainer chartContainer = new MyChartContainer(charts, "Full chart");
        chartContainer.create();

    }
}