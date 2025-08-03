package service;

import api.TA35;
import api.deltaTest.Calculator;
import locals.L;
import miniStocks.MiniStock;
import stocksHandler.StocksHandler;
import java.time.LocalTime;
import java.util.ArrayList;

public class BasketFinder_by_stocks extends MyBaseService {

    // Variables
    private int changesCount = 0;
    StocksHandler stocksHandler;
    private int targetChanges = 0;
    private BigFrame bigFrame;
    private int sleep_count = 0;
    private double biggest_change = 0;
    private final int SLEEP_BETWEEN_FRAMES = 1000;
    private int big_frame_time_in_secondes;
    private double last_0, last;
    private int basket_up = 0, basket_down = 0;
    int counter = 0;
    final int BUY_SELL_FRAME = 15000;

    public BasketFinder_by_stocks(TA35 client, int targetChanges, int big_frame_time_in_secondes) {
        super(client);
        this.targetChanges = targetChanges;
        this.big_frame_time_in_secondes = big_frame_time_in_secondes;
        this.stocksHandler = client.getStocksHandler();
        this.bigFrame = new BigFrame();
    }

    @Override
    public void go() {

        // Handle sleep
        sleep_count += getSleep();

        // Collect index changes
        // 100 millis
        append_index_changed();

        // Append frame (volume)
        // 1000 millis
        if (sleep_count % SLEEP_BETWEEN_FRAMES == 0) {
            append_volume_frame();
        }

        // Look for basket
        // 1000 millis
        if (sleep_count % SLEEP_BETWEEN_FRAMES == 0) {
            look_for_basket();
            sleep_count = 0;
        }

        // Update data
        update_data();

        // Calc buy sell stocks counters
        calc_buy_sell_stocks_counters();

    }

    private void calc_buy_sell_stocks_counters() {
        if (counter == BUY_SELL_FRAME) {
            Calculator.calc_stocks_buy_sell_counters();
            counter = 0;
            Calculator.PositiveTracker.update(client.getBuy_sell_counter());
        }
        counter += SLEEP_BETWEEN_FRAMES;
    }

    private void update_data() {
        last_0 = last;
        last = client.getLast_price();
    }

    private void append_volume_frame() {
        int last_change_count = find_volume_change_count();
        bigFrame.append_volume(LocalTime.now(), last_change_count);
    }

