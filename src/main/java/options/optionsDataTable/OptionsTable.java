package options.optionsDataTable;

import api.ApiObject;
import exp.Exp;
import gui.MyGuiComps;
import locals.L;
import locals.Themes;
import options.Option;
import options.Strike;
import threads.MyThread;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class OptionsTable extends MyGuiComps.MyTable {

    public static int call_pricing_col = 0;
    public static int call_open_pos_col = 1;
    public static int call_delta_col = 2;
    public static int strike_col = 3;
    public static int put_delta_col = 4;
    public static int put_pricing_col = 5;
    public static int put_open_pos_col = 6;
    ApiObject apiObject;
    Updater updater;
    Exp exp;

    public OptionsTable(Object[][] data, Object[] headers, Exp exp) {
        super(data, headers);
        apiObject = ApiObject.getInstance();
        this.exp = exp;
        header_style();
        start();
    }

    public void start() {
        updater = new Updater();
        updater.getHandler().start();
    }

    public void close() {
        updater.getHandler().close();
    }

    @Override
    protected void cell_change_listener(Component c, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        // Not strike
        if (col != strike_col) {
            color_forf(c, value);
            c.setBackground(Color.WHITE);
        }

        // Strike
        if (col == strike_col) {
            strike(c);
        }

        // Row % 2
        if (row % 2 == 0) {
            c.setBackground(Themes.GREY_LIGHT);
        } else {
            c.setBackground(Color.WHITE);
        }
    }

    private void header_style() {
        JTableHeader header = getTableHeader();
        header.setOpaque(false);
        header.setBackground(Themes.BLUE);
        header.setForeground(Themes.GREY_VERY_LIGHT);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    private void strike(Component c) {
        c.setForeground(Color.BLACK);
    }

    private void color_forf(Component c, Object value) {
        double val = L.dbl(value.toString());
        if (val >= 0) {
            c.setForeground(Themes.GREEN);
        } else {
            c.setForeground(Themes.RED);
        }
    }

    private class Updater extends MyThread implements Runnable {

        @Override
        public void run() {

            while (isRun()) {
                try {
                    // Sleep
                    Thread.sleep(1000);

                    // Update
                    update_data();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void update_data() {
            for (int i = 0; i < getRowCount(); i++) {
                try {
                    double strike_price = (double) getValueAt(i, strike_col);
                    Strike strike = exp.getOptions().getStrike(strike_price);
                    Option call = strike.getCall();
                    Option put = strike.getPut();

                    // Set text
                    setValueAt(call.getOpen_pos(), i, call_open_pos_col);
                    setValueAt(call.getPricing(), i, call_pricing_col);
                    setValueAt(call.getDelta(), i, call_delta_col);
                    setValueAt(put.getDelta(), i, put_delta_col);
                    setValueAt(put.getOpen_pos(), i, put_open_pos_col);
                    setValueAt(put.getPricing(), i, put_pricing_col);
                } catch (Exception e) {
                }
            }
        }

        @Override
        public void initRunnable() {
            setRunnable(this);
        }
    }

}


