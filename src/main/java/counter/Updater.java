package counter;

import api.ApiObject;
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

    ApiObject apiObject = ApiObject.getInstance();

    // local variables
    int count = 0;

    LocalTime current_time;

    // test
    double efresh;
    double text;
    boolean run = true;

    Color lightGreen = new Color(12, 135, 0);
    Color lightRed = new Color(229, 19, 0);

    WindowTA35 window;
    int sleep = 500;

    MyTimeSeries df_4, df_5, df_6, df_8, df_2, df_7, df_8_de_corr;


    // Constructor
    public Updater(WindowTA35 window) {
        super();
        this.window = window;
        setRunnable(this);

        this.df_2 = apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_2_CDF);
        this.df_7 = apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_7_CDF);
        this.df_8_de_corr = apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF);

        this.df_4 = apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_4_CDF_OLD);
        this.df_5 = apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_5_CDF_OLD);
        this.df_6 = apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_6_CDF_OLD);
        this.df_8 = apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF_OLD);
        this.df_8_de_corr = apiObject.getTimeSeriesHandler().get(Factories.TimeSeries.DF_8_CDF);
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

        Exp expMonth = apiObject.getExps().getMonth();
        Exp expWeek = apiObject.getExps().getWeek();

        try {
            count++;
            current_time = LocalTime.now();

            // OP
            efresh = expMonth.getOptions().getContract() - apiObject.getIndex();
            text = floor(efresh, 100);

            // After start trading
            if (apiObject.isStarted()) {

                // Baskets
                window.basket_up_field.setText(str(apiObject.getBasketUp()));
                window.basket_down_field.setText(str(apiObject.getBasketDown()));
                setColorInt(window.basketsSumField, (apiObject.getBasketUp() - apiObject.getBasketDown()));

                // Decision func
                window.v5_field.colorForgeRound(df_5.getValue());
                window.v6_field.colorForgeRound(df_6.getValue());
                window.v2_field.colorForgeRound(df_4.getValue());
                window.v7_field.colorForgeRound(df_8.getValue());

                // Exp
                // Week
                colorForgeRound(window.exp_v2_week_field, (int) (expWeek.getExpData().getV2() + df_2.getValue()), true);
                colorForgeRound(window.exp_v7_week_field, (int) (expWeek.getExpData().getV7() + df_7.getValue()), true);
                colorForgeRound(window.exp_v8_week_field, (int) (expWeek.getExpData().getV8_de_corr() + df_8_de_corr.getValue()), true);
                colorForgeRound(window.expBasketsWeekField, expWeek.getExpData().getTotalBaskets(), false);
                text = floor(((apiObject.getIndex() - expWeek.getExpData().getStart()) / expWeek.getExpData().getStart()) * 100, 100);
                setColorPresent(window.weekStartExpField, text);

                // Month
                colorForgeRound(window.exp_v2_month_field, (int) (expMonth.getExpData().getV2() + df_2.getValue()), true);
                colorForgeRound(window.exp_v7_month_field, (int) (expMonth.getExpData().getV7() + df_7.getValue()), true);
                colorForgeRound(window.exp_v8_month_field, (int) (expMonth.getExpData().getV8_de_corr() + df_8_de_corr.getValue()), true);
                colorForgeRound(window.expBasketsMonthField, expMonth.getExpData().getTotalBaskets(), false);
                text = floor(((apiObject.getIndex() - expMonth.getExpData().getStart()) / expMonth.getExpData().getStart()) * 100, 100);
                setColorPresent(window.monthStartExpField, text);
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
            textField.setForeground(lightGreen);
            textField.setText(String.valueOf(text));
        } else {
            textField.setForeground(lightRed);
            textField.setText(String.valueOf(text));
        }
    }

    // color setting function();
    public void setColorPresent(JTextField textField, double text) {

        if (text >= 0.0) {
            textField.setBackground(lightGreen);
            textField.setText(String.valueOf(text) + "% ");
        } else {
            textField.setBackground(lightRed);
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
