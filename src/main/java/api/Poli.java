package api;

import dataBase.Factories;
import dde.DDECells;
import handlers.TimeSeriesHandler;
import locals.Themes;
import races.Race_Logic;
import races.RacesService;

import java.awt.*;
import java.util.HashMap;

public class Poli extends BASE_CLIENT_OBJECT {

    static Poli client = null;

    // Constructor
    public Poli() {
        super();
    }

    // get instance
    public static Poli getInstance() {
        if (client == null) {
            client = new Poli();
        }
        return client;
    }

    @Override
    protected void init_race_service() {
        HashMap<Race_Logic.RACE_RUNNER_ENUM, Race_Logic> map = new HashMap<>();
        map.put(Race_Logic.RACE_RUNNER_ENUM.LAST_MID, new Race_Logic(this, Race_Logic.RACE_RUNNER_ENUM.LAST_MID, getRace_margin()));
        setRacesService(new RacesService(this, map));

    }

    @Override
    protected void init_name() {
        setName("poli");
    }

    @Override
    protected void init_data_base_service() {

    }

    @Override
    protected void init_exps() {

    }

    @Override
    public Race_Logic get_main_race() {
        return getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.LAST_MID);
    }

    @Override
    protected void init_timeseries_handler() {
        timeSeriesHandler = new TimeSeriesHandler();
        timeSeriesHandler.put_id(Factories.TimeSeries.LAST_PRICE, 10069);
        timeSeriesHandler.put_id(Factories.TimeSeries.BID, 10070);
        timeSeriesHandler.put_id(Factories.TimeSeries.ASK, 10071);
        timeSeriesHandler.put_id(Factories.TimeSeries.MID_DEV, 10072);
    }

    @Override
    protected void init_dde_cells() {
        ddeCells = new DDECells() {

            @Override
            public void initCells() {
//                addCell(DDECells.LAST_PRICE, L.cell());
//                addCell(DDECells.BID, L.cell());
//                addCell(DDECells.ASK, L.cell());
//                addCell(DDECells.MID, L.cell());
//                addCell(DDECells.OPEN, L.cell());
//                addCell(DDECells.BASE, L.cell());
            }
        };
//        setDdeCells(ddeCells);
    }

    @Override
    public Color get_index_race_serie_color() {
        return Themes.RED;
    }
}
