package counter;

import api.ApiObject;
import api.dde.DDE.DDEConnection;
import book.BookWindow;
import charts.charts.FullCharts2;
import charts.charts.MainMonthWeekChart;
import dataBase.mySql.JibeConnectionPool;
import gui.MyGuiComps;
import gui.details.DetailsWindow;
import options.optionsDataTable.OptionsTableWindow;
import setting.Setting;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;

public class WindowTA35 extends MyGuiComps.MyFrame {

    Color lightGreen = new Color(12, 135, 0);
    Color lightRed = new Color(229, 19, 0);

    public MyGuiComps.MyTextField op_avg_week;
    public MyGuiComps.MyTextField op_avg_month;
    public static JButton start;
    private MyGuiComps.MyPanel bottomPanel;
    public MyGuiComps.MyTextField monthStartExpField;
    public static JTextArea log;

    public MyGuiComps.MyTextField v5_field;
    public MyGuiComps.MyTextField v6_field;
    public MyGuiComps.MyTextField v4_field;
    public MyGuiComps.MyTextField v8_field;

    public MyGuiComps.MyTextField v2_field;
    public MyGuiComps.MyTextField v7_field;
    public MyGuiComps.MyTextField v8_de_corr_field;
    public int updater_id = 0;

    ApiObject apiObject = ApiObject.getInstance();

    // Threads
    Updater updater;
    BackGroundRunner backGroundRunner;
    MyGuiComps.MyTextField basket_down_field;
    MyGuiComps.MyTextField basket_up_field;
    public MyGuiComps.MyTextField basketsSumField;
    DDEConnection ddeConnection;
    public MyGuiComps.MyTextField weekStartExpField;
    private MyGuiComps.MyButton btnDetails;

    public MyGuiComps.MyTextField exp_v2_month_field;
    public MyGuiComps.MyTextField exp_v7_month_field;
    public MyGuiComps.MyTextField exp_v8_month_field;

    public MyGuiComps.MyTextField exp_v2_week_field;
    public MyGuiComps.MyTextField exp_v7_week_field;
    public MyGuiComps.MyTextField exp_v8_week_field;

    public MyGuiComps.MyTextField expBasketsWeekField;
    public MyGuiComps.MyTextField expBasketsMonthField;

    // Constructor
    public WindowTA35() {
        super("TA35");
        updater = new Updater(this);
        updater.getHandler().start();
        load_on_startup();
    }

