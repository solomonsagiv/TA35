package api.dde.DDE;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;

import api.ApiObject;

public class DDEWriter extends Thread {

	private String excelPath = "C://Users/user/Desktop/DDE/[DDE.xlsm]Data";
	private ApiObject apiObject = ApiObject.getInstance();
	private boolean run = true;
	private DDEClientConversation conversation;
	
	String futureUpCell = "R15C2";
	String futureDownCell = "R16C2";
	String indexUpCell = "R15C3";
	String indexDownCell = "R16C3";
	
	String opAvgCell = "R10C2";
	
	// Constructor 
	public DDEWriter(DDEConnection ddeConnection) {
		this.conversation = ddeConnection.createNewConversation(excelPath);
	}

	@Override
	public void run() {

		while (run) {
			try {
				// Write the data to the excel 
				writeData();
				
				// Sleep 
				sleep(1000);
			} catch (InterruptedException e) {
				close();
			}
		}
	}
	
	// Write the data to the excel 
	private void writeData() {
		try {
			conversation.poke(futureUpCell, str(apiObject.getConUp()));
			conversation.poke(futureDownCell, str(apiObject.getConDown()));
			conversation.poke(indexUpCell, str(apiObject.getIndUp()));
			conversation.poke(indexDownCell, str(apiObject.getIndDown()));
			conversation.poke(opAvgCell, str(apiObject.getExpMonth().getOptions().getOp_avg()));
		} catch (DDEException e) {
			System.out.println("DDE request error on updateData()");
			e.printStackTrace();
		}
	}
	
	public String str(Object o) {
		return String.valueOf(o);
	}
	
	// Close
	public void close() {
		try {
			conversation.disconnect();
		} catch (DDEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		run = false;
	}

}
