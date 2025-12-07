package api.deltaTest;

import api.BASE_CLIENT_OBJECT;
import api.Manifest;
import api.TA35;
import dataBase.mySql.MySql;
import backTest.Writer;
import service.MyBaseService;
import service.ServiceEnum;

//MySql class
public class CalcsService extends MyBaseService {

    BASE_CLIENT_OBJECT client;

    int counter = 0;
    double current_strike = 0;

    Writer writer;

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
