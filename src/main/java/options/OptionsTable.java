package options;

import api.TA35;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * טבלה שמציגה:
 * Delta Call | Bid/Ask Cnt (Call) | Strike | Bid/Ask Cnt (Put) | Delta Put
 * שינויי ערכים יחסית לריענון קודם נצבעים בירוק (עלייה) / אדום (ירידה).
 * <p>
 * שימוש:
 * OptionsTable table = new OptionsTable(options);
 * JScrollPane sp = new JScrollPane(table);
 * ...
 * // בכל עדכון נתונים:
 * table.refresh();
 */
public class OptionsTable extends JTable {

    public static void main(String[] args) {

        Options options = new Options(TA35.getInstance());

        for (int i = 10; i < 100; i += 10) {
            Option call = new Option(Option.Side.CALL.toString(), i, options);
            Option put = new Option(Option.Side.PUT.toString(), i, options);
            Strike strike = new Strike(i);
            strike.setCall(call);
            strike.setPut(put);
            options.addStrike(strike);
            System.out.println(strike.getStrike());
        }



        OptionsTable table = new OptionsTable(options);
        JFrame f = new JFrame();
        f.setContentPane(new JScrollPane(table));
        f.setSize(800, 500);
        f.setVisible(true);

// בכל עדכון נתונים:
        table.refresh();

    }

    private final OptionsTableModel model;

    // צבעים וסגנונות
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color BG_STRIPE = new Color(0xFAFAFA);
    private static final Color BG_GREEN_SOFT = new Color(0xE6F4EA);
    private static final Color BG_RED_SOFT = new Color(0xFDE7E9);
    private static final Color GRID_COLOR = new Color(0xEEEEEE);
    private static final Color HEADER_BG = new Color(0xF5F7FA);
    private static final Color HEADER_FG = new Color(0x263238);
    private static final Color SELECTION_BG = new Color(0xE3F2FD);
    private static final Color SELECTION_FG = new Color(0x000000);

    public OptionsTable() {
        this(null);
    }

    public OptionsTable(Options options) {
        this.model = new OptionsTableModel();
        setModel(model);
        setFillsViewportHeight(true);
        setRowHeight(28);
        setAutoCreateRowSorter(true);

        // גריד עדין
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setGridColor(GRID_COLOR);
        setIntercellSpacing(new Dimension(0, 1));

        // בחירה
        setSelectionBackground(SELECTION_BG);
        setSelectionForeground(SELECTION_FG);

        // רנדרר תאים מעוצב
        ChangeHighlightRenderer renderer = new ChangeHighlightRenderer(model);
        for (int c = 0; c < model.getColumnCount(); c++) {
            getColumnModel().getColumn(c).setCellRenderer(renderer);
        }

        // כותרת מעוצבת
        JTableHeaderStyled.apply(getTableHeader());

        // רוחבי עמודות נוחים
        setNiceColumnWidths();

        setOptions(options);
    }

    /**
     * להצמיד מקור נתונים (Options) לטבלה
     */
    public void setOptions(Options options) {
        model.setOptions(options);
        model.initialLoad();
    }

    /**
     * לרענן את הערכים מה־Options ולצבוע שינויים מול הריענון הקודם
     */
    public void refresh() {
        model.refresh();
    }

