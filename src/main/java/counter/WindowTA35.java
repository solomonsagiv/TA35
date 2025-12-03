package counter;

import api.TA35;
import api.dde.DDE.DDEConnection;
import charts.charts.Main_Chart;
import charts.charts.Realtime_Chart;
import dataBase.mySql.JibeConnectionPool;
import gui.MyGuiComps;
import gui.details.DetailsWindow;
import locals.Themes;
import options.Options;
import options.OptionsTableWindow;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
            week_races_wm_field, month_race_wm_field, future_week_counter_field, future_month_counter_field, weight_counter1_field, weight_counter2_field, weight_delta_field,
            ind_race_reset_field, week_race_reset_field, month_race_reset_field,
            future_week_counter_reset_field, future_month_counter_reset_field,
            weight_counter1_reset_field, weight_counter2_reset_field, weight_delta_reset_field;
    ;

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
    
    @Override
    protected void applyDarkMode() {
        super.applyDarkMode();
        // Update log panel background - darker in dark mode
        if (log != null) {
            if (Themes.isDarkMode()) {
                log.setBackground(Themes.DARK_BLUE_BG);
                log.setForeground(Themes.BRIGHT_WHITE_TEXT);
            } else {
                log.setBackground(new Color(176, 196, 222));
                log.setForeground(Color.BLACK);
            }
        }
        // Note: Charts will be updated when they are created/refreshed
        // For existing charts, they would need to be accessed and updated
        // This could be done by storing chart references if needed
    }

    /**
     * Configuration class for a column
     */
    private static class ColumnConfig {
        final String label;
        final List<Consumer<MyGuiComps.MyTextField>> fieldInitializers;
        
        ColumnConfig(String label, List<Consumer<MyGuiComps.MyTextField>> fieldInitializers) {
            this.label = label;
            this.fieldInitializers = fieldInitializers;
        }
    }

    /**
     * Creates a column with header label and fields
     * @return the x position after this column
     */
    private int createColumn(int x, int y, int columnWidth, int headerHeight, int fieldHeight, 
                             int fieldSpacing, int maxPanelHeight, Color headerBg, ColumnConfig config) {
        // Calculate actual panel height based on number of fields
        int numFields = config.fieldInitializers.size();
        int fieldsHeight = 5 + (fieldHeight + fieldSpacing) * numFields - fieldSpacing; // 5 is top padding
        
        // Header panel
        MyGuiComps.MyPanel headerPanel = new MyGuiComps.MyPanel();
        headerPanel.setBounds(x, y, columnWidth, headerHeight);
        if (headerBg != null) {
            headerPanel.setBackground(headerBg);
        }
        getContentPane().add(headerPanel);

        MyGuiComps.MyLabel label = new MyGuiComps.MyLabel(config.label);
        label.setBounds(0, 0, columnWidth, headerHeight);
        headerPanel.add(label);

        // Fields panel
        MyGuiComps.MyPanel fieldsPanel = new MyGuiComps.MyPanel();
        fieldsPanel.setBounds(x, y + headerHeight, columnWidth, fieldsHeight);
        getContentPane().add(fieldsPanel);
        fieldsPanel.setLayout(null);

        // Create fields
        int fieldY = 5;
        for (Consumer<MyGuiComps.MyTextField> initializer : config.fieldInitializers) {
            MyGuiComps.MyTextField field = new MyGuiComps.MyTextField();
            field.setBounds(5, fieldY, columnWidth - 10, fieldHeight);
            fieldsPanel.add(field);
            initializer.accept(field);
            fieldY += fieldHeight + fieldSpacing;
        }

        return x + columnWidth + 1;
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void initialize() {

        int columnWidth = 55;
        int headerHeight = 25;
        int fieldHeight = 25;
        int fieldSpacing = 3;
        int maxPanelsHeight = headerHeight + (fieldHeight + fieldSpacing) * 3; // Support up to 3 fields
        int startX = 0;
        int startY = 0;
        int currentX = startX;

        // ============================================================
        // Define all columns - easy to add/remove columns here!
        // To add a column: add a new ColumnConfig to the list
        // To remove a column: remove the corresponding ColumnConfig
        // Each column can have any number of fields (typically 2)
        // ============================================================
        List<ColumnConfig> columns = new ArrayList<>();
        
        // Baskets column (3 fields)
        columns.add(new ColumnConfig("Baskets", List.of(
            field -> basket_up_field = field,
            field -> basket_down_field = field,
            field -> basketsSumField = field
        )));
        
        // Main Decision column
        columns.add(new ColumnConfig("Main", List.of(
            field -> v2_field = field,
            field -> v5_field = field
        )));
        
        // Sec Decision column
        columns.add(new ColumnConfig("Sec", List.of(
            field -> v7_field = field,
            field -> v6_field = field
        )));
        
        // Races columns
        columns.add(new ColumnConfig("Ind", List.of(
            field -> index_races_iw_field = field,
            field -> ind_race_reset_field = field
        )));
        
        columns.add(new ColumnConfig("Week", List.of(
            field -> week_races_iw_field = field,
            field -> week_race_reset_field = field
        )));
        
        columns.add(new ColumnConfig("Month", List.of(
            field -> month_race_wm_field = field,
            field -> month_race_reset_field = field
        )));
        
        columns.add(new ColumnConfig("FWeek", List.of(
            field -> future_week_counter_field = field,
            field -> future_week_counter_reset_field = field
        )));
        
        columns.add(new ColumnConfig("FMonth", List.of(
            field -> future_month_counter_field = field,
            field -> future_month_counter_reset_field = field
        )));
        
        columns.add(new ColumnConfig("WC1", List.of(
            field -> weight_counter1_field = field,
            field -> weight_counter1_reset_field = field
        )));
        
        columns.add(new ColumnConfig("WC2", List.of(
            field -> weight_counter2_field = field,
            field -> weight_counter2_reset_field = field
        )));
        
        columns.add(new ColumnConfig("WDelta", List.of(
            field -> weight_delta_field = field,
            field -> weight_delta_reset_field = field
        )));

        // Create all columns
        MyGuiComps.MyPanel tempPanel = new MyGuiComps.MyPanel();
        Color headerBg = tempPanel.getBackground();
        for (ColumnConfig column : columns) {
            currentX = createColumn(currentX, startY, columnWidth, headerHeight, fieldHeight, 
                                    fieldSpacing, maxPanelsHeight, headerBg, column);
        }

        // Log Panel
        MyGuiComps.MyPanel logPanel = new MyGuiComps.MyPanel();
        logPanel.setBackground(new Color(176, 196, 222));
        logPanel.setBounds(currentX, startY, 147, maxPanelsHeight);
        getContentPane().add(logPanel);
        logPanel.setLayout(null);

        log = new JTextArea();
        log.setBounds(10, 11, 127, maxPanelsHeight - 22);
        logPanel.add(log);

        // ----------------- Bottom panel ---------------- //
        bottomPanel = new MyGuiComps.MyPanel();
        bottomPanel.setBounds(startX, startY + maxPanelsHeight, 
                currentX + logPanel.getWidth() - startX, 38);
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

        JComboBox<String> chartsCombo = new JComboBox<>(new String[]{"Real time", "Main", "Races", "Stocks", "Options"});
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
