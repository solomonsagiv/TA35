package threads;

public abstract class MyThread {
	
	// Variables 
	private boolean run = false;
	
	private String name;
	private MyThreadHandler handler;
	private Runnable runnable;
	
	public MyThread() {
		initRunnable();
	}

	// Getters and setters
	public boolean isRun() {
		return run;
	}
	
	public void setRun(boolean run) {
		this.run = run;
	}

	public MyThreadHandler getHandler() {
		return handler;
	}
	
	public void setHandler(MyThreadHandler handler) {
		this.handler = handler;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
		setHandler(new MyThreadHandler(this));
	}

	public abstract void initRunnable();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
