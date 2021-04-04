package charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import counter.WindowTA35;

public class TA35Chart extends JFrame {
	public XYSeries index_series = new XYSeries("Index");

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

	String updater_thread_name = "ta35_updater";
	
	TA35chart_updater freeUpdater;

	// Constructor
	public TA35Chart(String title) {
		super(title);
		// UI manager 
		UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(Color.black));
		UIManager.put("InternalFrame.activeTitleForeground", new ColorUIResource(Color.WHITE));
		UIManager.put("InternalFrame.titleFont", new Font("Dialog", Font.PLAIN, 11));;

		// Build the window
		init();

		// Show on screen 
		WindowTA35.showOnScreen(0, this);

		// Create the chart updater thread
		freeUpdater = new TA35chart_updater(chartPanel, data, chart, index_series, plot);
		freeUpdater.setName(updater_thread_name);
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

		setBounds(808, 0, 600, 240);

		data = new XYSeriesCollection();
		data.addSeries(index_series);

		chart = ChartFactory.createXYLineChart(null, null, null, data, PlotOrientation.VERTICAL, false, false, false);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 240));

		// Style
		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.WHITE);

		// Style lines
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.BLACK);
		renderer.setSeriesStroke(0, new BasicStroke(2.0f));
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
		final TA35Chart chartWindow = new TA35Chart("");
		chartWindow.pack();
		RefineryUtilities.centerFrameOnScreen(chartWindow);
		chartWindow.setVisible(true);
	}

}


class TA35chart_updater extends Thread{
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
	private boolean run = true;

	// Constructor
	public TA35chart_updater(ChartPanel chartPanel, XYSeriesCollection data, JFreeChart chart,
			XYSeries index_series, XYPlot plot) {
		this.chartPanel = chartPanel;
		this.data = data;
		this.chart = chart;
		this.index_series = index_series;
		this.plot = plot;
	}

	@Override
	public void run() {

		int x = 0;
		while (run) {
			try {
				// Sleep
				Thread.sleep(1000);

				// Get data from the api
				getDataFromApi();
				
				// Update the chart
				updateChart(x);

				// Increment x
				x++;
			} catch (InterruptedException e) {
				run = false;
			} catch (IOException e) {
				WindowTA35.popup("Api error ", e);
				e.printStackTrace();
				break;
			}
		}
	}

	// Get the data from the api
	private void getDataFromApi() throws IOException {
		Document text = Jsoup.connect("http://localhost:8080/ta_35/locals/ta35").get();
		String t = text.getElementsByTag("body").get(0).text();

		JSONObject data = new JSONObject(t);
		index = data.getDouble("last");
	}

	// Update data
	private void updateChart(int x) {

		// Add to the dots
		dots.add(index);

		range = (NumberAxis) plot.getRangeAxis();
		range.setRange(Collections.min(dots) - 0.1, Collections.max(dots) + 0.1);

		// Append series
		index_series.add(x, index);
	}
	
	public void close() {
		run = false;
	}
}

