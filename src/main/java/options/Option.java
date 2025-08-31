package options;

import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Objects;

public class Option implements IJsonData {

	public enum Side { CALL, PUT;
		public static Side from(String s) {
			if (s == null) return null;
			String u = s.trim().toUpperCase();
			if (u.startsWith("C")) return CALL;
			if (u.startsWith("P")) return PUT;
			return null;
		}
		@Override public String toString() { return this == CALL ? "C" : "P"; }
	}

	private String name;       // e.g. "C1875"
	private double strike;     // יישור ל-Options (double)
	private Side side;         // enum במקום String

	private Integer id = null; // מזהה אופציונלי; null אם לא ידוע
	private int cellRow = 0;
	private int bid = 0;
	private int ask = 0;
	private int last = 0;
	private int high = 0;
	private int low = 0;
	private int base = 0;
	private int prem = 10;
	private int pricing = -10;
	private int open_pos = 0;
	private double calcPrice = 0;
	private int volume = 0;
	private double stDev = 0;
	private double delta = 0;
	private int pre_bid = 0;
	private int pre_ask = 0;

	// מחזיקים שני מצבים אחרונים בלבד
	private final Deque<Integer> cycleState = new ArrayDeque<>(2);

	private int bidAskCounter = 0;
	private int deltaCounter = 0;
	private int deltaQuanCounter = 0;

	private HashMap<Integer, Level> levels;
	Options options;

	// ----- Constructors -----
	public Option(String sideStr, int strikeInt, Options options) {
		this(Side.from(sideStr), strikeInt, options);
	}
	public Option(Side side, double strike, Options options) {
		this.side = side;
		this.strike = strike;
		this.options = options;
		rebuildName();

		levels = new HashMap<>();
		for (int i = 1; i <= 5; i++) {
			levels.put(i, new Level(i));
		}
	}

	private void rebuildName() {
		this.name = (side == null ? "" : side.toString()) + (strike % 1.0 == 0 ? String.valueOf((int) strike) : String.valueOf(strike));
	}

	// ----- Getters / Setters -----
	public String getName() { return name; }
	/** שינוי שם ידני ייתקל עם צד/סטרייק — עדיף לא לגעת. */
	public void setName(String name) { this.name = name; } // שומר תאימות

	public double getStrike() { return strike; }
	public void setStrike(double strike) { this.strike = strike; rebuildName(); }

	public String getSide() { return side == null ? null : side.toString(); }
	public void setSide(String sideStr) { this.side = Side.from(sideStr); rebuildName(); }
	public Side getSideEnum() { return side; }
	public void setSideEnum(Side s) { this.side = s; rebuildName(); }

	public Integer getId() { return id; }
	public void setId(int id) { this.id = id; }

	public int getBid() { return bid; }
	public void setBid(int bid) {
		// Pre bid
		if (bid != this.bid) {
			this.pre_bid = this.bid;
		}
		// Counter
		if (bid > this.bid) {
			bidAskCounter++;
		}
		this.bid = bid;
	}

	public int getAsk() { return ask; }
	public void setAsk(int ask) {
		// Pre ask
		if (ask != this.ask) {
			this.pre_ask = this.ask;
		}
		// Counter
		if (ask < this.ask) {
			bidAskCounter--;
		}
		this.ask = ask;
	}

	public int getLast() { return last; }
	public void setLast(int last) { this.last = last; }

	public int getHigh() { return high; }
	public void setHigh(int high) { this.high = high; }

	public int getLow() { return low; }
	public void setLow(int low) { this.low = low; }

	public int getBase() { return base; }
	public void setBase(int base) { this.base = base; }

	public int getCellRow() { return cellRow; }
	public void setCellRow(int cellRow) { this.cellRow = cellRow; }

	public HashMap<Integer, Level> getLevels() { return levels; }
	public void setLevels(HashMap<Integer, Level> levels) { this.levels = levels; }

