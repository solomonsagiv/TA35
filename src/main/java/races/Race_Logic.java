package races;

import api.BASE_CLIENT_OBJECT;
import locals.L;

public class Race_Logic {

    public enum RACE_RUNNER_ENUM {
        Q1_INDEX(),
        Q1_Q2(),
        WEEK_MONTH(),
        WEEK_INDEX(),
        MONTH_INDEX(),
        LAST_MID();
    }

    RACE_RUNNER_ENUM race_runners;
    private BASE_CLIENT_OBJECT client;

    private static final double MIN_MOVE = 0.0001;

    private boolean R_ONE_UP, R_ONE_DOWN, R_TWO_UP, R_TWO_DOWN = false;
    private double r_one_price = 0, r_two_price = 0;
    private double r_one_price_0 = 0, r_two_price_0 = 0;
    private double r_one_margin, r_two_margin = 0;
    private double RACE_MARGIN;
    public double r_one_up_points, r_one_down_points, r_two_up_points, r_two_down_points = 0;
    private double r_one_points_from_database = 0;
    private double r_two_points_from_database = 0;

    final double r_one_increase_points = 1;
    final double r_two_increase_points = 1;


    public Race_Logic(BASE_CLIENT_OBJECT client, RACE_RUNNER_ENUM race_runners, double RACE_MARGIN) {
        this.client = client;
        this.race_runners = race_runners;
        this.RACE_MARGIN = RACE_MARGIN;
    }

    public void race_finder() {
        first_time_update_data();
        out_of_race_r1();
        in_race_r1();
        update_data();
    }

    private void first_time_update_data() {
        if (r_one_price == 0) {
            reset_races();
        }
    }

    private void in_race_r1() {
        // R_ONE
        if (R_ONE_UP) {
            if (r_one_margin < L.opo(RACE_MARGIN)) {
                R_ONE_UP = false;
                return;
            }
            if (r_two_margin > RACE_MARGIN && has_moved(r_two_margin)) {
                r_one_win_up();
                reset_races();
                return;
            }
        }

        if (R_ONE_DOWN) {
            if (r_one_margin > RACE_MARGIN) {
                R_ONE_DOWN = false;
                return;
            }
            if (r_two_margin < L.opo(RACE_MARGIN) && has_moved(r_two_margin)) {
                r_one_win_down();
                reset_races();
                return;
            }
        }

        // R_TWO
        if (R_TWO_UP) {
            if (r_one_margin > RACE_MARGIN && has_moved(r_one_margin)) {
                r_two_win_up();
                reset_races();
                return;
            }
            if (r_two_margin < L.opo(RACE_MARGIN)) {
                R_TWO_UP = false;
                return;
            }
        }

        if (R_TWO_DOWN) {
            if (r_one_margin < L.opo(RACE_MARGIN) && has_moved(r_one_margin)) {
                r_two_win_down();
                reset_races();
                return;
            }
            if (r_two_margin > RACE_MARGIN) {
                R_TWO_DOWN = false;
                return;
            }
        }
    }

    private void out_of_race_r1() {
        if (!is_in_race()) {
            if (r_one_margin > RACE_MARGIN) {
                R_ONE_UP = true;
                return;
            }
            if (r_one_margin < L.opo(RACE_MARGIN)) {
                R_ONE_DOWN = true;
                return;
            }
            if (r_two_margin > RACE_MARGIN) {
                R_TWO_UP = true;
                return;
            }
            if (r_two_margin < L.opo(RACE_MARGIN)) {
                R_TWO_DOWN = true;
                return;
            }
        }
    }

    public void update_data() {
        r_one_price_0 = r_one_price;
        r_two_price_0 = r_two_price;
        update_runners_price();
        r_one_margin = r_one_price - r_one_price_0;
        r_two_margin = r_two_price - r_two_price_0;
    }

    private void reset_races() {
        R_ONE_UP = false;
        R_ONE_DOWN = false;
        R_TWO_UP = false;
        R_TWO_DOWN = false;
    }
    // Update runners price
    private void update_runners_price() {
        switch (race_runners) {
            case WEEK_INDEX:
                r_one_price = client.getMid();
                r_two_price = client.getExps().getMonth().getOptions().getContract();
                return;
            case WEEK_MONTH:
                r_one_price = client.getExps().getMonth().getOptions().getContract();
                r_two_price = client.getExps().getWeek().getOptions().getContract();
        }
    }

    private boolean has_moved(double margin) {
        return Math.abs(margin) >= MIN_MOVE;
    }

    public double get_r1_minus_r2() {
        return get_r_one_points() - get_r_two_points();
    }
    public double get_sum_points() {
        return get_r_one_points() + get_r_two_points();
    }
    public double get_r_one_points() {
        return r_one_up_points - r_one_down_points;
    }
    public double get_r_two_points() {
        return r_two_up_points - r_two_down_points;
    }
    private boolean is_in_race() {
        return R_ONE_UP || R_ONE_DOWN || R_TWO_UP || R_TWO_DOWN;
    }
    private void r_one_win_up() {
        r_one_up_points += r_one_increase_points;
    }
    private void r_one_win_down() {
        r_one_down_points += r_one_increase_points;
    }
    private void r_two_win_up() {
        r_two_up_points += r_two_increase_points;
    }
    private void r_two_win_down() {
        r_two_down_points += r_two_increase_points;
    }
    public void setR_one_up_points(double r_one_up_points) {
        this.r_one_up_points = r_one_up_points;
    }
    public void setR_one_down_points(double r_one_down_points) {
        this.r_one_down_points = r_one_down_points;
    }
    public void setR_two_up_points(double r_two_up_points) {
        this.r_two_up_points = r_two_up_points;
    }
    public void setR_two_down_points(double r_two_down_points) {
        this.r_two_down_points = r_two_down_points;
    }
    public double getR_one_points_from_database() {
        return r_one_points_from_database;
    }
    public void setR_one_points_from_database(double r_one_points_from_database) {
        this.r_one_points_from_database = r_one_points_from_database;
    }
    public double getR_two_points_from_database() {
        return r_two_points_from_database;
    }
    public void setR_two_points_from_database(double r_two_points_from_database) {
        this.r_two_points_from_database = r_two_points_from_database;
    }

}
