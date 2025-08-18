package charts.charts;

import api.TA35;
import charts.myChart.*;
import locals.Themes;
import java.awt.*;

public class    Realtime_Chart extends MyChartCreator {

    TA35 client;

    // Constructor
    public Realtime_Chart(TA35 client) {
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
        MyTimeSeries index = new MyTimeSeries("Index", client) {
            @Override
            public double getValue() {
                return client.getIndex();
            }

            @Override
            public void load() {

            }

            @Override
            public void updateData() {

            }
        };
        index.setColor(Color.BLACK);
        index.setStokeSize(2.25f);
        index.setVisible(false);

        // Index
        MyTimeSeries index_bid_ask = new MyTimeSeries("Index bid ask avg", client) {

            @Override
            public double getValue() {
                return (client.getBid() + client.getAsk()) / 2;
            }

            @Override
            public void load() {

            }

            @Override
            public void updateData() {

            }
        };
        index_bid_ask.setColor(Themes.GREY_2);
        index_bid_ask.setStokeSize(2.25f);

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
        bid.setStokeSize(2.25f);

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
        ask.setStokeSize(2.25f);

        // Future
        MyTimeSeries future = new MyTimeSeries("Future", client) {
            @Override
            public double getValue() {
                return client.getExps().getMonth().getOptions().getContract();
            }

            @Override
            public void load() {

            }

            @Override
            public void updateData() {

            }
        };
        future.setColor(Themes.GREEN);
        future.setStokeSize(2.25f);

        // Future
        MyTimeSeries futureWeek = new MyTimeSeries("Future week", client) {
            @Override
            public double getValue() {
                return client.getExps().getWeek().getOptions().getContract();
            }

            @Override
            public void load() {

            }

            @Override
            public void updateData() {

            }
        };
        futureWeek.setColor(Themes.GREEN_7);
        futureWeek.setStokeSize(2.25f);

        MyTimeSeries[] series = {index, index_bid_ask, bid, ask, future, futureWeek};

        // Chart
        MyChart chart = new MyChart(series, props);

        // ----- Charts ----- //
        MyChart[] charts = {chart};

        // ----- Container ----- //
        MyChartContainer chartContainer = new MyChartContainer(charts, "Month main chart");
        chartContainer.create();

    }

}
