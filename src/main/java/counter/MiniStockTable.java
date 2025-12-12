package counter;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import api.deltaTest.Calculator;
import arik.Arik;
import gui.MyGuiComps;
import locals.Themes;
import miniStocks.MiniStock;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * טבלת מניות מעוצבת (תואם Java 11):
 * Name | Open% | Last% | Change% | Counter | Weight
 * - צביעת שינוי בין ריענונים (ירוק/אדום)
 * - זברה-רואו, כותרת מעוצבת, גריד עדין, מיון עמודות
 * - פורמט מספרים עם % וחץ ↑/↓
 */
public class MiniStockTable extends MyGuiComps.MyFrame {

    // פונטים בטוחים (נגזרים מה-UI)
    private static Font deriveUI(int style, float size) {
        Font base = UIManager.getFont("Label.font");
        if (base == null) base = new JLabel().getFont();
        return base.deriveFont(style, size);
    }

    private static final Font HEADER_FONT = deriveUI(Font.BOLD, 16f);
    private static final Font CELL_FONT   = deriveUI(Font.PLAIN, 16f);
    private static final Font NAME_FONT   = deriveUI(Font.BOLD, 16f);
    private static final Font KPI_FONT    = deriveUI(Font.PLAIN, 20f);

    /* ======== Colors / Styles ======== */
    private static final Color BG_WHITE      = Color.WHITE;
    private static final Color BG_STRIPE     = new Color(0xFAFAFA);
    private static final Color BG_GREEN_SOFT = new Color(0xE6F4EA);
    private static final Color BG_RED_SOFT   = new Color(0xFDE7E9);
    private static final Color GRID_COLOR    = new Color(0xEEEEEE);
    private static final Color HEADER_BG     = new Color(0xF5F7FA);
    private static final Color HEADER_FG     = new Color(0x263238);
    private static final Color SELECTION_BG  = new Color(0xE3F2FD);
    private static final Color SELECTION_FG  = new Color(0x000000);
    
    /* ======== Dark Mode Colors ======== */
    // Dark mode - רקע פחות כהה וטקסט בהיר יותר
    private static final Color DARK_BG_MAIN      = new Color(55, 65, 85);   // רקע פחות כהה
    private static final Color DARK_BG_STRIPE    = new Color(50, 60, 75);   // פסים
    private static final Color DARK_BG_GREEN     = new Color(50, 120, 70);  // ירוק רך
    private static final Color DARK_BG_RED       = new Color(120, 50, 60);  // אדום רך
    private static final Color DARK_GRID_COLOR   = new Color(70, 80, 95);   // רשת
    private static final Color DARK_HEADER_BG    = new Color(60, 70, 85);   // כותרת
    private static final Color DARK_HEADER_FG    = new Color(240, 240, 240); // טקסט כותרת בהיר
    private static final Color DARK_SELECTION_BG = new Color(70, 90, 120);  // בחירה
    private static final Color DARK_SELECTION_FG = new Color(255, 255, 255); // טקסט בחירה
    private static final Color DARK_TEXT         = new Color(230, 230, 230); // טקסט בהיר ומואר
    private static final Color DARK_TEXT_NAME    = new Color(255, 255, 255); // טקסט שם בהיר מאוד
    
    /* ======== Helper methods for dynamic colors ======== */
    private static Color getCellBackground(boolean isEvenRow) {
        if (Themes.isDarkMode()) {
            return isEvenRow ? DARK_BG_MAIN : DARK_BG_STRIPE;
        }
        return isEvenRow ? BG_WHITE : BG_STRIPE;
    }
    
    private static Color getGridColor() {
        return Themes.isDarkMode() ? DARK_GRID_COLOR : GRID_COLOR;
    }
    
    private static Color getHeaderBackground() {
        return Themes.isDarkMode() ? DARK_HEADER_BG : HEADER_BG;
    }
    
    private static Color getHeaderForeground() {
        return Themes.isDarkMode() ? DARK_HEADER_FG : HEADER_FG;
    }
    
    private static Color getSelectionBackground() {
        return Themes.isDarkMode() ? DARK_SELECTION_BG : SELECTION_BG;
    }
    
