package counter;

import miniStocks.MiniStock;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

public class MiniStockTable {

    public static void showTable(List<MiniStock> stocks) {
        // Sort by weight descending
        stocks.sort(Comparator.comparingDouble(MiniStock::getWeight).reversed());

        String[] columns = {"Name", "Open %", "Last %", "Bid/Ask Counter", "Weight"};
        Object[][] data = new Object[stocks.size()][5];
        DecimalFormat df = new DecimalFormat("0.00");

        for (int i = 0; i < stocks.size(); i++) {
            MiniStock s = stocks.get(i);
            data[i][0] = s.getName();

            if (s.getBase() != 0) {
                double openPct = ((s.getOpen() - s.getBase()) / s.getBase()) * 100;
                double lastPct = ((s.getLast() - s.getBase()) / s.getBase()) * 100;
                data[i][1] = formatWithArrow(openPct, df);
                data[i][2] = formatWithArrow(lastPct, df);
            } else {
                data[i][1] = "-";
                data[i][2] = "-";
            }

            data[i][3] = s.getBid_ask_counter();
            data[i][4] = df.format(s.getWeight());
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(22);
        table.setIntercellSpacing(new Dimension(5, 2));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(new Color(220, 235, 255));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setCellRenderer(new BoldCenterRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new OpenColorRenderer(stocks));
        table.getColumnModel().getColumn(2).setCellRenderer(new LastColorRenderer(stocks));
        table.getColumnModel().getColumn(3).setCellRenderer(new CounterColorRenderer());

        JFrame frame = new JFrame("MiniStocks Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(table));
        frame.setSize(750, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String formatWithArrow(double value, DecimalFormat df) {
        String arrow = value > 0 ? " ↑" : value < 0 ? " ↓" : "";
        return df.format(value) + "%" + arrow;
    }

    // ---- Renderers ----

    static class BoldCenterRenderer extends DefaultTableCellRenderer {
        public BoldCenterRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(new Font("SansSerif", Font.BOLD, 13));
            return c;
        }
    }

    static class OpenColorRenderer extends DefaultTableCellRenderer {
        List<MiniStock> stocks;

        OpenColorRenderer(List<MiniStock> stocks) {
            this.stocks = stocks;
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            MiniStock s = stocks.get(row);
            c.setForeground(s.getOpen() > s.getBase() ? Color.GREEN.darker() : Color.RED);
            return c;
        }
    }

    static class LastColorRenderer extends DefaultTableCellRenderer {
        List<MiniStock> stocks;

        LastColorRenderer(List<MiniStock> stocks) {
            this.stocks = stocks;
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            MiniStock s = stocks.get(row);
            c.setForeground(s.getLast() > s.getOpen() ? Color.GREEN.darker() : Color.RED);
            return c;
        }
    }

    static class CounterColorRenderer extends DefaultTableCellRenderer {
        public CounterColorRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int val;
            try {
                val = Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                val = 0;
            }
            if (val > 0) c.setForeground(Color.GREEN.darker());
            else if (val < 0) c.setForeground(Color.RED);
            else c.setForeground(Color.BLACK);
            return c;
        }
    }
}
