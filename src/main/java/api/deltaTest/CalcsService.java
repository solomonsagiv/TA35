package api.deltaTest;

import api.BASE_CLIENT_OBJECT;
import api.Manifest;
import service.MyBaseService;
import service.ServiceEnum;

//MySql class
public class CalcsService extends MyBaseService {

    BASE_CLIENT_OBJECT client;

    int counter = 0;

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
            if (counter % 60000 == 0) {
                counter = 0;
                // Calc stocks counter
                calc_stocks_raw_counter();
            }
            // Calc stocks weighted counter
            calc_stocks_weighted_counter();
        }
    }

    private void calc_stocks_weighted_counter() {
        double c = Calculator.calculateWeightedCounters()[0];
        client.setStocks_weighted_counter(c);
    }

    private void calc_stocks_raw_counter() {
        Calculator.PositiveTracker.update(client.getStocks_counter());
        Calculator.calc_stocks_counters();
    }


    @Override
    public String getName() {
        return "Calc service";
    }

    @Override
    public int getSleep() {
        return 1000;
    }

    @Override
    public ServiceEnum getType() {
        return ServiceEnum.MYSQL_RUNNER;
    }
}