	public int getBidAskCounter() { return bidAskCounter; }
	public void setBidAskCounter(int bidAskCounter) { this.bidAskCounter = bidAskCounter; }

	public int getPrem() { return prem; }
	public void setPrem(int prem) { this.prem = prem; }

	public int getPricing() { return pricing; }
	public void setPricing(int pricing) { this.pricing = pricing; }

	public int getOpen_pos() { return open_pos; }
	public void setOpen_pos(int open_pos) { this.open_pos = open_pos; }

	public double getCalcPrice() { return calcPrice; }
	public void setCalcPrice(double calcPrice) { this.calcPrice = calcPrice; }

	public int getDeltaQuanCounter() {
		return deltaQuanCounter;
	}

	public void setDeltaQuanCounter(int deltaQuanCounter) {
		this.deltaQuanCounter = deltaQuanCounter;
	}

	public int getVolume() { return volume; }
	public void setVolume(int volume) {
		if (volume > this.volume) {
			int change = volume - this.volume;
			calc_delta(change);
		}
		this.volume = volume;
	}

	private void calc_delta(int change) {
		if (last >= pre_ask) {
			deltaCounter++;
			deltaQuanCounter += change;
		}
		if (last <= pre_bid) {
			deltaCounter--;
			deltaQuanCounter -= change;
		}
	}

	public double getStDev() { return stDev; }
	public void setStDev(double stDev) { this.stDev = stDev; }

	public double getDelta() { return delta; }
	/** אם אתה רוצה לצבור – השאר; אם לא, שנה ל-setDelta */
	public void appendDelta(double delta) { this.delta += delta; }
	public void setDelta(double delta) { this.delta = delta; }

	public int getDeltaCounter() { return deltaCounter; }
	public void setDeltaCounter(int deltaCounter) { this.deltaCounter = deltaCounter; }
	public void increaseDeltaCounter() { deltaCounter++; }
	public void decreaseDeltaCounter() { deltaCounter--; }

	// ----- Cycle state (שני ערכים אחרונים) -----
	public Integer lastCycle() { return cycleState.size() >= 2 ? cycleState.toArray(new Integer[0])[1] : null; }
	public Integer preCycle()  { return cycleState.peekFirst(); }
	public void appendCycleState(int cycle) {
		if (cycleState.size() == 2) cycleState.removeFirst();
		cycleState.addLast(cycle);
	}

	// ----- JSON -----
	@Override
	public MyJson getAsJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.delta, delta);
		json.put(JsonStrings.open_pos, open_pos);
		json.put(JsonStrings.bid, bid);
		json.put(JsonStrings.ask, ask);
		return json;
	}

	@Override
	public void loadFromJson(MyJson json) {
		try {
			if (json.has(JsonStrings.delta)) {
				// אם אתה מעדיף לצבור: appendDelta(...)
				setDelta(json.getDouble(JsonStrings.delta));
			}
			if (json.has(JsonStrings.open_pos)) setOpen_pos(json.getInt(JsonStrings.open_pos));
			if (json.has(JsonStrings.bid)) setBid(json.getInt(JsonStrings.bid));
			if (json.has(JsonStrings.ask)) setAsk(json.getInt(JsonStrings.ask));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override public MyJson getResetJson() { return new MyJson(); }
	@Override public MyJson getFullResetJson() { return new MyJson(); }

	// ----- Utility -----
	@Override
	public String toString() {
		return "Option{" +
				"name='" + name + '\'' +
				", side=" + side +
				", strike=" + strike +
				", bid=" + bid +
				", ask=" + ask +
				", last=" + last +
				", volume=" + volume +
				", delta=" + delta +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Option)) return false;
		Option option = (Option) o;
		// אם יש id – זה העוגן; אחרת לפי name
		if (this.id != null && option.id != null) {
			return this.id.equals(option.id);
		}
		return Objects.equals(this.name, option.name);
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : Objects.hashCode(name));
	}
}
