package counter;

import api.TA35;
import api.dde.DDE.DDEConnection;
import charts.charts.Main_Chart;
import charts.charts.Races_chart;
import charts.charts.Realtime_Chart;
import dataBase.mySql.JibeConnectionPool;
import gui.MyGuiComps;
import gui.details.DetailsWindow;
import locals.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class WindowTA35 extends MyGuiComps.MyFrame {

    Color lightGreen = new Color(12, 135, 0);
    Color lightRed = new Color(229, 19, 0);

    public static JButton start;
    private MyGuiComps.MyPanel bottomPanel;
    public MyGuiComps.MyTextField monthStartExpField;
    public static JTextArea log;

    public MyGuiComps.MyTextField v5_field;
    public MyGuiComps.MyTextField v6_field;
    public MyGuiComps.MyTextField v2_field;
    public MyGuiComps.MyTextField v7_field;
    public MyGuiComps.MyTextField v9_field;

    public int updater_id = 0;

    // Threads
    Updater updater;
    BackGroundRunner backGroundRunner;
    MyGuiComps.MyTextField basket_down_field;
    MyGuiComps.MyTextField basket_up_field;
    public MyGuiComps.MyTextField basketsSumField;
    DDEConnection ddeConnection;
    public MyGuiComps.MyTextField weekStartExpField;
    private MyGuiComps.MyButton btnDetails;

    public MyGuiComps.MyTextField exp_v5_month_field;
    public MyGuiComps.MyTextField exp_v6_month_field;
    public MyGuiComps.MyTextField exp_v8_month_field;

    public MyGuiComps.MyTextField exp_v4_old_week_field;
    public MyGuiComps.MyTextField exp_v8_old_week_field;
    public MyGuiComps.MyTextField exp_v8_week_field;

    public MyGuiComps.MyTextField expBasketsWeekField;
    public MyGuiComps.MyTextField expBasketsMonthField;

    public MyGuiComps.MyTextField optimi_count_week_field;
    public MyGuiComps.MyTextField pesimi_count_week_field;

    public MyGuiComps.MyTextField roll_optimi_count_week_field;
    public MyGuiComps.MyTextField roll_pesimi_count_week_field;

    public MyGuiComps.MyTextField optimi_count_month_field;
    public MyGuiComps.MyTextField pesimi_count_month_field;

    public MyGuiComps.MyTextField roll_optimi_count_month_field;
    public MyGuiComps.MyTextField roll_pesimi_count_month_field;

    public MyGuiComps.MyTextField index_races_iw_field, week_races_iw_field,
            week_races_wm_field, month_race_wm_field;

    // Constructor
    public WindowTA35() {
        super(TA35.getInstance(),"TA35");
        updater = new Updater(this);
        updater.getHandler().start();
        load_on_startup();
    }

    // Load on startup
    private void load_on_startup() {
        try {
            // DDE connection
            ddeConnection = new DDEConnection();

            // Back ground runner
            backGroundRunner = new BackGroundRunner(TA35.getInstance());
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
        } finally {
            System.exit(0);
        }
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

        int panels_height = 90;


//        week_races_wm_field = new MyGuiComps.MyTextField();
//        week_races_wm_field.setBounds(index_races_iw_field.getX() + index_races_iw_field.getWidth() + 1, index_races_iw_field.getY(), 50, 25);
//        races_panel.add(week_races_wm_field);
//
//        month_race_wm_field = new MyGuiComps.MyTextField();
//        month_race_wm_field.setBounds(week_races_wm_field.getX(), week_races_wm_field.getY() + week_races_wm_field.getHeight() + 3, 50, 25);
//        races_panel.add(month_race_wm_field);


        // --------------------------- headers ---------------------------- //
        // ------- Header ------- //
        MyGuiComps.MyPanel basket_header_panel = new MyGuiComps.MyPanel();
        basket_header_panel.setBounds(0, 0, 55, 25);
        getContentPane().add(basket_header_panel);

        MyGuiComps.MyLabel baskets_lbl = new MyGuiComps.MyLabel("Baskets");
        baskets_lbl.setBounds(0, 0, 55, 26);
        basket_header_panel.add(baskets_lbl);

        MyGuiComps.MyPanel decision_header_panel = new MyGuiComps.MyPanel();
        decision_header_panel.setBounds(basket_header_panel.getX() + basket_header_panel.getWidth() + 1, basket_header_panel.getY(), 112, 25);
        decision_header_panel.setBackground(basket_header_panel.getBackground());
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
        decisions_panel.setWidth(decision_header_panel.getWidth());
        decisions_panel.setHeight(panels_height);
        getContentPane().add(decisions_panel);

        // Races
        MyGuiComps.MyPanel races_panel_header = new MyGuiComps.MyPanel();
        races_panel_header.setBounds(decision_header_panel.getX() + decision_header_panel.getWidth() + 1, decision_header_panel.getY(), 112, 25);
        getContentPane().add(races_panel_header);

        MyGuiComps.MyLabel index_races_lbl = new MyGuiComps.MyLabel("Ind");
        index_races_lbl.setBounds(0, 0, 55, 25);
        races_panel_header.add(index_races_lbl);

        MyGuiComps.MyLabel week_races_lbl = new MyGuiComps.MyLabel("Week");
        week_races_lbl.setBounds(index_races_lbl.getX() + index_races_lbl.getWidth(), index_races_lbl.getY(), 55, 25);
        races_panel_header.add(week_races_lbl);


        // Exp
        MyGuiComps.MyPanel exp_header_panel = new MyGuiComps.MyPanel();
        exp_header_panel.setLayout(null);
        exp_header_panel.setBounds(races_panel_header.getX() + races_panel_header.getWidth() + 1, 0, 450, 25);
        getContentPane().add(exp_header_panel);

        MyGuiComps.MyLabel exp_move_lbl = new MyGuiComps.MyLabel("Move");
        exp_move_lbl.setBounds(66, 0, 50, 25);
        exp_header_panel.add(exp_move_lbl);

        MyGuiComps.MyLabel exp_v5 = new MyGuiComps.MyLabel("V5");
        exp_v5.setBounds(exp_move_lbl.getX() + exp_move_lbl.getWidth() + 1, exp_move_lbl.getY(), 50, 25);
        exp_header_panel.add(exp_v5);

        MyGuiComps.MyLabel exp_v6 = new MyGuiComps.MyLabel("V6");
        exp_v6.setBounds(exp_v5.getX() + exp_v5.getWidth() + 1, exp_v5.getY(), 50, 25);
        exp_header_panel.add(exp_v6);

        MyGuiComps.MyLabel exp_v8 = new MyGuiComps.MyLabel("V8");
        exp_v8.setBounds(exp_v6.getX() + exp_v6.getWidth() + 1, exp_v6.getY(), 50, 25);
        exp_header_panel.add(exp_v8);

        // Baskets
        MyGuiComps.MyLabel exp_baskets = new MyGuiComps.MyLabel("Baskets");
        exp_baskets.setBounds(exp_v8.getX() + exp_v8.getWidth() + 1, exp_v8.getY(), 50, 25);
        exp_header_panel.add(exp_baskets);

        // Op
        MyGuiComps.MyLabel op_count_lbl = new MyGuiComps.MyLabel("O / P");
        op_count_lbl.setBounds(exp_baskets.getX() + exp_baskets.getWidth() + 5, exp_baskets.getY(), 50, 25);
        op_count_lbl.setAlignmentX(MyGuiComps.MyLabel.CENTER);
        exp_header_panel.add(op_count_lbl);


        // Roll
        MyGuiComps.MyLabel roll_count_lbl = new MyGuiComps.MyLabel("Roll");
        roll_count_lbl.setBounds(op_count_lbl.getX() + op_count_lbl.getWidth() + 5, op_count_lbl.getY(), 50, 25);
        roll_count_lbl.setAlignmentX(MyGuiComps.MyLabel.CENTER);
        exp_header_panel.add(roll_count_lbl);

        MyGuiComps.MyLabel exp_lbl = new MyGuiComps.MyLabel("Exp");
        exp_lbl.setBounds(0, 0, 68, 25);
        exp_header_panel.add(exp_lbl);

        // ---------------------------  Baskets --------------------------- //
        MyGuiComps.MyPanel basketsPanel = new MyGuiComps.MyPanel();
        basketsPanel.setBounds(basket_header_panel.getX(), basket_header_panel.getY() + basket_header_panel.getHeight() + 1, basket_header_panel.getWidth(), panels_height);
        getContentPane().add(basketsPanel);

        basket_up_field = new MyGuiComps.MyTextField();
        basket_up_field.setBounds(5, 5, 45, 25);
        basketsPanel.add(basket_up_field);

        basket_down_field = new MyGuiComps.MyTextField();
        basket_down_field.setBounds(basket_up_field.getX(), basket_up_field.getY() + basket_up_field.getHeight() + 3, 45, 25);
        basketsPanel.add(basket_down_field);

        basketsSumField = new MyGuiComps.MyTextField();
        basketsSumField.setBounds(basket_down_field.getX(), basket_down_field.getY() + basket_down_field.getHeight() + 3, 45, 25);
        basketsPanel.add(basketsSumField);

        // ---------------- Decision ---------------- //

        // V4
        v2_field = new MyGuiComps.MyTextField();
        v2_field.setBounds(5, 5, 50, 25);
        decisions_panel.add(v2_field);

        // V8
        v7_field = new MyGuiComps.MyTextField();
        v7_field.setBounds(v2_field.getX() + v2_field.getWidth() + 1, v2_field.getY(), 50, 25);
        decisions_panel.add(v7_field);

        // V5
        v5_field = new MyGuiComps.MyTextField();
        v5_field.setBounds(v2_field.getX(), v2_field.getY() + v2_field.getHeight() + 3, 50, 25);
        decisions_panel.add(v5_field);

        // V6
        v6_field = new MyGuiComps.MyTextField();
        v6_field.setBounds(v5_field.getX() + v5_field.getWidth() + 1, v5_field.getY(), 50, 25);
        decisions_panel.add(v6_field);

        // V9
        v9_field = new MyGuiComps.MyTextField();
        v9_field.setBounds(v5_field.getX(), v5_field.getY() + v5_field.getHeight() + 3, 50, 25);
        decisions_panel.add(v9_field);

        // ---------------------------  Races --------------------------- //
        MyGuiComps.MyPanel races_panel = new MyGuiComps.MyPanel();
        races_panel.setBounds(races_panel_header.getX(), races_panel.getY() + races_panel_header.getHeight() + 1, 112, panels_height);
        getContentPane().add(races_panel);

        index_races_iw_field = new MyGuiComps.MyTextField();
        index_races_iw_field.setBounds(5, 5, 50, 25);
        races_panel.add(index_races_iw_field);

        week_races_iw_field = new MyGuiComps.MyTextField();
        week_races_iw_field.setBounds(index_races_iw_field.getX() + index_races_iw_field.getWidth() + 1, index_races_iw_field.getY(), 50, 25);
        races_panel.add(week_races_iw_field);

        month_race_wm_field = new MyGuiComps.MyTextField();
        month_race_wm_field.setBounds(index_races_iw_field.getX(), index_races_iw_field.getY() + index_races_iw_field.getHeight() + 3, 50, 25);
        races_panel.add(month_race_wm_field);

        MyGuiComps.MyPanel logPanel = new MyGuiComps.MyPanel();
        logPanel.setBackground(new Color(176, 196, 222));
        logPanel.setBounds(897, 0, 147, panels_height);
        getContentPane().add(logPanel);
        logPanel.setLayout(null);

        log = new JTextArea();
        log.setBounds(10, 11, 127, 80);
        logPanel.add(log);

        // -------------------------- EXP -------------------------- //
        MyGuiComps.MyPanel exp_panel = new MyGuiComps.MyPanel();
        exp_panel.setBounds(exp_header_panel.getX(), exp_header_panel.getY() + exp_header_panel.getHeight() + 1, 450, panels_height);
        getContentPane().add(exp_panel);
        exp_panel.setLayout(null);

        // ------ Week ------ //
        MyGuiComps.MyLabel week_lbl = new MyGuiComps.MyLabel("Week");
        week_lbl.setBounds(0, 11, 70, 25);
        exp_panel.add(week_lbl);

        weekStartExpField = new MyGuiComps.MyTextField();
        weekStartExpField.setBounds(week_lbl.getX() + week_lbl.getWidth() + 1, week_lbl.getY(), 50, 25);
        weekStartExpField.setForeground(Color.WHITE);
        weekStartExpField.setFont(weekStartExpField.getFont().deriveFont(Font.BOLD));
        exp_panel.add(weekStartExpField);

        // V2 V7 V8 (WEEK)
        exp_v4_old_week_field = new MyGuiComps.MyTextField();
        exp_v4_old_week_field.setBounds(weekStartExpField.getX() + weekStartExpField.getWidth() + 1, weekStartExpField.getY(), 50, 25);
        exp_panel.add(exp_v4_old_week_field);

        exp_v8_old_week_field = new MyGuiComps.MyTextField();
        exp_v8_old_week_field.setBounds(exp_v4_old_week_field.getX() + exp_v4_old_week_field.getWidth() + 1, exp_v4_old_week_field.getY(), 50, 25);
        exp_panel.add(exp_v8_old_week_field);

        exp_v8_week_field = new MyGuiComps.MyTextField();
        exp_v8_week_field.setBounds(exp_v8_old_week_field.getX() + exp_v8_old_week_field.getWidth() + 1, exp_v8_old_week_field.getY(), 50, 25);
        exp_panel.add(exp_v8_week_field);

        // Baskets
        expBasketsWeekField = new MyGuiComps.MyTextField();
        expBasketsWeekField.setBounds(exp_v8_week_field.getX() + exp_v8_week_field.getWidth() + 1, exp_v8_week_field.getY(), 50, 25);
        exp_panel.add(expBasketsWeekField);

        // ----------- Count ---------- //
        // ----------- Week
        // Optimi
        optimi_count_week_field = new MyGuiComps.MyTextField();
        optimi_count_week_field.setForeground(Themes.GREEN);
        optimi_count_week_field.setBounds(expBasketsWeekField.getX() + expBasketsWeekField.getWidth() + 5, expBasketsWeekField.getY(), 25, 25);
        exp_panel.add(optimi_count_week_field);

        // Pesimi
        pesimi_count_week_field = new MyGuiComps.MyTextField();
        pesimi_count_week_field.setForeground(Themes.RED);
        pesimi_count_week_field.setBounds(optimi_count_week_field.getX() + optimi_count_week_field.getWidth() + 1, optimi_count_week_field.getY(), 25, 25);
        exp_panel.add(pesimi_count_week_field);

        // Roll optimi week count
        roll_optimi_count_week_field = new MyGuiComps.MyTextField();
        roll_optimi_count_week_field.setForeground(Themes.GREEN);
        roll_optimi_count_week_field.setBounds(pesimi_count_week_field.getX() + pesimi_count_week_field.getWidth() + 5, pesimi_count_week_field.getY(), 25, 25);
        exp_panel.add(roll_optimi_count_week_field);

        // Roll pesimi week count
        roll_pesimi_count_week_field = new MyGuiComps.MyTextField();
        roll_pesimi_count_week_field.setForeground(Themes.RED);
        roll_pesimi_count_week_field.setBounds(roll_optimi_count_week_field.getX() + roll_optimi_count_week_field.getWidth() + 1, roll_optimi_count_week_field.getY(), 25, 25);
        exp_panel.add(roll_pesimi_count_week_field);

        // ------ Month ------ //
        MyGuiComps.MyLabel month_lbl = new MyGuiComps.MyLabel("Month");
        month_lbl.setBounds(week_lbl.getX(), week_lbl.getY() + week_lbl.getHeight() + 3, 70, 25);
        exp_panel.add(month_lbl);

        monthStartExpField = new MyGuiComps.MyTextField();
        monthStartExpField.setForeground(Color.WHITE);
        monthStartExpField.setFont(monthStartExpField.getFont().deriveFont(Font.BOLD));
        monthStartExpField.setBounds(month_lbl.getX() + month_lbl.getWidth() + 1, month_lbl.getY(), 50, 25);
        exp_panel.add(monthStartExpField);

        // V2 V7 V8 (MONTH)
        exp_v5_month_field = new MyGuiComps.MyTextField();
        exp_v5_month_field.setBounds(monthStartExpField.getX() + monthStartExpField.getWidth() + 1, monthStartExpField.getY(), 50, 25);
        exp_panel.add(exp_v5_month_field);

        exp_v6_month_field = new MyGuiComps.MyTextField();
        exp_v6_month_field.setBounds(exp_v5_month_field.getX() + exp_v5_month_field.getWidth() + 1, exp_v5_month_field.getY(), 50, 25);
        exp_panel.add(exp_v6_month_field);

        exp_v8_month_field = new MyGuiComps.MyTextField();
        exp_v8_month_field.setBounds(exp_v6_month_field.getX() + exp_v6_month_field.getWidth() + 1, exp_v6_month_field.getY(), 50, 25);
        exp_panel.add(exp_v8_month_field);


        expBasketsMonthField = new MyGuiComps.MyTextField();
        expBasketsMonthField.setBounds(exp_v8_month_field.getX() + exp_v8_month_field.getWidth() + 1, exp_v8_month_field.getY(), 50, 25);
        exp_panel.add(expBasketsMonthField);

        // Avg count roll and OP
        optimi_count_month_field = new MyGuiComps.MyTextField();
        optimi_count_month_field.setBounds(expBasketsMonthField.getX() + expBasketsMonthField.getWidth() + 5, expBasketsMonthField.getY(), 25, 25);
        optimi_count_month_field.setForeground(Themes.GREEN);
        exp_panel.add(optimi_count_month_field);

        // Pesimi
        pesimi_count_month_field = new MyGuiComps.MyTextField();
        pesimi_count_month_field.setBounds(optimi_count_month_field.getX() + optimi_count_month_field.getWidth() + 1, optimi_count_month_field.getY(), 25, 25);
        pesimi_count_month_field.setForeground(Themes.RED);
        exp_panel.add(pesimi_count_month_field);

        // Roll optimi week count
        roll_optimi_count_month_field = new MyGuiComps.MyTextField();
        roll_optimi_count_month_field.setForeground(Themes.GREEN);
        roll_optimi_count_month_field.setBounds(pesimi_count_month_field.getX() + pesimi_count_month_field.getWidth() + 5, pesimi_count_month_field.getY(), 25, 25);
        exp_panel.add(roll_optimi_count_month_field);

        // Roll pesimi week count
        roll_pesimi_count_month_field = new MyGuiComps.MyTextField();
        roll_pesimi_count_month_field.setForeground(Themes.RED);
        roll_pesimi_count_month_field.setBounds(roll_optimi_count_month_field.getX() + roll_optimi_count_month_field.getWidth() + 1, roll_optimi_count_month_field.getY(), 25, 25);
        exp_panel.add(roll_pesimi_count_month_field);

        // ----------------- Bottom panel ---------------- //
        bottomPanel = new MyGuiComps.MyPanel();
        bottomPanel.setBounds(basketsPanel.getX(), basketsPanel.getY() + basketsPanel.getHeight() + 1, 801, 38);
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
                TA35.getInstance().start();
                start.setEnabled(false);
            }
        });
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
        JComboBox chartsCombo = new JComboBox(new String[]{"Realtime", "Main", "Races"});
        chartsCombo.setBounds(start.getX() + start.getWidth() + 5, 8, 182, 23);
        bottomPanel.add(chartsCombo);
        chartsCombo.setBorder(null);
        chartsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (chartsCombo.getSelectedItem().toString()) {
                    case "Main":
                        Main_Chart main_chart = new Main_Chart(TA35.getInstance());
                        main_chart.createChart();
                        break;
                    case "Realtime":
                        Realtime_Chart realtimeChart = new Realtime_Chart(TA35.getInstance());
                        realtimeChart.createChart();
                        break;
                    case "Races":
                        Races_chart races_chart = new Races_chart(TA35.getInstance());
                        races_chart.createChart();
                        break;
                    default:
                        break;
                }
            }
        });
        chartsCombo.setForeground(new Color(0, 0, 51));
        chartsCombo.setFont(new Font("Dubai Medium", Font.PLAIN, 15));
        ((JLabel) chartsCombo.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

    }

    public static void openCharts() {
        try {
            // Realtime chart
            Realtime_Chart realtimeChart = new Realtime_Chart(TA35.getInstance());
            realtimeChart.createChart();

            // Full charts
            Main_Chart main_chart = new Main_Chart(TA35.getInstance());
            main_chart.createChart();

            // Races chart
            Races_chart races_chart = new Races_chart(TA35.getInstance());
            races_chart.createChart();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- function -------------------- //

    // Floor
    public static double floor(double d) {
        return Math.floor(d * 100) / 100;
    }

    // Popup
    public static void popup(String message, Exception e) {
        JOptionPane.showMessageDialog(null, message + "\n" + e.getMessage());
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