    private int find_volume_change_count() {
        // Reset params
        int changesCount = 0;

        // Look for changes
        for (MiniStock stock : stocksHandler.getStocks()) {
            try {

                // If changed
                if (stock.getVolume() > stock.getPreVolume() && stock.getVolume() > 0 && stock.getPreVolume() > 0) {
                    changesCount++;
                }

                // Update pre volume
                stock.setPreVolume(stock.getVolume());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(changesCount);

        return changesCount;
    }

    private void look_for_basket() {
        int volume_sum = (int) bigFrame.get_volume_sum();
        changesCount = volume_sum;

        // If got enough changes
        if (volume_sum >= targetChanges) {
            // Up - last closer to the ask
            if (client.get_ask_last_margin() < client.get_bid_last_margin()) {
                add_basket_up();
                reset_data_after_basket();
            } else if (client.get_bid_last_margin() < client.get_ask_last_margin()) {
                add_basket_down();
                reset_data_after_basket();
            }
        }
    }

    private void reset_data_after_basket() {
        bigFrame.reset_data_after_basket();
    }

    private double pre_index_price = 0;

    private void append_index_changed() {
        double last_index_price = TA35.getInstance().getIndex();

        // If changed
        if (last_index_price != pre_index_price) {

            // Append to changes list
            bigFrame.append_change_frame(LocalTime.now(), last_index_price - pre_index_price);

            // Update pre index price
            pre_index_price = last_index_price;
        }
    }


    private class IndexFrame {
        LocalTime time;
        double change;

        public IndexFrame(LocalTime time, double change) {
            this.time = time;
            this.change = change;
        }
    }

    private class VolumeFrame {
        LocalTime time;
        double volume;

        public VolumeFrame(LocalTime time, double volume) {
            this.time = time;
            this.volume = volume;
        }
    }

    private class BigFrame {

        private ArrayList<IndexFrame> indexFrames;
        private ArrayList<VolumeFrame> volumeFrames;

        public BigFrame() {
            indexFrames = new ArrayList<>();
            volumeFrames = new ArrayList<>();
        }

        public void append_change_frame(LocalTime time, double value) {
            indexFrames.add(new IndexFrame(time, value));

            LocalTime time_0 = indexFrames.get(0).time;
            LocalTime time_minus = time.minusSeconds(big_frame_time_in_secondes);

            // Remove data before last sec
            if (time_0.isBefore(time_minus) || time_0 == time_minus) {
                indexFrames.remove(0);
            }
        }

        public void append_volume(LocalTime time, double value) {
            volumeFrames.add(new VolumeFrame(time, value));

            // Remove data before last sec
            if (volumeFrames.get(0).time.isBefore(time.minusSeconds(big_frame_time_in_secondes))) {
                volumeFrames.remove(0);
            }
        }

        public boolean is_index_up() {
            double change = 0;
            double change_abs = 0;
            for (IndexFrame index_frame : indexFrames) {
                // If time is between start and end (Frame)
                // Set biggest change if new got one
                if (L.abs(index_frame.change) > change_abs) {
                    change = index_frame.change;
                    change_abs = L.abs(index_frame.change);
                }
            }

            // Up or down
            return change > 0;
        }

        public double get_volume_sum() {
            double sum = 0;
            int size;

            if (volumeFrames.size() < big_frame_time_in_secondes) {
                size = volumeFrames.size();
            } else {
                size = volumeFrames.size() - 1;
            }

            for (int i = 0; i < size; i++) {
                sum += volumeFrames.get(i).volume;
            }
            return sum;
        }

        public void reset_data_after_basket() {
            LocalTime end_time = volumeFrames.get(volumeFrames.size() - 1).time;

            for (int i = 0; i < volumeFrames.size() - 1; i++) {
                volumeFrames.clear();
            }

            for (IndexFrame index_frame : indexFrames) {
                if (index_frame.time.isBefore(end_time)) {
                    indexFrames.remove(index_frame);
                }
            }

        }
    }

    public void setBig_frame_time_in_secondes(int big_frame_time_in_secondes) {
        this.big_frame_time_in_secondes = big_frame_time_in_secondes;
    }

    public void add_basket_up() {
        basket_up++;
    }

    public void add_basket_down() {
        basket_down++;
    }

    public int getChangesCount() {
        return changesCount;
    }

    public int getTargetChanges() {
        return targetChanges;
    }

    public void setTargetChanges(int targetChanges) {
        this.targetChanges = targetChanges;
    }

    public double getBiggest_change() {
        return biggest_change;
    }

    public int getBasket_up() {
        return basket_up;
    }

    public void setBasket_up(int basket_up) {
        this.basket_up = basket_up;
    }

    public int getBasket_down() {
        return basket_down;
    }

    public void setBasket_down(int basket_down) {
        this.basket_down = basket_down;
    }

    public int get_baskets() {
        return getBasket_up() - getBasket_down();
    }

    @Override
    public String getName() {
        return client + " " + "basket finder";
    }

    @Override
    public int getSleep() {
        return 100;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Target changes= " + targetChanges + "\n");
        str.append("Changes= " + changesCount + "\n");
        str.append("BasketUp= " + getBasket_up() + "\n");
        str.append("BasketDown= " + getBasket_down() + "\n");
        str.append("Biggest change= " + L.floor(biggest_change, 100));
        str.append("Stoocks= " + stocksHandler.toString());
        return str.toString();
    }
}
