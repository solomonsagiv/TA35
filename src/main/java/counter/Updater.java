package counter;

import api.TA35;
import charts.myChart.MyTimeSeries;
import dataBase.Factories;
import exp.Exp;
import locals.L;
import locals.Themes;
import threads.MyThread;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalTime;

public class Updater extends MyThread implements Runnable {

    // local variables
    int count = 0;
    private static final DecimalFormat DF_2 = new DecimalFormat("0.00");

    LocalTime current_time;

    // test
    double efresh;
    double text;
    boolean run = true;

    // Use Themes colors instead of hardcoded colors

    WindowTA35 window;
    int sleep = 500;

    MyTimeSeries df_4, df_5, df_6, df_8;

    TA35 client;

    // Constructor
    public Updater(WindowTA35 window) {
        super();
        this.client = api.TA35.getInstance();
        this.window = window;
        setRunnable(this);

        this.df_4 = client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_4_CDF_OLD);
        this.df_5 = client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_5_CDF_OLD);
        this.df_6 = client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_6_CDF_OLD);
        this.df_8 = client.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF_OLD);
    }

    @Override
    public void run() {
        while (run) {
            try {
                // Write the data to the window
                write();
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                run = false;
                System.out.println("Updater is stopped ");
            }
        }
    }

    // Write the data to the window
    private void write() throws InterruptedException {

        Exp expMonth = client.getExps().getMonth();

        try {
            count++;
            current_time = LocalTime.now();

            // OP
            efresh = expMonth.getOptions().getContract() - client.getIndex();
            text = floor(efresh, 100);

            // After start trading
            if (client.isStarted()) {
                int basket_up = client.getBasketFinder_by_stocks().getBasket_up();
                int basket_down = client.getBasketFinder_by_stocks().getBasket_down();

                // Baskets
                window.basket_up_field.setText(str(basket_up));
                window.basket_down_field.setText(str(basket_down));
                setColorInt(window.basketsSumField, (basket_up - basket_down));

                // Decision func
                window.v5_field.colorForgeRound(df_5.getValue());
                window.v6_field.colorForgeRound(df_6.getValue());
                window.v2_field.colorForgeRound(df_4.getValue());
                window.v7_field.colorForgeRound(df_8.getValue());

                // Races
                colorForgeRound(window.index_races_iw_field, (int) client.get_index_races_iw(), false);
                colorForgeRound(window.future_week_counter_field, client.get_week_bid_ask_counter(), false);
                colorForgeRound(window.future_month_counter_field, client.get_month_bid_ask_counter(), false);
                colorForgeRound(window.weight_counter2_field, (int) client.getCounter2_weight(), false);
                colorForgeRound(window.basket_field, (int) client.getCounter2_table_avg(), false);
                colorForgeRound(window.counter2_table_avg_field, (int) client.getCounter2_table_avg(), false);
                
                // O/P fields - op_avg values from WeekExp (formatted to 2 decimals)
                window.op_avg_field.colorForge(client.getExps().getWeek().getOp_avg(), DF_2);
                window.op_avg_60_field.colorForge(client.getExps().getWeek().getOp_avg_60(), DF_2);
                window.op_avg_15_field.colorForge(client.getExps().getWeek().getOp_avg_15(), DF_2);
                
                // Reset fields - Value changes since op_avg_60 crossed zero
                colorForgeRound(window.ind_race_reset_field, (int) client.getIndex_races_iw_change_since_op_avg_60_cross(), false);
                colorForgeRound(window.future_week_counter_reset_field, client.getWeek_bid_ask_counter_change_since_op_avg_60_cross(), false);
                colorForgeRound(window.future_month_counter_reset_field, client.getMonth_bid_ask_counter_change_since_op_avg_60_cross(), false);
                colorForgeRound(window.basket_reset_field, (int) client.getCounter2_table_avg_change_since_op_avg_60_cross(), false);
                colorForgeRound(window.counter2_table_avg_reset_field, (int) client.getCounter2_table_avg_change_since_op_avg_60_cross(), false);
                
                // Reset fields - Value changes since op_avg_15 crossed zero
                colorForgeRound(window.ind_race_reset_15_field, (int) client.getIndex_races_iw_change_since_op_avg_15_cross(), false);
                colorForgeRound(window.future_week_counter_reset_15_field, client.getWeek_bid_ask_counter_change_since_op_avg_15_cross(), false);
                colorForgeRound(window.future_month_counter_reset_15_field, client.getMonth_bid_ask_counter_change_since_op_avg_15_cross(), false);
                colorForgeRound(window.weight_counter2_reset_15_field, client.getWeight_counter2_change_since_op_avg_15_cross(), false);
                colorForgeRound(window.basket_reset_15_field, (int) client.getCounter2_table_avg_change_since_op_avg_15_cross(), false);
                colorForgeRound(window.counter2_table_avg_reset_15_field, (int) client.getCounter2_table_avg_change_since_op_avg_15_cross(), false);
                // Stocks count present
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        run = false;
    }

    // color setting function();
    public void setColor(JTextField textField, double text, Color color) {

        if (text >= 0.0) {
            textField.setForeground(color);
            textField.setText(String.valueOf(text));
        } else {
            textField.setForeground(Color.red);
            textField.setText(String.valueOf(text));
        }
    }

    // color setting function();
    public void setColorInt(JTextField textField, int text) {
        if (text >= 0.0) {
            textField.setForeground(Themes.getGreenStatusColor());
            textField.setText(String.valueOf(text));
        } else {
            textField.setForeground(Themes.getRedStatusColor());
            textField.setText(String.valueOf(text));
        }
    }

    // color setting function();
    public void setColorPresent(JTextField textField, double text) {

        if (text >= 0.0) {
            textField.setBackground(Themes.getGreenStatusColor());
            textField.setText(String.valueOf(text) + "% ");
        } else {
            textField.setBackground(Themes.getRedStatusColor());
            textField.setText(String.valueOf(text) + "% ");
        }
    }

    public void colorForge(JTextField textField, int val, DecimalFormat format) {
        if (val >= 0) {
            textField.setForeground(Themes.GREEN);
        } else {
            textField.setForeground(Themes.RED);
        }

        textField.setText(format.format(val));
    }


    public void colorForgeRound(JTextField textField, int val, boolean divide_1000) {

        if (divide_1000) {
            val /= 1000;
        }

        if (val >= 0) {
            textField.setForeground(Themes.GREEN);
        } else {
            textField.setForeground(Themes.RED);
        }
        textField.setText(L.str((int) val));
    }

    // pars double function();
    public double dbl(String string) {
        return Double.parseDouble(string);
    }

    // floor function();
    public double floor(double d, int zeros) {
        return Math.floor(d * zeros) / zeros;
    }

    // To string
    public String str(Object o) {
        return String.valueOf(o);
    }

    @Override
    public void initRunnable() {
        setRunnable(this);
    }
}
