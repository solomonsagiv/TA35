package counter;

import api.BASE_CLIENT_OBJECT;
import gui.MyGuiComps;
import miniStocks.MiniStock;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MiniStockTable extends MyGuiComps.MyFrame {

    static Thread runner;
    static boolean run = true;
    static DefaultTableModel model;
    static final DecimalFormat DF = new DecimalFormat("0.00");

    JTextField customField1 = new JTextField(10);
    JTextField customField2 = new JTextField(10);

    public MiniStockTable(BASE_CLIENT_OBJECT client, String title) throws HeadlessException {
        super(client, title);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initialize() {

    }

    private static String transliterateName(String hebrewName) {
        hebrewName = hebrewName.trim().replaceAll("[\\u200F\\u200E\\u202A-\\u202E]", "");
        switch (hebrewName) {
            case "שופרסל": return "Shufersal";
            case "טבע": return "Teva";
            case "בזק": return "Bezeq";
            case "לאומי": return "Leumi";
            case "פועלים": return "Poalim";
            case "כלל עסקי ביטוח": return "Clal Insurance";
            case "מבנה": return "Mivne";
            case "נייס": return "Nice";
            case "איי.סי.אל": return "ICL";
            case "מליסרון": return "Melisron";
            case "ניו-מד אנרג יהש": return "NewMed Energy";
            case "מנורה מב החז": return "Menora Mivtachim";
            case "חברה לישראל": return "Israel Corp";
            case "הראל השקעות": return "Harel Investments";
            case "בינלאומי": return "Bank International";
            case "דיסקונט": return "Discount";
            case "מזרחי טפחות": return "Mizrahi Tefahot";
            case "אנלייט אנרגיה": return "Enlight Energy";
            case "שטראוס": return "Strauss";
            case "הפניקס": return "Phoenix";
            case "אלביט מערכות": return "Elbit Systems";
            case "טאואר": return "Tower";
            case "דלק קבוצה": return "Delek Group";
            case "נובה": return "Nova";
            case "דמרי": return "Dimri";
            case "קמטק": return "Camtek";
            case "ביג": return "BIG";
            case "אמות": return "Amot";
            case "עזריאלי קבוצה": return "Azrieli Group";
            case "שפיר הנדסה": return "Shapir Engineering";
            case "אורמת טכנו": return "Ormat Technologies";
            case "או פי סי אנרגיה": return "OPC Energy";
            case "נאוויטס פטר יהש": return "Navitas Petroleum";
            case "פתאל החזקות": return "Fattal Holdings";
            case "אנרג'יאן": return "Energean";
            default: return hebrewName;
        }
    }

    @Override
    public void onClose() {
        run = false;
        if (runner != null) runner.interrupt();
        super.onClose();
    }

    public static void showTable(List<MiniStock> stocks) {
        stocks.sort(Comparator.comparingDouble(MiniStock::getWeight).reversed());
        String[] columns = {"Name", "Open %", "Last %", "Last-Open %", "Bid/Ask Counter", "Weight"};
        model = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int r, int c) { return false; } };

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(22);
        table.setIntercellSpacing(new Dimension(5, 2));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 15));

        table.getColumnModel().getColumn(0).setCellRenderer(new BoldCenterRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new OpenColorRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new LastColorRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new LastColorRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new CounterColorRenderer());

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Custom 1:"));
        controlPanel.add(customField1);
        controlPanel.add(new JLabel("Custom 2:"));
        controlPanel.add(customField2);

        JFrame frame = new JFrame("Stock Table");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setSize(850, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        refreshModel(stocks);
        start_runner(stocks);
    }

    private static void refreshModel(List<MiniStock> stocks) {
        List<MiniStock> snapshot = new ArrayList<>(stocks);
        snapshot.sort(Comparator.comparingDouble(MiniStock::getWeight).reversed());

        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            for (MiniStock s : snapshot) {
                Object[] row = new Object[6];
                row[0] = transliterateName(s.getName());
                if (s.getBase() != 0) {
                    double openPct = ((s.getOpen() - s.getBase()) / s.getBase()) * 100;
                    double lastPct = ((s.getLast() - s.getBase()) / s.getBase()) * 100;
                    double diffPct = lastPct - openPct;
                    row[1] = formatWithArrow(openPct, DF);
                    row[2] = formatWithArrow(lastPct, DF);
                    row[3] = formatWithArrow(diffPct, DF);
                } else {
                    row[1] = row[2] = row[3] = "-";
                }
                row[4] = s.getBid_ask_counter();
                row[5] = DF.format(s.getWeight());
                model.addRow(row);
            }
        });
    }

    private static void start_runner(List<MiniStock> stocks) {
        runner = new Thread(() -> {
            while (run) {
                try { Thread.sleep(30000); } catch (InterruptedException e) { break; }
                refreshModel(stocks);
            }
        }, "MiniStockTable-Refresher");
        runner.setDaemon(true);
        runner.start();
    }

    private static String formatWithArrow(double value, DecimalFormat df) {
        String arrow = value > 0 ? " ↑" : value < 0 ? " ↓" : "";
        return df.format(value) + "%" + arrow;
    }

    static class BoldCenterRenderer extends DefaultTableCellRenderer {
        public BoldCenterRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
            comp.setFont(new Font("Arial", Font.BOLD, 13));
            return comp;
        }
    }

    static class OpenColorRenderer extends DefaultTableCellRenderer {
        public OpenColorRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
            try {
                String str = (v == null) ? "0" : v.toString();
                int p = str.indexOf('%');
                double val = (p > 0) ? Double.parseDouble(str.substring(0, p)) : Double.parseDouble(str);
                comp.setForeground(val > 0 ? Color.GREEN.darker() : (val < 0 ? Color.RED : Color.BLACK));
            } catch (Exception e) {
                comp.setForeground(Color.BLACK);
            }
            return comp;
        }
    }

    static class LastColorRenderer extends OpenColorRenderer { }

    static class CounterColorRenderer extends DefaultTableCellRenderer {
        public CounterColorRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
            try {
                int val = Integer.parseInt(v.toString());
                if (val > 0) comp.setForeground(Color.GREEN.darker());
                else if (val < 0) comp.setForeground(Color.RED);
                else comp.setForeground(Color.BLACK);
            } catch (Exception e) {
                comp.setForeground(Color.BLACK);
            }
            return comp;
        }
    }
}
