package options;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import gui.MyGuiComps;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

/**
 * Window: Options table with background refresh + cross-platform fonts.
 * Columns:
 * B/A | Delta | Delta q | Strike | Delta q | Delta | B/A
 */
public class OptionsTableWindow extends MyGuiComps.MyFrame {

    /* ===================== Demo ===================== */
    public static void main(String[] args) {
        // Demo options data
        Options options = new Options(TA35.getInstance());
        try {
            for (int i = 1000; i <= 1050; i += 10) {
                Option call = new Option(Option.Side.CALL.toString(), i, options);
                Option put = new Option(Option.Side.PUT.toString(), i, options);
                Strike strike = new Strike(i);
                strike.setCall(call);
                strike.setPut(put);
                options.addStrike(strike);
            }
            TA35.getInstance().getExps().getWeek().setOptions(options);
            TA35.getInstance().getExps().getMonth().setOptions(options);
        } finally {
            // Launch window
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    OptionsTableWindow w = new OptionsTableWindow(TA35.getInstance(), "Options Monitor", options);
                    w.setSize(900, 600);
                    w.setLocationRelativeTo(null);
                    w.setVisible(true);
                }
            });
        }

    }

    /* ===================== Fonts (cross-platform) ===================== */
    private static final class FontPick {
        private static Set<String> installed() {
            return new HashSet<String>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
            ));
        }

        static Font pickSans(float size) {
            String[] prefs = {
                    "Segoe UI", "Helvetica Neue", "Arial", "Verdana", "Tahoma",
                    "DejaVu Sans", "Liberation Sans", "Noto Sans", "SansSerif"
            };
            return firstAvailable(prefs, Font.PLAIN, size);
        }

        static Font pickMono(float size) {
            String[] prefs = {
                    "Consolas", "Menlo", "DejaVu Sans Mono", "Liberation Mono", "Monospaced"
            };
            return firstAvailable(prefs, Font.PLAIN, size);
        }

        private static Font firstAvailable(String[] families, int style, float size) {
            Set<String> inst = installed();
            for (String fam : families) if (inst.contains(fam)) return new Font(fam, style, Math.round(size));
            return new Font("SansSerif", style, Math.round(size));
        }
    }

    private static final Font FONT_TEXT = FontPick.pickSans(13f);
    private static final Font FONT_TEXT_B = FONT_TEXT.deriveFont(Font.BOLD);
    private static final Font FONT_NUM = FontPick.pickMono(13f);
    private static final Font FONT_KPI = FontPick.pickSans(15f);

    /* ===================== Colors / Styles ===================== */
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color BG_STRIPE = new Color(0xFAFAFA);
    private static final Color BG_GREEN_SOFT = new Color(0xE6F4EA);
    private static final Color BG_RED_SOFT = new Color(0xFDE7E9);
    private static final Color GRID_COLOR = new Color(0xEEEEEE);
    private static final Color HEADER_BG = new Color(0xF5F7FA);
    private static final Color HEADER_FG = new Color(0x263238);
    private static final Color SELECTION_BG = new Color(0xE3F2FD);
    private static final Color SELECTION_FG = new Color(0x000000);

    /* ===================== UI ===================== */
    private Options optionsRef;
    private OptionsTable table;
    private Thread runner;
    private volatile boolean run = true;

    private MyGuiComps.MyTextField
            kpi1, kpi2, kpi3, kpi4; // placeholders – תשלים חישובים אם תרצה

    public OptionsTableWindow(BASE_CLIENT_OBJECT client, String title, Options options) throws HeadlessException {
        super(client, title); // אם הסופר שלך הפוך (title, client) – החלף כאן
        this.optionsRef = options;
    }

    @Override
    public void initListeners() { /* no-op */ }

    @Override
    public void initialize() {
        setLayout(new BorderLayout());

        // KPIs top panel
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

        optionsRef = TA35.getInstance().getExps().getMonth().getOptions();
                
        // Table
        table = new OptionsTable(optionsRef);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // First fill + start background refresh
        table.refresh();
        startRunner();
    }

    private void startRunner() {
        runner = new Thread(new Runnable() {
            @Override
            public void run() {
                run = true;
                while (run) {
                    try {
                        Thread.sleep(2000); // interval
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                // data in optionsRef assumed live-updated elsewhere
                                table.refresh();
                            }
                        });
                    } catch (InterruptedException ignore) {
                        break;
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
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

    private static JPanel createColumn(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(FONT_TEXT);
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
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
            int[] widths = {80, 100, 90, 100, 90, 100, 80};
            for (int i = 0; i < widths.length && i < cm.getColumnCount(); i++) {
                cm.getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    /* ======================= MODEL ======================= */

    static class OptionsTableModel extends AbstractTableModel {
        // Columns order (mirror of user code):
        static final int COL_BIDASK_CALL = 0;
        static final int COL_DELTA_COUNTER_CALL = 1;
        static final int COL_DELTA_QUAN_COUNTER_CALL = 2;
        static final int COL_STRIKE = 3;
        static final int COL_DELTA_QUAN_COUNTER_PUT = 4;
        static final int COL_DELTA_COUNTER_PUT = 5;
        static final int COL_BIDASK_PUT = 6;

        private final String[] cols = {
                "B/A", "Delta", "Delta q", "Strike", "Delta q", "Delta", "B/A"
        };

        private static class Row {
            double strike;
            Option call;
            Option put;

            Row(double strike, Option call, Option put) {
                this.strike = strike;
                this.call = call;
                this.put = put;
            }
        }

        private Options options;
        private final List<Row> rows = new ArrayList<Row>();

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
            List<Strike> strikes = new ArrayList<Strike>(options.getStrikes());
            strikes.sort(Comparator.comparingDouble(Strike::getStrike));
            rows.clear();
            for (Strike s : strikes) rows.add(new Row(s.getStrike(), s.getCall(), s.getPut()));
            currValues = buildMatrixFromRows(rows);
            prevValues = copy2D(currValues);
            fireTableDataChanged();
        }

        void refresh() {
            if (options == null) return;

            List<Strike> strikes = new ArrayList<Strike>(options.getStrikes());

            strikes.sort(Comparator.comparingDouble(Strike::getStrike));

            List<Row> newRows = new ArrayList<Row>();
            for (Strike s : strikes) newRows.add(new Row(s.getStrike(), s.getCall(), s.getPut()));

            double[][] newCurr = buildMatrixFromRows(newRows);
            double[][] newPrev = remapPrevByStrike(rows, prevValues, newRows, getColumnCount());

            rows.clear();
            rows.addAll(newRows);
            prevValues = newPrev;
            currValues = newCurr;

            fireTableDataChanged();
        }

        private double[][] buildMatrixFromRows(List<Row> rows) {
            int rc = rows.size();
            int cc = getColumnCount();
            double[][] m = new double[rc][cc];
            for (int r = 0; r < rc; r++) {
                Row row = rows.get(r);
                // NOTE: דורש ש-Option יכיל את המתודות האלה בצדך.
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
            for (int i = 0; i < rows.size(); i++) {
                if (Double.compare(rows.get(i).strike, k) == 0) return i;
            }
            return -1;
        }

        private static double[][] remapPrevByStrike(List<Row> oldRows, double[][] oldPrev,
                                                    List<Row> newRows, int cols) {
            double[][] remapped = new double[newRows.size()][cols];
            for (int r = 0; r < newRows.size(); r++) {
                double k = newRows.get(r).strike;
                int oldIndex = findRowIndexByStrike(oldRows, k);
                if (oldIndex >= 0) {
                    remapped[r] = oldPrev[oldIndex].clone();
                } else {
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
            return Double.isNaN(v) ? null : Double.valueOf(v);
        }

        int getChangeDirection(int row, int col) {
            if (row < 0 || row >= rows.size()) return 0;
            double prev = prevValues[row][col];
            double curr = currValues[row][col];
            if (Double.isNaN(prev) || Double.isNaN(curr)) return 0;
            if (curr > prev) return 1;
            if (curr < prev) return -1;
            return 0;
        }

        Double getDeltaVsPrev(int row, int col) {
            if (row < 0 || row >= rows.size()) return null;
            double prev = prevValues[row][col];
            double curr = currValues[row][col];
            if (Double.isNaN(prev) || Double.isNaN(curr)) return null;
            return Double.valueOf(curr - prev);
        }
    }

    /* ======================= HEADER RENDERER ======================= */

    private static class JTableHeaderStyled {
        static void apply(JTableHeader header) {
            header.setReorderingAllowed(true);
            header.setResizingAllowed(true);
            header.setOpaque(true);
            final TableCellRenderer base = header.getDefaultRenderer();
            header.setDefaultRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    Component comp = base.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                    if (comp instanceof JComponent) ((JComponent) comp).setOpaque(true);
                    comp.setBackground(HEADER_BG);
                    comp.setForeground(HEADER_FG);
                    comp.setFont(FONT_TEXT_B);
                    if (comp instanceof JLabel) ((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
                    return comp;
                }
            });
        }
    }

    /* ======================= CELL RENDERER ======================= */

    private static class ChangeHighlightRenderer extends DefaultTableCellRenderer {
        private final OptionsTableModel model;
        private final DecimalFormat fmtDelta = new DecimalFormat("0.000");
        private final DecimalFormat fmtInt = new DecimalFormat("0");
        private final DecimalFormat fmtK = new DecimalFormat("#,##0.##");

        ChangeHighlightRenderer(OptionsTableModel model) {
            this.model = model;
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // ----- Fonts: mono for numbers -----
            c.setFont((column == OptionsTableModel.COL_STRIKE) ? FONT_NUM : FONT_NUM);

            // ----- Text formatting -----
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

            // ----- Tooltip: diff vs prev -----
            Double diff = model.getDeltaVsPrev(row, column);
            if (diff != null) {
                String s;
                if (column == OptionsTableModel.COL_DELTA_COUNTER_CALL ||
                        column == OptionsTableModel.COL_DELTA_COUNTER_PUT) {
                    s = "Δ change: " + (diff.doubleValue() >= 0 ? "+" : "") + fmtDelta.format(diff.doubleValue());
                } else if (column == OptionsTableModel.COL_STRIKE) {
                    s = "Strike";
                } else {
                    s = "Change: " + (diff.doubleValue() >= 0 ? "+" : "") + fmtInt.format(diff.doubleValue());
                }
                setToolTipText(s);
            } else {
                setToolTipText(null);
            }

            // ----- Background priority: selection > change highlight > zebra -----
            if (isSelected) {
                c.setBackground(SELECTION_BG);
                c.setForeground(SELECTION_FG);
            } else {
                boolean paintable =
                        column == OptionsTableModel.COL_DELTA_COUNTER_CALL ||
                                column == OptionsTableModel.COL_DELTA_QUAN_COUNTER_CALL ||
                                column == OptionsTableModel.COL_BIDASK_CALL ||
                                column == OptionsTableModel.COL_BIDASK_PUT ||
                                column == OptionsTableModel.COL_DELTA_COUNTER_PUT ||
                                column == OptionsTableModel.COL_DELTA_QUAN_COUNTER_PUT;

                int dir = paintable ? model.getChangeDirection(row, column) : 0;
                if (dir > 0) {
                    c.setBackground(BG_GREEN_SOFT);
                    c.setForeground(Color.BLACK);
                } else if (dir < 0) {
                    c.setBackground(BG_RED_SOFT);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground((row % 2 == 0) ? BG_WHITE : BG_STRIPE);
                    c.setForeground(Color.BLACK);
                }
            }

            return c;
        }
    }
}
