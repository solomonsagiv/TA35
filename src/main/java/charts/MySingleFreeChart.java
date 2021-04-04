package charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import arik.Arik;
import locals.Themes;

public class MySingleFreeChart {

	private JFreeChart chart;
	XYSeries[] series;
	Color[] colors;
	XYPlot plot;
	double margin;
	ChartUpdater chartUpdater;
	int seconds;
	private MyChartPanel chartPanel;
	private boolean includeTickerData;

	public MySingleFreeChart(XYSeries[] series, Color[] colors, double margin,
			ArrayList<ArrayList<Double>> list, int seconds, boolean includeTickerData) {

		this.series = series;
		this.colors = colors;
		this.margin = margin;
		this.seconds = seconds;
		this.setIncludeTickerData(includeTickerData);

		// Series
		XYSeriesCollection data = new XYSeriesCollection();

		// Create the chart
		chart = ChartFactory.createXYLineChart(null, null, null, data, PlotOrientation.VERTICAL, false, true, false);

		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinesVisible(true);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		plot.getDomainAxis().setVisible(false);

		// Style lines
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setShapesVisible(false);
		plot.setRenderer(renderer);

		// For each serie
		for (int i = 0; i < series.length; i++) {

			// Append serie
			data.addSeries(series[i]);

			// Style serie
			renderer.setSeriesPaint(i, colors[i]);
			renderer.setSeriesStroke(i, new BasicStroke(1.5f));
		}

		// Run chart updater
		chartUpdater = new ChartUpdater(list);
		chartUpdater.start();

	}

	public void closeUpdate() {
		if (chartUpdater != null) {
			if (chartUpdater.isAlive()) {
				chartUpdater.close();
			}
		}
	}

	public JFreeChart getChart() {
		return chart;
	}

	public MyChartPanel getChartPanel() {
		return chartPanel;
	}

	public void setChartPanel(MyChartPanel chartPanel) {
		this.chartPanel = chartPanel;
	}

	public boolean isIncludeTickerData() {
		return includeTickerData;
	}

	public void setIncludeTickerData(boolean includeTickerData) {
		this.includeTickerData = includeTickerData;
	}

	// Chart updater thread
	private class ChartUpdater extends Thread {

		ArrayList<Double> dots = new ArrayList<>();
		ArrayList<ArrayList<Double>> lists;
		NumberAxis range;
		boolean run = true;
		
		public ChartUpdater(ArrayList<ArrayList<Double>> lists) {
			this.lists = lists;
		}

		@Override
		public void run() {
			
			// Load chart data from mongo 
			int i = 0;
			
			try {
				
			for (ArrayList<Double> list : lists) {
				
				if (list.size() > 0) {
					
					int start = 0;
					if (list.size() > seconds && seconds > 0) {
						start = (int) (list.size() - seconds);
					}
					
					for (int j = start; j < list.size(); j++) {
						series[i].add(j, list.get(j));
						dots.add(list.get(j));
					}
				}
				i++;
			}
			} catch (Exception e) {
				Arik.getInstance().sendMessage(Arik.sagivID, "Load chart data from Mongo faild \n" + e.getStackTrace(), null);
			}

			// While loop
			while (run) {
				
				try {
					
					updateChart();
					
					// Sleep
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					run = false;
				}
			}
		}

		// Update data
		private void updateChart() {
			
			int i = 0;
			
			for (ArrayList<Double> list : lists) {
				if (list.size() > 0) {
					try {
						// Remove 0
						if (seconds > 0 && list.size() > seconds) {
							series[i].remove(0);
							dots.remove(0);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					series[i].add(list.size(), list.get(list.size() - 1));
					dots.add(list.get(list.size() - 1));
					i++;
				}
			}
			
			// Labels
			if (lists.size() == 1 && lists.get(0).size() > 0 && includeTickerData) {
				try {
					
					double high = floor(Collections.max(lists.get(0)));
					double low = floor(Collections.min(lists.get(0)));
					double last = floor(lists.get(0).get(lists.get(0).size() - 1));
					
					setTextWithColor(chartPanel.highLbl, high);
					setTextWithColor(chartPanel.lowLbl, low);
					chartPanel.lastLbl.setText(str(last));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// Update the range
			try {
				range = (NumberAxis) plot.getRangeAxis();
				range.setRange(Collections.min(dots) - margin, Collections.max(dots) + margin);
			} catch (NoSuchElementException e) {
			}
		}

		public void close() {
			run = false;
		}

		public String str(Object o) {
			return String.valueOf(o);
		}

		public double floor(double d) {
			return Math.floor(d * 10) / 10;
		}

		public void setTextWithColor(JLabel label, double price) {

			label.setText(str(price));

			if (price > 0) {
				label.setForeground(Themes.GREEN);
			} else {
				label.setForeground(Themes.RED);
			}
		}
	}
}
