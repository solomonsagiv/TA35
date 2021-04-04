package options;

import javax.swing.JTable;

import api.ApiObject;

public class OptionsDataUpdater extends Thread {
	
	ApiObject apiObject = ApiObject.getInstance();

	Object lock = new Object();

	boolean run = true;

	public static int tableStartStrike = 0;
	public static int tableEndStrike = 0;
	
	JTable table;
	JTable calcTable;

	Options optionsMonth;
	
	// Constructor 
	public OptionsDataUpdater(JTable table, JTable calcTable) {
		this.table = table;
		this.calcTable = calcTable;
		optionsMonth = apiObject.getExpMonth().getOptions();
	}
	
	@Override
	public void run() {
		
		updateStrikes();
		
		while (run) {
			try {
				synchronized (lock) {
					
					int cellRow = 0;
					// Set the strike
					for (int strike = tableStartStrike; strike < tableEndStrike; strike += 10) {
						table.setValueAt(strike, cellRow, 1);
						calcTable.setValueAt(strike, cellRow, 1);
						cellRow++;
					}
					// Set the bid ask counter
					for (int i = 0; i < table.getRowCount(); i++) {
						if (table.getValueAt(i, 1).equals("")) {
							break;
						}
						Option call = optionsMonth.getOption("c" + table.getValueAt(i, 1));
						Option put = optionsMonth.getOption("p" + table.getValueAt(i, 1));
						
						table.setValueAt(call.getBidAskCounter(), i, 0);
						table.setValueAt(put.getBidAskCounter(), i, 2);
						
						calcTable.setValueAt(call.getBidAskCalcCounter(), i, 0);
						calcTable.setValueAt(put.getBidAskCalcCounter(), i, 2);
					}
					
					// Sleep
					sleep(1000);
				}
				
			} catch (InterruptedException e) {
				close();
			} catch (NullPointerException e) {
				
			}
		}
	}
	
	private void updateStrikes() {
		if (optionsMonth.getContract() % 10 > 5) {
			tableStartStrike = ((int) (optionsMonth.getContract()  / 10)) * 10 - 20;
			tableEndStrike = ((int) (optionsMonth.getContract()  / 10)) * 10 + 30;
		} else {
			tableStartStrike = ((int) (optionsMonth.getContract()  / 10)) * 10 - 20;
			tableEndStrike = ((int) (optionsMonth.getContract()  / 10)) * 10 + 30;
		}
	}
	
	public static void updateStrikes(int strike) {
		tableStartStrike = strike - 20;
		tableEndStrike = strike + 30;
	}

	public void close() {
		run = false;
	}

}
