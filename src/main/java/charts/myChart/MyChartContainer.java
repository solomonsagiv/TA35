package charts.myChart;

import api.ApiObject;
import charts.MyChartPanel;
import dataBase.mySql.MySql;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.sql.ResultSet;

public class MyChartContainer extends JFrame {

    private static final long serialVersionUID = 1L;

    // Index series array
    MyChart[] charts;

    String name;

    ApiObject apiObject;

    public MyChartContainer(MyChart[] charts, String name) {
        this.apiObject = ApiObject.getInstance();
        this.charts = charts;
        this.name = name;
        init();
    }

    @Override
    public String getName() {
        return name;
    }

    private void init() {

        // Set title
        setTitle(name);

        // Load bounds
        loadBounds();

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

        // Load series data
        load_data();

    }

    private void load_data() {
        for (MyChart chart : charts) {
            for (MyTimeSeries serie: chart.getSeries()) {
                serie.load_data();
                System.out.println("Loadinn" + " " + serie.getName());
            }
        }
    }

    public void create() {
        pack();
        setVisible(true);
    }

    private void appendCharts() {
        for (MyChart myChart : charts) {
            MyChartPanel chartPanel = new MyChartPanel(myChart.chart, myChart.getProps().getBool(ChartPropsEnum.IS_INCLUDE_TICKER));
            myChart.chartPanel = chartPanel;

            initProps(chartPanel);
            addPan(chartPanel);
            mouseListener(chartPanel, myChart);
            mouseWheel(chartPanel, myChart);
            add(chartPanel);
        }
    }

    private void initProps(MyChartPanel chartPanel) {
        chartPanel.setMouseZoomable(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(false);
        chartPanel.setZoomTriggerDistance(Integer.MAX_VALUE);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setZoomAroundAnchor(true);
    }

    private void mouseWheel(MyChartPanel chartPanel, MyChart myChart) {
        chartPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                myChart.getUpdater().updateChartRange();
            }
        });
    }

    private void mouseListener(MyChartPanel chartPanel, MyChart myChart) {

        // 2 Clicks
        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    DateAxis axis = (DateAxis) myChart.plot.getDomainAxis();
                    NumberAxis rangeAxis = (NumberAxis) myChart.plot.getRangeAxis();

                    rangeAxis.setAutoRange(true);
                    axis.setAutoRange(true);
                }
            }
        });

        // Mouse release
        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                System.out.println("Mouse released");
                myChart.getUpdater().updateChartRange();
            }
        });

        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    new ChartFilterWindow("Filter", myChart);
                }
            }
        });
    }

    private void addPan(MyChartPanel chartPanel) {
        try {
            Field mask = ChartPanel.class.getDeclaredField("panMask");
            mask.setAccessible(true);
            mask.set(chartPanel, 0);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void loadBounds() {
        new Thread(() -> {
            try {
                int width = 100, height = 100, x = 100, y = 100;

                String query = String.format("SELECT * FROM sagiv.bounds WHERE stock_name = '%s' and item_name = '%s';", apiObject.getName(), getName());
                ResultSet rs = MySql.select(query);

                while (rs.next()) {
                    x = rs.getInt("x");
                    y = rs.getInt("y");
                    width = rs.getInt("width");
                    height = rs.getInt("height");
                }

                setPreferredSize(new Dimension(width, height));
                setBounds(x, y, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void onClose(WindowEvent e) {
        // Update bound to database
        insetOrUpdateBounds();

        for (MyChart myChart : charts) {
            myChart.getUpdater().getHandler().close();
        }
        dispose();
    }

    private void insetOrUpdateBounds() {
        try {
            String query = String.format("SELECT sagiv.update_bounds('%s', '%s', %s, %s, %s, %s);", apiObject.getName(), getName(), getX(), getY(), getWidth(), getHeight());
            MySql.select(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}