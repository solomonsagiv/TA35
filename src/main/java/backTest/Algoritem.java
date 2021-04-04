package backTest;

import jxl.Sheet;

public class Algoritem {
	
	MyObject myObject;
	double targetMargin = 0;
	double comision = 0;
	
	PositionBackTest position;
	
	public Algoritem() {
		
		myObject = MyObject.getInstance();
		
	}
	
	// A style algoritem
	public void A(Sheet sheet, int row, double optimiLiveMove, double pesimiLiveMove) {

		// No position
		if (!longBool && !shortBool) {

			// Optimi
			if (myObject.getOp() >= targetMargin) {
				
				// Long 
				if (optimiLiveMove > 0) {
					
					System.out.println();
					System.out.println(row + " LONG");
					System.out.println("OP: " + myObject.getOp());
					System.out.println("Optimi move: " + optimiLiveMove);
					System.out.println("Pesimi move: " + pesimiLiveMove);
					LONG();
					
				// Short	
				} else if (optimiLiveMove < 0) {
					
					System.out.println();
					System.out.println(row + " SHORT");
					System.out.println("OP: " + myObject.getOp());
					System.out.println("Optimi move: " + optimiLiveMove);
					System.out.println("Pesimi move: " + pesimiLiveMove);
					SHORT();
					
				}
				
			}

			// Pesimi
			if (myObject.getOp() <= oposite(targetMargin)) {
				
				// Short
				if (pesimiLiveMove < 0) {
					
					System.out.println();
					System.out.println(row + " SHORT");
					System.out.println("OP: " + myObject.getOp());
					System.out.println("Optimi move: " + optimiLiveMove);
					System.out.println("Pesimi move: " + pesimiLiveMove);
					SHORT();
					
				// Long	
				} else if (pesimiLiveMove > 0) {
					
					System.out.println();
					System.out.println(row + " LONG");
					System.out.println("OP: " + myObject.getOp());
					System.out.println("Optimi move: " + optimiLiveMove);
					System.out.println("Pesimi move: " + pesimiLiveMove);
					LONG();
					
				}
				
			}
			
		}

		// In long
		if (longBool) {
			
			// Optimi
			if (myObject.getOp() > targetMargin) {
				
				// Optimi move < than 0
				if (optimiLiveMove < 0) {
					
					System.out.println();
					System.out.println(row + " EXIT LONG");
					System.out.println("OP: " + myObject.getOp());
					System.out.println("Optimi move: " + optimiLiveMove);
					System.out.println("Pesimi move: " + pesimiLiveMove);
					EXIT_LONG();
					
				}
				
			}
			
			// Pesimi
			if (myObject.getOp() < oposite(targetMargin)) {
				
				// Pesimi is
				if (pesimiLiveMove < 0) {
					
					System.out.println();
					System.out.println(row + " EXIT LONG");
					System.out.println("OP: " + myObject.getOp());
					System.out.println("Optimi move: " + optimiLiveMove);
					System.out.println("Pesimi move: " + pesimiLiveMove);
					EXIT_LONG();
					
				}
				
			}
			
		}

		// In short
		if (shortBool) {
			
			// Optimi
			if (myObject.getOp() > targetMargin) {
				
				// Optimi move > 0 
				if (optimiLiveMove > 0) {
					
					System.out.println();
					System.out.println(row + " EXIT SHORT");
					System.out.println("OP: " + myObject.getOp());
					System.out.println("Optimi move: " + optimiLiveMove);
					System.out.println("Pesimi move: " + pesimiLiveMove);
					EXIT_SHORT();
					
				}
				
			}
			
			// Pesimi
			if (myObject.getOp() < oposite(targetMargin)) {
				
				// Pesimi > 0
				if (pesimiLiveMove > 0) {
					
					System.out.println();
					System.out.println(row + " EXIT SHORT");
					System.out.println("OP: " + myObject.getOp());
					System.out.println("Optimi move: " + optimiLiveMove);
					System.out.println("Pesimi move: " + pesimiLiveMove);
					EXIT_SHORT();
					
				}
				
			}
			
		}
		
	}
	
	private boolean longBool = false;
	private boolean shortBool = false;

	public void LONG() {
		
		longBool = true;
		position = new PositionBackTest();
		position.start(true, myObject.getFuture());
		
	}
	
	public void SHORT() {
		
		shortBool = true;
		position = new PositionBackTest();
		position.start(false, myObject.getFuture());

	}
	
	public void EXIT_LONG() {
		
		longBool = false;
		position.close(myObject.getFuture() - comision);
		
	}
	
	public void EXIT_SHORT() {
		
		shortBool = false;
		position.close(myObject.getFuture() + comision);
		
	}
	
	public double oposite(double d) {
		return d * -1;
	}

}
