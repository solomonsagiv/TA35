package threads;

import arik.Arik;

public class MyThreadHandler {

	// Variables
	MyThread myThread;
	Thread thread;
	Arik arik;

	// Constructor
	public MyThreadHandler(MyThread myThread) {
		this.myThread = myThread;
		this.arik = Arik.getInstance();
	}
	
	// ---------- Functions ---------- //
	
	// Start
	public void start() {
		if (thread == null) {
			myThread.setRun(true);
			thread = new Thread(myThread.getRunnable());
			thread.start();
		}
	}
	
	// Close
	public void close() {
		try {
			thread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		myThread.setRun(false);
		thread = null;
	}

	// Restart
	public void restart() {
		close();
		start();
	}
}
