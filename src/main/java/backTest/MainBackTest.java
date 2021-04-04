package backTest;

public class MainBackTest {
	
	// Main 
	public static void main(String[] args) {
		
		// Start the window 
		BackTestWindow backTestWindow = new BackTestWindow();
		backTestWindow.frame.setVisible(true);
		
		// Start flow handler 
		FlowHandler flowHandler = new FlowHandler();
		flowHandler.start();
		
	}

}
