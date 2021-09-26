package counter;

import api.ApiObject;
import api.dde.DDE.DDEConnection;
import book.BookWindow;
import charts.charts.FullCharts2;
import charts.charts.MainMonthWeekChart;
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

public class WindowTA35 extends MyGuiComps.MyFrame {

    Color lightGreen = new Color(12, 135, 0);
    Color lightRed = new Color(229, 19, 0);

    public MyGuiComps.MyTextField op_avg;
    public static MyGuiComps.MyTextField rando;
    public static JButton start;
    private MyGuiComps.MyPanel bottomPanel;
    public MyGuiComps.MyTextField monthStartExpField;
    public static JTextArea log;
    public MyGuiComps.MyTextField monthDeltaField;
    public MyGuiComps.MyTextField weekDeltaField;

    MyGuiComps.MyLabel v5_lbl;
    MyGuiComps.MyLabel v6_lbl;
    public MyGuiComps.MyTextField v5_field;
    public MyGuiComps.MyTextField v6_field;

    public int updater_id = 0;

    ApiObject apiObject = ApiObject.getInstance();

    // Threads
    Updater updater;
    BackGroundRunner backGroundRunner;
    MyGuiComps.MyTextField basket_down_field;
    MyGuiComps.MyTextField basket_up_field;
    public JTextField basketsSumField;
    DDEConnection ddeConnection;
    public MyGuiComps.MyTextField weekStartExpField;
    private MyGuiComps.MyButton btnDetails;
    public MyGuiComps.MyTextField expDeltaWeekField;
    public MyGuiComps.MyTextField expDeltaMonthField;
    public MyGuiComps.MyTextField expIndDeltaWeekField;
    public MyGuiComps.MyTextField expIndDeltaMonthField;
    public MyGuiComps.MyTextField expBasketsWeekField;
    public MyGuiComps.MyTextField expBasketsMonthField;
    public MyGuiComps.MyTextField indDeltaNoBasketsField;

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
        bottomPanel.setBounds(0, 129, 801, 38);
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
        JComboBox chartsCombo = new JComboBox(new String[]{ "Main chart", "Full chart 2", "Options window"});
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
                        new OptionsTableWindow("Options window", apiObject.getExpMonth());
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
        basketsPanel.setBounds(0, 26, 55, 102);
        getContentPane().add(basketsPanel);

        basket_down_field = new MyGuiComps.MyTextField();
        basket_down_field.setBorder(null);
        basket_down_field.setHorizontalAlignment(SwingConstants.CENTER);
        basket_down_field.setForeground(lightRed);
        basket_down_field.setColumns(10);
        basket_down_field.setBounds(5, 35, 45, 25);
        basketsPanel.add(basket_down_field);

        basket_up_field = new MyGuiComps.MyTextField();
        basket_up_field.setBorder(null);
        basket_up_field.setHorizontalAlignment(SwingConstants.CENTER);
        basket_up_field.setForeground(lightGreen);
        basket_up_field.setColumns(10);
        basket_up_field.setBounds(5, 5, 45, 25);
        basketsPanel.add(basket_up_field);

        basketsSumField = new JTextField();
        basketsSumField.setBorder(null);
        basketsSumField.setHorizontalAlignment(SwingConstants.CENTER);
        basketsSumField.setForeground(new Color(229, 19, 0));
        basketsSumField.setColumns(10);
        basketsSumField.setBounds(5, 70, 45, 25);
        basketsPanel.add(basketsSumField);

        MyGuiComps.MyPanel basket_header_panel = new MyGuiComps.MyPanel();
        basket_header_panel.setLayout(null);
        basket_header_panel.setBounds(0, 0, 55, 25);
        getContentPane().add(basket_header_panel);

        MyGuiComps.MyLabel baskets_lbl = new MyGuiComps.MyLabel("סלים");
        baskets_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        baskets_lbl.setForeground(new Color(0, 0, 51));
        baskets_lbl.setBounds(0, 0, 55, 26);
        basket_header_panel.add(baskets_lbl);

