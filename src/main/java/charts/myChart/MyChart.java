package charts.myChart;


import api.ApiObject;
import charts.MyChartPanel;
import dataBase.DataBaseHandler;
import locals.L;
import locals.Themes;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;
import threads.MyThread;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class MyChart {

    public XYPlot plot;
    public ChartUpdater updater;
    // Variables
    double[] oldVals;
    JFreeChart chart;
    MyChartPanel chartPanel;
    private MyTimeSeries[] series;
    private MyProps props;
    boolean load = false;
    XYLineAndShapeRenderer renderer;
    ApiObject apiObject;

    // Constructor
    public MyChart(MyTimeSeries[] series, MyProps props) {
        apiObject = ApiObject.getInstance();
        this.series = series;
        this.props = props;
        oldVals = new double[series.length];

        // Init
        init(series, props);

        // Start updater
        updater = new ChartUpdater(series);
        updater.getHandler().start();
    }

    private void init(MyTimeSeries[] series, MyProps props) {
        // Series
        TimeSeriesCollection data = new TimeSeriesCollection();

        // Create the chart
        chart = ChartFactory.createTimeSeriesChart(null, null, null, data, false, true, false);

        // plot style
        plot_style();

        // Date axis
        date_axis();

        // Number axis
        number_axis();

        // Marker
        marker();

        // Renderer (Style series)
        renderer(data);

        // Update visibility
        updateSeriesVisibility();
    }

    private void plot_style() {
        plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinesVisible(props.getBool(ChartPropsEnum.IS_RANGE_GRID_VISIBLE));
        plot.setDomainGridlinesVisible(props.getBool(ChartPropsEnum.IS_DOMAIN_GRID_VISIBLE));
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plot.getDomainAxis().setVisible(props.getBool(ChartPropsEnum.INCLUDE_DOMAIN_AXIS));
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainPannable(false);
        plot.setRangePannable(false);
        plot.getRangeAxis().setAutoRange(true);
        plot.getRangeAxis().setTickLabelPaint(Themes.BINANCE_GREY);
        plot.getRangeAxis().setTickLabelFont(Themes.ARIEL_BOLD_15);
    }

    private void number_axis() {
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        DecimalFormat df = new DecimalFormat("#00000.00");
        df.setNegativePrefix("-");
        numberAxis.setNumberFormatOverride(df);
    }

    private void date_axis() {
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
    }

    private void renderer(TimeSeriesCollection data) {
        // Style lines
        renderer = new XYLineAndShapeRenderer();
        renderer.setShapesVisible(false);
        plot.setRenderer(renderer);

        int i = 0;
        for (MyTimeSeries serie : series) {
            // Append serie
            data.addSeries(serie);
            serie.setId(i);

            // Style serie
            renderer.setSeriesShapesVisible(i, false);
            renderer.setSeriesPaint(i, serie.getColor());
            renderer.setSeriesStroke(i, new BasicStroke(serie.getStokeSize()));
            i++;
        }
    }

    public MyProps getProps() {
        return props;
    }

    private void marker() {
        // Marker
        if (props.getProp(ChartPropsEnum.MARKER) != MyProps.p_null) {
            ValueMarker marker = new ValueMarker(props.getProp(ChartPropsEnum.MARKER));
            marker.setStroke(new BasicStroke(1.2f));
            marker.setPaint(Themes.GREY_2);
            marker.setValue(props.getProp(ChartPropsEnum.MARKER));
            plot.addRangeMarker(marker, Layer.BACKGROUND);
        }
    }

    public MyTimeSeries[] getSeries() {
        return series;
    }

    public ChartUpdater getUpdater() {
        return updater;
    }

    public void updateSeriesVisibility() {
        for (MyTimeSeries serie : series) {
            renderer.setSeriesVisible(serie.getId(), serie.isVisible());
        }
    }

    // ---------- Chart updater thread ---------- //
    class ChartUpdater extends MyThread implements Runnable {

        // Variables
//        ArrayList< Double > dots = new ArrayList<>( );
        MyTimeSeries[] series;
        NumberAxis range;
        double min, max;

        // Constructor
        public ChartUpdater(MyTimeSeries[] series) {
            this.series = series;
            initListeners();
        }

        private void can_i_start() {
            // Should load
            if (props.getBool(ChartPropsEnum.IS_LOAD_DB)) {

                while (true) {
                    try {
                        boolean loaded = true;

                        // Sleep
                        Thread.sleep(500);

                        // Is load each serie
                        for (MyTimeSeries serie : series) {
                            if (!serie.isLoad()) {
                                loaded = false;
                            }
                        }
                        // On Done
                        if (loaded) {
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void initListeners() {

        }

        @Override
        public void run() {

            // Can start data updating
            can_i_start();

            // While loop
            while (isRun()) {
                try {
                    if (apiObject.isStarted()) {

                        // Sleep
                        Thread.sleep((long) props.getProp(ChartPropsEnum.SLEEP));

                        if (props.getBool(ChartPropsEnum.IS_LIVE)) {
                            if (isDataChanged()) {
                                append();
                            }
                        } else if (props.getProp(ChartPropsEnum.RETRO_MINS) > 0) {
                            append_retro();
                        } else {
                            append();
                        }
                    } else {
                        // Sleep
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        private void append_retro() {
            int minuts = (int) props.getProp(ChartPropsEnum.RETRO_MINS);

            if (minuts > 0) {
                for (MyTimeSeries serie : series) {
                    new Thread(() -> {
                        serie.clear_data();
                        DataBaseHandler.loadSerieData(serie.load_last_x_time(minuts), serie);
                    }).start();
                }
            }
        }

        private void append() {
            // Append data
            appendDataToSeries();
            // Change range getting bigger
            chartRangeGettingBigFilter();
            // Ticker
            setTickerData();
        }

        // Append data to series
        private void appendDataToSeries() {
            try {
                for (MyTimeSeries serie : series) {
                    // If bigger then target Seconds
                    if (serie.getItemCount() > props.getProp(ChartPropsEnum.SECONDS)) {
                        serie.remove(0);
                    }
                    // Append data
                    serie.add();
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        public void setTickerData() {
            if (props.getBool(ChartPropsEnum.IS_INCLUDE_TICKER)) {
                try {
                    double min = Collections.min(series[0].getMyValues());
                    double max = Collections.max(series[0].getMyValues());
                    double last = (double) series[0].getLastItem().getValue();

                    chartPanel.getHighLbl().colorForge(max, L.format10());
                    chartPanel.getLowLbl().colorForge(min, L.format10());
                    chartPanel.getLastLbl().colorForge(last, L.format10());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void updateChartRange() {
            try {
                if (series[0].getItemCount() > 0) {

                    // X
                    DateRange xRange = (DateRange) plot.getDomainAxis().getRange();

                    RegularTimePeriod startPeroid = new Second(L.formatter.parse(xRange.getLowerDate().toString()));
                    RegularTimePeriod endPeroid = new Second(L.formatter.parse(xRange.getUpperDate().toString()));

                    int startIndex = (int) L.abs(series[0].getIndex(startPeroid));
                    int endIndex = (int) L.abs(series[0].getIndex(endPeroid));

                    ArrayList<Double> dots = new ArrayList<>();

                    try {
                        if (series[0].isScaled()) {

                            for (MyTimeSeries mts : series) {
                                for (int i = startIndex; i < endIndex; i++) {
                                    if (mts.isVisible()) {
                                        dots.add(mts.getScaledData(i));
                                    }
                                }
                            }
                            min = Collections.min(dots);
                            max = Collections.max(dots);
                        } else {

                            for (MyTimeSeries mts : series) {
                                for (int i = startIndex; i < endIndex; i++) {
                                    if (mts.isVisible()) {
                                        dots.add((Double) mts.getValue(i));
                                    }
                                }
                            }
                            min = Collections.min(dots);
                            max = Collections.max(dots);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    min = min - (min * 0.0001);
                    max = max + (max * 0.0001);

                    range = (NumberAxis) plot.getRangeAxis();
                    range.setRange(min, max);

                }
            } catch (NoSuchElementException | ParseException e) {
                e.printStackTrace();
            }
        }

        private void updateChartRange(double min, double max) {
            try {
                if (series[0].getItemCount() > 0) {
                    range = (NumberAxis) plot.getRangeAxis();
                    range.setRange(min, max);
                }
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }

        public void setTextWithColor(JLabel label, double price) {
            label.setText(L.str(price));

            if (price > 0) {
                label.setForeground(Themes.GREEN);
            } else {
                label.setForeground(Themes.RED);
            }
        }

        private void chartRangeGettingBigFilter() {

            if (series[0].getItemCount() > 0) {

                ArrayList<Double> dots = new ArrayList<>();
                for (MyTimeSeries serie : series) {
                    if (serie.isScaled()) {
                        if (serie.isVisible()) {
                            dots.addAll(serie.getMyValues().scaledList());
                        }
                    } else {
                        if (serie.isVisible()) {
                            dots.addAll(serie.getMyValues());
                        }
                    }
                }

                double min = Collections.min(dots);
                double max = Collections.max(dots);

                double range = (max - min) * 0.2;
                min -= range;
                max += range;

                if (dots.size() > series.length * props.getProp(ChartPropsEnum.SECONDS_ON_MESS)) {
                    // If need to rearrange
                    if (max - min > props.getProp(ChartPropsEnum.CHART_MAX_HEIGHT_IN_DOTS)) {
                        // For each serie
                        for (MyTimeSeries serie : series) {
                            for (int i = 0; i < serie.getItemCount() - props.getProp(ChartPropsEnum.SECONDS_ON_MESS) - 1; i++) {
                                serie.remove(i);
                            }
                        }
                    }
                }
                // Update chart range
                updateChartRange(min, max);
            }
        }

        // Is data changed
        private boolean isDataChanged() {
            boolean change = false;
            double oldVal = 0;
            double newVal = 0;

            int i = 0;
            for (MyTimeSeries serie : series) {
                oldVal = oldVals[i];
                try {
                    newVal = serie.getData();
                } catch (Exception e) {
                    e.printStackTrace();
                    change = false;
                    break;
                }

                // If new val
                if (newVal != oldVal) {
                    oldVals[i] = newVal;
                    change = true;
                }
                i++;
            }
            return change;
        }

        @Override
        public void initRunnable() {
            setRunnable(this);
        }

        public void moveForward() {
            try {
                // X
                DateRange xRange = (DateRange) plot.getDomainAxis().getRange();
                RegularTimePeriod startPeroid = new Second(L.formatter.parse(xRange.getLowerDate().toString()));
                RegularTimePeriod endPeroid = new Second(L.formatter.parse(xRange.getUpperDate().toString()));

                int startIndex = (int) L.abs(series[0].getIndex(startPeroid));
                int endIndex = (int) L.abs(series[0].getIndex(endPeroid));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
