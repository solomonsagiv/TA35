package charts.myChart;

import api.TA35;
import charts.MyChartPanel;
import dataBase.mySql.MySql;
import locals.Themes;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class MyChartContainer extends JFrame {

    private static final long serialVersionUID = 1L;

    // Index series array
    MyChart[] charts;

    String name;

    TA35 client;

    public MyChartContainer(MyChart[] charts, String name) {
        this.client = TA35.getInstance();
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

        // Add right-click context menu for dark mode
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                    showDarkModeMenu(e);
                }
            }
        });

        // Layout
        setLayout(new GridLayout(charts.length, 0));

        // Load series data
        load_data();

        // Append charts
        appendCharts();
        
        // Apply dark mode if enabled
        applyDarkMode();

    }
    
    private void showDarkModeMenu(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        String menuText = Themes.isDarkMode() ? "מצב בהיר" : "מצב כהה";
        JMenuItem menuItem = new JMenuItem(menuText);
        menuItem.addActionListener(ev -> {
            Themes.toggleDarkMode();
            applyDarkMode();
        });
        popup.add(menuItem);
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
    
    private void applyDarkMode() {
        // Update frame background using Themes helper methods
        setBackground(Themes.getBackgroundColor());
        getContentPane().setBackground(Themes.getBackgroundColor());
        
        // Update all charts
        if (charts != null) {
            for (MyChart chart : charts) {
                chart.applyDarkMode();
            }
        }
    }

    private void load_data() {
        // Load each serie
        for (MyChart chart : charts) {
            for (MyTimeSeries serie : chart.getSeries()) {
                try {
                    serie.clear_data();
                    serie.load_data();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private void check_load() {

        while (true) {

            try {
                // Sleep
                Thread.sleep(1000);

                boolean load = true;

                // Check for load = true
                for (MyChart chart : charts) {
                    for (MyTimeSeries serie : chart.getSeries()) {
                        if (!serie.isLoad()) {
                            load = false;
                        }
                        System.out.println(serie.getName() + " Load " + serie.isLoad());
                    }
                }

                // Is load
                if (load) {
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
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
            myChart.setChartPanel(chartPanel);

            initProps(chartPanel);
            addPan(chartPanel);
            mouseListener(chartPanel, myChart, this);
            add(chartPanel);
        }
    }

    private void initProps(MyChartPanel chartPanel) {
        chartPanel.setMouseZoomable(false);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
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

    private void mouseListener(MyChartPanel chartPanel, MyChart myChart, MyChartContainer container) {

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
                    new ChartFilterWindow(client, "Filter", myChart, container);
                }
            }
        });
    }

    private void addPan(MyChartPanel chartPanel) {
        try {
            Field mask = ChartPanel.class.getDeclaredField("panMask");
            mask.setAccessible(true);
            mask.set(chartPanel, 0);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void loadBounds() {
        new Thread(() -> {
            try {
                int width = 300, height = 300, x = 300, y = 300;

                if (client == null) {
                    client = TA35.getInstance();
                }

                String query = String.format("SELECT * FROM sagiv.bounds WHERE stock_name = '%s' and item_name = '%s';", client.getName(), getTitle());
                List<Map<String, Object>> rs = MySql.select(query, MySql.JIBE_PROD_CONNECTION);

                for(Map<String, Object> row: rs) {
                    x = (int) row.get("x");
                    y = (int) row.get("y");
                    width = (int) row.get("width");
                    height = (int) row.get("height");
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
            String query = String.format("SELECT sagiv.update_bounds('%s', '%s', %s, %s, %s, %s);", client.getName(), getName(), getX(), getY(), getWidth(), getHeight());
            MySql.select(query, MySql.JIBE_PROD_CONNECTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}