    private static Color getSelectionForeground() {
        return Themes.isDarkMode() ? DARK_SELECTION_FG : SELECTION_FG;
    }
    
    private static Color getTextColor() {
        return Themes.isDarkMode() ? DARK_TEXT : Color.BLACK;
    }
    
    private static Color getNameTextColor() {
        return Themes.isDarkMode() ? DARK_TEXT_NAME : Color.BLACK;
    }
    
    private static Color getChangeBackground(int direction) {
        if (direction == 0) return null;
        if (Themes.isDarkMode()) {
            return direction > 0 ? DARK_BG_GREEN : DARK_BG_RED;
        }
        return direction > 0 ? BG_GREEN_SOFT : BG_RED_SOFT;
    }

    private static final DecimalFormat DF_PCT = new DecimalFormat("0.00");
    private static final DecimalFormat DF_WGT = new DecimalFormat("0.00");
    private static final DecimalFormat DF_INT = new DecimalFormat("0");

    /* ======== UI ======== */
    private JTable table;
    private Model model;

    private MyGuiComps.MyTextField
            delta_field,
            delta_weight_field,
            counter_2_weight_field,
            top_weight_counter_2_field,
            top60_avg_counter_2_field,
            min_weight_field,
            max_weight_field;

    /* ======== Data ======== */
    private List<MiniStock> stocksRef;

    /* ======== Refresh Thread ======== */
    private Thread runner;
    private volatile boolean run = true;


    public MiniStockTable(BASE_CLIENT_OBJECT client, String title) throws HeadlessException {
        super(client, title);
    }

    @Override
    public void initListeners() { /* no-op */ }

    @Override
    public void initialize() {
        this.stocksRef = new ArrayList<>(TA35.getInstance().getStocksHandler().getStocks());
        buildUI();
        refreshNow();     // טעינה ראשונה
        startRunner();    // רענון מחזורי
    }

    @Override
    public void onClose() {
        run = false;
        if (runner != null) runner.interrupt();
        super.onClose();
    }

    /* ======================== UI Build ======================== */

    private void buildUI() {
        setLayout(new BorderLayout());

        // ---- Controls (KPIs) ----
        delta_field                     = new MyGuiComps.MyTextField(); delta_field.setFontSize(22);
        delta_weight_field              = new MyGuiComps.MyTextField(); delta_weight_field.setFontSize(22);
        counter_2_weight_field          = new MyGuiComps.MyTextField(); counter_2_weight_field.setFontSize(22);
        top_weight_counter_2_field      = new MyGuiComps.MyTextField(); top_weight_counter_2_field.setFontSize(22);
        top60_avg_counter_2_field       = new MyGuiComps.MyTextField(); top60_avg_counter_2_field.setFontSize(22);
        min_weight_field             = new MyGuiComps.MyTextField(); min_weight_field.setFontSize(22);
        max_weight_field            = new MyGuiComps.MyTextField(); max_weight_field.setFontSize(22);

        JPanel controlPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        controlPanel.setOpaque(true);
        controlPanel.add(createColumn("C W:", counter_2_weight_field));
        controlPanel.add(createColumn("T60%:", top_weight_counter_2_field));
        controlPanel.add(createColumn("AVG:", top60_avg_counter_2_field));
        controlPanel.add(createColumn("D W:", delta_weight_field));

        JPanel midPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        midPanel.setOpaque(true);
        midPanel.add(createColumn("Min:", min_weight_field));
        midPanel.add(createColumn("Max:", max_weight_field));

        JPanel summaryPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        summaryPanel.setOpaque(true);
        summaryPanel.add(controlPanel);
        summaryPanel.add(midPanel);
        add(summaryPanel, BorderLayout.NORTH);

        // Apply initial colors based on dark mode
        if (Themes.isDarkMode()) {
            controlPanel.setBackground(Themes.getPanelBackgroundColor());
            midPanel.setBackground(Themes.getPanelBackgroundColor());
            summaryPanel.setBackground(Themes.getPanelBackgroundColor());
        }

        // ---- Table ----
        model = new Model();
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(getGridColor());
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(getSelectionBackground());
        table.setSelectionForeground(getSelectionForeground());

        // Header style
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setReorderingAllowed(true);
            header.setResizingAllowed(true);
            header.setBackground(getHeaderBackground());
            header.setForeground(getHeaderForeground());
            header.setFont(new Font("Ariel", Font.BOLD, 15));
            header.setOpaque(true);
            
            // Custom header renderer
            final TableCellRenderer base = header.getDefaultRenderer();
            header.setDefaultRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    Component comp = base.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                    comp.setFont(HEADER_FONT);
                    if (comp instanceof JComponent) {
                        ((JComponent) comp).setOpaque(true);
                    }
                    comp.setBackground(getHeaderBackground());
                    comp.setForeground(getHeaderForeground());
                    if (comp instanceof JLabel) {
                        ((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    return comp;
                }
            });
        }
        
