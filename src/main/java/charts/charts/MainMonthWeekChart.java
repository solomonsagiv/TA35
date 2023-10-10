package charts.charts;

import api.ApiObject;
import charts.myChart.*;
import locals.Themes;
import java.awt.*;

public class MainMonthWeekChart extends MyChartCreator {

    // Constructor
    public MainMonthWeekChart(ApiObject apiObject) {
        super(apiObject);
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
        MyTimeSeries index = new MyTimeSeries("Index") {
            @Override
            public double getValue() {
                return apiObject.getIndex();
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

        // Index
        MyTimeSeries index_bid_ask = new MyTimeSeries("Index bid ask avg") {

            @Override
            public double getValue() {
                return (apiObject.getIndex_bid() + apiObject.getIndex_ask()) / 2;
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
        MyTimeSeries bid = new MyTimeSeries("Bid") {

            @Override
            public double getValue() {
                return apiObject.getIndex_bid();
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
        MyTimeSeries ask = new MyTimeSeries("Ask") {

            @Override
            public double getValue() {
                return apiObject.getIndex_ask();
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
        MyTimeSeries future = new MyTimeSeries("Future") {
            @Override
            public double getValue() {
                return apiObject.getExps().getMonth().getOptions().getContract();
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
        MyTimeSeries futureWeek = new MyTimeSeries("Future week") {
            @Override
            public double getValue() {
                return apiObject.getExps().getWeek().getOptions().getContract();
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
