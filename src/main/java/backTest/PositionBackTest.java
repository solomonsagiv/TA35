package backTest;

public class PositionBackTest {

	MyObject myObject;

	// Variables
	private double startPrice = 0;
	private double closePrice = 0;
	private int status = 0;
	private boolean type;

	// Constructor
	public PositionBackTest() {
		myObject = MyObject.getInstance();
	}

	// Start position
	public void start(boolean type, double price) {
		
		status = 1;
		startPrice = price;
		this.type = type;
		myObject.getPositionHandler().addPosition(this);
		
	}

	// Close position
	public void close(double price) {
		
		status = 2;
		closePrice = price;
		
	}

	public double getPnl() {
		// If long
		if (type) {
			return closePrice - startPrice;
		}
		// If short
		else {
			return startPrice - closePrice;
		}
	}

	public double getPnl(double lastPrice) {
		// If long
		if (type) {
			return lastPrice - startPrice;
		}
		// If short
		else {
			return startPrice - lastPrice;
		}
	}

	// --------- Getters and Setters ---------- //
	public double getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(double startPrice) {
		this.startPrice = startPrice;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return " [startPrice=" + startPrice + ", closePrice=" + closePrice + ", status=" + status + ", type=" + type
				+ " PNL= " + getPnl() + "]";
	}

}
