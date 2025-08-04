package api.deltaTest;

import api.BASE_CLIENT_OBJECT;
import api.Manifest;
import service.MyBaseService;
import service.ServiceEnum;

//MySql class
public class CalcsService extends MyBaseService {

    BASE_CLIENT_OBJECT client;

    public CalcsService(BASE_CLIENT_OBJECT client) {
        super(client);
        this.client = client;
    }

    @Override
    public void go() {
        // DB runner
        if (Manifest.DB_UPLOAD) {
            // Calc stocks counter
            calc_stocks_counter();
        }
    }

    private void calc_stocks_counter() {
        Calculator.PositiveTracker.update(client.getStocks_counter());
        Calculator.calc_stocks_counters();
    }


    @Override
    public String getName() {
        return "Calc service";
    }

    @Override
    public int getSleep() {
        return 60000;
    }

    @Override
    public ServiceEnum getType() {
        return ServiceEnum.MYSQL_RUNNER;
    }
}
