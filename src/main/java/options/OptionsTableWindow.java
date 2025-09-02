package options;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import gui.MyGuiComps;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

/**
 * Options Table Window:
 * - Styled header, zebra rows, subtle grid
 * - Cross-platform font fallback (UI + monospaced numbers)
 * - Background refresh thread (assumes options data updated elsewhere)
 *
 * Columns (left→right):
 *   B/A | Delta | Delta q | Strike | Delta q | Delta | B/A
 *
 * NOTE: Requires Option to provide:
 *   getBidAskCounter(), getDeltaCounter(), getDeltaQuanCounter()
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
            s.setCall(c); s.setPut(p);
            options.addStrike(s);
        }
        SwingUtilities.invokeLater(() -> {
            OptionsTableWindow w = new OptionsTableWindow(TA35.getInstance(), "Options Monitor", options);
            w.setSize(940, 620);
            w.setLocationRelativeTo(null);
            w.setVisible(true);
        });
    }

    /* ===================== Font picker (cross-platform) ===================== */
    private static final class FontPick {
        private static Set<String> installed() {
            return new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
            ));
        }
        static Font pickSans(float size, int style) {
            String[] prefs = {
                    "Segoe UI","Helvetica Neue","Arial","Verdana","Tahoma",
                    "DejaVu Sans","Liberation Sans","Noto Sans","SansSerif"
            };
            return firstAvailable(prefs, style, size);
        }
        static Font pickMono(float size, int style) {
            String[] prefs = {
                    "Consolas","Menlo","DejaVu Sans Mono","Liberation Mono","Monospaced"
            };
            return firstAvailable(prefs, style, size);
        }
        private static Font firstAvailable(String[] families, int style, float size) {
            Set<String> inst = installed();
            for (String fam : families) if (inst.contains(fam)) return new Font(fam, style, Math.round(size));
            return new Font("SansSerif", style, Math.round(size));
        }
    }

    private static final Font FONT_TEXT   = FontPick.pickSans(13f, Font.PLAIN);
    private static final Font FONT_TEXT_B = FontPick.pickSans(13f, Font.BOLD);
    private static final Font FONT_NUM    = FontPick.pickMono(13f, Font.PLAIN);
    private static final Font FONT_KPI    = FontPick.pickSans(15f, Font.PLAIN);

    /* ===================== Colors / Styles ===================== */
    private static final Color BG_WHITE       = Color.WHITE;
    private static final Color BG_STRIPE      = new Color(0xFAFAFA);
    private static final Color BG_GREEN_SOFT  = new Color(0xE6F4EA);
    private static final Color BG_RED_SOFT    = new Color(0xFDE7E9);
    private static final Color GRID_COLOR     = new Color(0xEEEEEE);
    private static final Color HEADER_BG      = new Color(0xF5F7FA);
    private static final Color HEADER_FG      = new Color(0x263238);
    private static final Color SELECTION_BG   = new Color(0xE3F2FD);
    private static final Color SELECTION_FG   = new Color(0x000000);

    /* ===================== UI / Data ===================== */
    private Options optionsRef;
    private OptionsTable table;

    private Thread runner;
    private volatile boolean run = true;

    private MyGuiComps.MyTextField kpi1, kpi2, kpi3, kpi4; // placeholders

    public OptionsTableWindow(BASE_CLIENT_OBJECT client, String title, Options options) throws HeadlessException {
        super(client, title);
        this.optionsRef = options;
        initialize();      // build UI
        table.refresh();   // first fill
        startRunner();     // background refresh
    }

    @Override public void initListeners() {}

    @Override
    public void initialize() {
        setLayout(new BorderLayout());

        // ---------- KPIs top bar ----------
        kpi1 = new MyGuiComps.MyTextField(); kpi1.setFont(FONT_KPI);
        kpi2 = new MyGuiComps.MyTextField(); kpi2.setFont(FONT_KPI);
        kpi3 = new MyGuiComps.MyTextField(); kpi3.setFont(FONT_KPI);
        kpi4 = new MyGuiComps.MyTextField(); kpi4.setFont(FONT_KPI);

        JPanel controlPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        controlPanel.add(createColumn("Positive counter :", kpi1));
        controlPanel.add(createColumn("Total weight:",      kpi2));
        controlPanel.add(createColumn("Weighted counter:",  kpi3));
        controlPanel.add(createColumn("Green stocks:",      kpi4));
        add(controlPanel, BorderLayout.NORTH);

        // ---------- Table ----------
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
                    Thread.sleep(2000); // refresh interval
                    SwingUtilities.invokeLater(() -> {
                        // assume optionsRef objects are updated elsewhere
                        table.refresh();
                    });
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

    /* ===========================================================
       =======================  TABLE  ============================
       =========================================================== */

    static class OptionsTable extends JTable {
        private final OptionsTableModel model;

        OptionsTable(Options options) {
            this.model = new OptionsTableModel();
            setModel(model);

            setFillsViewportHeight(true);
            setRowHeight(30);
            setAutoCreateRowSorter(true);

            setShowHorizontalLines(true);
            setShowVerticalLines(false);
            setGridColor(GRID_COLOR);
            setIntercellSpacing(new Dimension(0, 1));

            setSelectionBackground(SELECTION_BG);
            setSelectionForeground(SELECTION_FG);

            // Header style
            JTableHeaderStyled.apply(getTableHeader());

            // Renderers
            ChangeHighlightRenderer renderer = new ChangeHighlightRenderer(model);
            for (int c = 0; c < model.getColumnCount(); c++) {
                getColumnModel().getColumn(c).setCellRenderer(renderer);
            }

            // Column widths
            setNiceColumnWidths();

            // bind options
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
            // B/A | Delta | Delta q | Strike | Delta q | Delta | B/A
            int[] widths = {80, 90, 90, 110, 90, 90, 80};
            for (int i = 0; i < widths.length && i < cm.getColumnCount(); i++) {
                cm.getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    /* ======================= MODEL ======================= */

    static class OptionsTableModel extends AbstractTableModel {
        // columns
        static final int COL_BIDASK_CALL            = 0;
        static final int COL_DELTA_COUNTER_CALL     = 1;
        static final int COL_DELTA_QUAN_COUNTER_CALL= 2;
        static final int COL_STRIKE                 = 3;
        static final int COL_DELTA_QUAN_COUNTER_PUT = 4;
        static final int COL_DELTA_COUNTER_PUT      = 5;
        static final int COL_BIDASK_PUT             = 6;

        private final String[] cols = {"B/A", "Delta", "Delta q", "Strike", "Delta q", "Delta", "B/A"};

        private static class Row {
            final double strike;
            final Option call;
            final Option put;
            Row(double strike, Option call, Option put) {
                this.strike = strike; this.call = call; this.put = put;
            }
        }

        private Options options;
        private final List<Row> rows = new ArrayList<>();

        // prev/curr snapshots for change highlighting
        private double[][] prevValues = new double[0][0];
        private double[][] currValues = new double[0][0];

        void setOptions(Options options) { this.options = options; }

        void initialLoad() {
            if (options == null) { rows.clear(); prevValues = new double[0][0]; currValues = new double[0][0]; fireTableDataChanged(); return; }
            List<Strike> strikes = new ArrayList<>(options.getStrikes());
            strikes.sort(Comparator.comparingDouble(Strike::getStrike));
            rows.clear();
            for (Strike s : strikes) rows.add(new Row(s.getStrike(), s.getCall(), s.getPut()));
            currValues = buildMatrixFromRows(rows);
            prevValues = copy2D(currValues); // first snapshot
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
                m[r][COL_DELTA_COUNTER_CALL]      = (row.call != null) ? row.call.getDeltaCounter()      : Double.NaN;
                m[r][COL_DELTA_QUAN_COUNTER_CALL] = (row.call != null) ? row.call.getDeltaQuanCounter() : Double.NaN;
                m[r][COL_BIDASK_CALL]             = (row.call != null) ? row.call.getBidAskCounter()    : Double.NaN;

                m[r][COL_STRIKE]                  = row.strike;

                m[r][COL_BIDASK_PUT]              = (row.put  != null) ? row.put.getBidAskCounter()     : Double.NaN;
                m[r][COL_DELTA_COUNTER_PUT]       = (row.put  != null) ? row.put.getDeltaCounter()      : Double.NaN;
                m[r][COL_DELTA_QUAN_COUNTER_PUT]  = (row.put  != null) ? row.put.getDeltaQuanCounter()  : Double.NaN;
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

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }
        @Override public Class<?> getColumnClass(int columnIndex) { return Double.class; }
        @Override public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }

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
        private final DecimalFormat fmtDelta = new DecimalFormat("0.000");
        private final DecimalFormat fmtInt   = new DecimalFormat("0");
        private final DecimalFormat fmtK     = new DecimalFormat("#,##0.##");

        ChangeHighlightRenderer(OptionsTableModel model) {
            this.model = model;
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Fonts on each paint (LAF-safe)
            c.setFont(FONT_NUM); // numbers everywhere in this grid

            // Text formatting
            if (value instanceof Number) {
                double d = ((Number) value).doubleValue();
                String txt;
                if (column == OptionsTableModel.COL_DELTA_COUNTER_CALL ||
                        column == OptionsTableModel.COL_DELTA_COUNTER_PUT) {
                    txt = fmtDelta.format(d);
                } else if (column == OptionsTableModel.COL_STRIKE) {
                    txt = fmtK.format(d);
                } else {
                    txt = fmtInt.format(d);
                }
                setText(txt);
            } else {
                setText(value == null ? "" : value.toString());
            }

            // Tooltip (diff)
            Double diff = model.getDeltaVsPrev(row, column);
            if (diff != null) {
                String tip;
                if (column == OptionsTableModel.COL_DELTA_COUNTER_CALL ||
                        column == OptionsTableModel.COL_DELTA_COUNTER_PUT) {
                    tip = "Δ change: " + (diff >= 0 ? "+" : "") + fmtDelta.format(diff);
                } else if (column == OptionsTableModel.COL_STRIKE) {
                    tip = "Strike";
                } else {
                    tip = "Change: " + (diff >= 0 ? "+" : "") + fmtInt.format(diff);
                }
                setToolTipText(tip);
            } else {
                setToolTipText(null);
            }

            // Background priority: selection > change highlight > zebra
            if (isSelected) {
                c.setBackground(SELECTION_BG); c.setForeground(SELECTION_FG);
            } else {
                boolean paintable =
                        column == OptionsTableModel.COL_DELTA_COUNTER_CALL ||
                                column == OptionsTableModel.COL_DELTA_QUAN_COUNTER_CALL ||
                                column == OptionsTableModel.COL_BIDASK_CALL ||
                                column == OptionsTableModel.COL_BIDASK_PUT ||
                                column == OptionsTableModel.COL_DELTA_COUNTER_PUT ||
                                column == OptionsTableModel.COL_DELTA_QUAN_COUNTER_PUT;

                int dir = paintable ? model.getChangeDirection(row, column) : 0;
                if (dir > 0)       { c.setBackground(BG_GREEN_SOFT); c.setForeground(Color.BLACK); }
                else if (dir < 0)  { c.setBackground(BG_RED_SOFT);   c.setForeground(Color.BLACK); }
                else               { c.setBackground((row % 2 == 0) ? BG_WHITE : BG_STRIPE); c.setForeground(Color.BLACK); }
            }

            return c;
        }
    }
}
