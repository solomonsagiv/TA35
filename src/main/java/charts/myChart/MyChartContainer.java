package charts.myChart;

import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;

import api.ApiObject;
import charts.MyChartPanel;
import gui.popupsFactory.PopupsMenuFactory;

public class MyChartContainer extends JFrame {

	private static final long serialVersionUID = 1L;

	// Index series array
	MyChart[] charts;

	ApiObject apiObject = ApiObject.getInstance();
	String name;

	public MyChartContainer( MyChart[] charts, String name ) {
		this.charts = charts;
		this.name = name;
		init();
	}
	
	
	public String getName() {
		return name;
	}
	
	private void init() {
		setTitle(name);
		
		// On Close
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onClose(e);
			}
		});
		
		// Layout
		setLayout(new GridLayout(charts.length, 0));

		// Append charts
		appendCharts();
	}
	
	public void create() {
		pack();
		setVisible(true);
		try {
			ResultSet rs = apiObject.getDataBaseService().getBoundsTable().getBound("ta35", getName());
			while (rs.next()) {
				setBounds(rs.getInt("x"), rs.getInt("y"), rs.getInt("width"), rs.getInt("height"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void appendCharts() {
		for (MyChart myChart : charts) {
			MyChartPanel chartPanel = new MyChartPanel(myChart.chart, myChart.props.getBool(ChartPropsEnum.IS_INCLUDE_TICKER));
			chartPanel.setPopupMenu(PopupsMenuFactory.chartMenu(chartPanel, myChart));
			myChart.chartPanel = chartPanel;
			add(chartPanel);
		}
	}

	public void removeChart(MyChart chart) {
		remove(chart.chartPanel);
	}

	public void onClose(WindowEvent e) {
		for (MyChart myChart : charts) {
			myChart.getUpdater().getHandler().close();
		}
		dispose();
		
		apiObject.getDataBaseService().getBoundsTable().updateBoundOrCreateNewOne("ta35", getName(), getX(), getY(),
				getWidth(), getHeight());
	}

}
