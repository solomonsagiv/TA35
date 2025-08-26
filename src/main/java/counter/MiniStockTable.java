
package counter;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import api.deltaTest.Calculator;
import arik.Arik;
import gui.MyGuiComps;
import miniStocks.MiniStock;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
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

    private static final DecimalFormat DF_PCT = new DecimalFormat("0.00");
    private static final DecimalFormat DF_WGT = new DecimalFormat("0.00");

    /* ======== UI ======== */
    private JTable table;
    private Model model;

    private MyGuiComps.MyTextField
            number_of_positive_stocks_field,
            weight_of_positive_stocks_field,
            weighted_counter_field,
            green_stocks_field;

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
        this.stocksRef = new ArrayList<MiniStock>(TA35.getInstance().getStocksHandler().getStocks());
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
        number_of_positive_stocks_field = new MyGuiComps.MyTextField(); number_of_positive_stocks_field.setFontSize(15);
        weight_of_positive_stocks_field = new MyGuiComps.MyTextField(); weight_of_positive_stocks_field.setFontSize(15);
        weighted_counter_field          = new MyGuiComps.MyTextField(); weighted_counter_field.setFontSize(15);
        green_stocks_field              = new MyGuiComps.MyTextField(); green_stocks_field.setFontSize(15);

        JPanel controlPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        controlPanel.add(createColumn("Positive counter :", number_of_positive_stocks_field));
        controlPanel.add(createColumn("Total weight:",      weight_of_positive_stocks_field));
        controlPanel.add(createColumn("Weighted counter:",  weighted_counter_field));
        controlPanel.add(createColumn("Green stocks:",      green_stocks_field));
        add(controlPanel, BorderLayout.NORTH);

        // ---- Table ----
        model = new Model();
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(GRID_COLOR);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(SELECTION_FG);

        // Header style
        JTableHeader header = table.getTableHeader();
        JTableHeaderStyled.apply(header);

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
        int[] widths = {160, 100, 100, 110, 100, 100};
        TableColumnModel cm = table.getColumnModel();
        for (int i = 0; i < Math.min(widths.length, cm.getColumnCount()); i++) {
            cm.getColumn(i).setPreferredWidth(widths[i]);
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private static JPanel createColumn(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    /* ======================== Refresh & Runner ======================== */

    private void refreshNow() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // עדכון מודל הטבלה
                model.refreshFrom(stocksRef);

                // עדכון ה-KPIs העליונים
                number_of_positive_stocks_field.colorForge(Calculator.get_stocks_positive_count());
                weight_of_positive_stocks_field.colorForge(Calculator.get_stocks_positive_weight_count());
                green_stocks_field.colorForge(Calculator.get_green_stocks());
                weighted_counter_field.colorForge((int) Calculator.calculateWeightedCounters()[0]);
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
                        Thread.sleep(10_000);
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
        static final int COL_NAME    = 0;
        static final int COL_OPEN    = 1;
        static final int COL_LAST    = 2;
        static final int COL_CHANGE  = 3;
        static final int COL_COUNTER = 4;
        static final int COL_WEIGHT  = 5;

        private final String[] cols = {"Name", "Open", "Last", "Change", "Counter", "Weight"};

        /** Row holder */
        private static class Row {
            final String name;
            final double openPct;   // NaN אם base=0
            final double lastPct;
            final double changePct; // last-open
            final int    counter;
            final double weight;

            Row(String name, double openPct, double lastPct, double changePct, int cnt, double weight) {
                this.name = name;
                this.openPct = openPct;
                this.lastPct = lastPct;
                this.changePct = changePct;
                this.counter = cnt;
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
                    lastPct = ((s.getPre_last() - s.getBase()) / s.getBase()) * 100.0;
                    diffPct = lastPct - openPct;
                }
                int counter = s.getBid_ask_counter();
                double weight = s.getWeight();

                // fill curr map for change detection (name-based)
                double[] currVals = new double[6];
                currVals[COL_NAME]    = Double.NaN;    // לא מספרי
                currVals[COL_OPEN]    = openPct;
                currVals[COL_LAST]    = lastPct;
                currVals[COL_CHANGE]  = diffPct;
                currVals[COL_COUNTER] = counter;
                currVals[COL_WEIGHT]  = weight;
                currByName.put(name, currVals);

                newRows.add(new Row(name, openPct, lastPct, diffPct, counter, weight));
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
                case COL_NAME:    return r.name;
                case COL_OPEN:    return Double.isNaN(r.openPct)   ? null : r.openPct;
                case COL_LAST:    return Double.isNaN(r.lastPct)   ? null : r.lastPct;
                case COL_CHANGE:  return Double.isNaN(r.changePct) ? null : r.changePct;
                case COL_COUNTER: return (double) r.counter; // נשמר Double למיון
                case COL_WEIGHT:  return r.weight;
                default:          return null;
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

    private static class JTableHeaderStyled {
        static void apply(JTableHeader header) {
            header.setReorderingAllowed(true);
            header.setResizingAllowed(true);
            header.setBackground(HEADER_BG);
            header.setForeground(HEADER_FG);
            header.setFont(new Font("Segoe UI", Font.BOLD, 13));
            header.setOpaque(true);

            final TableCellRenderer base = header.getDefaultRenderer();
            header.setDefaultRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    Component comp = base.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                    if (comp instanceof JComponent) {
                        ((JComponent) comp).setOpaque(true);
                    }
                    comp.setBackground(HEADER_BG);
                    comp.setForeground(HEADER_FG);
                    if (comp instanceof JLabel) {
                        ((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    return comp;
                }
            });
        }
    }

    private static class NameRenderer extends DefaultTableCellRenderer {
        NameRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            // Zebra / selection
            if (isSelected) {
                c.setBackground(SELECTION_BG); c.setForeground(SELECTION_FG);
            } else {
                c.setBackground((row % 2 == 0) ? BG_WHITE : BG_STRIPE); c.setForeground(Color.BLACK);
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
            setFont(new Font("Consolas", Font.PLAIN, 13));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

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
                } else if (column == Model.COL_COUNTER) {
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
                } else if (column == Model.COL_COUNTER) {
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
                    if (column == Model.COL_OPEN || column == Model.COL_LAST || column == Model.COL_CHANGE || column == Model.COL_COUNTER) {
                        setForeground(v > 0 ? new Color(0x1B5E20) : (v < 0 ? new Color(0xB71C1C) : Color.BLACK));
                    } else {
                        setForeground(Color.BLACK);
                    }
                } else {
                    setForeground(Color.BLACK);
                }
            }

            // ----- Background priority: selection > change highlight > zebra -----
            if (isSelected) {
                c.setBackground(SELECTION_BG); c.setForeground(SELECTION_FG);
            } else {
                boolean paintable =
                        column == Model.COL_OPEN || column == Model.COL_LAST ||
                                column == Model.COL_CHANGE || column == Model.COL_COUNTER;

                int dir = paintable ? model.getChangeDirection(row, column, table) : 0;
                if (dir > 0) {
                    c.setBackground(BG_GREEN_SOFT);
                } else if (dir < 0) {
                    c.setBackground(BG_RED_SOFT);
                } else {
                    c.setBackground((row % 2 == 0) ? BG_WHITE : BG_STRIPE);
                }
            }

            return c;
        }
    }
}
