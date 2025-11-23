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
    private int first_hour_counter_2 = 0;

    // Bid/Ask averages and counters
    private double average_bid_change = 0.0;
    private double average_ask_change = 0.0;
    private int bid_change_count = 0; // כמות שינויים של bid מתחילת היום
    private int ask_change_count = 0; // כמות שינויים של ask מתחילת היום
    private int bid_counter_2 = 0; // אינדיקטור bid: +1 למעלה, -1 למטה
    private int ask_counter_2 = 0; // אינדיקטור ask: +1 למעלה (ask יורד), -1 למטה (ask עולה)

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
        // Track bid movement for indicator
        if (this.bid != 0 && bid != this.bid) {
            double change = bid - this.bid;
            updateBidAverage(change);

            if (bid > this.bid) {
                bid_ask_counter++;
                bid_counter_2++;
            }

            if (bid < this.bid) {
                bid_counter_2--;
            }

            // Set pre bid
            this.pre_bid = this.bid;
            this.bid = bid;
        }
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {

        // Track ask movement for indicator
        if (this.ask != 0 && ask != this.ask) {
            double change = Math.abs(ask - this.ask);
            updateAskAverage(change);

            if (ask > this.ask) {
                ask_counter_2++;
            }
    
            if (ask < this.ask) {
                bid_ask_counter--;
                ask_counter_2--;
            }
    
            // Set pre ask
            this.pre_ask = this.ask;
            this.ask = ask;
        } 
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

            if (this.volume != 0 && volume != 0 && pre_ask != 0 && pre_bid != 0) {
                calc_delta(change);
            }
            this.volume = volume;
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

    public int getFirst_hour_counter_2() {
        return first_hour_counter_2;
    }

    public void setFirst_hour_counter_2(int first_hour_counter_2) {
        this.first_hour_counter_2 = first_hour_counter_2;
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

    /**
     * מחשב את ההפרש של counter_2 ביחס לשעה האחרונה
     * 
     * @return ההפרש (נוכחי - ראשון)
     */
    public int getCounter2HourlyDelta() {
        return this.getCounter_2() - this.first_hour_counter_2;
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

    public int getCounter_2() {
        return getCombinedCounter2(); // משתמש ב-bid_counter_2 + ask_counter_2
    }

    public void setCounter_2(int counter_2) {
        setBid_counter_2((int) counter_2 / 2);
        setAsk_counter_2((int) counter_2 / 2);
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

    /**
     * מעדכן את ממוצע השינוי של bid
     */
    private void updateBidAverage(double change) {
        bid_change_count++;
        if (bid_change_count == 1) {
            average_bid_change = Math.abs(change);
        } else {
            average_bid_change = (average_bid_change * (bid_change_count - 1) + Math.abs(change)) / bid_change_count;
        }
    }

    /**
     * מעדכן את ממוצע השינוי של ask
     */
    private void updateAskAverage(double change) {
        ask_change_count++;
        if (ask_change_count == 1) {
            average_ask_change = Math.abs(change);
        } else {
            average_ask_change = (average_ask_change * (ask_change_count - 1) + Math.abs(change)) / ask_change_count;
        }
    }

    // Getters for averages and counters
    public double getAverageBidChange() {
        return average_bid_change;
    }

    public double getAverageAskChange() {
        return average_ask_change;
    }

    public int getBidChangeCount() {
        return bid_change_count;
    }

    public int getAskChangeCount() {
        return ask_change_count;
    }

    public int getBid_counter_2() {
        return bid_counter_2;
    }

    public void setBid_counter_2(int bid_counter_2) {
        this.bid_counter_2 = bid_counter_2;
    }

    public int getAsk_counter_2() {
        return ask_counter_2;
    }

    public void setAsk_counter_2(int ask_counter_2) {
        this.ask_counter_2 = ask_counter_2;
    }

    /**
     * מחזיר את הסכום של bid_counter_2 + ask_counter_2
     */
    public int getCombinedCounter2() {
        return bid_counter_2 + ask_counter_2;
    }

}
