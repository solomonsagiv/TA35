package options;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import gui.MyGuiComps;
import locals.Themes;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Options Table Window (styled + background refresh + cross-platform fonts)
 * Columns: B/A | Delta | Delta q | Mid | IV | Strike | Fair IV | IV chg | IV | Mid | Delta q | Delta | B/A
 */
public class OptionsTableWindow extends MyGuiComps.MyFrame {

    /* ===================== Demo ===================== */
    public static void main(String[] args) {
        // Demo data (remove in production)
        Options options = new Options(TA35.getInstance());
        for (int k = 1000; k <= 1100; k += 20) {
            Option c = new Option("C", k, options);
            Option p = new Option("P", k, options);
            Strike s = new Strike(k);
            s.setCall(c);
            s.setPut(p);
            options.addStrike(s);
        }
        SwingUtilities.invokeLater(() -> {
            OptionsTableWindow w = new OptionsTableWindow(TA35.getInstance(), "Options Monitor", options);
            w.setSize(980, 660);
            w.setLocationRelativeTo(null);
            w.setVisible(true);
        });
    }

    /* ===================== Font picker (cross-platform) ===================== */

    private static Font deriveUI(int style, float size) {
        Font base = UIManager.getFont("Label.font");
        if (base == null) base = new JLabel().getFont();
        return base.deriveFont(style, size);
    }

    // Slightly larger fonts
    private static final Font FONT_TEXT = deriveUI(Font.PLAIN, 16);
    private static final Font FONT_TEXT_B = deriveUI(Font.BOLD, 16);
    private static final Font FONT_NUM = deriveUI(Font.PLAIN, 16);
    private static final Font FONT_NUM_B = deriveUI(Font.BOLD, 16);   // bold for Strike column
    private static final Font FONT_KPI = deriveUI(Font.PLAIN, 18);

    /* ===================== Colors / Styles ===================== */
    // Use Themes helper methods instead of hardcoded colors
    private static Color getBG_WHITE() { return Themes.getBackgroundColor(); }
    private static Color getBG_STRIPE() { return Themes.getStripeBackgroundColor(); }
    private static Color getGRID_COLOR() { return Themes.getGridColor(); }
    private static Color getHEADER_BG() { return Themes.getHeaderBackgroundColor(); }
    private static Color getHEADER_FG() { return Themes.getHeaderTextColor(); }
    private static Color getSELECTION_BG() { return Themes.getSelectionBackgroundColor(); }
    private static Color getSELECTION_FG() { return Themes.getSelectionTextColor(); }
    private static Color getHL_GREEN() { return Themes.getGreenHighlightColor(); }
    private static Color getHL_RED() { return Themes.getRedHighlightColor(); }
    private static Color getVAL_GREEN_FG() { return Themes.getGreenTextColor(); }
    private static Color getVAL_RED_FG() { return Themes.getRedTextColor(); }
    private static Color getSTRIKE_BG() { return Themes.getStrikeBackgroundColor(); }

    /* ===================== UI / Data ===================== */
    private Options optionsRef;
    private OptionsTable table;
    private Thread runner;
    private volatile boolean run = true;

    private MyGuiComps.MyTextField kpi1, kpi2, kpi3, kpi4; // placeholders

    public OptionsTableWindow(BASE_CLIENT_OBJECT client, String title, Options options) throws HeadlessException {
        super(client, title);
        this.optionsRef = options;
        // initialize() is already called by super(), but table might not be created yet
        // Ensure table is set up with options
        if (table != null) {
            table.setOptions(optionsRef);
            table.refresh();
        } else {
            // If table wasn't created in initialize(), create it now
            setupTable();
        }
        startRunner();
    }
    
