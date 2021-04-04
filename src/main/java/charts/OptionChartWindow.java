package charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;
import org.jsoup.select.Elements;

import api.ApiObject;
import logger.MyLogger;
import options.Option;

public class OptionChartWindow extends JFrame {

	public XYSeries option_series = new XYSeries("");

	Font title_font = new Font("Ariel", Font.BOLD, 14);

	public ChartPanel chartPanel;
	public XYSeriesCollection data = null;
	public JFreeChart chart = null;
	public XYPlot plot;
	JLabel xlabel;

	String option_name;
	boolean option_type;

	Color blue = new Color(0, 51, 180);
	Color green = new Color(12, 135, 0);
	Color red = new Color(190, 23, 0);

	OptionChartUpdater freeUpdater;

	// Constructor
	public OptionChartWindow(String option_name, boolean option_type) {
		super(option_name.toUpperCase());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAutoRequestFocus(false);
		
		// UI manager
		UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(Color.black));
		UIManager.put("InternalFrame.activeTitleForeground", new ColorUIResource(Color.WHITE));
		UIManager.put("InternalFrame.titleFont", new Font("Dialog", Font.PLAIN, 11));

		this.option_name = option_name;
		this.option_type = option_type;

		// Build the window
		init();

		// Show on screen
//		Counter.showOnScreen(0, this);

		// Creates all the threads
		open_threads();
	}

	// Show on screen
	public static void showOnScreen(int screen, JPanel frame) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		if (screen > -1 && screen < gd.length) {
			frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x + 500, frame.getY());
		} else if (gd.length > 0) {
			frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x + 500, frame.getY());
		} else {
			throw new RuntimeException("No Screens Found");
		}
	}

	// Open all the nessesery threads
	private void open_threads() {
		// Create the chart updater thread
		freeUpdater = new OptionChartUpdater(chartPanel, data, chart, option_series, plot, option_name, option_type);
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

		data = new XYSeriesCollection();
		data.addSeries(option_series);

		chart = ChartFactory.createXYLineChart(null, null, null, data, PlotOrientation.VERTICAL, false, true, false);
		TextTitle title = new TextTitle(option_name.toUpperCase(), title_font);
		chart.setTitle(title);
		chartPanel = new ChartPanel(chart);

		// Mouse listener
		chartPanel.addChartMouseListener(new ChartMouseListener() {

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {}

			@Override
			public void chartMouseClicked(ChartMouseEvent arg0) {
				xlabel.setText(String.valueOf((int) plot.getRangeCrosshairValue()));
				plot.getDomainCrosshairValue();
			}
		});
		
		xlabel = new JLabel("Xlabel");
		xlabel.setFont(new Font("Arial", Font.PLAIN, 14));
		xlabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_xlabel = new GridBagConstraints();
		gbc_xlabel.insets = new Insets(0, 0, 5, 5);
		gbc_xlabel.anchor = GridBagConstraints.NORTH;
		gbc_xlabel.gridx = 0;
		gbc_xlabel.gridy = 0;
		chartPanel.add(xlabel, gbc_xlabel);
		

		if (option_type) {
			setBounds(2698, 0, 298, 340);
		} else {
			setBounds(2698, 415, 298, 340);
		}
		chartPanel.setPreferredSize(new Dimension(448, 380));

		// Style
		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.WHITE);

		plot.getRangeCrosshairValue();
		plot.getDomainCrosshairValue();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

		// Style lines
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		if (option_type) {
			renderer.setSeriesPaint(0, green);
		} else {
			renderer.setSeriesPaint(0, red);
		}
		renderer.setSeriesStroke(0, new BasicStroke(2.0f));
		renderer.setShapesVisible(false);
		plot.setRenderer(renderer);

		// Add to the contentpane
		setContentPane(chartPanel);
		GridBagLayout gbl_chartPanel = new GridBagLayout();
		gbl_chartPanel.columnWidths = new int[]{156, 28, 0};
		gbl_chartPanel.rowHeights = new int[]{0, 14, 0};
		gbl_chartPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_chartPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		chartPanel.setLayout(gbl_chartPanel);
		
		
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
	public static void main(final String[] args) {
		// Create the window
		OptionChartWindow chartWindow = new OptionChartWindow("c1430", true);
		chartWindow.pack();
//		RefineryUtilities.centerFrameOnScreen(chartWindow);
		chartWindow.setVisible(true);
	}
}

class OptionChartUpdater extends Thread {
	Logger logger;

	ChartPanel chartPanel;
	XYSeriesCollection data;
	JFreeChart chart;
	XYSeries option_series;
	XYPlot plot;
	NumberAxis range;
	String option_name;
	boolean option_type;
	Elements document;
	int last;
	int x = 0;

	ArrayList<Integer> dots = new ArrayList<>();
	ApiObject apiObject = ApiObject.getInstance();
	
	ValueMarker marker;
	private boolean run = true;

	// Constructor
	public OptionChartUpdater(ChartPanel chartPanel, XYSeriesCollection data, JFreeChart chart, XYSeries option_series,
			XYPlot plot, String option_name, boolean option_type) {

		this.chartPanel = chartPanel;
		this.data = data;
		this.chart = chart;
		this.option_series = option_series;
		this.plot = plot;
		this.option_name = option_name;
		this.option_type = option_type;
		
		marker = createMarker(0, Color.GRAY, 0.5f);
		plot.addRangeMarker(marker, Layer.BACKGROUND);

		// Reload
		reloadData();
	}

	@Override
	public void run() {
		try {
			logger = MyLogger.getInstance();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		while (run) {
			try {
				// Get the data from the api
				getDataFromApi();

				// Update the series
				updateChart();

				// Incremenet x
				x++;
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				run = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Get the data from the api
	private void getDataFromApi() {
		try {
			Option option = apiObject.getExpMonth().getOptions().getOption(option_name);
			last = option.getLast();
		} catch (Exception e) {
			logger.info(e.getCause().toString());
			e.printStackTrace();
		}
	}

	// Reload history lists
	public void reloadData() {
		
		ArrayList<Integer> list = apiObject.getExpMonth().getOptions().getOption(option_name).getLastList();
		if (list != null) {
			x = list.size() + 1;
			for (int i = 0; i < list.size(); i++) {
				option_series.add(i, list.get(i));
				dots.add(list.get(i));
			}
		}
	}
	
	// Update series
	private void updateChart() {
		// Dots
		if (last != 0) {
			dots.add(last);
			
			// Range
			range = (NumberAxis) plot.getRangeAxis();
			range.setRange(Collections.min(dots) - 50, Collections.max(dots) + 50);
			
			marker.setLabel(String.valueOf((int) last));
			marker.setValue(last);

			// Series
			option_series.add(x, last);
		}
	}
	
	
	// Create marker 
	public ValueMarker createMarker(double price, Color color, float stroke) {
		ValueMarker marker = new ValueMarker(price, color,new BasicStroke(stroke));
		marker.setLabelFont(new Font("Ariel", Font.PLAIN, 14));
		marker.setLabelOffset(new RectangleInsets(10d, 360d, 0d, 0d));
		return marker;
	}
	
	
	public void close() {
		run = false;
	}
}
