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
    private int counter_2 = 0; // BA2 counter

    MiniStockDDECells ddeCells;

    // ערכים ראשונים מהשעה האחרונה (לחישוב הפרש)
    private int first_hour_counter = 0;
    private int first_hour_delta_counter = 0;

    // BA2 אינדיקטור - משתנים לחישוב ממוצע ועוצמה
    private int movement_sample_count_bid = 0;
    private int movement_sample_count_ask = 0;
    private double average_bid_change = 0.0;
    private double average_ask_change = 0.0;
    private int bid_strength_score = 0;
    private int ask_strength_score = 0;

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

        // BA2: חישוב שינוי bid והשוואה לממוצע
        if (this.bid != 0 && bid != this.bid) {
            double bidChange = bid - this.bid;
            updateBA2Bid(bidChange);
        }

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

        // BA2: חישוב שינוי ask והשוואה לממוצע
        if (this.ask != 0 && ask != this.ask) {
            double askChange = this.ask - ask; // שינוי הפוך (כשאסק יורד זה חיובי)
            updateBA2Ask(askChange);
        }

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

    public int getCounter_2() {
        return counter_2;
    }

    public void setCounter_2(int counter_2) {
        this.counter_2 = counter_2;
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
     * מעדכן את BA2 counter לפי שינויי bid
     */
    private void updateBA2Bid(double change) {
        if (change == 0) return;

        double changeSize = Math.abs(change);
        boolean isUp = change > 0;
        
        updateBidMovement(changeSize, isUp);
        
        // עדכון counter_2 כחיבור של bid ו-ask scores
        counter_2 = bid_strength_score + ask_strength_score;
    }

    /**
     * מעדכן את BA2 counter לפי שינויי ask
     */
    private void updateBA2Ask(double change) {
        if (change == 0) return;

        double changeSize = Math.abs(change);
        // ask: כשעולה זה bearish, כשיורד זה bullish
        // change > 0 אומר ask ירד (זה bullish), change < 0 אומר ask עלה (זה bearish)
        boolean isUp = change < 0; // ask עולה = true
        
        updateAskMovement(changeSize, isUp);
        
        // עדכון counter_2 כחיבור של bid ו-ask scores
        counter_2 = bid_strength_score + ask_strength_score;
    }

    /**
     * מעדכן ממוצע שינוי bid ואינדיקטור
     * @param changeSize גודל השינוי (ערך מוחלט)
     * @param isUp האם bid עלה
     */
    private void updateBidMovement(double changeSize, boolean isUp) {
        // First time - initialize
        if (movement_sample_count_bid == 0) {
            average_bid_change = changeSize;
            movement_sample_count_bid = 1;
            return;
        }
        
        // Update average
        average_bid_change = (average_bid_change * movement_sample_count_bid + changeSize) / (movement_sample_count_bid + 1);
        movement_sample_count_bid++;
        
        // Update indicator only if change is significant
        // Bid עולה = bullish, Bid יורד = bearish
        if (changeSize > average_bid_change) {
            bid_strength_score += (isUp ? 1 : -1);
        }
    }

    /**
     * מעדכן ממוצע שינוי ask ואינדיקטור
     * @param changeSize גודל השינוי (ערך מוחלט)
     * @param isUp האם ask עלה
     */
    private void updateAskMovement(double changeSize, boolean isUp) {
        // First time - initialize
        if (movement_sample_count_ask == 0) {
            average_ask_change = changeSize;
            movement_sample_count_ask = 1;
            return;
        }
        
        // Update average
        average_ask_change = (average_ask_change * movement_sample_count_ask + changeSize) / (movement_sample_count_ask + 1);
        movement_sample_count_ask++;
        
        // Update indicator only if change is significant
        // Ask עולה = bearish, Ask יורד = bullish
        if (changeSize > average_ask_change) {
            ask_strength_score += (isUp ? -1 : 1);
        }
    }

}
