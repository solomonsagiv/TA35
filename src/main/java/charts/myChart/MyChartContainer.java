package charts.myChart;

import api.ApiObject;
import charts.MyChartPanel;
import dataBase.DataBaseHandler;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import gui.popupsFactory.PopupsMenuFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class MyChartContainer extends JFrame {

    private static final long serialVersionUID = 1L;

    // Index series array
    MyChart[] charts;

    ApiObject apiObject = ApiObject.getInstance();
    String name;

    public MyChartContainer(MyChart[] charts, String name) {
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
        loadBounds(Queries.get_bounds(getTitle()));
    }

    private void loadBounds(HashMap<String, Integer> map) {
        setPreferredSize(new Dimension(map.get(DataBaseHandler.x), map.get(DataBaseHandler.y)));
        int x = map.get(DataBaseHandler.x);
        int y = map.get(DataBaseHandler.y);
        int width = map.get(DataBaseHandler.width);
        int height = map.get(DataBaseHandler.height);
        setBounds(x, y, width, height);
    }

    private void appendCharts() {
        for (MyChart myChart : charts) {
            MyChartPanel chartPanel = new MyChartPanel(myChart.chart, myChart.getProps().getBool(ChartPropsEnum.IS_INCLUDE_TICKER));
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
        update_bounds();
    }

    private void update_bounds() {
        try {
            String query = String.format("SELECT sagiv.update_bounds('%s', '%s', %s, %s, %s, %s);", apiObject.getName(), getTitle(), getX(), getY(), getWidth(), getHeight());
            MySql.select(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
