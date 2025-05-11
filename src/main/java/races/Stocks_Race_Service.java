package races;

import api.BASE_CLIENT_OBJECT;
import locals.L;
import service.MyBaseService;

import java.util.ArrayList;

public class Stocks_Race_Service extends MyBaseService {

    ArrayList<Race_Logic> race_logics;

    public Stocks_Race_Service(BASE_CLIENT_OBJECT client) {
        super(client);

        // Init logics
        init_logics();

    }

    private void init_logics() {

        race_logics = new ArrayList<>();

        for (BASE_CLIENT_OBJECT stock : L.stocks) {
            race_logics.add(stock.get_main_race());
        }
    }

    @Override
    public void go() {

        for (Race_Logic logic : race_logics) {
            logic.race_finder();
            logic.update_data();
        }

    }

    @Override
    public String getName() {
        return client.getName() + " Races";
    }

    @Override
    public int getSleep() {
        return 200;
    }

}
