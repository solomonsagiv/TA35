package charts.charts;

import api.TA35;
import charts.myChart.*;
import locals.Themes;

public class Realtime_Interest_Chart extends MyChartCreator {

    TA35 client;

    // Constructor
    public Realtime_Interest_Chart(TA35 client) {
        super(client);
        this.client = client;
    }

    @SuppressWarnings("serial")
    @Override
    public void init() throws CloneNotSupportedException {

        // Props
        props = new MyProps();
        props.setProp(ChartPropsEnum.SECONDS, 900);
        props.setProp(ChartPropsEnum.IS_INCLUDE_TICKER, -1);
        props.setProp(ChartPropsEnum.IS_GRID_VISIBLE, -1);
        props.setProp(ChartPropsEnum.IS_LOAD_DB, -1);
        props.setProp(ChartPropsEnum.IS_LIVE, 1);
        props.setProp(ChartPropsEnum.SLEEP, 200);
        props.setProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS, (double) INFINITE);
        props.setProp(ChartPropsEnum.SECONDS_ON_MESS, 10);

        // ----- Chart 1 ----- //
        // Index
        MyTimeSeries op_cost = new MyTimeSeries("O/P Cost", client) {
            @Override
            public double getValue() {
                return client.getOp_month_interest() + client.getMid();
            }

            @Override
            public void load() {

            }

            @Override
            public void updateData() {

            }
        };
        op_cost.setColor(Themes.GREEN);
        op_cost.setStokeSize(1.25f);

        // Index
        MyTimeSeries roll_cost = new MyTimeSeries("Roll cost", client) {

            @Override
            public double getValue() {
                return client.getRoll_interest() + client.getMid();
            }

            @Override
            public void load() {

            }

            @Override
            public void updateData() {

            }
        };
        roll_cost.setColor(Themes.BLACK);
        roll_cost.setStokeSize(1.25f);

        // Bid
        MyTimeSeries bid = new MyTimeSeries("Bid", client) {

            @Override
            public double getValue() {
                return client.getBid();
            }

            @Override
            public void load() {

            }

            @Override
            public void updateData() {

            }
        };
        bid.setColor(Themes.BLUE);
        bid.setStokeSize(1.25f);
        bid.setVisible(false);

        // Ask
        MyTimeSeries ask = new MyTimeSeries("Ask", client) {

            @Override
            public double getValue() {
                return client.getAsk();
            }

            @Override
            public void load() {

            }

            @Override
            public void updateData() {

            }
        };
        ask.setColor(Themes.RED);
        ask.setStokeSize(1.25f);
        ask.setVisible(false);

        MyTimeSeries[] series = {op_cost, roll_cost, bid, ask};

        // Chart
        MyChart chart = new MyChart(series, props);

        // ----- Charts ----- //
        MyChart[] charts = {chart};

        // ----- Container ----- //
        MyChartContainer chartContainer = new MyChartContainer(charts, "Real time costs");
        chartContainer.create();

    }

}
