package backTest;

public class MyObject {

	private static MyObject myObject;
	private PositionHandler positionHandler;
	
	private MyObject() {
		setPositionHandler(new PositionHandler());
	}
	
	public static MyObject getInstance() {
		
		if (myObject == null) {
			
			myObject = new MyObject();
			
		}
		
		return myObject;
	}
	
	private double totalPnl = 0;
	private double open = 0;
	private double future = 0;
	private double index = 0;
	private int futureUp = 0;
	private int futureDown = 0;
	private int indexUp = 0;
	private int indexDown = 0;
	
	public static MyObject getMyObject() {
		return myObject;
	}

	public static void setMyObject(MyObject myObject) {
		MyObject.myObject = myObject;
	}

	public double getFuture() {
		return future;
	}

	public void setFuture(double future) {
		this.future = future;
	}

	public double getIndex() {
		return index;
	}

	public void setIndex(double index) {
		this.index = index;
	}

	public int getFutureUp() {
		return futureUp;
	}

	public void setFutureUp(int futureUp) {
		this.futureUp = futureUp;
	}

	public int getFutureDown() {
		return futureDown;
	}

	public void setFutureDown(int futureDown) {
		this.futureDown = futureDown;
	}

	public int getIndexUp() {
		return indexUp;
	}

	public void setIndexUp(int indexUp) {
		this.indexUp = indexUp;
	}

	public int getIndexDown() {
		return indexDown;
	}

	public void setIndexDown(int indexDown) {
		this.indexDown = indexDown;
	}

	public double getOp() {
		return future - index;
	}

	public double getTotalPnl() {
		return totalPnl;
	}

	public void setTotalPnl(double totalPnl) {
		this.totalPnl = totalPnl;
	}

	public PositionHandler getPositionHandler() {
		return positionHandler;
	}

	public void setPositionHandler(PositionHandler positionHandler) {
		this.positionHandler = positionHandler;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}
	
}