    // Load on startup
    private void load_on_startup() {
        try {
            // DDE connection
            ddeConnection = new DDEConnection(apiObject);

            // Back ground runner
            backGroundRunner = new BackGroundRunner();
            backGroundRunner.getHandler().start();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showConfirmDialog(this, e.getMessage() + "\n" + e.getCause());
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        try {
            JibeConnectionPool pool = JibeConnectionPool.getConnectionsPoolInstance();
            pool.shutdown();
        } catch (SQLException throwables) {
            JOptionPane.showMessageDialog(this, "Connections shut down failed");
            throwables.printStackTrace();
        }
        System.exit(0);
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

    @Override
    public void initListeners() {

    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void initialize() {

        bottomPanel = new MyGuiComps.MyPanel();
        bottomPanel.setBounds(0, 120, 801, 38);
        getContentPane().add(bottomPanel);
        bottomPanel.setLayout(null);
        
        start = new MyGuiComps.MyButton("Start");
        start.setBorder(null);
        start.setBounds(248, 7, 72, 23);
        bottomPanel.add(start);
        start.setForeground(new Color(0, 0, 51));
        start.setBackground(new Color(211, 211, 211));
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                apiObject.start();
                start.setEnabled(false);
            }
        });

        MyGuiComps.MyButton options = new MyGuiComps.MyButton("Options");
        options.setBorder(null);
        options.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                boolean b;
                if (updater_id % 2 == 0) {
                    b = true;
                } else {
                    b = false;
                }
                BookWindow window = new BookWindow(updater_id, b);
                window.frame.setVisible(true);
                updater_id++;
            }
        });
        options.setForeground(new Color(0, 0, 51));
        options.setBackground(new Color(211, 211, 211));
        options.setBounds(166, 7, 78, 23);
        bottomPanel.add(options);

        MyGuiComps.MyButton settingBtn = new MyGuiComps.MyButton("Setting");
        settingBtn.setBorder(null);
        settingBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Setting setting = new Setting();
                setting.setVisible();
            }
        });
        settingBtn.setForeground(new Color(0, 0, 51));
        settingBtn.setBackground(new Color(211, 211, 211));
        settingBtn.setBounds(10, 7, 72, 23);
        bottomPanel.add(settingBtn);

        btnDetails = new MyGuiComps.MyButton("Details");
        btnDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                DetailsWindow detailsWindow = new DetailsWindow();
                detailsWindow.frame.setVisible(true);
            }
        });
        btnDetails.setBorder(null);
        btnDetails.setForeground(new Color(0, 0, 51));
        btnDetails.setBackground(new Color(211, 211, 211));
        btnDetails.setBounds(88, 7, 72, 23);
        bottomPanel.add(btnDetails);

        @SuppressWarnings("unchecked")
        JComboBox chartsCombo = new JComboBox(new String[]{"Main chart", "Full chart 2", "Options window"});
        chartsCombo.setBounds(start.getX() + start.getWidth() + 5, 8, 182, 23);
        bottomPanel.add(chartsCombo);
        chartsCombo.setBorder(null);
        chartsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (chartsCombo.getSelectedItem().toString()) {
                    case "Full chart 2":
                        FullCharts2 chart = new FullCharts2(apiObject);
                        chart.createChart();
                        break;
                    case "Main chart":
                        MainMonthWeekChart mainMonthWeekChart = new MainMonthWeekChart(apiObject);
                        mainMonthWeekChart.createChart();
                    case "Options window":
                        new OptionsTableWindow("Options window");
                        break;
                    default:
                        break;
                }
            }
        });
        chartsCombo.setForeground(new Color(0, 0, 51));
        chartsCombo.setFont(new Font("Dubai Medium", Font.PLAIN, 15));
        ((JLabel) chartsCombo.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

        MyGuiComps.MyPanel basketsPanel = new MyGuiComps.MyPanel();
        basketsPanel.setLayout(null);
        basketsPanel.setBounds(0, 26, 55, 93);
        getContentPane().add(basketsPanel);

        // ---------------------------  Baskets --------------------------- //
        basket_up_field = new MyGuiComps.MyTextField();
        basket_up_field.setBorder(null);
        basket_up_field.setHorizontalAlignment(SwingConstants.CENTER);
        basket_up_field.setForeground(lightGreen);
        basket_up_field.setColumns(10);
        basket_up_field.setBounds(5, 5, 45, 25);
        basketsPanel.add(basket_up_field);

        basket_down_field = new MyGuiComps.MyTextField();
        basket_down_field.setBorder(null);
        basket_down_field.setHorizontalAlignment(SwingConstants.CENTER);
        basket_down_field.setForeground(lightRed);
        basket_down_field.setColumns(10);
        basket_down_field.setBounds(basket_up_field.getX(), basket_up_field.getY() + basket_up_field.getHeight() + 3, 45, 25);
        basketsPanel.add(basket_down_field);

        basketsSumField = new MyGuiComps.MyTextField();
        basketsSumField.setBorder(null);
        basketsSumField.setHorizontalAlignment(SwingConstants.CENTER);
        basketsSumField.setForeground(new Color(229, 19, 0));
        basketsSumField.setColumns(10);
        basketsSumField.setBounds(basket_down_field.getX(), basket_down_field.getY() + basket_down_field.getHeight() + 3, 45, 25);
        basketsPanel.add(basketsSumField);

        MyGuiComps.MyPanel basket_header_panel = new MyGuiComps.MyPanel();
        basket_header_panel.setLayout(null);
        basket_header_panel.setBounds(0, 0, 55, 25);
        getContentPane().add(basket_header_panel);

        MyGuiComps.MyLabel baskets_lbl = new MyGuiComps.MyLabel("Baskets");
        baskets_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        baskets_lbl.setForeground(new Color(0, 0, 51));
        baskets_lbl.setBounds(0, 0, 55, 26);
        basket_header_panel.add(baskets_lbl);

        MyGuiComps.MyPanel op_avg_header_panel = new MyGuiComps.MyPanel();
        op_avg_header_panel.setLayout(null);
        op_avg_header_panel.setBounds(basket_header_panel.getX() + basket_header_panel.getWidth() + 1, 0, 64, 25);
        getContentPane().add(op_avg_header_panel);

        MyGuiComps.MyLabel move_lbl = new MyGuiComps.MyLabel("Avg");
        move_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        move_lbl.setForeground(new Color(0, 0, 51));
        move_lbl.setBounds(0, 0, 68, 25);
        op_avg_header_panel.add(move_lbl);

        MyGuiComps.MyPanel op_avg_panel = new MyGuiComps.MyPanel();
        op_avg_panel.setBounds(op_avg_header_panel.getX(), op_avg_header_panel.getY() + op_avg_header_panel.getHeight() + 1, 64, 93);
        getContentPane().add(op_avg_panel);
        op_avg_panel.setLayout(null);

        op_avg_week = new MyGuiComps.MyTextField();
        op_avg_week.setBorder(null);
        op_avg_week.setBounds(7, 5, 49, 25);
        op_avg_panel.add(op_avg_week);
        op_avg_week.setHorizontalAlignment(SwingConstants.CENTER);
        op_avg_week.setForeground(Color.WHITE);
        op_avg_week.setColumns(10);

        op_avg_month = new MyGuiComps.MyTextField();
        op_avg_month.setBorder(null);
        op_avg_month.setBounds(op_avg_week.getX(), op_avg_week.getY() + op_avg_week.getHeight() + 3, 49, 25);
        op_avg_panel.add(op_avg_month);
        op_avg_month.setHorizontalAlignment(SwingConstants.CENTER);
        op_avg_month.setForeground(new Color(255, 255, 255));
        op_avg_month.setColumns(10);

        // ---------------- Decision ---------------- //
        // ------- Header ------- //
        MyGuiComps.MyPanel decision_header_panel = new MyGuiComps.MyPanel();
        decision_header_panel.setBounds(op_avg_header_panel.getX() + op_avg_header_panel.getWidth() + 1, op_avg_header_panel.getY(), 166, 25);
        decision_header_panel.setBackground(op_avg_header_panel.getBackground());
        getContentPane().add(decision_header_panel);

        MyGuiComps.MyLabel main_decision_lbl = new MyGuiComps.MyLabel("Main");
        main_decision_lbl.setBounds(3, 0, 50, 25);
        decision_header_panel.add(main_decision_lbl);

        MyGuiComps.MyLabel secondary_decision_lbl = new MyGuiComps.MyLabel("Sec");
        secondary_decision_lbl.setBounds(main_decision_lbl.getX() + main_decision_lbl.getWidth(), main_decision_lbl.getY(), main_decision_lbl.getWidth(), main_decision_lbl.getHeight());
        decision_header_panel.add(secondary_decision_lbl);

        // Vs Panel
        MyGuiComps.MyPanel decisions_panel = new MyGuiComps.MyPanel();
        decisions_panel.setXY(decision_header_panel.getX(), decision_header_panel.getY() + decision_header_panel.getHeight() + 1);
        decisions_panel.setWidth(166);
        decisions_panel.setHeight(93);
        getContentPane().add(decisions_panel);

        // V4
        v4_field = new MyGuiComps.MyTextField();
        v4_field.setBounds(5, 5, 50, 25);
        v4_field.setForeground(Color.BLACK);
        decisions_panel.add(v4_field);

        // V8
        v8_field = new MyGuiComps.MyTextField();
        v8_field.setBounds(v4_field.getX() + v4_field.getWidth() + 1, v4_field.getY(), 50, 25);
        v8_field.setForeground(Color.BLACK);
        decisions_panel.add(v8_field);

        // V5
        v5_field = new MyGuiComps.MyTextField();
        v5_field.setBounds(v4_field.getX(), v4_field.getY() + v4_field.getHeight() + 3, 50, 25);
        decisions_panel.add(v5_field);

        // V6
        v6_field = new MyGuiComps.MyTextField();
        v6_field.setBounds(v5_field.getX() + v5_field.getWidth() + 1, v5_field.getY(), 50, 25);
        decisions_panel.add(v6_field);

        // V2
        v2_field = new MyGuiComps.MyTextField();
        v2_field.setBounds(v5_field.getX(), v5_field.getY() + v5_field.getHeight() + 3, 50, 25);
        decisions_panel.add(v2_field);

        // V7
        v7_field = new MyGuiComps.MyTextField();
        v7_field.setBounds(v2_field.getX() + v2_field.getWidth() + 1,v2_field.getY(), 50, 25);
        decisions_panel.add(v7_field);

        // V8 De corr
        v8_de_corr_field = new MyGuiComps.MyTextField();
        v8_de_corr_field.setBounds(v7_field.getX() + v7_field.getWidth() + 1, v7_field.getY(), 50, 25);
        decisions_panel.add(v8_de_corr_field);

        MyGuiComps.MyPanel logPanel = new MyGuiComps.MyPanel();
        logPanel.setBackground(new Color(176, 196, 222));
        logPanel.setBounds(897, 0, 147, 93);
        getContentPane().add(logPanel);
        logPanel.setLayout(null);

        log = new JTextArea();
        log.setBounds(10, 11, 127, 80);
        logPanel.add(log);

        MyGuiComps.MyPanel exp_header_panel = new MyGuiComps.MyPanel();
        exp_header_panel.setLayout(null);
        exp_header_panel.setBounds(decision_header_panel.getX() + decision_header_panel.getWidth() + 1, 0, 362, 25);
        getContentPane().add(exp_header_panel);

        MyGuiComps.MyLabel exp_move_lbl = new MyGuiComps.MyLabel("Move");
        exp_move_lbl.setBounds(66, 0, 50, 25);
        exp_header_panel.add(exp_move_lbl);
        exp_move_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        exp_move_lbl.setForeground(new Color(0, 0, 51));

        MyGuiComps.MyLabel exp_baskets_lbl = new MyGuiComps.MyLabel("Baskets");
        exp_baskets_lbl.setBounds(exp_move_lbl.getX() + exp_move_lbl.getWidth() + 1, exp_move_lbl.getY(), 50, 25);
        exp_header_panel.add(exp_baskets_lbl);
        exp_baskets_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        exp_baskets_lbl.setForeground(new Color(0, 0, 51));

        MyGuiComps.MyLabel v2_exp_lbl = new MyGuiComps.MyLabel("V2");
        v2_exp_lbl.setBounds(exp_baskets_lbl.getX() + exp_baskets_lbl.getWidth() + 1, exp_baskets_lbl.getY(), 50, 25);
        exp_header_panel.add(v2_exp_lbl);
        v2_exp_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        v2_exp_lbl.setForeground(new Color(0, 0, 51));

        MyGuiComps.MyLabel v7_exp_lbl = new MyGuiComps.MyLabel("V7");
        v7_exp_lbl.setBounds(v2_exp_lbl.getX() + v2_exp_lbl.getWidth() + 1, v2_exp_lbl.getY(), 50, 25);
        exp_header_panel.add(v7_exp_lbl);
        v7_exp_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        v7_exp_lbl.setForeground(new Color(0, 0, 51));

        MyGuiComps.MyLabel v8_exp_lbl = new MyGuiComps.MyLabel("V8");
        v8_exp_lbl.setBounds(v7_exp_lbl.getX() + v7_exp_lbl.getWidth() + 1, v7_exp_lbl.getY(), 50, 25);
        exp_header_panel.add(v8_exp_lbl);
        v8_exp_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        v8_exp_lbl.setForeground(new Color(0, 0, 51));

        MyGuiComps.MyLabel exp_lbl = new MyGuiComps.MyLabel("Exp");
        exp_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        exp_lbl.setForeground(new Color(0, 0, 51));
        exp_lbl.setBounds(0, 0, 68, 25);
        exp_header_panel.add(exp_lbl);

        // -------------------------- EXP -------------------------- //
        MyGuiComps.MyPanel exp_panel = new MyGuiComps.MyPanel();
        exp_panel.setBounds(exp_header_panel.getX(), exp_header_panel.getY() + exp_header_panel.getHeight() + 1, 362, 93);
        getContentPane().add(exp_panel);
        exp_panel.setLayout(null);

        // Start
        weekStartExpField = new MyGuiComps.MyTextField();
        weekStartExpField.setBounds(66, 11, 50, 25);
        exp_panel.add(weekStartExpField);
        weekStartExpField.setBorder(null);
        weekStartExpField.setFontSize(12);
        weekStartExpField.setHorizontalAlignment(SwingConstants.CENTER);
        weekStartExpField.setForeground(Color.WHITE);
        weekStartExpField.setColumns(10);

        monthStartExpField = new MyGuiComps.MyTextField();
        monthStartExpField.setBounds(66, 41, 50, 25);
        monthStartExpField.setFontSize(12);
        exp_panel.add(monthStartExpField);
        monthStartExpField.setBorder(null);
        monthStartExpField.setHorizontalAlignment(SwingConstants.CENTER);
        monthStartExpField.setForeground(Color.WHITE);
        monthStartExpField.setColumns(10);

        // Baskets
        expBasketsWeekField = new MyGuiComps.MyTextField();
        expBasketsWeekField.setHorizontalAlignment(SwingConstants.CENTER);
        expBasketsWeekField.setForeground(Color.WHITE);
        expBasketsWeekField.setColumns(10);
        expBasketsWeekField.setBorder(null);
        expBasketsWeekField.setBounds(weekStartExpField.getX() + weekStartExpField.getWidth() + 1, weekStartExpField.getY(), 50, 25);
        exp_panel.add(expBasketsWeekField);

        expBasketsMonthField = new MyGuiComps.MyTextField();
        expBasketsMonthField.setHorizontalAlignment(SwingConstants.CENTER);
        expBasketsMonthField.setForeground(Color.WHITE);
        expBasketsMonthField.setColumns(10);
        expBasketsMonthField.setBorder(null);
        expBasketsMonthField.setBounds(monthStartExpField.getX() + monthStartExpField.getWidth() + 1, monthStartExpField.getY(), 50, 25);
        exp_panel.add(expBasketsMonthField);

        // V2 V7 V8 (WEEK)
        exp_v2_week_field = new MyGuiComps.MyTextField();
        exp_v2_week_field.setBounds(expBasketsWeekField.getX() + expBasketsWeekField.getWidth() + 1, expBasketsWeekField.getY(), 50, 25);
        exp_panel.add(exp_v2_week_field);
        exp_v2_week_field.setHorizontalAlignment(SwingConstants.CENTER);
        exp_v2_week_field.setForeground(Color.WHITE);
        exp_v2_week_field.setColumns(10);
        exp_v2_week_field.setBorder(null);

        exp_v7_week_field = new MyGuiComps.MyTextField();
        exp_v7_week_field.setBounds(exp_v2_week_field.getX() + exp_v2_week_field.getWidth() + 1, exp_v2_week_field.getY(), 50, 25);
        exp_panel.add(exp_v7_week_field);
        exp_v7_week_field.setHorizontalAlignment(SwingConstants.CENTER);
        exp_v7_week_field.setForeground(Color.WHITE);
        exp_v7_week_field.setColumns(10);
        exp_v7_week_field.setBorder(null);

        exp_v8_week_field = new MyGuiComps.MyTextField();
        exp_v8_week_field.setBounds(exp_v7_week_field.getX() + exp_v7_week_field.getWidth() + 1, exp_v7_week_field.getY(), 50, 25);
        exp_v8_week_field.setHorizontalAlignment(SwingConstants.CENTER);
        exp_v8_week_field.setForeground(Color.WHITE);
        exp_v8_week_field.setColumns(10);
        exp_v8_week_field.setBorder(null);
        exp_panel.add(exp_v8_week_field);


        // V2 V7 V8 (MONTH)
        exp_v2_month_field = new MyGuiComps.MyTextField();
        exp_v2_month_field.setHorizontalAlignment(SwingConstants.CENTER);
        exp_v2_month_field.setForeground(Color.WHITE);
        exp_v2_month_field.setColumns(10);
        exp_v2_month_field.setBorder(null);
        exp_v2_month_field.setBounds(expBasketsMonthField.getX() + expBasketsMonthField.getWidth() + 1, expBasketsMonthField.getY(), 50, 25);
        exp_panel.add(exp_v2_month_field);

        exp_v7_month_field = new MyGuiComps.MyTextField();
        exp_v7_month_field.setHorizontalAlignment(SwingConstants.CENTER);
        exp_v7_month_field.setForeground(Color.WHITE);
        exp_v7_month_field.setColumns(10);
        exp_v7_month_field.setBorder(null);
        exp_v7_month_field.setBounds(exp_v2_month_field.getX() + exp_v2_month_field.getWidth() + 1, exp_v2_month_field.getY(), 50, 25);
        exp_panel.add(exp_v7_month_field);

        exp_v8_month_field = new MyGuiComps.MyTextField();
        exp_v8_month_field.setHorizontalAlignment(SwingConstants.CENTER);
        exp_v8_month_field.setForeground(Color.WHITE);
        exp_v8_month_field.setColumns(10);
        exp_v8_month_field.setBorder(null);
        exp_v8_month_field.setBounds(exp_v7_month_field.getX() + exp_v7_month_field.getWidth() + 1, exp_v7_month_field.getY(), 50, 25);
        exp_panel.add(exp_v8_month_field);

        MyGuiComps.MyLabel label_4 = new MyGuiComps.MyLabel("Week");
        label_4.setHorizontalAlignment(SwingConstants.CENTER);
        label_4.setForeground(new Color(0, 0, 51));
        label_4.setBounds(0, 11, 68, 25);
        exp_panel.add(label_4);

        MyGuiComps.MyLabel label_14 = new MyGuiComps.MyLabel("Month");
        label_14.setHorizontalAlignment(SwingConstants.CENTER);
        label_14.setForeground(new Color(0, 0, 51));
        label_14.setBounds(0, 41, 68, 25);
        exp_panel.add(label_14);
    }

    public static void openCharts() {
        try {
            MainMonthWeekChart mainMonthWeekChart = new MainMonthWeekChart(ApiObject.getInstance());
            mainMonthWeekChart.createChart();

            // Full charts
            FullCharts2 fullCharts = new FullCharts2(ApiObject.getInstance());
            fullCharts.createChart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- function -------------------- //

    // Floor
    public static double floor(double d) {
        return Math.floor(d * 100) / 100;
    }

    // Open setting window if 2 clicks
    private void open_setting_window(MouseEvent e) {
        if (e.getClickCount() == 2) {
            Setting setting = new Setting();
            setting.setVisible();
        }
    }

    // Popup
    public static void popup(String message, Exception e) {
        JOptionPane.showMessageDialog(null, message + "\n" + e.getMessage());
    }

    // Create xls file with the date of today
    public FileOutputStream getFile(String name) {
        try {
            return new FileOutputStream(apiObject.getExport_dir() + name);
        } catch (FileNotFoundException e) {
            WindowTA35.popup("Creating file error ", e);
            e.printStackTrace();
        }
        return null;
    }

    // Getters and Setters

    public Updater getUpdater() {
        return updater;
    }

    public void setUpdater(Updater updater) {
        this.updater = updater;
    }


    public BackGroundRunner getBackGroundRunner() {
        return backGroundRunner;
    }
}