        MyGuiComps.MyPanel op_avg_header_panel = new MyGuiComps.MyPanel();
        op_avg_header_panel.setLayout(null);
        op_avg_header_panel.setBounds(basket_header_panel.getX() + basket_header_panel.getWidth() + 1, 0, 64, 25);
        getContentPane().add(op_avg_header_panel);

        MyGuiComps.MyLabel move_lbl = new MyGuiComps.MyLabel("ממוצע");
        move_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        move_lbl.setForeground(new Color(0, 0, 51));
        move_lbl.setBounds(0, 0, 68, 25);
        op_avg_header_panel.add(move_lbl);

        MyGuiComps.MyPanel op_avg_panel = new MyGuiComps.MyPanel();
        op_avg_panel.setBounds(op_avg_header_panel.getX(), op_avg_header_panel.getY() + op_avg_header_panel.getHeight() + 1, 64, 102);
        getContentPane().add(op_avg_panel);
        op_avg_panel.setLayout(null);

        op_avg = new MyGuiComps.MyTextField();
        op_avg.setBorder(null);
        op_avg.setBounds(7, 5, 49, 25);
        op_avg_panel.add(op_avg);
        op_avg.setHorizontalAlignment(SwingConstants.CENTER);
        op_avg.setForeground(Color.WHITE);
        op_avg.setColumns(10);

        MyGuiComps.MyPanel panel_18 = new MyGuiComps.MyPanel();
        panel_18.setLayout(null);
        panel_18.setBounds(0, 35, 64, 25);
        op_avg_panel.add(panel_18);

        MyGuiComps.MyLabel label_10 = new MyGuiComps.MyLabel("רנדומלי");
        label_10.setHorizontalAlignment(SwingConstants.CENTER);
        label_10.setForeground(new Color(0, 0, 51));
        label_10.setBounds(0, 0, 64, 25);
        panel_18.add(label_10);

        rando = new MyGuiComps.MyTextField();
        rando.setBorder(null);
        rando.setBounds(7, 66, 49, 25);
        op_avg_panel.add(rando);
        rando.setHorizontalAlignment(SwingConstants.CENTER);
        rando.setForeground(new Color(255, 255, 255));
        rando.setColumns(10);

        MyGuiComps.MyPanel deltaHeaderPanel = new MyGuiComps.MyPanel();
        deltaHeaderPanel.setLayout(null);
        deltaHeaderPanel.setBounds(op_avg_header_panel.getX() + op_avg_header_panel.getWidth() + 1, op_avg_header_panel.getY(), 80, 25);

        MyGuiComps.MyLabel deltaLbl = new MyGuiComps.MyLabel("דלתא");
        deltaLbl.setBounds(0, 0, deltaHeaderPanel.getWidth(), deltaHeaderPanel.getHeight());
        deltaLbl.setHorizontalAlignment(JLabel.CENTER);
        deltaLbl.setForeground(new Color(0, 0, 51));
        deltaHeaderPanel.add(deltaLbl);
        getContentPane().add(deltaHeaderPanel);

        // Delta panel
        MyGuiComps.MyPanel deltaPanel = new MyGuiComps.MyPanel();
        deltaPanel.setLayout(null);
        deltaPanel.setBounds(deltaHeaderPanel.getX(), deltaHeaderPanel.getY() + deltaHeaderPanel.getHeight() + 1, 80, 102);
        getContentPane().add(deltaPanel);

        // Week delta
        weekDeltaField = new MyGuiComps.MyTextField();
        weekDeltaField.setBorder(null);
        weekDeltaField.setBounds(5, 5, 65, 25);
        weekDeltaField.setHorizontalAlignment(JTextField.CENTER);
        deltaPanel.add(weekDeltaField);

        // Month delta
        monthDeltaField = new MyGuiComps.MyTextField();
        monthDeltaField.setBorder(null);
        monthDeltaField.setBounds(5, 35, 65, 25);
        monthDeltaField.setHorizontalAlignment(JTextField.CENTER);
        deltaPanel.add(monthDeltaField);

        indDeltaNoBasketsField = new MyGuiComps.MyTextField();
        indDeltaNoBasketsField.setHorizontalAlignment(SwingConstants.CENTER);
        indDeltaNoBasketsField.setBorder(null);
        indDeltaNoBasketsField.setBounds(5, 66, 65, 25);
        deltaPanel.add(indDeltaNoBasketsField);


