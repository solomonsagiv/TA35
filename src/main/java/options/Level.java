package options;

public class Level {
	
	// Variables 
	private int level_id;
	private int bidPrice;
	private int bidQuantity;
	private int askPrice;
	private int askQuantity;
	
	// Constructor 
	public Level(int level_id) {
		this.level_id = level_id;
	}

	// Getters ans Setters 

	public int getLevel_id() {
		return level_id;
	}

	public void setLevel_id(int level_id) {
		this.level_id = level_id;
	}

	public int getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(int bidPrice) {
		this.bidPrice = bidPrice;
	}

	public int getBidQuantity() {
		return bidQuantity;
	}

	public void setBidQuantity(int bidQuantity) {
		this.bidQuantity = bidQuantity;
	}

	public int getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(int askPrice) {
		this.askPrice = askPrice;
	}

	public int getAskQuantity() {
		return askQuantity;
	}

	public void setAskQuantity(int askQuantity) {
		this.askQuantity = askQuantity;
	}

	@Override
	public String toString() {
		return "Level [level_id=" + level_id + ", bidPrice=" + bidPrice + ", bidQuantity=" + bidQuantity + ", askPrice="
				+ askPrice + ", askQuantity=" + askQuantity + "]";
	}
	
}
