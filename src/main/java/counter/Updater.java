package counter;

import api.ApiObject;
import exp.Exp;
import locals.L;
import locals.Themes;
import options.Options;
import threads.MyThread;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;

public class Updater extends MyThread implements Runnable {

    ApiObject apiObject = ApiObject.getInstance();

    // local variables
    int count = 0;

    LocalTime current_time;

    // Avg list
    public ArrayList<Double> avg_day = new ArrayList<>();

    // test
    double efresh;
    double text;
    boolean run = true;

    Color lightGreen = new Color(12, 135, 0);
    Color lightRed = new Color(229, 19, 0);

    WindowTA35 window;
    int sleep = 500;

    // Constructor
    public Updater(WindowTA35 window) {
        super();
        this.window = window;
        setRunnable(this);
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
        Options optionsMonth = expMonth.getOptions();
        Options optionsWeek = expWeek.getOptions();

        try {
            count++;
            current_time = LocalTime.now();

            // OP
            efresh = expMonth.getOptions().getContract() - apiObject.getIndex();
            text = floor(efresh, 100);

            // After start trading
            if (apiObject.isStarted()) {
                // AVG OP
                text = floor(avg(), 10);
                setColor(window.op_avg, text, lightGreen);

                // Baskets
                window.basket_up_field.setText(str(apiObject.getBasketUp()));
                window.basket_down_field.setText(str(apiObject.getBasketDown()));
                setColorInt(window.basketsSumField, (apiObject.getBasketUp() - apiObject.getBasketDown()));

                // Delta calc
                // Month
                colorForge(window.monthDeltaField, (int) optionsMonth.getTotal_delta(), L.df());
                // Week
                colorForge(window.weekDeltaField, (int) optionsWeek.getTotal_delta(), L.df());

                // Ind baskets
                double indDelta = apiObject.getStocksHandler().getDelta();
                int baskets = apiObject.getBasketUp() - apiObject.getBasketDown();
                double indeDeltaNoBakets = indDelta + (baskets * -1000);

                // Ind delta no baskets
                colorForge(window.indDeltaNoBasketsField, (int) indeDeltaNoBakets, L.df());

                // Decision func
                window.v5_field.colorForge(apiObject.getV5());
                window.v6_field.colorForge(apiObject.getV6());
                window.v5_speed_field.colorForge(apiObject.getV5_speed_300());
                window.v6_speed_field.colorForge(apiObject.getV6_speed_300());

                // Exp
                // Week
                colorForge(window.expDeltaWeekField, (int) expWeek.getExpData().getTotalDelta(), L.df());
                colorForge(window.expIndDeltaWeekField, (int) expWeek.getExpData().getTotalIndDelta(), L.df());
                colorForge(window.expBasketsWeekField, expWeek.getExpData().getTotalBaskets(), L.df());
                text = floor(
                        ((apiObject.getIndex() - expWeek.getExpData().getStart()) / expWeek.getExpData().getStart())
                                * 100,
                        100);
                setColorPresent(window.weekStartExpField, text);

                // Month
                colorForge(window.expDeltaMonthField, (int) expMonth.getExpData().getTotalDelta(), L.df());
                colorForge(window.expIndDeltaMonthField, (int) expMonth.getExpData().getTotalIndDelta(), L.df());
                colorForge(window.expBasketsMonthField, expMonth.getExpData().getTotalBaskets(), L.df());
                text = floor(
                        ((apiObject.getIndex() - expMonth.getExpData().getStart()) / expMonth.getExpData().getStart())
                                * 100,
                        100);
                setColorPresent(window.monthStartExpField, text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        run = false;
    }

    private double avg() {
        efresh = apiObject.getExps().getMonth().getOptions().getContract() - apiObject.getIndex();
        avg_day.add(efresh);
        double f = 0;
        for (int i = 0; i < avg_day.size(); i++) {
            f += avg_day.get(i);
        }
        return f / avg_day.size();
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
