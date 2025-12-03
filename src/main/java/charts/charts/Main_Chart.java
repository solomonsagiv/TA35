package charts.charts;

import charts.myChart.*;
import dataBase.Factories;
import dataBase.IDataBaseHandler;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import locals.Themes;
import api.TA35;
import api.deltaTest.Calculator;
import org.jfree.chart.plot.ValueMarker;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class Main_Chart extends MyChartCreator {

    // Constructor
    public Main_Chart(TA35 ta35) {
        super(ta35);
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

        // ----------------------------------------- Index
        // ----------------------------------------- //

        // Index
        MyTimeSeries index_with_bid_ask_Serie = TimeSeriesFactory.get_serie(Factories.TimeSeries.MID_DEV, client);
        index_with_bid_ask_Serie.setColor(Color.BLACK);
        index_with_bid_ask_Serie.setStokeSize(1.2f);

        // Index avg 3600
        MyTimeSeries index_avg_3600_serie = TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_AVG_3600, client);
        index_avg_3600_serie.setColor(Themes.PURPLE);
        index_avg_3600_serie.setStokeSize(0.75f);

        // Index avg 900
        MyTimeSeries index_avg_900_serie = TimeSeriesFactory.get_serie(Factories.TimeSeries.INDEX_AVG_900, client);
        index_avg_900_serie.setColor(Themes.RED);
        index_avg_900_serie.setStokeSize(0.75f);

        MyTimeSeries future_week_serie = new MyTimeSeries(Factories.TimeSeries.FUTURE_WEEK, client) {

            @Override
            public double getValue() {
                return TA35.getInstance().getExps().getWeek().getOptions().getContract();
            }

            @Override
            public void load() {
                int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.FUTURE_WEEK);
                List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, Queries.RAW,
                        MySql.JIBE_PROD_CONNECTION);
                IDataBaseHandler.loadSerieData(rs, this);
            }

            @Override
            public void updateData() {
            }
        };
        future_week_serie.setColor(Themes.GREEN);
        future_week_serie.setStokeSize(1.2f);

        MyTimeSeries future_month_serie = new MyTimeSeries(Factories.TimeSeries.FUTURE_MONTH, client) {

            @Override
            public double getValue() {
                return TA35.getInstance().getExps().getMonth().getOptions().getContract();
            }

            @Override
            public void load() {
                int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.FUTURE_MONTH);
                List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, Queries.RAW,
                        MySql.JIBE_PROD_CONNECTION);
                IDataBaseHandler.loadSerieData(rs, this);
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

        // ----------------------------------------- OP AVG 2
        // ----------------------------------------- //
        // --------------- WEEK --------------- //

        // Op avg 5
        MyTimeSeries opavg_5_week = TA35.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_15);
        opavg_5_week.setColor(Themes.RED);
        opavg_5_week.setStokeSize(1.2f);

        // Op avg 60
        MyTimeSeries opavg_60_week = TA35.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.OP_AVG_WEEK_60);
        opavg_60_week.setColor(Themes.BLUE);
        opavg_60_week.setStokeSize(1.2f);

        // Op avg 240 yesterday
        MyTimeSeries continue_opavg_240_week = TA35.getInstance().getTimeSeriesHandler()
                .get(Factories.TimeSeries.OP_AVG_240_CONTINUE);
        continue_opavg_240_week.setColor(Themes.ORANGE);
        continue_opavg_240_week.setStokeSize(1.2f);

        series = new MyTimeSeries[3];
        series[0] = opavg_5_week;
        series[1] = opavg_60_week;
        series[2] = continue_opavg_240_week;

        ValueMarker plus0_3 = new ValueMarker(0.3);
        plus0_3.setStroke(new BasicStroke(1.5f));

        ValueMarker minus0_3 = new ValueMarker(-0.3);
        minus0_3.setStroke(new BasicStroke(1.5f));

        MyChart op_avg_chart = new MyChart(series, props);
        op_avg_chart.add_marker(plus0_3);
        op_avg_chart.add_marker(minus0_3);

        // ----------------------------------------- Races
        // ----------------------------------------- //
        // Index races wi
        MyTimeSeries index_races_wi = TA35.getInstance().getTimeSeriesHandler()
                .get(Factories.TimeSeries.INDEX_RACES_WI);
        index_races_wi.setColor(Themes.ORANGE);
        index_races_wi.setStokeSize(1.2f);

        // Index races wi
        MyTimeSeries month_races_wm = TA35.getInstance().getTimeSeriesHandler()
                .get(Factories.TimeSeries.MONTH_RACES_WM);
        month_races_wm.setColor(Themes.RED);
        month_races_wm.setStokeSize(1.2f);

        series = new MyTimeSeries[2];
        series[0] = index_races_wi;
        series[1] = month_races_wm;

        MyChart races_chart = new MyChart(series, props);

        // ----------------------------------------- Bid ask counter
        // ----------------------------------------- //
        // Week counter
        MyTimeSeries week_counter = TA35.getInstance().getTimeSeriesHandler()
                .get(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD);
        week_counter.setColor(Themes.GREEN);
        week_counter.setStokeSize(1.2f);

        // Month counter
        MyTimeSeries month_counter = TA35.getInstance().getTimeSeriesHandler()
                .get(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD);
        month_counter.setColor(Themes.GREEN_7);
        month_counter.setStokeSize(1.2f);

        series = new MyTimeSeries[2];
        series[0] = week_counter;
        series[1] = month_counter;

        MyChart bid_ask_counter_chart = new MyChart(series, props);

        // ----------------------------------------- Stocks counter
        // ----------------------------------------- //

        MyTimeSeries total_delta = TA35.getInstance().getTimeSeriesHandler()
                .get(Factories.TimeSeries.TOTAL_DELTA);
        total_delta.setColor(Themes.OPEN_RACE);
        total_delta.setStokeSize(1.2f);

        series = new MyTimeSeries[1];
        series[0] = total_delta;

        MyChart total_delta_chart = new MyChart(series, props);

        // ----------------------------------------- Counter 2 table avg
        // ----------------------------------------- //

        MyTimeSeries counter_2_table_avg = TA35.getInstance().getTimeSeriesHandler().get(Factories.TimeSeries.COUNTER_2_TOT_WEIGHT_PROD);
        counter_2_table_avg.setColor(Themes.GREEN);
        counter_2_table_avg.setStokeSize(1.2f);

        series = new MyTimeSeries[1];
        series[0] = counter_2_table_avg;
        
        // Chart
        MyChart counter_2_table_avg_chart = new MyChart(series, props);

        // ----------------------------------------- Chart
        // ----------------------------------------- //

        // ----- Charts ----- //
        MyChart[] charts = { indexChart, op_avg_chart, races_chart, bid_ask_counter_chart, total_delta_chart,
                counter_2_table_avg_chart };

        // ----------------------------------------- Container
        // ----------------------------------------- //
        MyChartContainer chartContainer = new MyChartContainer(charts, "Main chart");
        chartContainer.create();

    }
}