package counter;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import api.deltaTest.Calculator;
import gui.MyGuiComps;
import miniStocks.MiniStock;
import stocksHandler.StocksHandler;

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

    static MyGuiComps.MyTextField
            number_of_positive_stocks_field,
            weight_of_positive_stocks_field,
            weighted_counter_field,
            green_stocks_field;

    public MiniStockTable(BASE_CLIENT_OBJECT client, String title) throws HeadlessException {
        super(client, title);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initialize() {
        showTable(((TA35) client).getStocksHandler().getStocks());
    }

    @Override
    public void onClose() {
        run = false;
        if (runner != null) runner.interrupt();
        super.onClose();
    }

    private void showTable(List<MiniStock> stocks) {
        stocks.sort(Comparator.comparingDouble(MiniStock::getWeight).reversed());
        String[] columns = {"Name", "Open", "Last", "Change", "Counter", "Weight"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

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
        table.getColumnModel().getColumn(5).setCellRenderer(new WeightRenderer());

        number_of_positive_stocks_field = new MyGuiComps.MyTextField();
        weight_of_positive_stocks_field = new MyGuiComps.MyTextField();
        weighted_counter_field = new MyGuiComps.MyTextField();
        green_stocks_field = new MyGuiComps.MyTextField();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 4, 15, 0)); // 1 שורה, 4 עמודות
        controlPanel.add(createColumn("Positive counter :", number_of_positive_stocks_field));
        controlPanel.add(createColumn("Total weight:", weight_of_positive_stocks_field));
        controlPanel.add(createColumn("Weighted counter:", weighted_counter_field));
        controlPanel.add(createColumn("Green stocks:", green_stocks_field));

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
                row[0] = s.getName();
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

            number_of_positive_stocks_field.colorForge(Calculator.get_stocks_positive_count());
            weight_of_positive_stocks_field.colorForge(Calculator.get_stocks_positive_weight_count());
            green_stocks_field.colorForge(Calculator.get_green_stocks());
            weighted_counter_field.colorForge((int) Calculator.calculateWeightedCounters()[0]);
        });
    }

    private static void start_runner(List<MiniStock> stocks) {
        runner = new Thread(() -> {
            while (run) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    break;
                }
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
        public BoldCenterRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
            comp.setFont(new Font("Arial", Font.BOLD, 13));
            return comp;
        }
    }

    static class OpenColorRenderer extends DefaultTableCellRenderer {
        public OpenColorRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

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

    static class WeightRenderer extends DefaultTableCellRenderer {
        public WeightRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
            try {
                comp.setForeground(Color.BLACK);
            } catch (Exception e) {
                comp.setForeground(Color.BLACK);
            }
            return comp;
        }
    }

    static class LastColorRenderer extends OpenColorRenderer {
    }

    static class CounterColorRenderer extends DefaultTableCellRenderer {
        public CounterColorRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

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


    private static JPanel createColumn(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);      // כותרת למעלה
        panel.add(textField, BorderLayout.CENTER); // השדה מתחת
        return panel;
    }


    public static void main(String[] args) {
        ArrayList<MiniStock> stocks = new ArrayList<>();
        MiniStock poli = new MiniStock(new StocksHandler(), 1);
        MiniStock bezeq = new MiniStock(new StocksHandler(), 2);
        MiniStock lumi = new MiniStock(new StocksHandler(), 3);
        MiniStock nice = new MiniStock(new StocksHandler(), 4);

        stocks.add(poli);
        stocks.add(bezeq);
        stocks.add(lumi);
        stocks.add(nice);

        TA35 client = TA35.getInstance();
        client.getStocksHandler().setStocks(stocks);

        new MiniStockTable(client, "Mini");
    }

}