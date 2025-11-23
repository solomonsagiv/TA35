package api.deltaTest;

import api.TA35;
import locals.L;
import miniStocks.MiniStock;
import options.Option;
import stocksHandler.StocksHandler;

import java.util.ArrayList;
import java.util.List;

public class Calculator {


    public static final int BA_NUMBER_POSITIVE_STOCKS = 0,
            BA_WEIGHT_POSITIVE_STOCKS = 1,
            GREEN_STOCKS = 2,
            DELTA_WEIGHT_POSITIVE_STOCKS = 3,
            TOTAL_DELTA = 4,
            TOTAL_UP_WITH_SHORT_DELTA = 5,
            TOTAL_DOWN_WITH_LONG_DELTA = 6,
            COUNTER_2_WEIGHT_POSITIVE = 7;

    public static final int SOFT_PLUS = 0, 
                            SOFT_MINUS = 1;

    public static double calc(Option option, int newLast, int newVolume, double newDelta) {

        // Volume check
        if (newVolume > option.getVolume()) {

            double quantity = newVolume - option.getVolume();
            double delta = 0;

            // Buy ( Last == pre ask )
            if (newLast == option.getAsk()) {
                delta = quantity * newDelta;
            }

            // Buy ( Last == pre bid )
            if (newLast == option.getBid()) {
                delta = quantity * newDelta * -1;
            }

            // Append delta
            return delta;
        }
        return 0;
    }

    public static double get_stocks_ba_counter_hourly() {
        List<MiniStock> snapshot = new ArrayList<>(TA35.getInstance().getStocksHandler().getStocks());
        double counter_hourly = 0.0;
        for (MiniStock s : snapshot) {

            if (s.getCounterHourlyDelta() > 0) {
                counter_hourly += s.getWeight();
            }
        }
        return counter_hourly;
    }

    public static double get_stocks_delta_counter_hourly() {
        List<MiniStock> snapshot = new ArrayList<>(TA35.getInstance().getStocksHandler().getStocks());
        double delta_counter_hourly = 0.0;
        for (MiniStock s : snapshot) {
            if (s.getDeltaCounterHourlyDelta() > 0) {
                delta_counter_hourly += s.getWeight();
            }
        }
        return delta_counter_hourly;
    }

    public static double get_stocks_counter_2_hourly() {
        List<MiniStock> snapshot = new ArrayList<>(TA35.getInstance().getStocksHandler().getStocks());
        double counter_2_hourly = 0.0;
        for (MiniStock s : snapshot) {
            if (s.getCounter2HourlyDelta() > 0) {
                counter_2_hourly += s.getWeight();
            }
        }
        return counter_2_hourly;
    }
    
    public static double[] get_midle_stocks_ba_counter() {
        List<MiniStock> snapshot = new ArrayList<>(TA35.getInstance().getStocksHandler().getStocks());
        double soft_plus = 0.0,
                soft_minus = 0.0;
        for (MiniStock s : snapshot) {
            if (s.getCounter_2() < 0 && s.getCounter_2() > -10) {
                soft_minus += s.getWeight();
            }
            if (s.getCounter_2() > 0 && s.getCounter_2() < 10) {
                soft_plus += s.getWeight();
            }
        }
        return new double[]{soft_plus, soft_minus};
    }

    public static double[] calculateWeightedCounters() {

        TA35 client = TA35.getInstance();

        List<MiniStock> snapshot = new ArrayList<>(client.getStocksHandler().getStocks());

        double weightedSum = 0.0;
        double totalWeight = 0.0;
        double maxAbsCounter = 0.0;

        // שלב 1: מציאת הערך המוחלט המקסימלי
        for (MiniStock s : snapshot) {
            maxAbsCounter = Math.max(maxAbsCounter, Math.abs(s.getBid_ask_counter()));
        }

        // שלב 2: חישוב weighted רגיל ומנורמל
        double weightedSumNormalized = 0.0;
        for (MiniStock s : snapshot) {
            weightedSum += s.getWeight() * s.getBid_ask_counter();
            if (maxAbsCounter != 0) {
                double normalizedCounter = s.getBid_ask_counter() / maxAbsCounter;
                weightedSumNormalized += s.getWeight() * normalizedCounter;
            }
            totalWeight += s.getWeight();
        }

        double weighted = totalWeight != 0 ? weightedSum / totalWeight : 0.0;
        double weightedNormalized = totalWeight != 0 ? weightedSumNormalized / totalWeight : 0.0;

        return new double[]{weighted, weightedNormalized};
    }


