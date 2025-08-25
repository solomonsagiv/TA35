package options;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// ====== מודל טבלה ======
public class OptionsTableModel extends AbstractTableModel {

    public static final int COL_DELTA_CALL = 0;
    public static final int COL_BIDASK_CALL = 1;
    public static final int COL_STRIKE = 2;
    public static final int COL_BIDASK_PUT = 3;
    public static final int COL_DELTA_PUT = 4;

    private final String[] cols = {
            "Delta Call", "Bid/Ask Cnt (Call)", "Strike", "Bid/Ask Cnt (Put)", "Delta Put"
    };

    private static class Row {
        double strike;
        Option call;
        Option put;
        Row(double strike, Option call, Option put) {
            this.strike = strike; this.call = call; this.put = put;
        }
    }

    // הרשימה הממוינת לפי סטרייק
    private final List<Row> rows = new ArrayList<>();

    // שמירת ערכים קודמים לצביעה (אחרון מול נוכחי)
    // prev[row][col], curr[row][col]
    private double[][] prevValues = new double[0][0];
    private double[][] currValues = new double[0][0];

    public void refreshFromOptions(Options options) {
        // לבנות רשימת שורות מסטרייקים קיימים
        List<Strike> strikes = new ArrayList<>(options.getStrikes());
        strikes.sort(Comparator.comparingDouble(Strike::getStrike));

        List<Row> newRows = new ArrayList<>();
        for (Strike s : strikes) {
            Option call = s.getCall();
            Option put  = s.getPut();
            newRows.add(new Row(s.getStrike(), call, put));
        }

        // להבטיח מטריצות בגודל מתאים
        int newRowCount = newRows.size();
        int colCount = getColumnCount();

        double[][] newCurr = new double[newRowCount][colCount];
        for (int r = 0; r < newRowCount; r++) {
            Row row = newRows.get(r);
            newCurr[r][COL_DELTA_CALL]  = (row.call != null) ? row.call.getDelta() : Double.NaN;
            newCurr[r][COL_BIDASK_CALL] = (row.call != null) ? row.call.getBidAskCounter() : Double.NaN;
            newCurr[r][COL_STRIKE]      = row.strike;
            newCurr[r][COL_BIDASK_PUT]  = (row.put  != null) ? row.put.getBidAskCounter() : Double.NaN;
            newCurr[r][COL_DELTA_PUT]   = (row.put  != null) ? row.put.getDelta() : Double.NaN;
        }

        // אם זו הטעינה הראשונה – השווה אל עצמך כדי שלא יצבע הכל
        if (currValues.length == 0) {
            prevValues = copy2D(newCurr);
        } else {
            // הזזה: הערכים שהיו curr הופכים ל-prev בגודל החדש (ממפים לפי סטרייק)
            prevValues = remapPrevByStrike(this.rows, prevValues, newRows, colCount);
        }

        // עדכון המבנים
        this.rows.clear();
        this.rows.addAll(newRows);
        this.currValues = newCurr;

        fireTableDataChanged();
    }

    private static double[][] copy2D(double[][] src) {
        double[][] dst = new double[src.length][];
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i].clone();
        }
        return dst;
    }

    // למקרה שמספר השורות משתנה – עושים מיפוי לפי strike
    private static double[][] remapPrevByStrike(List<Row> oldRows, double[][] oldPrev, List<Row> newRows, int cols) {
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

    private static int findRowIndexByStrike(List<Row> rows, double k) {
        for (int i = 0; i < rows.size(); i++) {
            if (Double.compare(rows.get(i).strike, k) == 0) return i;
        }
        return -1;
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

    /** -1 ירד, 0 ללא שינוי/לא ידוע, +1 עלה */
    public int getChangeDirection(int row, int col) {
        if (row < 0 || row >= rows.size()) return 0;
        double prev = prevValues[row][col];
        double curr = currValues[row][col];
        if (Double.isNaN(prev) || Double.isNaN(curr)) return 0;
        if (curr > prev) return 1;
        if (curr < prev) return -1;
        return 0;
    }

    /** לקרוא אחרי עדכון ערכים באובייקטים (delta/bidAskCounter) כדי “להזיז חלון” */
    public void snapshotAsPrev() {
        this.prevValues = copy2D(this.currValues);
    }
}

// ====== Renderer לצביעה ירוק/אדום ======
class ChangeHighlightRenderer extends DefaultTableCellRenderer {
    private final OptionsTableModel model;

    public ChangeHighlightRenderer(OptionsTableModel model) {
        this.model = model;
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // צבע ברירת מחדל
        if (!isSelected) c.setBackground(Color.WHITE);

        // צובעים רק עמודות שמשתנות בזמן: דלתא/מונה bid-ask (לא סטרייק)
        boolean paintable =
                column == OptionsTableModel.COL_DELTA_CALL ||
                column == OptionsTableModel.COL_BIDASK_CALL ||
                column == OptionsTableModel.COL_BIDASK_PUT ||
                column == OptionsTableModel.COL_DELTA_PUT;

        if (paintable) {
            int dir = model.getChangeDirection(row, column);
            if (!isSelected) {
                if (dir > 0)      c.setBackground(new Color(0xE6F4EA)); // ירקרק
                else if (dir < 0) c.setBackground(new Color(0xFDE7E9)); // אדמדם
                else              c.setBackground(Color.WHITE);
            }
        }
        return c;
    }
}

// ====== דוגמה לשימוש ======
class OptionsTableDemo {
    public static JTable buildTable(Options options) {
        OptionsTableModel model = new OptionsTableModel();
        model.refreshFromOptions(options);

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);

        ChangeHighlightRenderer renderer = new ChangeHighlightRenderer(model);
        for (int c = 0; c < model.getColumnCount(); c++) {
            table.getColumnModel().getColumn(c).setCellRenderer(renderer);
        }
        return table;
    }

    // דוגמת ריענון: לקרוא אותה כל פעם שה־Option-ים התעדכנו
    public static void refresh(JTable table, Options options) {
        OptionsTableModel model = (OptionsTableModel) table.getModel();
        // קודם: לשמור “צילום קודם”
        model.snapshotAsPrev();
        // ואז למלא ערכים נוכחיים
        model.refreshFromOptions(options);
    }

    // דמו מינימלי להרצה
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // נניח שיש לך Options מאוכלס (כולל Strike -> Call/Put עם Delta/BidAskCounter)
            Options options = /* load/build your Options */ null;

            JFrame f = new JFrame("Options Monitor");
            JTable table = buildTable(options);
            f.setContentPane(new JScrollPane(table));
            f.setSize(800, 500);
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setVisible(true);

            // Timer לריענון (למשל כל 500ms)
            new Timer(500, e -> {
                // כאן אתה מעדכן את option.setDelta(...) / setBidAskCounter(...) מבחוץ
                refresh(table, options);
            }).start();
        });
    }
}
