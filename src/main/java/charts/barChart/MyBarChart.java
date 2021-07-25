package charts.barChart;

import api.ApiObject;
import charts.barChart.updater.IBarChartUpdater;
import charts.barChart.updater.StocksChartBarUpdater;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;
import threads.MyThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MyBarChart extends JFrame {
	
	String applicationTitle;
	String chartTitle;
	DefaultCategoryDataset dataset;
	ChartBarUpdater updater;
	
	public MyBarChart(String applicationTitle, String chartTitle, IBarChartUpdater updaterImp)
			throws InterruptedException {
		super(applicationTitle);
		this.applicationTitle = applicationTitle;
		this.chartTitle = chartTitle;
		init();
		initListener();
		updater(updaterImp);
	}
	
	private void initListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				updater.getHandler().close();
				dispose();
			}
		});
	}

	private void updater(IBarChartUpdater updaterImp) {
		updater = new ChartBarUpdater(updaterImp);
		updater.getHandler().start();
	}

	private void init() {

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		dataset = new DefaultCategoryDataset();

		JFreeChart chart = ChartFactory.createBarChart(chartTitle, "Category", "Score", dataset,
				PlotOrientation.VERTICAL, true, false, true);

		// Legend to the right
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);

		chart.setBackgroundPaint(Color.WHITE);
		chart.setBorderVisible(false);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
		
		// Chart panel
		CategoryPlot cplot = (CategoryPlot) chart.getPlot();
		cplot.setBackgroundPaint(Color.WHITE);
		
		// Serires paint
		((BarRenderer) cplot.getRenderer()).setBarPainter(new StandardBarPainter());

		BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		
	}
	
	public void createChart() {
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
	}
	
	public static void main(String[] args) throws InterruptedException {

		ApiObject apiObject = ApiObject.getInstance();
		apiObject.getStocksHandler().getStocks()[0].setDelta(5000);
		apiObject.getStocksHandler().getStocks()[1].setDelta(2000);
		apiObject.getStocksHandler().getStocks()[2].setDelta(1500);
		apiObject.getStocksHandler().getStocks()[3].setDelta(2000);
		apiObject.getStocksHandler().getStocks()[4].setDelta(1500);
		apiObject.getStocksHandler().getStocks()[5].setDelta(2000);
		apiObject.getStocksHandler().getStocks()[6].setDelta(1500);
		apiObject.getStocksHandler().getStocks()[7].setDelta(2000);
		apiObject.getStocksHandler().getStocks()[8].setDelta(1500);
		apiObject.getStocksHandler().getStocks()[9].setDelta(2000);
		apiObject.getStocksHandler().getStocks()[11].setDelta(1500);
		apiObject.getStocksHandler().getStocks()[10].setDelta(2000);
		apiObject.getStocksHandler().getStocks()[12].setDelta(1500);
		apiObject.getStocksHandler().getStocks()[13].setDelta(2000);
		apiObject.getStocksHandler().getStocks()[14].setDelta(1500);

		apiObject.getStocksHandler().getStocks()[apiObject.getStocksHandler().getStocks().length - 1].setDelta(2350);

		MyBarChart chart = new MyBarChart("Stocks", "S",
				new StocksChartBarUpdater(apiObject.getStocksHandler().getStocks()));

		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);

	}

	// Chart bar updater
	private class ChartBarUpdater extends MyThread implements Runnable {
		
		IBarChartUpdater updaterImp;
		
		// Constructor
		public ChartBarUpdater(IBarChartUpdater updaterImp) {
			this.updaterImp = updaterImp;
			setRunnable(this);
		}
		
		@Override
		public void run() {

			// Basic data
			updaterImp.updateBasics(dataset);

			while (isRun()) {
				try {
					// Sleep
					Thread.sleep(5000);

					// Update
					updaterImp.update(dataset);

				} catch (InterruptedException e) {
					getHandler().close();
					break;
				}

				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void initRunnable() {
			setRunnable(this);
		}
	}
}