        MyGuiComps.MyPanel decision_header_panel = new MyGuiComps.MyPanel();
        decision_header_panel.setBounds(deltaHeaderPanel.getX() + deltaHeaderPanel.getWidth() + 1, deltaHeaderPanel.getY(), 80, 25);
        getContentPane().add(decision_header_panel);

        MyGuiComps.MyLabel decision_lbl = new MyGuiComps.MyLabel("מכונה");
        decision_lbl.setBounds(0, 0, decision_header_panel.getWidth(), decision_header_panel.getHeight());
        decision_header_panel.add(decision_lbl);

        // Vs Panel
        MyGuiComps.MyPanel decisions_panel = new MyGuiComps.MyPanel();
        decisions_panel.setXY(decision_header_panel.getX(), decision_header_panel.getY() + decision_header_panel.getHeight() + 1);
        decisions_panel.setWidth(80);
        decisions_panel.setHeight(102);
        getContentPane().add(decisions_panel);

        // V5
        v5_field = new MyGuiComps.MyTextField();
        v5_field.setBounds(5, 5, 65, 25);
        decisions_panel.add(v5_field);

        // V6
        v6_field = new MyGuiComps.MyTextField();
        v6_field.setBounds(5, 35, 65, 25);
        decisions_panel.add(v6_field);


        MyGuiComps.MyPanel logPanel = new MyGuiComps.MyPanel();
        logPanel.setBackground(new Color(176, 196, 222));
        logPanel.setBounds(897, 0, 147, 102);
        getContentPane().add(logPanel);
        logPanel.setLayout(null);

        log = new JTextArea();
        log.setBounds(10, 11, 127, 80);
        logPanel.add(log);

        MyGuiComps.MyPanel exp_header_panel = new MyGuiComps.MyPanel();
        exp_header_panel.setLayout(null);
        exp_header_panel.setBounds(decision_header_panel.getX() + decision_header_panel.getWidth() + 1, 0, 362, 25);
        getContentPane().add(exp_header_panel);

        MyGuiComps.MyLabel label_2 = new MyGuiComps.MyLabel("תנועה");
        label_2.setBounds(66, 0, 68, 25);
        exp_header_panel.add(label_2);
        label_2.setHorizontalAlignment(SwingConstants.CENTER);
        label_2.setForeground(new Color(0, 0, 51));

        MyGuiComps.MyLabel label_13 = new MyGuiComps.MyLabel("סלים");
        label_13.setBounds(139, 0, 72, 25);
        exp_header_panel.add(label_13);
        label_13.setHorizontalAlignment(SwingConstants.CENTER);
        label_13.setForeground(new Color(0, 0, 51));

        MyGuiComps.MyLabel label_11 = new MyGuiComps.MyLabel("דלתא");
        label_11.setBounds(212, 0, 68, 25);
        exp_header_panel.add(label_11);
        label_11.setHorizontalAlignment(SwingConstants.CENTER);
        label_11.setForeground(new Color(0, 0, 51));

        MyGuiComps.MyLabel label_1 = new MyGuiComps.MyLabel("מניות");
        label_1.setBounds(284, 0, 68, 25);
        exp_header_panel.add(label_1);
        label_1.setHorizontalAlignment(SwingConstants.CENTER);
        label_1.setForeground(new Color(0, 0, 51));

        MyGuiComps.MyLabel exp_lbl = new MyGuiComps.MyLabel("פקיעה");
        exp_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        exp_lbl.setForeground(new Color(0, 0, 51));
        exp_lbl.setBounds(0, 0, 68, 25);
        exp_header_panel.add(exp_lbl);

        MyGuiComps.MyPanel exp_panel = new MyGuiComps.MyPanel();
        exp_panel.setBounds(exp_header_panel.getX(), exp_header_panel.getY() + exp_header_panel.getHeight() + 1, 362, 102);
        getContentPane().add(exp_panel);
        exp_panel.setLayout(null);