        updateTableColors(); // Apply initial colors based on dark mode

        // Renderers
        CellRenderer renderer = new CellRenderer(model);
        for (int c = 0; c < model.getColumnCount(); c++) {
            TableColumn col = table.getColumnModel().getColumn(c);
            if (c == Model.COL_NAME) {
                col.setCellRenderer(new NameRenderer());
            } else {
                col.setCellRenderer(renderer);
            }
        }

        // Column widths
        int[] widths = {160, 100, 100, 110, 100, 100, 100, 100};
        TableColumnModel cm = table.getColumnModel();
        for (int i = 0; i < Math.min(widths.length, cm.getColumnCount()); i++) {
            cm.getColumn(i).setPreferredWidth(widths[i]);
        }

        // Context menu
        setupContextMenu();

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * מגדיר תפריט קליק ימני לטבלה
     */
    private void setupContextMenu() {
        JPopupMenu popup = new JPopupMenu();
        
        JMenuItem detailsItem = new JMenuItem("📊 View All Stocks Details");
        detailsItem.addActionListener(e -> {
            openAllStocksDetailsWindow();
        });
        popup.add(detailsItem);
        
        // Add mouse listener to table
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            
            private void showPopup(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * פותח חלון עם פרטי כל המניות
     */
    private void openAllStocksDetailsWindow() {
        SwingUtilities.invokeLater(() -> {
            AllStocksDetailsWindow detailsWindow = new AllStocksDetailsWindow();
            detailsWindow.setVisible(true);
        });
    }

    private JPanel createColumn(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(HEADER_FONT);
        // Apply dark mode colors
        updatePanelColors(panel, label);
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * מעדכן צבעים של panel ו-label לפי dark mode
     */
    private void updatePanelColors(JPanel panel, JLabel label) {
        if (Themes.isDarkMode()) {
            panel.setBackground(Themes.getPanelBackgroundColor());
            panel.setOpaque(true);
            label.setForeground(getHeaderForeground());
        } else {
            panel.setBackground(null);
            panel.setOpaque(false);
            label.setForeground(HEADER_FG);
        }
    }
    
    /**
     * מעדכן את כל הצבעים בטבלה לפי dark mode
     */
    private void updateTableColors() {
        if (table == null) return;
        
        SwingUtilities.invokeLater(() -> {
            table.setGridColor(getGridColor());
            table.setSelectionBackground(getSelectionBackground());
            table.setSelectionForeground(getSelectionForeground());
            
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                header.setBackground(getHeaderBackground());
                header.setForeground(getHeaderForeground());
            }
            
            // Force repaint of all cells
            table.repaint();
        });
    }
    
    /**
     * Override applyDarkMode כדי לעדכן את כל הצבעים בטבלה
     */
    @Override
    public void applyDarkMode() {
        super.applyDarkMode();
        updateTableColors();
        // Update all panels in the summary panel
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                updatePanelColorsRecursive((JPanel) comp);
            }
        }
    }
    
