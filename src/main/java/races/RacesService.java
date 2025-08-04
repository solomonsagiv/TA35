package races;

import api.BASE_CLIENT_OBJECT;
import service.MyBaseService;

import java.util.HashMap;

public class RacesService extends MyBaseService {

    HashMap<Race_Logic.RACE_RUNNER_ENUM, Race_Logic> map;

    public RacesService(BASE_CLIENT_OBJECT client, HashMap<Race_Logic.RACE_RUNNER_ENUM, Race_Logic> map) {
        super(client);
        this.map = map;
    }

    @Override
    public void go() {
        for (Race_Logic logic : map.values()) {
            logic.race_finder();
        }
    }

    public Race_Logic get_race_logic(Race_Logic.RACE_RUNNER_ENUM ENUM) {
        return map.get(ENUM);
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