        expDeltaWeekField = new MyGuiComps.MyTextField();
        expDeltaWeekField.setHorizontalAlignment(SwingConstants.CENTER);
        expDeltaWeekField.setForeground(Color.WHITE);
        expDeltaWeekField.setColumns(10);
        expDeltaWeekField.setBorder(null);
        expDeltaWeekField.setBounds(212, 11, 68, 25);
        exp_panel.add(expDeltaWeekField);

        expDeltaMonthField = new MyGuiComps.MyTextField();
        expDeltaMonthField.setHorizontalAlignment(SwingConstants.CENTER);
        expDeltaMonthField.setForeground(Color.WHITE);
        expDeltaMonthField.setColumns(10);
        expDeltaMonthField.setBorder(null);
        expDeltaMonthField.setBounds(212, 41, 68, 25);
        exp_panel.add(expDeltaMonthField);

        expBasketsWeekField = new MyGuiComps.MyTextField();
        expBasketsWeekField.setHorizontalAlignment(SwingConstants.CENTER);
        expBasketsWeekField.setForeground(Color.WHITE);
        expBasketsWeekField.setColumns(10);
        expBasketsWeekField.setBorder(null);
        expBasketsWeekField.setBounds(139, 11, 68, 25);
        exp_panel.add(expBasketsWeekField);

        expBasketsMonthField = new MyGuiComps.MyTextField();
        expBasketsMonthField.setHorizontalAlignment(SwingConstants.CENTER);
        expBasketsMonthField.setForeground(Color.WHITE);
        expBasketsMonthField.setColumns(10);
        expBasketsMonthField.setBorder(null);
        expBasketsMonthField.setBounds(139, 41, 68, 25);
        exp_panel.add(expBasketsMonthField);

        weekStartExpField = new MyGuiComps.MyTextField();
        weekStartExpField.setBounds(66, 11, 68, 25);
        exp_panel.add(weekStartExpField);
        weekStartExpField.setBorder(null);
        weekStartExpField.setHorizontalAlignment(SwingConstants.CENTER);
        weekStartExpField.setForeground(Color.WHITE);
        weekStartExpField.setColumns(10);

        monthStartExpField = new MyGuiComps.MyTextField();
        monthStartExpField.setBounds(66, 41, 68, 25);
        exp_panel.add(monthStartExpField);
        monthStartExpField.setBorder(null);
        monthStartExpField.setHorizontalAlignment(SwingConstants.CENTER);
        monthStartExpField.setForeground(Color.WHITE);
        monthStartExpField.setColumns(10);

        expIndDeltaWeekField = new MyGuiComps.MyTextField();
        expIndDeltaWeekField.setBounds(284, 11, 68, 25);
        exp_panel.add(expIndDeltaWeekField);
        expIndDeltaWeekField.setHorizontalAlignment(SwingConstants.CENTER);
        expIndDeltaWeekField.setForeground(Color.WHITE);
        expIndDeltaWeekField.setColumns(10);
        expIndDeltaWeekField.setBorder(null);

        expIndDeltaMonthField = new MyGuiComps.MyTextField();
        expIndDeltaMonthField.setBounds(284, 41, 68, 25);
        exp_panel.add(expIndDeltaMonthField);
        expIndDeltaMonthField.setHorizontalAlignment(SwingConstants.CENTER);
        expIndDeltaMonthField.setForeground(Color.WHITE);
        expIndDeltaMonthField.setColumns(10);
        expIndDeltaMonthField.setBorder(null);

        MyGuiComps.MyLabel label_4 = new MyGuiComps.MyLabel("שבועי");
        label_4.setHorizontalAlignment(SwingConstants.CENTER);
        label_4.setForeground(new Color(0, 0, 51));
        label_4.setBounds(0, 11, 68, 25);
        exp_panel.add(label_4);

        MyGuiComps.MyLabel label_14 = new MyGuiComps.MyLabel("חודשי");
        label_14.setHorizontalAlignment(SwingConstants.CENTER);
        label_14.setForeground(new Color(0, 0, 51));
        label_14.setBounds(0, 41, 68, 25);
        exp_panel.add(label_14);
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
