package counter;

import api.TA35;
import charts.myChart.MyTimeSeries;
import dataBase.Factories;
import exp.Exp;
import locals.L;
import locals.Themes;
import races.Race_Logic;
import threads.MyThread;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalTime;

public class Updater extends MyThread implements Runnable {
    
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
        Exp expWeek = client.getExps().getWeek();

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

                // Exp
                // Week
                colorForgeRound(window.exp_v4_old_week_field, (int) (expWeek.getExpData().getV4() + df_4.getValue()), true);
                colorForgeRound(window.exp_v8_old_week_field, (int) (expWeek.getExpData().getV8() + df_8.getValue()), true);
                colorForgeRound(window.expBasketsWeekField, expWeek.getExpData().getTotalBaskets(), false);
                text = floor(((client.getIndex() - expWeek.getExpData().getStart()) / expWeek.getExpData().getStart()) * 100, 100);
                setColorPresent(window.weekStartExpField, text);
                window.optimi_count_week_field.setText(str(expWeek.getOptimi_count()));
                window.pesimi_count_week_field.setText(str(expWeek.getPesimi_count()));
                window.roll_optimi_count_week_field.setText(str(expWeek.getRoll_optimi_count()));
                window.roll_pesimi_count_week_field.setText(str(expWeek.getRoll_pesimi_count()));

                // Month
                colorForgeRound(window.exp_v5_month_field, (int) (expMonth.getExpData().getV5() + df_5.getValue()), true);
                colorForgeRound(window.exp_v6_month_field, (int) (expMonth.getExpData().getV6() + df_6.getValue()), true);
                colorForgeRound(window.expBasketsMonthField, expMonth.getExpData().getTotalBaskets(), false);
                text = floor(((client.getIndex() - expMonth.getExpData().getStart()) / expMonth.getExpData().getStart()) * 100, 100);
                setColorPresent(window.monthStartExpField, text);
                window.optimi_count_month_field.setText(str(expMonth.getOptimi_count()));
                window.pesimi_count_month_field.setText(str(expMonth.getPesimi_count()));
                window.roll_optimi_count_month_field.setText(str(expMonth.getRoll_optimi_count()));
                window.roll_pesimi_count_month_field.setText(str(expMonth.getRoll_pesimi_count()));

                // Races
                colorForgeRound(window.index_races_iw_field, (int) client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX).get_r_one_points(), false);
                colorForgeRound(window.week_races_iw_field, (int) client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX).get_r_two_points(), false);
                colorForgeRound(window.month_race_wm_field, (int) client.getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_one_points(), false);
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