    public static int[] get_stocks_counters() {
        ArrayList<MiniStock> stocks = new ArrayList<>(TA35.getInstance().getStocksHandler().getStocks());
        int ba_number_of_positive = 0,
                ba_weight_positive = 0,
                green_stocks = 0,
                delta_weight_positive = 0,
                total_delta = 0,
                total_up_with_short_delta = 0,
                total_down_with_long_delta = 0,
                counter_2_weight_positive = 0;

        for (MiniStock stock : stocks) {

            // BA positive number
            if (stock.getBid_ask_counter() > 0) {
                ba_number_of_positive++;
                ba_weight_positive += stock.getWeight();
            }

            // Green stocks
            if (stock.getLast() > stock.getBase()) {
                green_stocks++;
            }

            // Delta weight
            if (stock.getDelta_counter() > 0) {
                delta_weight_positive += stock.getWeight();
            }

            // Up with short delta
            if (stock.get_open_close() > 0 && stock.getDelta_counter() < 0 ) {
                total_up_with_short_delta += stock.getWeight();
            }

            // Down with long delta
            if (stock.get_open_close() < 0 && stock.getDelta_counter() > 0) {
                total_down_with_long_delta += stock.getWeight();
            }

            // Total delta
            total_delta += (int) stock.getDelta_counter() * stock.getWeight() / 100;

            // Counter_2 weight positive
            if (stock.getCounter_2() > 0) {
                counter_2_weight_positive += stock.getWeight();
            }
        }

        TA35.getInstance().setBa_total_positive_weight(ba_weight_positive);
        TA35.getInstance().setDelta_potisive_weight(delta_weight_positive);
        TA35.getInstance().setCounter_2_tot_weight(counter_2_weight_positive);
        
        int[] vals = new int[8];
        vals[BA_NUMBER_POSITIVE_STOCKS]     = ba_number_of_positive;
        vals[BA_WEIGHT_POSITIVE_STOCKS]     = ba_weight_positive;
        vals[GREEN_STOCKS]                  = green_stocks;
        vals[DELTA_WEIGHT_POSITIVE_STOCKS] = delta_weight_positive;
        vals[TOTAL_DELTA]                   = (int)total_delta;
        vals[TOTAL_UP_WITH_SHORT_DELTA]     = total_up_with_short_delta;
        vals[TOTAL_DOWN_WITH_LONG_DELTA]    = total_down_with_long_delta;
        vals[COUNTER_2_WEIGHT_POSITIVE]     = counter_2_weight_positive;
        return vals;
    }


    public static void calc_stocks_counters() {
        StocksHandler stocksHandler = TA35.getInstance().getStocksHandler();
        List<MiniStock> snapshot = new ArrayList<>(stocksHandler.getStocks());
        int bid_ask_counter = 0;

        for (MiniStock stock : snapshot) {
            double change = stock.getBid_ask_counter() - stock.getBid_ask_counter_0();

            if (change > 0) {
                bid_ask_counter += stock.getWeight();
            } else if (change < 0) {
                bid_ask_counter -= stock.getWeight();
            }

            // Append buy sell _0
            stock.setBid_ask_counter_0(stock.getBid_ask_counter());
        }

        if (L.abs(bid_ask_counter) < 100) {
            TA35 client = TA35.getInstance();
            client.setStocks_counter_change(bid_ask_counter);
        }
    }

    public static class PositiveTracker {

        private static int countPositive = 0;
        private static int totalCount = 0;
        private static double sumPositive = 0.0;
        private static final int intervalSeconds = 15;

        // קריאה בכל 15 שניות עם הערך שאתה רוצה לעקוב אחריו
        public static void update(double value) {
            totalCount++;
            if (value > 0) {
                countPositive++;
                sumPositive += value;
            }
        }

        // משך הזמן (בשניות) שבו הערך היה חיובי
        public static int getPositiveDurationInSeconds() {
            return countPositive * intervalSeconds;
        }

        // ממוצע של הערכים החיוביים
        public static double getPositiveAverage() {
            return countPositive > 0 ? sumPositive / countPositive : 0.0;
        }

        // אחוז הזמן שהערך היה חיובי מתוך כלל הדגימות
        public static double getPositivePercentage() {
            return totalCount > 0 ? (countPositive * 100.0) / totalCount : 0.0;
        }

        // איפוס לערכים התחלתיים – למשל בתחילת יום חדש
        public static void reset() {
            countPositive = 0;
            totalCount = 0;
            sumPositive = 0.0;
        }

        // סיכום כתוב
        public static String summary() {
            int seconds = getPositiveDurationInSeconds();
            return String.format(
                    "Positive duration: %d sec (%d:%02d)\nAverage while positive: %.2f\nPositive %% of day: %.1f%%",
                    seconds, seconds / 60, seconds % 60, getPositiveAverage(), getPositivePercentage()
            );
        }
    }
}