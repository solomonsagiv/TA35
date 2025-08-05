package miniStocks;

import api.deltaTest.Calculator;
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
    private double delta = 0;
    private double weight = 0;
    private String name = "";
    private int row = 0;
    private double pre_last = 0;
    private double open = 0;
    private double base  = 0;
//    private double bid_size = 0;
//    private double ask_size =

    private int buy_sell_counter = 0,
            buy_sell_quantity_counter = 0,
            buy_sell_counter_0 = 0,
            buy_sell_uuantity_counter_0 = 0,
            bid_ask_counter = 0,
            bid_ask_counter_0 = 0;

    MiniStockDDECells ddeCells;

    // Constructor
    public MiniStock(StocksHandler handler, int row) {
        this.handler = handler;
        this.row = row;
        this.ddeCells = new MiniStockDDECells(row);

    }

    public void stock_buy(int quantity) {
        buy_sell_counter++;
        buy_sell_quantity_counter += quantity;
    }

    public void stock_sell(int quantity) {
        buy_sell_counter--;
        buy_sell_quantity_counter -= quantity;
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
        if (bid > this.bid) {
            bid_ask_counter++;
        }
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        if (ask < this.ask) {
            bid_ask_counter--;
        }
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
            int q = volume - this.volume;
            this.volume = volume;
            Calculator.calc_stock_buy_sell_counter(this, q);
        }
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public int getPreVolume() {
        return preVolume;
    }

    public void setPreVolume(int preVolume) {
        this.preVolume = preVolume;
    }

    public void appendDelta(double newDelta) {
        this.delta += newDelta;

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

    public int getBuy_sell_counter() {
        return buy_sell_counter;
    }

    public void setBuy_sell_counter(int buy_sell_counter) {
        this.buy_sell_counter = buy_sell_counter;
    }

    public int getBuy_sell_quantity_counter() {
        return buy_sell_quantity_counter;
    }

    public void setBuy_sell_quantity_counter(int buy_sell_quantity_counter) {
        this.buy_sell_quantity_counter = buy_sell_quantity_counter;
    }

    public int getBuy_sell_counter_0() {
        return buy_sell_counter_0;
    }

    public void setBuy_sell_counter_0(int buy_sell_counter_0) {
        this.buy_sell_counter_0 = buy_sell_counter_0;
    }

    public int getBuy_sell_uuantity_counter_0() {
        return buy_sell_uuantity_counter_0;
    }

    public void setBuy_sell_uuantity_counter_0(int buy_sell_uuantity_counter_0) {
        this.buy_sell_uuantity_counter_0 = buy_sell_uuantity_counter_0;
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

    //    public double getBid_size() {
//        return bid_size;
//    }
//
//    public void setBid_size(double bid_size) {
//        this.bid_size = bid_size;
//    }
//
//    public double getAsk_size() {
//        return ask_size;
//    }
//
//    public void setAsk_size(double ask_size) {
//        this.ask_size = ask_size;
//    }

    @Override
    public MyJson getAsJson() {
        MyJson json = new MyJson();
        json.put(JsonStrings.name, name);
        json.put(JsonStrings.last, last);
        json.put(JsonStrings.bid, bid);
        json.put(JsonStrings.ask, ask);
        json.put(JsonStrings.delta, delta);
        json.put(JsonStrings.volume, volume);
        json.put(JsonStrings.weight, weight);
        return json;
    }

    public MiniStockDDECells getDdeCells() {
        return ddeCells;
    }

    @Override
    public void loadFromJson(MyJson json) {
        setDelta(json.getDouble(JsonStrings.delta));
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



