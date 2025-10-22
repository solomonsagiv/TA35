package miniStocks;

import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import stocksHandler.StocksHandler;

public class MiniStock implements IJsonData {

    // Variables
    StocksHandler handler;

    private double bid = 0;
    private double ask = 0;
    private double last = 0;
    private int volume = 0;
    private int preVolume = 0;
    private double weight = 0;
    private String name = "";
    private int row = 0;
    private double pre_last = 0;
    private double open = 0;
    private double base = 0;
    private double pre_bid = 0,
            pre_ask = 0;
    private int delta_counter = 0;
    private int bid_ask_counter = 0,
            bid_ask_counter_0 = 0;

    MiniStockDDECells ddeCells;

    // ערכים ראשונים מהשעה האחרונה (לחישוב הפרש)
    private int first_hour_counter = 0;
    private int first_hour_delta_counter = 0;

    // Constructor
    public MiniStock(StocksHandler handler, int row) {
        this.handler = handler;
        this.row = row;
        this.ddeCells = new MiniStockDDECells(row);

    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        // Counter
        if (bid > this.bid && this.bid != 0)
            bid_ask_counter++;

        // Set pre bid
        if (bid != this.bid)
            this.pre_bid = this.bid;

        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {

        // Counter
        if (ask < this.ask && this.ask != 0)
            bid_ask_counter--;

        // Set pre ask
        if (ask != this.ask)
            this.pre_ask = this.ask;

        this.ask = ask;
    }

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        if (last != this.last) {
            setPre_last(this.last);
            this.last = last;
        }
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        if (volume != this.volume) {
            int change = volume - this.volume;
            this.volume = volume;
            if (this.volume != 0 && volume != 0 && pre_ask != 0 && pre_bid != 0) {
                calc_delta(change);
            }
        }
    }

    // ===== First Hour Values (for hourly delta calculation) =====

    public int getFirst_hour_counter() {
        return first_hour_counter;
    }

    public void setFirst_hour_counter(int first_hour_counter) {
        this.first_hour_counter = first_hour_counter;
    }

    public int getFirst_hour_delta_counter() {
        return first_hour_delta_counter;
    }

    public void setFirst_hour_delta_counter(int first_hour_delta_counter) {
        this.first_hour_delta_counter = first_hour_delta_counter;
    }

    /**
     * מחשב את ההפרש של counter ביחס לשעה האחרונה
     * 
     * @return ההפרש (נוכחי - ראשון)
     */
    public int getCounterHourlyDelta() {
        return this.bid_ask_counter - this.first_hour_counter;
    }

    /**
     * מחשב את ההפרש של delta_counter ביחס לשעה האחרונה
     * 
     * @return ההפרש (נוכחי - ראשון)
     */
    public int getDeltaCounterHourlyDelta() {
        return this.delta_counter - this.first_hour_delta_counter;
    }

    public double get_open_close() {
        return last - open;
    }

    private void calc_delta(int change) {
        // Buy
        if (last >= pre_ask) {
            delta_counter += (int) ((change * last) / 1_000_000);
        }

        // Sell
        if (last <= pre_bid) {
            delta_counter -= (int) ((change * last) / 1_000_000);
        }
    }

    public int getPreVolume() {
        return preVolume;
    }

    public void setPreVolume(int preVolume) {
        this.preVolume = preVolume;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPre_last() {
        return pre_last;
    }

    public void setPre_last(double pre_last) {
        this.pre_last = pre_last;
    }

    public int getBid_ask_counter() {
        return bid_ask_counter;
    }

    public void setBid_ask_counter(int bid_ask_counter) {
        this.bid_ask_counter = bid_ask_counter;
    }

    public int getBid_ask_counter_0() {
        return bid_ask_counter_0;
    }

    public void setBid_ask_counter_0(int bid_ask_counter_0) {
        this.bid_ask_counter_0 = bid_ask_counter_0;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public int getDelta_counter() {
        return delta_counter;
    }

    public void setDelta_counter(int delta_counter) {
        this.delta_counter = delta_counter;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public MyJson getAsJson() {
        MyJson json = new MyJson();
        json.put(JsonStrings.name, name);
        json.put(JsonStrings.last, last);
        json.put(JsonStrings.bid, bid);
        json.put(JsonStrings.ask, ask);
        json.put(JsonStrings.volume, volume);
        json.put(JsonStrings.weight, weight);
        return json;
    }

    public MiniStockDDECells getDdeCells() {
        return ddeCells;
    }

    @Override
    public void loadFromJson(MyJson json) {
    }

    @Override
    public MyJson getResetJson() {
        return new MyJson();
    }

    @Override
    public MyJson getFullResetJson() {
        return getResetJson();
    }

}
