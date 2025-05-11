package dde;

import java.util.HashMap;
import java.util.Map;

public abstract class DDECells {

    public static final int
            LAST_PRICE = 1,
            MID = 2,
            BID = 3,
            ASK = 4,
            OPEN = 5,
            BASE = 6;

    public DDECells() {
        initCells();
    }

    Map cells = new HashMap<Integer, String>();

    public abstract void initCells();

    public void addCell(int ddeCellsEnum, String cellLocation) {
        cells.put(ddeCellsEnum, cellLocation);
    }

    public String getCell(int cell) {
        return (String) cells.get(cell);
    }

}
