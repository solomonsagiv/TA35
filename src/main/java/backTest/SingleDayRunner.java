package backTest;

import jxl.Sheet;

public class SingleDayRunner {

	Sheet sheet;
	Writer writer;
	Algoritem algoritem;
	MyObject myObject;

	// Constructor
	public SingleDayRunner(Sheet sheet) {

		this.sheet = sheet;
		this.writer = new Writer();
		this.algoritem = new Algoritem();
		this.myObject = MyObject.getInstance();

	}

	// Run
	public void run() {

		myRun();

	}

	String time;
	double future;
	double index;
	int futureUp;
	int futureDown;
	int indexUp;
	int indexDown;

	private void myRun() {

		// Loop sheet rows
		for (int row = 0; row < sheet.getRows(); row++) {
			try {
				
				// Grab data
				time = sheet.getCell(0, row).getContents();
				future = dbl(sheet.getCell(1, row).getContents());
				index = dbl(sheet.getCell(2, row).getContents());
				futureUp = toInt(sheet.getCell(3, row).getContents());
				futureDown = toInt(sheet.getCell(4, row).getContents());
				indexUp = toInt(sheet.getCell(5, row).getContents());
				indexDown = toInt(sheet.getCell(6, row).getContents());

				// Set open 
				if (row == 0) {
					
					myObject.setOpen(index);
					
				}
				
				// Calculate optimi pesimi move
				calculateOptimiPesimiBehavior();

				// Update my object
				updateMyObject();

				// Logic
				algoritem.A(sheet, row, optimiLiveMove, pesimiLiveMove);
				
				// End of the day 
				if (row == sheet.getRows() - 1) {
					closePositionEndOfTheDay();
				}
				

				// Write Data
				writer.write(floor(optimiLiveMove, 100), BackTestWindow.optimiMoveField);
				writer.write(floor(pesimiLiveMove, 100), BackTestWindow.pesimiMoveField);

			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	private void closePositionEndOfTheDay() {
		
		// Last row close position if open
		PositionBackTest position = myObject.getPositionHandler().getLastPosition();
		
		if (position.getStatus() == 1 || position.getClosePrice() == 0) {
			
			// If long
			if (position.isType()) {
				
				algoritem.EXIT_LONG();
				
			}
			// If short
			else {
				
				algoritem.EXIT_SHORT();
				
			}
			
		}
		
	}

	// Update my object
	private void updateMyObject() {

		myObject.setFuture(future);
		myObject.setIndex(index);
		myObject.setFutureUp(futureUp);
		myObject.setFutureDown(futureDown);
		myObject.setIndexUp(indexUp);
		myObject.setIndexDown(indexDown);

	}

	String[] state = { "", "" };

	boolean optimiStart = false;
	boolean pesimiStart = false;

	double optimiMoveSum = 0;
	double pesimiMoveSum = 0;

	double optimiLiveMove = 0;
	double pesimiLiveMove = 0;

	double lastMove = 0;

	double startPrice = 0;
	double endPrice = 0;

	int LAST_STATE = 1;
	int PRE_STATE = 0;

	String OPTIMI = "optimi";
	String PESIMI = "pesimi";

	void calculateOptimiPesimiBehavior() {

		double op = future - index;

		// Optimi
		if (op > 0.0) {

			if (!optimiStart) {

				// Change state
				state[PRE_STATE] = state[LAST_STATE];
				state[LAST_STATE] = OPTIMI;

				// Reset pesimi
				if (state[PRE_STATE].equals(PESIMI)) {
					pesimiStart = false;

					endPrice = index;
					pesimiMoveSum += endPrice - startPrice;
					pesimiLiveMove = pesimiMoveSum;
				}

				// Optimi set data
				optimiStart = true;
				startPrice = index;
			}

			lastMove = index - startPrice;
			optimiLiveMove = optimiMoveSum + lastMove;
		}

		// Pesimi
		if (op < 0.0) {

			if (!pesimiStart) {

				// Change state
				state[PRE_STATE] = state[LAST_STATE];
				state[LAST_STATE] = PESIMI;

				// Reset pesimi
				if (state[PRE_STATE].equals(OPTIMI)) {
					optimiStart = false;

					endPrice = index;
					optimiMoveSum += endPrice - startPrice;
					optimiLiveMove = optimiMoveSum;
				}

				// Optimi set data
				pesimiStart = true;
				startPrice = index;
			}

			lastMove = index - startPrice;
			pesimiLiveMove = pesimiMoveSum + lastMove;
		}

		if (op == 0) {
			if (state[LAST_STATE].equals(OPTIMI)) {
				lastMove = index - startPrice;

				optimiLiveMove = optimiMoveSum + lastMove;
			}

			if (state[LAST_STATE].equals(PESIMI)) {
				lastMove = index - startPrice;
				pesimiLiveMove = pesimiMoveSum + lastMove;
			}
		}
	}

	public double dbl(String s) {
		return Double.parseDouble(s);
	}

	public Integer toInt(String s) {
		return Integer.parseInt(s);
	}

	public double floor(double d, int zeros) {
		return Math.floor(d * zeros) / zeros;
	}

}
