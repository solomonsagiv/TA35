package charts;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import api.Manifest;

public class MyFreeChart extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// Index series array
	XYPlot plot;
	MySingleFreeChart[] singleFreeCharts;
	
	int[] bounds;
	
	public MyFreeChart(MySingleFreeChart[] singleFreeCharts, int[] bounds) {
		this.singleFreeCharts = singleFreeCharts;
		this.bounds = bounds;
		init();
	}

	private void init() {
		
		setPreferredSize(new Dimension(bounds[2], bounds[3]));

		showOnScreen(Manifest.screen, this);
		
		// On Close
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onClose(e);
			}
		});

		setLayout(new GridLayout(singleFreeCharts.length, 0));
		setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
		
		// Append charts 
		for (MySingleFreeChart mySingleFreeChart : singleFreeCharts) {
			JFreeChart chart = mySingleFreeChart.getChart();
			MyChartPanel chartPanel = new MyChartPanel(chart, mySingleFreeChart.isIncludeTickerData());
			mySingleFreeChart.setChartPanel(chartPanel);
			add(chartPanel);
		}
		
	}
	
	
	public void onClose(WindowEvent e) {
		for (MySingleFreeChart mySingleFreeChart : singleFreeCharts) {
			mySingleFreeChart.closeUpdate();
		}
		dispose();
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

}
