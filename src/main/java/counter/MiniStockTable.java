package counter;

import miniStocks.MiniStock;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MiniStockTable {

    public static void showTable(List<MiniStock> stocks) {
        String[] columns = {"Name", "Open", "Last", "Bid/Ask Counter"};
        Object[][] data = new Object[stocks.size()][4];

        for (int i = 0; i < stocks.size(); i++) {
            MiniStock s = stocks.get(i);
            data[i][0] = s.getName();
            data[i][1] = s.getOpen();
            data[i][2] = s.getLast();
            data[i][3] = s.getBid_ask_counter();
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int column) {
                return false; // נעול לעריכה
            }
        };

        JTable table = new JTable(model);

        // עיצוב מותאם אישית לכל עמודה
        table.getColumnModel().getColumn(0).setCellRenderer(new BoldCellRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new OpenColorRenderer(stocks));
        table.getColumnModel().getColumn(2).setCellRenderer(new LastColorRenderer(stocks));
        table.getColumnModel().getColumn(3).setCellRenderer(new CounterColorRenderer());

        JFrame frame = new JFrame("MiniStocks Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(table));
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    // ---- Renderers ----

    static class BoldCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(c.getFont().deriveFont(Font.BOLD));
            return c;
        }
    }

    static class OpenColorRenderer extends DefaultTableCellRenderer {
        List<MiniStock> stocks;

        OpenColorRenderer(List<MiniStock> stocks) {
            this.stocks = stocks;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            MiniStock s = stocks.get(row);
            c.setForeground(s.getOpen() > s.getBase() ? Color.GREEN : Color.RED);
            return c;
        }
    }

    static class LastColorRenderer extends DefaultTableCellRenderer {
        List<MiniStock> stocks;

        LastColorRenderer(List<MiniStock> stocks) {
            this.stocks = stocks;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            MiniStock s = stocks.get(row);
            c.setForeground(s.getLast() > s.getOpen() ? Color.GREEN : Color.RED);
            return c;
        }
    }

    static class CounterColorRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int val = Integer.parseInt(value.toString());
            c.setForeground(val > 0 ? Color.GREEN : (val < 0 ? Color.RED : Color.BLACK));
            return c;
        }
    }
}
