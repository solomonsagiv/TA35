package api.deltaTest;

import api.BASE_CLIENT_OBJECT;
import api.Manifest;
import api.TA35;
import blackScholes.FairIVCalc;
import dataBase.Factories;
import dataBase.mySql.MySql;
import backTest.Writer;
import options.Options;
import service.MyBaseService;
import service.ServiceEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//MySql class
public class CalcsService extends MyBaseService {

    BASE_CLIENT_OBJECT client;

    int counter = 0;
    double current_strike = 0;

    Writer writer;
    private boolean indexClosesListInitialized = false;  // Flag to ensure updateIndexClosesList runs only once

    public CalcsService(BASE_CLIENT_OBJECT client) {
        super(client);
        this.client = client;
    }

    @Override
    public void go() {

        // Increment counter
        counter += getSleep();

        if (Manifest.DB_UPLOAD) {
            // Calc
            if (counter >= 60_000) {
                counter = 0;
                // Calc stocks counter
                calc_stocks_raw_counter();
                updateFirstHourCounters();
            }
            // Calc stocks weighted counter
            calc_stocks_weighted_counter();
        }

        if (counter % 10_000 == 0) {
            Options monthOptions = client.getExps().getMonth().getOptions();
            
            // Calc IV
            monthOptions.calcIv();
            
            // Calc Fair IV
            calcFairIv(monthOptions);

            if(writer != null) {
                writer.write_iv();
            }   else {
                writer = new Writer(TA35.getInstance());
                writer.write_iv();
            }
        }
    }
    
    private void set_strikes() {
        if (current_strike == 0) {
            current_strike = client.getMid();
        }
    }

    /**
     * מעדכן את הערכים הראשונים מהשעה האחרונה לכל המניות
     * מתבצע כל דקה
     */
    private void updateFirstHourCounters() {
        try {
            MySql.update_first_hour_counters(MySql.JIBE_PROD_CONNECTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calc_stocks_weighted_counter() {
        double c = Calculator.calculateWeightedCounters()[0];
        client.setStocks_weighted_counter(c);
    }

    private void calc_stocks_raw_counter() {
        Calculator.PositiveTracker.update(client.getStocks_counter_change());
        Calculator.calc_stocks_counters();
    }

    /**
     * מחשב Fair IV לאופציות חודש
     * משתמש במחירי סגירה היסטוריים מה-INDEX time series
     */
    private void calcFairIv(Options monthOptions) {
        try {
            // עדכון רשימת המחירים מה-INDEX
            updateIndexClosesList();
            
            // קבלת מחירי סגירה היסטוריים מהרשימה
            double[] closes = getHistoricalCloses();
            
            if (closes != null && closes.length >= 21) {
                // חישוב ועדכון Fair IV
                FairIVCalc.calculateAndUpdateFairIV(monthOptions, closes);
                System.out.println("FairIV calculation completed for " + monthOptions.getStrikes().size() + " strikes");
            } else {
                System.out.println("Warning: Not enough historical closes data. closes=" + (closes != null ? closes.length : 0));
            }
            // אם אין מספיק נתונים, פשוט לא מחשבים Fair IV
        } catch (Exception e) {
            // Log error but don't break the service
            System.err.println("Error in calcFairIv: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * מעדכן את רשימת מחירי הסגירה של INDEX מה-INDEX time series
     * שומר את הרשימה ב-BASE_CLIENT_OBJECT
     * רץ רק פעם אחת או אם הרשימה ריקה/לא מספיקה
     */
    private void updateIndexClosesList() {
        // בדיקה אם הרשימה כבר קיימת ויש בה מספיק נתונים (לפחות 20)
        ArrayList<Double> existingList = client.getIndex_closes_list();
        if (existingList != null && existingList.size() >= 20) {
            indexClosesListInitialized = true;
            return;
        }

        // אם הרשימה כבר מאותחלת אבל אין מספיק נתונים, נטען שוב (רק פעם אחת)
        if (indexClosesListInitialized && (existingList == null || existingList.size() < 20)) {
            // Reset flag to allow one more attempt
            indexClosesListInitialized = false;
        }

        // בדיקה אם כבר ניסינו לטעון - אם כן, לא לעדכן שוב (להימנע מלולאה)
        if (indexClosesListInitialized) {
            return;
        }

        try {
            // קבלת ID של INDEX time series
            Integer indexId = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX);
            if (indexId == null) {
                return;
            }

            // קבלת 20 ימי מסחר אחרונים
            List<Map<String, Object>> results = MySql.get_last_20_trading_days_closes(indexId, MySql.JIBE_PROD_CONNECTION);
            
            if (results == null || results.isEmpty()) {
                return;
            }

            // עדכון הרשימה ב-BASE_CLIENT_OBJECT
            ArrayList<Double> closesList = new ArrayList<>();
            for (Map<String, Object> row : results) {
                Object valueObj = row.get("value");
                if (valueObj instanceof Number) {
                    closesList.add(((Number) valueObj).doubleValue());
                }
            }

            // שמירת הרשימה ב-BASE_CLIENT_OBJECT רק אם יש לפחות כמה ערכים
            if (closesList.size() >= 20) {
                client.setIndex_closes_list(closesList);
                indexClosesListInitialized = true;  // סמן שהרשימה מאותחלת
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * מקבל מחירי סגירה היסטוריים מהרשימה ב-BASE_CLIENT_OBJECT
     * מחזיר את המחירים (לפחות 21 דרוש לחישוב)
     */
    private double[] getHistoricalCloses() {
        try {
            // קבלת הרשימה מ-BASE_CLIENT_OBJECT
            ArrayList<Double> closesList = client.getIndex_closes_list();
            
            if (closesList == null || closesList.size() < 21) {
                return null;
            }

            // קבלת המחיר הנוכחי - נשתמש ב-mid או last_price
            double currentPrice = client.getMid();
            if (currentPrice <= 0) {
                currentPrice = client.getLast_price();
            }

            // הוספת המחיר הנוכחי אם הוא שונה מהאחרון ברשימה
            if (currentPrice > 0) {
                if (closesList.isEmpty() || Math.abs(closesList.get(closesList.size() - 1) - currentPrice) > 0.01) {
                    closesList.add(currentPrice);
                }
            }

            // המרה ל-array
            if (closesList.size() >= 21) {
                double[] closes = new double[closesList.size()];
                for (int i = 0; i < closesList.size(); i++) {
                    closes[i] = closesList.get(i);
                }
                return closes;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String getName() {
        return "Calc service";
    }

    @Override
    public int getSleep() {
        return 1_000;
    }

    @Override
    public ServiceEnum getType() {
        return ServiceEnum.MYSQL_RUNNER;
    }
}