    private void setNiceColumnWidths() {
        TableColumnModel cm = getColumnModel();
        // Delta Call | Bid/Ask (Call) | Strike | Bid/Ask (Put) | Delta Put
        int[] widths = {110, 140, 100, 140, 110};
        for (int i = 0; i < widths.length && i < cm.getColumnCount(); i++) {
            cm.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    // ======================= מודל הטבלה =======================

    private static class OptionsTableModel extends AbstractTableModel {

        static final int COL_DELTA_CALL = 0;
        static final int COL_BIDASK_CALL = 1;
        static final int COL_STRIKE = 2;
        static final int COL_BIDASK_PUT = 3;
        static final int COL_DELTA_PUT = 4;

        private final String[] cols = {
                "Delta Call", "Bid/Ask Cnt (Call)", "Strike", "Bid/Ask Cnt (Put)", "Delta Put"
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

        // רשימת שורות (מסודר לפי סטרייק)
        private final List<Row> rows = new ArrayList<>();

        // prev[row][col] ו־ curr[row][col] להשוואה בין ריענונים
        private double[][] prevValues = new double[0][0];
        private double[][] currValues = new double[0][0];

        void setOptions(Options options) {
            this.options = options;
        }

        /**
         * טעינה ראשונה — מונעת צביעה מלאכותית של “כל הערכים השתנו”
         */
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
            for (Strike s : strikes) {
                rows.add(new Row(s.getStrike(), s.getCall(), s.getPut()));
            }
            currValues = buildMatrixFromRows(rows);
            prevValues = copy2D(currValues); // צילום ראשון לעצמך
            fireTableDataChanged();
        }

        /**
         * ריענון מלא מה־Options (כולל שמירת “עבר” וצביעת שינוי)
         */
        void refresh() {
            if (options == null) return;

            // בונים רשימת שורות חדשה לפי מצב עדכני
            List<Strike> strikes = new ArrayList<>(options.getStrikes());
            strikes.sort(Comparator.comparingDouble(Strike::getStrike));

            List<Row> newRows = new ArrayList<>();
            for (Strike s : strikes) {
                newRows.add(new Row(s.getStrike(), s.getCall(), s.getPut()));
            }

            // בונים מטריצת "נוכחי" חדשה
            double[][] newCurr = buildMatrixFromRows(newRows);

            // remap prev: למפות את prev הישן לשורות החדשות לפי strike
            double[][] newPrev = remapPrevByStrike(rows, prevValues, newRows, getColumnCount());

            // מחליפים מבנים
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
                m[r][COL_DELTA_CALL] = (row.call != null) ? row.call.getDelta() : Double.NaN;
                m[r][COL_BIDASK_CALL] = (row.call != null) ? row.call.getBidAskCounter() : Double.NaN;
                m[r][COL_STRIKE] = row.strike;
                m[r][COL_BIDASK_PUT] = (row.put != null) ? row.put.getBidAskCounter() : Double.NaN;
                m[r][COL_DELTA_PUT] = (row.put != null) ? row.put.getDelta() : Double.NaN;
            }
            return m;
        }

        private static double[][] copy2D(double[][] src) {
            double[][] dst = new double[src.length][];
            for (int i = 0; i < src.length; i++) {
                dst[i] = src[i].clone();
            }
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

        // ----- AbstractTableModel -----
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
            // Double כדי שמיון יהיה מספרי ולא לקסיקוגרפי
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

        /**
         * -1 ירד, 0 ללא שינוי/לא ידוע, +1 עלה
         */
        int getChangeDirection(int row, int col) {
            if (row < 0 || row >= rows.size()) return 0;
            double prev = prevValues[row][col];
            double curr = currValues[row][col];
            if (Double.isNaN(prev) || Double.isNaN(curr)) return 0;
            if (curr > prev) return 1;
            if (curr < prev) return -1;
            return 0;
        }

        /**
         * הפרש נוכחי-קודם להצגת tooltip
         */
        Double getDeltaVsPrev(int row, int col) {
            if (row < 0 || row >= rows.size()) return null;
            double prev = prevValues[row][col];
            double curr = currValues[row][col];
            if (Double.isNaN(prev) || Double.isNaN(curr)) return null;
            return curr - prev;
        }
    }

    // ======================= רנדררים מעוצבים =======================

    private static class JTableHeaderStyled {
        static void apply(JTableHeader header) {
            header.setReorderingAllowed(true);
            header.setResizingAllowed(true);
            header.setBackground(HEADER_BG);
            header.setForeground(HEADER_FG);
            header.setFont(new Font("Segoe UI", Font.BOLD, 15));
            header.setOpaque(true);

            // Renderer מותאם כדי לשמר צבע רקע גם בח部分 מערכות
            TableCellRenderer base = header.getDefaultRenderer();
            header.setDefaultRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
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
            });
        }
    }

    /**
     * רנדרר שמדגיש שינוי, עם זברה-סטייל ופורמט מספרים
     */
    private static class ChangeHighlightRenderer extends DefaultTableCellRenderer {
        private final OptionsTableModel model;
        private final DecimalFormat fmtDelta = new DecimalFormat("0.000");
        private final DecimalFormat fmtInt = new DecimalFormat("0");
        private final DecimalFormat fmtK = new DecimalFormat("#,##0.##");

        ChangeHighlightRenderer(OptionsTableModel model) {
            this.model = model;
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Consolas", Font.PLAIN, 15)); // ספרות מונוספייס
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // טקסט: פורמט מספרי לפי עמודה
            if (value instanceof Number) {

                String txt;
                if (column == OptionsTableModel.COL_DELTA_CALL || column == OptionsTableModel.COL_DELTA_PUT) {
                    txt = fmtDelta.format(((Number) value).doubleValue());
                } else if (column == OptionsTableModel.COL_STRIKE) {
                    txt = fmtK.format(((Number) value).doubleValue());
                } else {
                    txt = fmtInt.format(((Number) value).doubleValue());
                }
                setText(txt);
            } else {
                setText(value == null ? "" : value.toString());
            }

            // Tooltip: מראה שינוי מול הקודם
            Double diff = model.getDeltaVsPrev(row, column);
            if (diff != null) {
                String s;
                if (column == OptionsTableModel.COL_DELTA_CALL || column == OptionsTableModel.COL_DELTA_PUT) {
                    s = "Δ שינוי: " + (diff >= 0 ? "+" : "") + fmtDelta.format(diff);
                } else if (column == OptionsTableModel.COL_STRIKE) {
                    s = "Strike";
                } else {
                    s = "שינוי: " + (diff >= 0 ? "+" : "") + fmtInt.format(diff);
                }
                setToolTipText(s);
            } else {
                setToolTipText(null);
            }

            // רקע: בחירה גוברת; אחרת—הדגשת שינוי; אחרת—זברה
            if (isSelected) {
                c.setBackground(SELECTION_BG);
                c.setForeground(SELECTION_FG);
            } else {
                int dir = model.getChangeDirection(row, column);
                boolean paintable =
                        column == OptionsTableModel.COL_DELTA_CALL ||
                                column == OptionsTableModel.COL_BIDASK_CALL ||
                                column == OptionsTableModel.COL_BIDASK_PUT ||
                                column == OptionsTableModel.COL_DELTA_PUT;

                if (paintable && dir != 0) {
                    c.setBackground(dir > 0 ? BG_GREEN_SOFT : BG_RED_SOFT);
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
