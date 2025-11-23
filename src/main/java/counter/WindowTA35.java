package counter;

import api.TA35;
import api.dde.DDE.DDEConnection;
import charts.charts.Main_Chart;
import charts.charts.Realtime_Chart;
import dataBase.mySql.JibeConnectionPool;
import gui.MyGuiComps;
import gui.details.DetailsWindow;
import options.Options;
import options.OptionsTableWindow;
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
    public static JTextArea log;

    public MyGuiComps.MyTextField v5_field;
    public MyGuiComps.MyTextField v6_field;
    public MyGuiComps.MyTextField v2_field;
    public MyGuiComps.MyTextField v7_field;

    public int updater_id = 0;

    // Threads
    Updater updater;
    BackGroundRunner backGroundRunner;
    MyGuiComps.MyTextField basket_down_field;
    MyGuiComps.MyTextField basket_up_field;
    public MyGuiComps.MyTextField basketsSumField;
    DDEConnection ddeConnection;
    private MyGuiComps.MyButton btnDetails;

    public MyGuiComps.MyTextField index_races_iw_field, week_races_iw_field,
            week_races_wm_field, month_race_wm_field, stocks_counter_present_field;

    // Constructor
    public WindowTA35() {
        super(TA35.getInstance(), "TA35");
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

        // ---------------------------  Races --------------------------- //
        MyGuiComps.MyPanel races_panel = new MyGuiComps.MyPanel();
        races_panel.setBounds(races_panel_header.getX(), races_panel_header.getY() + races_panel_header.getHeight() + 1, 112, panels_height);
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

        stocks_counter_present_field = new MyGuiComps.MyTextField();
        stocks_counter_present_field.setBounds(month_race_wm_field.getX() + month_race_wm_field.getWidth() + 1, month_race_wm_field.getY(), 50, 25);
        stocks_counter_present_field.setForeground(Color.WHITE);
        stocks_counter_present_field.setHorizontalAlignment(JTextField.CENTER);
        stocks_counter_present_field.setFont(stocks_counter_present_field.getFont().deriveFont(Font.BOLD));
        races_panel.add(stocks_counter_present_field);

        // Log Panel - מיקום מותאם אחרי הסרת exp
        MyGuiComps.MyPanel logPanel = new MyGuiComps.MyPanel();
        logPanel.setBackground(new Color(176, 196, 222));
        logPanel.setBounds(races_panel_header.getX() + races_panel_header.getWidth() + 1, 0, 147, panels_height + 25);
        getContentPane().add(logPanel);
        logPanel.setLayout(null);

        log = new JTextArea();
        log.setBounds(10, 11, 127, 80);
        logPanel.add(log);

        // ----------------- Bottom panel ---------------- //
        bottomPanel = new MyGuiComps.MyPanel();
        bottomPanel.setBounds(basketsPanel.getX(), basketsPanel.getY() + basketsPanel.getHeight() + 1, 
                races_panel_header.getX() + races_panel_header.getWidth() + logPanel.getWidth() - basketsPanel.getX(), 38);
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
        JComboBox chartsCombo = new JComboBox(new String[]{"Real time", "Main", "Races", "Stocks", "Options"});
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
                    case "Real time":
                        Realtime_Chart realtimeChart = new Realtime_Chart(TA35.getInstance());
                        realtimeChart.createChart();
                        break;
                    case "Stocks":
                        new MiniStockTable(TA35.getInstance(), "Stocks table");
                        break;
                    case "Options":
                        Options options = TA35.getInstance().getExps().getMonth().getOptions();
                        new OptionsTableWindow(TA35.getInstance(), "Options", options);
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

            // Stocks table
            new MiniStockTable(TA35.getInstance(), "Stocks table");

            // Options window
//            new OptionsTableWindow(TA35.getInstance(), "Options", TA35.getInstance().getExps().getMonth().getOptions());

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