    private void setupTable() {
        // Remove existing table if any
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                getContentPane().remove(comp);
            }
        }
        
        // Create and add table
        table = new OptionsTable(client);
        table.setOptions(optionsRef);
        add(new JScrollPane(table), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void initListeners() {
    }

    @Override
    public void initialize() {
        // Remove any existing components to avoid duplicates
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // KPIs
        kpi1 = new MyGuiComps.MyTextField();
        kpi1.setFont(FONT_KPI);
        kpi2 = new MyGuiComps.MyTextField();
        kpi2.setFont(FONT_KPI);
        kpi3 = new MyGuiComps.MyTextField();
        kpi3.setFont(FONT_KPI);
        kpi4 = new MyGuiComps.MyTextField();
        kpi4.setFont(FONT_KPI);

        JPanel controlPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        controlPanel.add(createColumn("Positive counter :", kpi1));
        controlPanel.add(createColumn("Total weight:", kpi2));
        controlPanel.add(createColumn("Weighted counter:", kpi3));
        controlPanel.add(createColumn("Green stocks:", kpi4));
        add(controlPanel, BorderLayout.NORTH);

        // Table - create only if optionsRef is already set (might be null here)
        // If optionsRef is null, table will be created in constructor
        if (optionsRef != null) {
            table = new OptionsTable(client);
            table.setOptions(optionsRef);
            add(new JScrollPane(table), BorderLayout.CENTER);
        }
        
        // Force layout update
        revalidate();
        repaint();
    }

    private static JPanel createColumn(String labelText, JTextField textField) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel l = new JLabel(labelText, SwingConstants.CENTER);
        l.setFont(FONT_TEXT);
        p.add(l, BorderLayout.NORTH);
        p.add(textField, BorderLayout.CENTER);
        return p;
    }

    private void startRunner() {
        runner = new Thread(() -> {
            run = true;
            while (run) {
                try {
                    Thread.sleep(2000);
                    SwingUtilities.invokeLater(() -> table.refresh());
                } catch (InterruptedException ie) {
                    break;
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }, "OptionsTableWindow-Refresher");
        runner.setDaemon(true);
        runner.start();
    }

    @Override
    public void onClose() {
        run = false;
        if (runner != null) runner.interrupt();
        super.onClose();
    }

    /* ======================= TABLE ======================= */

    static class OptionsTable extends JTable {
        private final OptionsTableModel model;

        OptionsTable(BASE_CLIENT_OBJECT client) {
            this.model = new OptionsTableModel(client);
            setModel(model);

            setFillsViewportHeight(true);
            setRowHeight(34);
            setAutoCreateRowSorter(true);

            setShowHorizontalLines(true);
            setShowVerticalLines(false);
            setGridColor(getGRID_COLOR());
            setIntercellSpacing(new Dimension(0, 1));

            setSelectionBackground(getSELECTION_BG());
            setSelectionForeground(getSELECTION_FG());

            JTableHeaderStyled.apply(getTableHeader());

            ChangeHighlightRenderer renderer = new ChangeHighlightRenderer(model);
            for (int c = 0; c < model.getColumnCount(); c++) {
                getColumnModel().getColumn(c).setCellRenderer(renderer);
            }

            setNiceColumnWidths();
        }

        public void setOptions(Options options) {
            model.setOptions(options);
            model.initialLoad();
        }

        public void refresh() {
            model.refresh();
        }

        private void setNiceColumnWidths() {
            TableColumnModel cm = getColumnModel();
            int[] widths = {70, 80, 80, 70, 75, 100, 75, 70, 75, 70, 80, 80, 70}; // B/A | Delta | Delta q | Mid | IV | Strike | Fair IV | IV chg | IV | Mid | Delta q | Delta | B/A
            for (int i = 0; i < widths.length && i < cm.getColumnCount(); i++) {
                cm.getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    /* ======================= MODEL ======================= */

    static class OptionsTableModel extends AbstractTableModel {
        static final int COL_BIDASK_CALL = 0;
        static final int COL_DELTA_COUNTER_CALL = 1;
        static final int COL_DELTA_QUAN_COUNTER_CALL = 2;
        static final int COL_MID_CALL = 3;
        static final int COL_IV_CALL = 4;
        static final int COL_STRIKE = 5;
        static final int COL_FAIR_IV = 6;
        static final int COL_IV_CHG = 7;
        static final int COL_IV_PUT = 8;
        static final int COL_MID_PUT = 9;
        static final int COL_DELTA_QUAN_COUNTER_PUT = 10;
        static final int COL_DELTA_COUNTER_PUT = 11;
        static final int COL_BIDASK_PUT = 12;

        private final String[] cols = {"B/A", "Delta", "Delta q", "Mid", "IV", "Strike", "Fair IV", "IV chg", "IV", "Mid", "Delta q", "Delta", "B/A"};
        
        private static final double OPTION_MULTIPLIER = 50.0;
        private final BASE_CLIENT_OBJECT client;

        private static class Row {
            final double strike;
            final Option call;
            final Option put;
            final Strike strikeObj;

            Row(Strike strikeObj) {
                this.strikeObj = strikeObj;
                this.strike = strikeObj != null ? strikeObj.getStrike() : 0;
                this.call = strikeObj != null ? strikeObj.getCall() : null;
                this.put = strikeObj != null ? strikeObj.getPut() : null;
            }
        }

        private Options options;
        private final List<Row> rows = new ArrayList<>();
        private double[][] prevValues = new double[0][0];
        private double[][] currValues = new double[0][0];

        OptionsTableModel(BASE_CLIENT_OBJECT client) {
            this.client = client;
        }

        void setOptions(Options options) {
            this.options = options;
        }

        void initialLoad() {
            if (options == null) {
                rows.clear();
                prevValues = new double[0][0];
                currValues = new double[0][0];
                fireTableDataChanged();
                return;
            }
            List<Strike> strikes = new ArrayList<>(options.getStrikes());
            strikes.sort(Comparator.comparingDouble(Strike::getStrike));
            rows.clear();
            for (Strike s : strikes) rows.add(new Row(s));
            currValues = buildMatrixFromRows(rows);
            prevValues = copy2D(currValues);
            fireTableDataChanged();
        }

        void refresh() {
            if (options == null) return;
            List<Strike> strikes = new ArrayList<>(options.getStrikes());
            strikes.sort(Comparator.comparingDouble(Strike::getStrike));

            List<Row> newRows = new ArrayList<>();
            for (Strike s : strikes) newRows.add(new Row(s));

            double[][] newCurr = buildMatrixFromRows(newRows);
            double[][] newPrev = remapPrevByStrike(rows, prevValues, newRows, getColumnCount());

            rows.clear();
            rows.addAll(newRows);
            prevValues = newPrev;
            currValues = newCurr;
            fireTableDataChanged();
        }

        private double[][] buildMatrixFromRows(List<Row> rs) {
            int rc = rs.size(), cc = getColumnCount();
            double[][] m = new double[rc][cc];
            
            for (int r = 0; r < rc; r++) {
                Row row = rs.get(r);
                
                // Call columns: B/A | Delta | Delta q | Mid | IV
                if (row.call != null) {
                    m[r][COL_BIDASK_CALL] = row.call.getBidAskCounter();
                    m[r][COL_DELTA_COUNTER_CALL] = row.call.getDeltaCounter();
                    m[r][COL_DELTA_QUAN_COUNTER_CALL] = row.call.getDeltaQuanCounter();
                    
                    // Update Mid from option
                    int mid = row.call.getMid();
                    if (mid > 0) {
                        m[r][COL_MID_CALL] = mid;
                    } else {
                        // Fallback: calculate from bid/ask
                        int bid = row.call.getBid();
                        int ask = row.call.getAsk();
                        if (bid > 0 && ask > 0) {
                            m[r][COL_MID_CALL] = (bid + ask) / 2.0;
                        } else {
                            m[r][COL_MID_CALL] = Double.NaN;
                        }
                    }
                    
                    // Update IV from option
                    double ivFromOption = row.call.getIv();
                    if (ivFromOption > 0) {
                        m[r][COL_IV_CALL] = ivFromOption * 100; // Convert to percentage
                    } else {
                        m[r][COL_IV_CALL] = Double.NaN;
                    }
                } else {
                    m[r][COL_BIDASK_CALL] = Double.NaN;
                    m[r][COL_DELTA_COUNTER_CALL] = Double.NaN;
                    m[r][COL_DELTA_QUAN_COUNTER_CALL] = Double.NaN;
                    m[r][COL_MID_CALL] = Double.NaN;
                    m[r][COL_IV_CALL] = Double.NaN;
                }
                
                // Strike
                m[r][COL_STRIKE] = row.strike;
                
                // Fair IV - get from strike object
                double fairIv = Double.NaN;
                if (row.strikeObj != null) {
                    fairIv = row.strikeObj.getFairIv();
                    // Display Fair IV if it's a valid positive number (greater than 0.0001 to avoid rounding issues)
                    if (!Double.isNaN(fairIv) && fairIv > 0.0001) {
                        m[r][COL_FAIR_IV] = fairIv * 100; // Convert to percentage
                    } else {
                        // If fairIv is 0 or NaN, it means it hasn't been calculated yet
                        m[r][COL_FAIR_IV] = Double.NaN;
                        fairIv = Double.NaN;
                    }
                } else {
                    m[r][COL_FAIR_IV] = Double.NaN;
                }
                
                // IV chg - difference between IV and Fair IV (mispricing)
                // Formula: (IV / Fair IV - 1) * 100
                double strikeIv = row.strikeObj != null ? row.strikeObj.getIv() : Double.NaN;
                if (!Double.isNaN(strikeIv) && strikeIv > 0.0001 && !Double.isNaN(fairIv) && fairIv > 0.0001) {
                    m[r][COL_IV_CHG] = (strikeIv / fairIv - 1.0) * 100.0; // Percentage difference
                } else {
                    m[r][COL_IV_CHG] = Double.NaN;
                }
                
                // Put columns: IV | Mid | Delta q | Delta | B/A
                if (row.put != null) {
                    // Update Mid from option
                    int mid = row.put.getMid();
                    if (mid > 0) {
                        m[r][COL_MID_PUT] = mid;
                    } else {
                        // Fallback: calculate from bid/ask
                        int bid = row.put.getBid();
                        int ask = row.put.getAsk();
                        if (bid > 0 && ask > 0) {
                            m[r][COL_MID_PUT] = (bid + ask) / 2.0;
                        } else {
                            m[r][COL_MID_PUT] = Double.NaN;
                        }
                    }
                    
                    // Update IV from option
                    double ivFromOption = row.put.getIv();
                    if (ivFromOption > 0) {
                        m[r][COL_IV_PUT] = ivFromOption * 100; // Convert to percentage
                    } else {
                        m[r][COL_IV_PUT] = Double.NaN;
                    }
                    
                    m[r][COL_DELTA_QUAN_COUNTER_PUT] = row.put.getDeltaQuanCounter();
                    m[r][COL_DELTA_COUNTER_PUT] = row.put.getDeltaCounter();
                    m[r][COL_BIDASK_PUT] = row.put.getBidAskCounter();
                } else {
                    m[r][COL_IV_PUT] = Double.NaN;
                    m[r][COL_MID_PUT] = Double.NaN;
                    m[r][COL_DELTA_QUAN_COUNTER_PUT] = Double.NaN;
                    m[r][COL_DELTA_COUNTER_PUT] = Double.NaN;
                    m[r][COL_BIDASK_PUT] = Double.NaN;
                }
            }
            return m;
        }

        private static double[][] copy2D(double[][] src) {
            double[][] dst = new double[src.length][];
            for (int i = 0; i < src.length; i++) dst[i] = src[i].clone();
            return dst;
        }

        private static int findRowIndexByStrike(List<Row> rows, double k) {
            for (int i = 0; i < rows.size(); i++) if (Double.compare(rows.get(i).strike, k) == 0) return i;
            return -1;
        }

        private static double[][] remapPrevByStrike(List<Row> oldRows, double[][] oldPrev, List<Row> newRows, int cols) {
            double[][] remapped = new double[newRows.size()][cols];
            for (int r = 0; r < newRows.size(); r++) {
                double k = newRows.get(r).strike;
                int oldIndex = findRowIndexByStrike(oldRows, k);
                if (oldIndex >= 0) remapped[r] = oldPrev[oldIndex].clone();
                else {
                    remapped[r] = new double[cols];
                    for (int c = 0; c < cols; c++) remapped[r][c] = Double.NaN;
                }
            }
            return remapped;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int column) {
            return cols[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Double.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= rows.size()) return null;
            double v = currValues[rowIndex][columnIndex];
            return Double.isNaN(v) ? null : v;
        }

        int getChangeDirection(int row, int col) {
            if (row < 0 || row >= rows.size()) return 0;
            double prev = prevValues[row][col], curr = currValues[row][col];
            if (Double.isNaN(prev) || Double.isNaN(curr)) return 0;
            return (curr > prev) ? 1 : (curr < prev) ? -1 : 0;
        }

        Double getDeltaVsPrev(int row, int col) {
            if (row < 0 || row >= rows.size()) return null;
            double prev = prevValues[row][col], curr = currValues[row][col];
            if (Double.isNaN(prev) || Double.isNaN(curr)) return null;
            return curr - prev;
        }
    }

    /* ======================= HEADER RENDERER ======================= */

    private static class JTableHeaderStyled {
        static void apply(JTableHeader header) {
            header.setReorderingAllowed(true);
            header.setResizingAllowed(true);
            header.setOpaque(true);
            final TableCellRenderer base = header.getDefaultRenderer();
            header.setDefaultRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
                Component comp = base.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (comp instanceof JComponent) ((JComponent) comp).setOpaque(true);
                comp.setBackground(getHEADER_BG());
                comp.setForeground(getHEADER_FG());
                comp.setFont(FONT_TEXT_B);
                if (comp instanceof JLabel) ((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
                return comp;
            });
        }
    }

    /* ======================= CELL RENDERER ======================= */

    private static class ChangeHighlightRenderer extends DefaultTableCellRenderer {
        private final OptionsTableModel model;

        // Formats: integers (no decimals) for Delta, Strike, B/A
        private final DecimalFormat fmtIntNoDec = new DecimalFormat("0");
        private final DecimalFormat fmtStrike0 = new DecimalFormat("#,##0");
        private final DecimalFormat fmtInt = new DecimalFormat("0"); // Delta q shown as int (change if needed)
        private final DecimalFormat fmtMid = new DecimalFormat("0.0"); // Mid price with 1 decimal
        private final DecimalFormat fmtIV = new DecimalFormat("0.00"); // IV as percentage with 2 decimals

        private static boolean isDeltaCounterCol(int col) {
            return col == OptionsTableModel.COL_DELTA_COUNTER_CALL ||
                    col == OptionsTableModel.COL_DELTA_COUNTER_PUT;
        }

        private static boolean isBidAskCol(int col) {
            return col == OptionsTableModel.COL_BIDASK_CALL ||
                    col == OptionsTableModel.COL_BIDASK_PUT;
        }

        private static boolean isMidCol(int col) {
            return col == OptionsTableModel.COL_MID_CALL ||
                    col == OptionsTableModel.COL_MID_PUT;
        }

        private static boolean isIVCol(int col) {
            return col == OptionsTableModel.COL_IV_CALL ||
                    col == OptionsTableModel.COL_IV_PUT;
        }

        private static boolean isFairIVCol(int col) {
            return col == OptionsTableModel.COL_FAIR_IV;
        }

        private static boolean isIVChgCol(int col) {
            return col == OptionsTableModel.COL_IV_CHG;
        }

        ChangeHighlightRenderer(OptionsTableModel model) {
            this.model = model;
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Fonts on paint: bold for Strike, else regular mono
            c.setFont(column == OptionsTableModel.COL_STRIKE ? FONT_NUM_B : FONT_NUM);

            // ----- Text formatting (no decimals for Delta, Strike, B/A) -----
            if (value instanceof Number) {
                double d = ((Number) value).doubleValue();
                String txt;
                if (isDeltaCounterCol(column)) {
                    txt = fmtIntNoDec.format(d);           // Delta: no decimals
                } else if (column == OptionsTableModel.COL_STRIKE) {
                    txt = fmtStrike0.format(d);            // Strike: no decimals
                } else if (isBidAskCol(column)) {
                    txt = fmtIntNoDec.format(d);           // B/A: no decimals
                } else if (isMidCol(column)) {
                    txt = fmtMid.format(d);                // Mid: 1 decimal
                } else if (isIVCol(column) || isFairIVCol(column)) {
                    txt = fmtIV.format(d);                 // IV / Fair IV: percentage with 2 decimals
                } else if (isIVChgCol(column)) {
                    txt = fmtIV.format(d) + "%";           // IV chg: percentage with 2 decimals + % sign
                } else {
                    txt = fmtInt.format(d);                // Delta q: integer (change to "0.00" if desired)
                }
                setText(txt);
            } else {
                setText(value == null ? "" : value.toString());
            }

            // ----- Tooltip (diff) -----
            Double diff = model.getDeltaVsPrev(row, column);
            if (diff != null) {
                String tip;
                if (isDeltaCounterCol(column)) {
                    tip = "Δ change: " + (diff >= 0 ? "+" : "") + fmtIntNoDec.format(diff);
                } else if (column == OptionsTableModel.COL_STRIKE) {
                    tip = "Strike";
                } else if (isBidAskCol(column)) {
                    tip = "Change: " + (diff >= 0 ? "+" : "") + fmtIntNoDec.format(diff);
                } else if (isMidCol(column)) {
                    tip = "Mid price change: " + (diff >= 0 ? "+" : "") + fmtMid.format(diff);
                } else if (isIVCol(column)) {
                    tip = "IV change: " + (diff >= 0 ? "+" : "") + fmtIV.format(diff) + "%";
                } else if (isFairIVCol(column)) {
                    tip = "Fair IV change: " + (diff >= 0 ? "+" : "") + fmtIV.format(diff) + "%";
                } else if (isIVChgCol(column)) {
                    tip = "IV chg change: " + (diff >= 0 ? "+" : "") + fmtIV.format(diff) + "%";
                } else {
                    tip = "Change: " + (diff >= 0 ? "+" : "") + fmtInt.format(diff);
                }
                setToolTipText(tip);
            } else {
                setToolTipText(null);
            }

            // ----- Foreground by sign (Delta & B/A) -----
            if (!isSelected && value instanceof Number) {
                double d = ((Number) value).doubleValue();
                if (isDeltaCounterCol(column) || isBidAskCol(column)) {
                    if (d > 0) c.setForeground(getVAL_GREEN_FG());
                    else if (d < 0) c.setForeground(getVAL_RED_FG());
                    else c.setForeground(Themes.getTextColor());
                } else if (isIVCol(column) || isFairIVCol(column)) {
                    // IV / Fair IV always normal text color
                    c.setForeground(Themes.getTextColor());
                } else if (isIVChgCol(column)) {
                    // IV chg: green if positive (IV > Fair IV), red if negative (IV < Fair IV)
                    if (d > 0) {
                        c.setForeground(getVAL_GREEN_FG()); // IV is higher than Fair IV (overpriced)
                    } else if (d < 0) {
                        c.setForeground(getVAL_RED_FG());   // IV is lower than Fair IV (underpriced)
                    } else {
                        c.setForeground(Themes.getTextColor());
                    }
                } else if (isMidCol(column)) {
                    // Mid price always normal text color
                    c.setForeground(Themes.getTextColor());
                } else {
                    c.setForeground(Themes.getTextColor());
                }
            }

            // ----- Background: selection > change highlight > column base > zebra -----
            if (isSelected) {
                c.setBackground(getSELECTION_BG());
                c.setForeground(getSELECTION_FG());
            } else {
                // Strike column has special background, no change highlighting
                if (column == OptionsTableModel.COL_STRIKE) {
                    c.setBackground(getSTRIKE_BG());
                } else {
                    // For all other columns, check change direction and color accordingly
                    int dir = model.getChangeDirection(row, column);
                    if (dir > 0) {
                        // Number increased - green
                        c.setBackground(getHL_GREEN());
                    } else if (dir < 0) {
                        // Number decreased - red
                        c.setBackground(getHL_RED());
                    } else {
                        // No change - zebra pattern
                        c.setBackground((row % 2 == 0) ? getBG_WHITE() : getBG_STRIPE());
                    }
                }
            }

            return c;
        }
    }
}