    /**
     * מעדכן צבעים רקורסיבית לכל ה-panels
     */
    private void updatePanelColorsRecursive(JPanel panel) {
        if (Themes.isDarkMode()) {
            panel.setBackground(Themes.getPanelBackgroundColor());
            panel.setOpaque(true);
        } else {
            panel.setBackground(null);
            panel.setOpaque(false);
        }
        
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                updatePanelColorsRecursive((JPanel) comp);
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                // Update label colors for KPI headers
                if (Themes.isDarkMode()) {
                    label.setForeground(getHeaderForeground());
                } else {
                    label.setForeground(HEADER_FG);
                }
            }
        }
    }

    /**
     * מעדכן את צבע הטקסט של השדה לפי הערך
     * @param field השדה לעדכון
     * @param greenThreshold ערך מינימלי לצבע ירוק (אם הערך > greenThreshold -> ירוק)
     * @param redThreshold ערך מקסימלי לצבע אדום (אם הערך < redThreshold -> אדום)
     */
    private void updateFieldColor(JTextField field, int greenThreshold, int redThreshold) {
        try {
            String text = field.getText();
            if (text == null || text.trim().isEmpty()) {
                return;
            }
            int value = Integer.parseInt(text.trim());
            if (value > greenThreshold) {
                field.setForeground(Themes.GREEN);
            } else if (value < redThreshold) {
                field.setForeground(Themes.RED);
            }
        } catch (NumberFormatException e) {
            // אם לא ניתן לפרסר את הערך, לא מעדכן צבע
        }
    }

    /* ======================== Refresh & Runner ======================== */

    private void refreshNow() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // עדכון מודל הטבלה
                model.refreshFrom(stocksRef);

                double[] vals = Calculator.get_stocks_counters();

                // עדכון ה-KPIs העליונים
                int counter2WeightValue = (int)vals[Calculator.COUNTER_2_WEIGHT_POSITIVE];
                counter_2_weight_field.colorForge(counter2WeightValue);
                counter_2_weight_field.setText(counter_2_weight_field.getText() + "%");
                
                // Update top_weight_counter_2 as percentage with % sign and color coding (>50 green, <50 red)
                double topWeightPercent = TA35.getInstance().getTop_weight_counter_2();
                if (topWeightPercent > 50.0) {
                    top_weight_counter_2_field.setForeground(Themes.GREEN);
                } else {
                    top_weight_counter_2_field.setForeground(Themes.RED);
                }
                top_weight_counter_2_field.setText(String.valueOf((int)Math.round(topWeightPercent)) + "%");
                
                delta_field.colorForge((int)vals[Calculator.DELTA_WEIGHT_POSITIVE_STOCKS]);
                int deltaWeightValue = (int)Math.round(TA35.getInstance().getDelta_weight());
                delta_weight_field.colorForge(deltaWeightValue);
                delta_weight_field.setText(delta_weight_field.getText() + "%");
                
                // Get counter2_table_avg from BASE_CLIENT_OBJECT (calculated in Calculator.get_stocks_counters)
                int top60AvgValue = (int) Math.round(TA35.getInstance().getCounter2_table_avg());
                top60_avg_counter_2_field.colorForge(top60AvgValue);
                top60_avg_counter_2_field.setText(top60_avg_counter_2_field.getText() + "%");


                double[] midVals = Calculator.get_midle_stocks_ba_counter();
                min_weight_field.setText((int)(vals[Calculator.COUNTER_2_WEIGHT_POSITIVE] - midVals[Calculator.SOFT_PLUS]), DF_INT);
                max_weight_field.setText((int)(vals[Calculator.COUNTER_2_WEIGHT_POSITIVE] + midVals[Calculator.SOFT_MINUS]), DF_INT);
                // Update colors 
                updateFieldColor(min_weight_field, 55, 45);
                updateFieldColor(max_weight_field, 55, 45);
                updateFieldColor(counter_2_weight_field, 55, 45);
                // top_weight_counter_2_field is now handled above with percentage and custom color logic
                updateFieldColor(top60_avg_counter_2_field, 0, -1);
            }
        });
    }


    private void startRunner() {
        runner = new Thread(new Runnable() {
            @Override
            public void run() {
                run = true;
                while (run) {
                    try {
                        Thread.sleep(5000);
                        // משיכת רשימת המניות העדכנית (אם האובייקטים מתעדכנים במקום – אפשר להשאיר את אותה רשימה)
                        stocksRef = new ArrayList<MiniStock>(TA35.getInstance().getStocksHandler().getStocks());
                        refreshNow();
                    } catch (InterruptedException e) {
                        Arik.getInstance().sendErrorMessage(e);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, "MiniStockTable-Refresher");
        runner.setDaemon(true);
        runner.start();
    }

    /* ======================== Table Model ======================== */

    private static class Model extends AbstractTableModel {
        static final int COL_NAME      = 0;
        static final int COL_OPEN      = 1;
        static final int COL_LAST      = 2;
        static final int COL_CHANGE    = 3;
        static final int COL_COUNTER   = 4;
        static final int COL_COUNTER_2 = 5;
        static final int COL_DELTA     = 6;
        static final int COL_WEIGHT    = 7;

        private final String[] cols = {"Name", "Open", "Last", "Change", "Counter", "Counter_2", "Delta", "Weight"};

        /** Row holder */
        private static class Row {
            final String name;
            final double openPct;   // NaN אם base=0
            final double lastPct;
            final double changePct; // last-open
            final int    counter;
            final int    ba2;
            final int    delta;
            final double weight;

            Row(String name, double openPct, double lastPct, double changePct, int cnt, int ba2, int delta,  double weight) {
                this.name = name;
                this.openPct = openPct;
                this.lastPct = lastPct;
                this.changePct = changePct;
                this.counter = cnt;
                this.ba2 = ba2;
                this.delta = delta;
                this.weight = weight;
            }
        }

        private final List<Row> rows = new ArrayList<Row>();

        /** Prev snapshot by stock name -> values per column (numeric only) */
        private final Map<String, double[]> prevByName = new ConcurrentHashMap<String, double[]>();
        /** Curr snapshot by stock name -> values per column (numeric only) */
        private final Map<String, double[]> currByName = new ConcurrentHashMap<String, double[]>();

        void refreshFrom(List<MiniStock> stocks) {
            if (stocks == null) return;

            // prev <- curr
            prevByName.clear();
            prevByName.putAll(currByName);
            currByName.clear();

            // build rows
            List<MiniStock> sorted = new ArrayList<MiniStock>(stocks);
            Collections.sort(sorted, new Comparator<MiniStock>() {
                @Override
                public int compare(MiniStock a, MiniStock b) {
                    return Double.compare(b.getWeight(), a.getWeight()); // ירידה
                }
            });

            List<Row> newRows = new ArrayList<Row>(sorted.size());
            for (MiniStock s : sorted) {
                String name = s.getName();
                double openPct = Double.NaN, lastPct = Double.NaN, diffPct = Double.NaN;
                if (s.getBase() != 0) {
                    openPct = ((s.getOpen()      - s.getBase()) / s.getBase()) * 100.0;
                    lastPct = ((s.getLast() - s.getBase()) / s.getBase()) * 100.0;
                    diffPct = lastPct - openPct;
                }
                int counter = s.getBid_ask_counter();
                int counter_2 = s.getCounter_2(); // משתמש ב-getCombinedCounter2()
                double weight = s.getWeight();
                int delta = (int) s.getDelta_counter();

                // fill curr map for change detection (name-based)
                double[] currVals = new double[8];
                currVals[COL_NAME]      = Double.NaN;    // לא מספרי
                currVals[COL_OPEN]      = openPct;
                currVals[COL_LAST]      = lastPct;
                currVals[COL_CHANGE]    = diffPct;
                currVals[COL_COUNTER]   = counter;
                currVals[COL_COUNTER_2] = counter_2;
                currVals[COL_DELTA]     = delta;
                currVals[COL_WEIGHT]    = weight;
                currByName.put(name, currVals);

                newRows.add(new Row(name, openPct, lastPct, diffPct, counter, counter_2, delta, weight));
            }

            rows.clear();
            rows.addAll(newRows);
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == COL_NAME) return String.class;
            return Double.class; // כדי שמיון יהיה מספרי
        }

        @Override public boolean isCellEditable(int r, int c) { return false; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Row r = rows.get(rowIndex);
            switch (columnIndex) {
                case COL_NAME:      return r.name;
                case COL_OPEN:      return Double.isNaN(r.openPct)   ? null : r.openPct;
                case COL_LAST:      return Double.isNaN(r.lastPct)   ? null : r.lastPct;
                case COL_CHANGE:    return Double.isNaN(r.changePct) ? null : r.changePct;
                case COL_COUNTER:   return (double) r.counter; // נשמר Double למיון
                case COL_COUNTER_2: return (double) r.ba2; // נשמר Double למיון
                case COL_DELTA:     return r.delta;
                case COL_WEIGHT:    return r.weight;
                default:            return null;
            }
        }

        /** -1 ירד, 0 ללא שינוי/לא ידוע, +1 עלה — לפי שם המניה */
        int getChangeDirection(int viewRow, int col, JTable table) {
            if (viewRow < 0 || viewRow >= rows.size()) return 0;
            int modelRow = table.convertRowIndexToModel(viewRow);
            String name = rows.get(modelRow).name;

            double[] prev = prevByName.get(name);
            double[] curr = currByName.get(name);
            if (prev == null || curr == null) return 0;

            double p = prev[col], c = curr[col];
            if (Double.isNaN(p) || Double.isNaN(c)) return 0;
            if (c > p) return 1;
            if (c < p) return -1;
            return 0;
        }

        Double getDiffValue(int viewRow, int col, JTable table) {
            if (viewRow < 0 || viewRow >= rows.size()) return null;
            int modelRow = table.convertRowIndexToModel(viewRow);
            String name = rows.get(modelRow).name;

            double[] prev = prevByName.get(name);
            double[] curr = currByName.get(name);
            if (prev == null || curr == null) return null;

            double p = prev[col], c = curr[col];
            if (Double.isNaN(p) || Double.isNaN(c)) return null;
            return Double.valueOf(c - p);
        }
    }

    /* ======================== Renderers ======================== */

    private static class NameRenderer extends DefaultTableCellRenderer {
        NameRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Ariel", Font.BOLD, 18));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            c.setFont(NAME_FONT);

            // Zebra / selection
            if (isSelected) {
                c.setBackground(getSelectionBackground()); 
                c.setForeground(getSelectionForeground());
            } else {
                c.setBackground(getCellBackground(row % 2 == 0)); 
                c.setForeground(getNameTextColor());
            }
            return c;
        }
    }

    /** Renderer כללי: פורמט, חצים, הדגשת שינוי, זברה */
    private static class CellRenderer extends DefaultTableCellRenderer {
        private final Model model;
        CellRenderer(Model model) {
            this.model = model;
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Ariel", Font.PLAIN, 22));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            c.setFont(CELL_FONT);

            // ----- Text format -----
            String text;
            if (value == null) {
                text = "-";
            } else if (value instanceof Number) {
                double v = ((Number) value).doubleValue();
                if (column == Model.COL_OPEN || column == Model.COL_LAST || column == Model.COL_CHANGE) {
                    String arrow = v > 0 ? " ↑" : (v < 0 ? " ↓" : "");
                    text = DF_PCT.format(v) + "%" + arrow;
                } else if (column == Model.COL_WEIGHT) {
                    text = DF_WGT.format(v);
                } else if (column == Model.COL_COUNTER || column == Model.COL_COUNTER_2 || column == Model.COL_DELTA) {
                    text = String.valueOf((int) v);
                } else {
                    text = DF_WGT.format(v);
                }
            } else {
                text = value.toString();
            }
            setText(text);

            // ----- Tooltip: שינוי מול הקודם -----
            Double diff = model.getDiffValue(row, column, table);
            if (diff != null) {
                String tip;
                if (column == Model.COL_OPEN || column == Model.COL_LAST || column == Model.COL_CHANGE) {
                    tip = "Δ שינוי: " + (diff.doubleValue() >= 0 ? "+" : "") + DF_PCT.format(diff.doubleValue()) + "%";
                } else if (column == Model.COL_COUNTER || column == Model.COL_COUNTER_2) {
                    tip = "שינוי: " + (diff.doubleValue() >= 0 ? "+" : "") + ((int) Math.round(diff.doubleValue()));
                } else {
                    tip = "שינוי: " + (diff.doubleValue() >= 0 ? "+" : "") + DF_WGT.format(diff.doubleValue());
                }
                setToolTipText(tip);
            } else {
                setToolTipText(null);
            }

            // ----- Foreground by sign -----
            if (!isSelected) {
                if (value instanceof Number) {
                    double v = ((Number) value).doubleValue();
                    if (column == Model.COL_OPEN || column == Model.COL_LAST || column == Model.COL_CHANGE || column == Model.COL_COUNTER || column == Model.COL_COUNTER_2 || column == Model.COL_DELTA) {
                        if (Themes.isDarkMode()) {
                            setForeground(v > 0 ? Themes.DARK_TEXT_GREEN : (v < 0 ? Themes.DARK_TEXT_RED : DARK_TEXT));
                        } else {
                            setForeground(v > 0 ? new Color(0x1B5E20) : (v < 0 ? new Color(0xB71C1C) : Color.BLACK));
                        }
                    } else {
                        setForeground(getTextColor());
                    }
                } else {
                    setForeground(getTextColor());
                }
            }

            // ----- Background priority: selection > change highlight > zebra -----
            if (isSelected) {
                c.setBackground(getSelectionBackground()); 
                c.setForeground(getSelectionForeground());
            } else {
                boolean paintable =
                        column == Model.COL_OPEN || column == Model.COL_LAST ||
                                column == Model.COL_CHANGE || column == Model.COL_COUNTER || column == Model.COL_COUNTER_2 || column == Model.COL_DELTA;

                int dir = paintable ? model.getChangeDirection(row, column, table) : 0;
                Color changeBg = getChangeBackground(dir);
                if (changeBg != null) {
                    c.setBackground(changeBg);
                } else {
                    c.setBackground(getCellBackground(row % 2 == 0));
                }
            }

            return c;
        }
    }

    /* ======================== All Stocks Details Window ======================== */
    
    /**
     * חלון שמציג את כל המניות עם פרטים מלאים
     */
    private class AllStocksDetailsWindow extends JFrame {
        private JTable detailsTable;
        private DetailsTableModel detailsModel;
        private Thread refreshThread;
        private volatile boolean running = true;
        
        public AllStocksDetailsWindow() {
            super("All Stocks - Detailed View");
            
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(1400, 800);
            setLocationRelativeTo(null);
            
            buildUI();
            startRefreshThread();
        }
        
        private void buildUI() {
            setLayout(new BorderLayout());
            
            // Create table model and table
            detailsModel = new DetailsTableModel();
            detailsTable = new JTable(detailsModel);
            detailsTable.setRowHeight(28);
            detailsTable.setAutoCreateRowSorter(true);
            detailsTable.setFont(CELL_FONT);
            detailsTable.setShowHorizontalLines(true);
            detailsTable.setShowVerticalLines(true);
            detailsTable.setGridColor(getGridColor());
            detailsTable.setSelectionBackground(getSelectionBackground());
            detailsTable.setSelectionForeground(getSelectionForeground());
            
            // Header styling
            JTableHeader header = detailsTable.getTableHeader();
            header.setFont(HEADER_FONT);
            header.setBackground(getHeaderBackground());
            header.setForeground(getHeaderForeground());
            
            // Set column widths
            int[] widths = {120, 90, 80, 80, 80, 80, 80, 120, 120, 100, 100, 90, 80};
            TableColumnModel cm = detailsTable.getColumnModel();
            for (int i = 0; i < Math.min(widths.length, cm.getColumnCount()); i++) {
                cm.getColumn(i).setPreferredWidth(widths[i]);
            }
            
            // Custom renderer for number formatting and coloring
            DetailsRenderer renderer = new DetailsRenderer();
            for (int i = 0; i < detailsModel.getColumnCount(); i++) {
                if (i != 0) { // Skip name column
                    detailsTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
                }
            }
            
            JScrollPane scrollPane = new JScrollPane(detailsTable);
            add(scrollPane, BorderLayout.CENTER);
            
            // Initial load
            refreshData();
        }
        
        private void startRefreshThread() {
            refreshThread = new Thread(() -> {
                while (running) {
                    try {
                        Thread.sleep(5000); // Refresh every 5 seconds
                        SwingUtilities.invokeLater(() -> refreshData());
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }, "AllStocksDetails-Refresher");
            refreshThread.setDaemon(true);
            refreshThread.start();
        }
        
        private void refreshData() {
            List<MiniStock> stocks = new ArrayList<>(TA35.getInstance().getStocksHandler().getStocks());
            detailsModel.updateData(stocks);
        }
        
        @Override
        public void dispose() {
            running = false;
            if (refreshThread != null) {
                refreshThread.interrupt();
            }
            super.dispose();
        }
    }
    
    /**
     * Table model for detailed stocks view
     */
    private static class DetailsTableModel extends AbstractTableModel {
        private final String[] columns = {
            "Name", "Last Price", "Bid", "Ask", "Counter", "Counter_2", "Delta",
            "Avg Bid Change", "Avg Ask Change", "Bid Changes", "Ask Changes", "Volume", "Weight"
        };
        
        private List<Object[]> data = new ArrayList<>();
        
        public void updateData(List<MiniStock> stocks) {
            data.clear();
            
            for (MiniStock s : stocks) {
                Object[] row = new Object[13];
                row[0] = s.getName();
                row[1] = s.getLast();
                row[2] = s.getBid();
                row[3] = s.getAsk();
                row[4] = s.getBid_ask_counter();
                row[5] = s.getCounter_2();
                row[6] = s.getDelta_counter();
                row[7] = s.getAverageBidChange();
                row[8] = s.getAverageAskChange();
                row[9] = s.getBidChangeCount();
                row[10] = s.getAskChangeCount();
                row[11] = s.getVolume();
                row[12] = s.getWeight();
                
                data.add(row);
            }
            
            fireTableDataChanged();
        }
        
        @Override
        public int getRowCount() {
            return data.size();
        }
        
        @Override
        public int getColumnCount() {
            return columns.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columns[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.get(rowIndex)[columnIndex];
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return String.class;
            if (columnIndex == 4 || columnIndex == 5 || columnIndex == 6 || 
                columnIndex == 9 || columnIndex == 10) {
                return Integer.class;
            }
            return Double.class;
        }
    }
    
    /**
     * Renderer for details table
     */
    private static class DetailsRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat df2 = new DecimalFormat("0.00");
        private final DecimalFormat df4 = new DecimalFormat("0.0000");
        
        public DetailsRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            c.setFont(CELL_FONT);
            
            // Format text
            if (value instanceof Double) {
                double v = (Double) value;
                if (column == 7 || column == 8) { // Avg changes
                    setText(df4.format(v));
                } else {
                    setText(df2.format(v));
                }
            } else if (value instanceof Integer) {
                setText(String.valueOf(value));
            } else if (value != null) {
                setText(value.toString());
            } else {
                setText("-");
            }
            
            // Coloring
            if (!isSelected) {
                // Color based on value
                if (value instanceof Number) {
                    double v = ((Number) value).doubleValue();
                    if (column == 4 || column == 5 || column == 6) { // Counter, Counter_2, Delta
                        if (Themes.isDarkMode()) {
                            setForeground(v > 0 ? Themes.DARK_TEXT_GREEN : (v < 0 ? Themes.DARK_TEXT_RED : DARK_TEXT));
                        } else {
                            setForeground(v > 0 ? new Color(0x1B5E20) : (v < 0 ? new Color(0xB71C1C) : Color.BLACK));
                        }
                    } else {
                        setForeground(getTextColor());
                    }
                } else {
                    setForeground(getTextColor());
                }
                
                // Zebra striping
                c.setBackground(getCellBackground(row % 2 == 0));
            } else {
                c.setBackground(getSelectionBackground());
                c.setForeground(getSelectionForeground());
            }
            
            return c;
        }
    }
}


//  for (int i = 10; i < 100; i+=10) {
//        Option call = new Option(Option.Side.CALL.toString(), i, options);
//        Option put = new Option(Option.Side.PUT.toString(), i, options);
//        Strike strike = new Strike(i);
//        strike.setCall(call);
//        strike.setPut(put);
//        options.addStrike(strike);
//        System.out.println(strike.getStrike());
//        }