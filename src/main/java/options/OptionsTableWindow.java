package options;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import gui.MyGuiComps;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Options Table Window (styled + background refresh + cross-platform fonts)
 * Columns: B/A | Delta | Delta q | Strike | Delta q | Delta | B/A
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
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color BG_STRIPE = new Color(0xFAFAFA);
    private static final Color GRID_COLOR = new Color(0xEEEEEE);
    private static final Color HEADER_BG = new Color(0xF5F7FA);
    private static final Color HEADER_FG = new Color(0x263238);
    private static final Color SELECTION_BG = new Color(0xE3F2FD);
    private static final Color SELECTION_FG = new Color(0x000000);

    // Stronger change highlights (a bit more saturated)
    private static final Color HL_GREEN = new Color(0xC8E6C9); // stronger green
    private static final Color HL_RED = new Color(0xFFCDD2); // stronger red

    // Value foreground colors for sign
    private static final Color VAL_GREEN_FG = new Color(0x1B5E20);
    private static final Color VAL_RED_FG = new Color(0xB71C1C);

    // Strike column base background (distinct, subtle)
    private static final Color STRIKE_BG = new Color(0xEEF3FF);

    /* ===================== UI / Data ===================== */
    private Options optionsRef;
    private OptionsTable table;
    private Thread runner;
    private volatile boolean run = true;

    private MyGuiComps.MyTextField kpi1, kpi2, kpi3, kpi4; // placeholders

    public OptionsTableWindow(BASE_CLIENT_OBJECT client, String title, Options options) throws HeadlessException {
        super(client, title);
        this.optionsRef = options;
        initialize();
        table.refresh();
        startRunner();
    }

    @Override
    public void initListeners() {
    }

    @Override
    public void initialize() {
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

        // Table
        table = new OptionsTable(optionsRef);
        add(new JScrollPane(table), BorderLayout.CENTER);
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

        OptionsTable(Options options) {
            this.model = new OptionsTableModel();
            setModel(model);

            setFillsViewportHeight(true);
            setRowHeight(34);
            setAutoCreateRowSorter(true);

            setShowHorizontalLines(true);
            setShowVerticalLines(false);
            setGridColor(GRID_COLOR);
            setIntercellSpacing(new Dimension(0, 1));

            setSelectionBackground(SELECTION_BG);
            setSelectionForeground(SELECTION_FG);

            JTableHeaderStyled.apply(getTableHeader());

            ChangeHighlightRenderer renderer = new ChangeHighlightRenderer(model);
            for (int c = 0; c < model.getColumnCount(); c++) {
                getColumnModel().getColumn(c).setCellRenderer(renderer);
            }

            setNiceColumnWidths();
            setOptions(options);
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
            int[] widths = {90, 90, 90, 120, 90, 90, 90}; // B/A | Delta | Delta q | Strike | Delta q | Delta | B/A
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
        static final int COL_STRIKE = 3;
        static final int COL_DELTA_QUAN_COUNTER_PUT = 4;
        static final int COL_DELTA_COUNTER_PUT = 5;
        static final int COL_BIDASK_PUT = 6;

        private final String[] cols = {"B/A", "Delta", "Delta q", "Strike", "Delta q", "Delta", "B/A"};

        private static class Row {
            final double strike;
            final Option call;
            final Option put;

            Row(double strike, Option call, Option put) {
                this.strike = strike;
                this.call = call;
                this.put = put;
            }
        }

        private Options options;
        private final List<Row> rows = new ArrayList<>();
        private double[][] prevValues = new double[0][0];
        private double[][] currValues = new double[0][0];

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
            for (Strike s : strikes) rows.add(new Row(s.getStrike(), s.getCall(), s.getPut()));
            currValues = buildMatrixFromRows(rows);
            prevValues = copy2D(currValues);
            fireTableDataChanged();
        }

        void refresh() {
            if (options == null) return;
            List<Strike> strikes = new ArrayList<>(options.getStrikes());
            strikes.sort(Comparator.comparingDouble(Strike::getStrike));

            List<Row> newRows = new ArrayList<>();
            for (Strike s : strikes) newRows.add(new Row(s.getStrike(), s.getCall(), s.getPut()));

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
                m[r][COL_DELTA_COUNTER_CALL] = (row.call != null) ? row.call.getDeltaCounter() : Double.NaN;
                m[r][COL_DELTA_QUAN_COUNTER_CALL] = (row.call != null) ? row.call.getDeltaQuanCounter() : Double.NaN;
                m[r][COL_BIDASK_CALL] = (row.call != null) ? row.call.getBidAskCounter() : Double.NaN;
                m[r][COL_STRIKE] = row.strike;
                m[r][COL_BIDASK_PUT] = (row.put != null) ? row.put.getBidAskCounter() : Double.NaN;
                m[r][COL_DELTA_COUNTER_PUT] = (row.put != null) ? row.put.getDeltaCounter() : Double.NaN;
                m[r][COL_DELTA_QUAN_COUNTER_PUT] = (row.put != null) ? row.put.getDeltaQuanCounter() : Double.NaN;
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
                comp.setBackground(HEADER_BG);
                comp.setForeground(HEADER_FG);
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

        private static boolean isDeltaCounterCol(int col) {
            return col == OptionsTableModel.COL_DELTA_COUNTER_CALL ||
                    col == OptionsTableModel.COL_DELTA_COUNTER_PUT;
        }

        private static boolean isBidAskCol(int col) {
            return col == OptionsTableModel.COL_BIDASK_CALL ||
                    col == OptionsTableModel.COL_BIDASK_PUT;
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
                    if (d > 0) c.setForeground(VAL_GREEN_FG);
                    else if (d < 0) c.setForeground(VAL_RED_FG);
                    else c.setForeground(Color.BLACK);
                } else {
                    c.setForeground(Color.BLACK);
                }
            }

            // ----- Background: selection > change highlight > column base > zebra -----
            if (isSelected) {
                c.setBackground(SELECTION_BG);
                c.setForeground(SELECTION_FG);
            } else {
                boolean paintable =
                        isDeltaCounterCol(column) ||
                                column == OptionsTableModel.COL_DELTA_QUAN_COUNTER_CALL ||
                                isBidAskCol(column) ||
                                column == OptionsTableModel.COL_DELTA_QUAN_COUNTER_PUT;

                int dir = paintable ? model.getChangeDirection(row, column) : 0;
                if (dir > 0) {
                    c.setBackground(HL_GREEN);
                } else if (dir < 0) {
                    c.setBackground(HL_RED);
                } else if (column == OptionsTableModel.COL_STRIKE) {
                    c.setBackground(STRIKE_BG); // distinct background for Strike
                } else {
                    c.setBackground((row % 2 == 0) ? BG_WHITE : BG_STRIPE);
                }
            }

            return c;
        }
    }
}
