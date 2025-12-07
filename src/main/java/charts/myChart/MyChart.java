package charts.myChart;


import api.TA35;
import charts.MyChartPanel;
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
import java.time.LocalDateTime;
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
    TA35 TA35;

    // Constructor
    public MyChart(MyTimeSeries[] series, MyProps props) {
        TA35 = TA35.getInstance();
        this.series = series;
        this.props = props;
        oldVals = new double[series.length];

        // Init
        init();

        // Start updater
        updater = new ChartUpdater(series);
        updater.getHandler().start();
    }

    private void init() {
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
        
        // Apply dark mode if enabled
        if (Themes.isDarkMode()) {
            applyDarkMode();
        }
    }

    public void add_marker(ValueMarker marker) {
        plot.addRangeMarker(marker, Layer.BACKGROUND);
    }

    private void plot_style() {
        plot = chart.getXYPlot();
        updatePlotStyle();
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plot.getDomainAxis().setVisible(props.getBool(ChartPropsEnum.INCLUDE_DOMAIN_AXIS));
        plot.setAxisOffset(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        plot.setDomainPannable(false);
        plot.setRangePannable(false);
        plot.getRangeAxis().setAutoRange(true);
    }
    
    private void updatePlotStyle() {
        if (plot == null) return;
        
        if (Themes.isDarkMode()) {
            plot.setBackgroundPaint(Themes.DARK_BLUE_BG);
            plot.setRangeGridlinesVisible(props.getBool(ChartPropsEnum.IS_RANGE_GRID_VISIBLE));
            plot.setDomainGridlinesVisible(props.getBool(ChartPropsEnum.IS_DOMAIN_GRID_VISIBLE));
            plot.setRangeGridlinePaint(Themes.LIGHT_GRAY_TEXT);
            plot.setDomainGridlinePaint(Themes.LIGHT_GRAY_TEXT);
            plot.getRangeAxis().setTickLabelPaint(Themes.WHITE_TEXT);
            plot.getDomainAxis().setTickLabelPaint(Themes.WHITE_TEXT);
        } else {
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinesVisible(props.getBool(ChartPropsEnum.IS_RANGE_GRID_VISIBLE));
            plot.setDomainGridlinesVisible(props.getBool(ChartPropsEnum.IS_DOMAIN_GRID_VISIBLE));
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinePaint(Themes.LIGHT_BLUE);
            plot.getRangeAxis().setTickLabelPaint(Themes.BINANCE_GREY);
            plot.getDomainAxis().setTickLabelPaint(Themes.BINANCE_GREY);
        }
        plot.getRangeAxis().setTickLabelFont(Themes.ARIEL_BOLD_15);
    }
    
    public void setChartPanel(MyChartPanel panel) {
        this.chartPanel = panel;
        // Apply dark mode when panel is set
        if (Themes.isDarkMode()) {
            applyDarkMode();
        }
    }
    
    public void applyDarkMode() {
        updatePlotStyle();
        updateSeriesColors(); // Update series colors for dark mode
        updateMarkerColors(); // Update marker colors for dark mode
        if (chartPanel != null) {
            if (Themes.isDarkMode()) {
                chartPanel.setBackground(Themes.DARK_BLUE_BG);
                if (chartPanel.getHighLbl() != null) {
                    chartPanel.getHighLbl().setForeground(Themes.WHITE_TEXT);
                }
                if (chartPanel.getLowLbl() != null) {
                    chartPanel.getLowLbl().setForeground(Themes.WHITE_TEXT);
                }
                if (chartPanel.getLastLbl() != null) {
                    chartPanel.getLastLbl().setForeground(Themes.WHITE_TEXT);
                }
            } else {
                chartPanel.setBackground(Color.WHITE);
                if (chartPanel.getHighLbl() != null) {
                    chartPanel.getHighLbl().setForeground(Themes.BLUE);
                }
                if (chartPanel.getLowLbl() != null) {
                    chartPanel.getLowLbl().setForeground(Themes.BLUE);
                }
                if (chartPanel.getLastLbl() != null) {
                    chartPanel.getLastLbl().setForeground(Themes.BLUE);
                }
            }
        }
    }
    
    /**
     * Updates marker colors for dark mode
     */
    private void updateMarkerColors() {
        if (plot == null) return;
        
        // Update range markers
        java.util.Collection<?> rangeMarkers = plot.getRangeMarkers(Layer.BACKGROUND);
        if (rangeMarkers != null) {
            for (Object markerObj : rangeMarkers) {
                if (markerObj instanceof ValueMarker) {
                    ValueMarker marker = (ValueMarker) markerObj;
                    // Set light gray in dark mode, grey in light mode
                    marker.setPaint(Themes.isDarkMode() ? Themes.LIGHT_GRAY_TEXT : Themes.GREY_2);
                }
            }
        }
    }

    private void number_axis() {
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        DecimalFormat df = new DecimalFormat("#00000.0");
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

            // Style serie - use display color which adjusts for dark mode
            renderer.setSeriesShapesVisible(i, false);
            renderer.setSeriesPaint(i, serie.getDisplayColor());
            renderer.setSeriesStroke(i, new BasicStroke(serie.getStokeSize()));
            i++;
        }
    }
    
    /**
     * Updates series colors for dark mode
     */
    private void updateSeriesColors() {
        if (renderer == null || series == null) return;
        
        for (MyTimeSeries serie : series) {
            if (serie.getId() >= 0) {
                // Update the color in the renderer
                renderer.setSeriesPaint(serie.getId(), serie.getDisplayColor());
            }
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
            // Use light gray in dark mode for better visibility
            marker.setPaint(Themes.isDarkMode() ? Themes.LIGHT_GRAY_TEXT : Themes.GREY_2);
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
                    if (true) {

                        // Sleep
                        Thread.sleep((long) props.getProp(ChartPropsEnum.SLEEP));

                        if (props.getBool(ChartPropsEnum.IS_LIVE)) {
                            if (isDataChanged()) {
                                append();
                            }
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
                    if (props.getBool(ChartPropsEnum.IS_LIVE)) {
                        serie.add();
                    } else {
                        serie.add(LocalDateTime.now());
                    }
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
                        for (MyTimeSeries mts : series) {
                            for (int i = startIndex; i < endIndex; i++) {
                                if (mts.isVisible()) {
                                    dots.add((Double) mts.getValue(i));
                                }
                            }
                        }
                        min = Collections.min(dots);
                        max = Collections.max(dots);
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
                    if (serie.isVisible()) {
                        dots.addAll(serie.getMyValues());
                    }
                }

                double min = Collections.min(dots);
                double max = Collections.max(dots);

                double range = (max - min) * 0.07;
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
                    newVal = serie.getValue();
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
