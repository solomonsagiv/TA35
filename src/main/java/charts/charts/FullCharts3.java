package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import dataBase.DataBaseHandler;
import dataBase.Factories;
import dataBase.mySql.Queries;
import locals.Themes;

import java.awt.*;
import java.sql.ResultSet;

public class FullCharts3 extends MyChartCreator {

    public static void main(String[] args) {
        FullCharts3 fullCharts2 = new FullCharts3(ApiObject.getInstance());
        fullCharts2.createChart();
    }

    // Constructor
    public FullCharts3(ApiObject apiObject) {
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

        // ----------------------------------------- OP AVG 2 ----------------------------------------- //
        // --------------- WEEK --------------- //

        // Op avg 5
        MyTimeSeries opavg_5_week = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_5);
        opavg_5_week.setColor(Themes.RED);
        opavg_5_week.setStokeSize(1.2f);

        // Op avg 60
        MyTimeSeries opavg_60_week = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_60);
        opavg_60_week.setColor(Themes.BLUE);
        opavg_60_week.setStokeSize(1.2f);

        // Op avg 240 yesterday
        MyTimeSeries continue_opavg_240_week = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240);
        continue_opavg_240_week.setColor(Themes.ORANGE);
        continue_opavg_240_week.setStokeSize(1.2f);

        series = new MyTimeSeries[3];
        series[0] = opavg_5_week;
        series[1] = opavg_60_week;
        series[2] = continue_opavg_240_week;


        MyChart op_avg_chart = new MyChart(series, props);

        // --------------- ROLL --------------- //

        // Op avg 5
        MyTimeSeries roll_3600 = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.ROLL_3600);
        roll_3600.setColor(Themes.BLUE3);
        roll_3600.setStokeSize(1.2f);
        
        // Op avg 60
        MyTimeSeries roll_900 = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.ROLL_900);
        roll_900.setColor(Themes.PURPLE);
        roll_900.setStokeSize(1.2f);

        series = new MyTimeSeries[2];
        series[0] = roll_3600;
        series[1] = roll_900;


        MyChart roll_chart = new MyChart(series, props);


        // ----------------------------------------- DF ----------------------------------------- //

        MyTimeSeries df_5_old = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.DF_5_CDF_OLD);
        df_5_old.setColor(Themes.ORANGE);
        df_5_old.setStokeSize(1.2f);

        // Op avg 60
        MyTimeSeries df_6_old = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.DF_6_CDF_OLD);
        df_6_old.setColor(Themes.PURPLE);
        df_6_old.setStokeSize(1.2f);

        // Op avg 240 yesterday
        MyTimeSeries df_9 = ApiObject.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF);
        df_9.setColor(Themes.RED);
        df_9.setStokeSize(1.2f);

        series = new MyTimeSeries[3];
        series[0] = df_5_old;
        series[1] = df_6_old;
        series[2] = df_9;


        MyChart df_chart = new MyChart(series, props);


        // ----------------------------------------- Chart ----------------------------------------- //

        // ----- Charts ----- //
        MyChart[] charts = {indexChart, op_avg_chart, roll_chart, df_chart};

        // ----------------------------------------- Container ----------------------------------------- //
        MyChartContainer chartContainer = new MyChartContainer(charts, "Full chart");
        chartContainer.create();

    }
}