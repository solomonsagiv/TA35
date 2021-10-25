package charts;

import api.ApiObject;
import options.Options;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

public class ChartWindow extends JFrame {

	public XYSeries future_series = new XYSeries("Future");
	public XYSeries index_series = new XYSeries("Index");
	public XYSeries index_bid_series = new XYSeries("Bid");
	public XYSeries index_ask_series = new XYSeries("Ask");

	public ChartPanel chartPanel;
	public XYSeriesCollection data = null;
	public JFreeChart chart = null;
	public XYPlot plot;

	String option_name;
	boolean option_type;

	// Colors
	Color blue = new Color(0, 51, 180);
	Color green = new Color(0, 150, 48);
	Color red = new Color(190, 23, 0);

	String updater_thread_name = "jfree_updater";

	JFreeUpdater freeUpdater;

	// Constructor
	public ChartWindow(String title) {
		super(title);
		// UI manager
		UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(Color.BLACK));
		UIManager.put("InternalFrame.activeTitleForeground", new ColorUIResource(Color.WHITE));
		UIManager.put("InternalFrame.titleFont", new Font("Dialog", Font.PLAIN, 11));

		// Build the window
		init();

		// Create the chart updater thread
		freeUpdater = new JFreeUpdater(chartPanel, data, chart, future_series, index_series, index_bid_series,
				index_ask_series, plot);
		freeUpdater.start();
		
	}

	// Init function
	private void init() {
		// On Close
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onClose(e);
			}
		});

		setBounds(-6, 162, 730, 515);

		data = new XYSeriesCollection();
		data.addSeries(future_series);
		data.addSeries(index_series);
		data.addSeries(index_bid_series);
		data.addSeries(index_ask_series);

		chart = ChartFactory.createXYLineChart(null, null, null, data, PlotOrientation.VERTICAL, false, false, false);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(775, 490));

		// Style
		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

		// Style lines
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, green);
		renderer.setSeriesPaint(1, Color.BLACK);
		renderer.setSeriesPaint(2, blue);
		renderer.setSeriesPaint(3, red);
		renderer.setSeriesStroke(0, new BasicStroke(2.2f));
		renderer.setSeriesStroke(1, new BasicStroke(2.2f));
		renderer.setSeriesStroke(2, new BasicStroke(2.2f));
		renderer.setSeriesStroke(3, new BasicStroke(2.2f));
		renderer.setShapesVisible(false);
		plot.setRenderer(renderer);

		// Add to the contentpane
		setContentPane(chartPanel);
	}

	// Show on screen
	public static void showOnScreen(int screen, JFrame frame) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		if (screen > -1 && screen < gd.length) {
			frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x + frame.getX(), frame.getY());
		} else if (gd.length > 0) {
			frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x + frame.getX(), frame.getY());
		} else {
			throw new RuntimeException("No Screens Found");
		}
	}

	// On close
	public void onClose(WindowEvent e) {
		freeUpdater.close();
		dispose();
	}

	/**
	 * Starting point for the demonstration application.
	 *
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		// Create the window
		final ChartWindow chartWindow = new ChartWindow("");
		chartWindow.pack();
		RefineryUtilities.centerFrameOnScreen(chartWindow);
		chartWindow.setVisible(true);
	}

}

class JFreeUpdater extends Thread {
	XYSeries future_series;
	XYSeries index_series;
	XYSeries index_bid_series;
	XYSeries index_ask_series;
	ChartPanel chartPanel;
	XYSeriesCollection data;
	JFreeChart chart;
	XYPlot plot;
	NumberAxis range;

	double future;
	double index;
	double index_bid;
	double index_ask;

	ArrayList<Double> dots = new ArrayList<>();

	ApiObject apiObject = ApiObject.getInstance();

	private boolean run = true;

	// Constructor
	public JFreeUpdater(ChartPanel chartPanel, XYSeriesCollection data, JFreeChart chart, XYSeries future_series,
			XYSeries index_series, XYSeries index_bid_series, XYSeries index_ask_series, XYPlot plot) {
		this.chartPanel = chartPanel;
		this.data = data;
		this.chart = chart;
		this.plot = plot;
		this.future_series = future_series;
		this.index_series = index_series;
		this.index_bid_series = index_bid_series;
		this.index_ask_series = index_ask_series;

	}
	
	
	int x = 0;

	@Override
	public void run() {

		
		while (run) {
			try {
				// Sleep
				Thread.sleep(200);

				// Update the chart
				updateChart();

				// Increment x
			} catch (InterruptedException e) {
				run = false;
			}
		}
	}

	// Update data
	private void updateChart() {

		Options optionsMonth = apiObject.getExps().getMonth().getOptions();
		
		if (optionsMonth.getContract() != 0 && apiObject.getIndex() != 0 && apiObject.getIndex_bid() != 0
				&& apiObject.getIndex_ask() != 0) { // Add to the dots

			if (future != optionsMonth.getContract()|| index != apiObject.getIndex()
					|| index_bid != apiObject.getIndex_bid() || index_ask != apiObject.getIndex_ask()) {
				
				future = optionsMonth.getContract();
				index = apiObject.getIndex();
				index_bid = apiObject.getIndex_bid();
				index_ask = apiObject.getIndex_ask();

				dots.add(future);
				dots.add(index);
				dots.add(index_bid);
				dots.add(index_ask);

				range = (NumberAxis) plot.getRangeAxis();
				range.setRange(Collections.min(dots) - 0.1, Collections.max(dots) + 0.1);

				// Append series
				future_series.add(x, future);
				index_series.add(x, index);
				index_bid_series.add(x, index_bid);
				index_ask_series.add(x, index_ask);
				
				// If longer than 15 minutes
				if (x > 900) {
					future_series.remove(0);
					index_series.remove(0);
					index_bid_series.remove(0);
					index_ask_series.remove(0);

					dots.remove(0);
					dots.remove(1);
					dots.remove(2);
					dots.remove(3);
				}
				
				x++;
				
			}

		}

	}

	public void close() {
		run = false;
	}

}
