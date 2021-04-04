package service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import threads.MyThread;

public class MyServiceHandler extends MyThread implements Runnable {

	// Variables
	private List<MyBaseService> servies = new ArrayList<>();
	private ExecutorService executor;
	final int sleep = 100;
	int sleepCount = 0;

	// Constructor
	public MyServiceHandler() {
		super();
		setRunnable(this);
	}
	
	@Override
	public void run() {
		init();
	}

	private void init() {
		
		executor = Executors.newCachedThreadPool();

		while (isRun()) {
			try {

				Thread.sleep(sleep);

				executServices();

				initSleepCount();

			} catch (InterruptedException e) {
				executor.shutdownNow();
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<MyBaseService> getServies() {
		return servies;
	}

	public void setServies(List<MyBaseService> servies) {
		this.servies = servies;
	}

	public void addService(MyBaseService newService) {
		if (!isExist(newService)) {
			servies.add(newService);
		}
	}

	public boolean isExist(MyBaseService newService) {
		boolean exist = false;

		for (MyBaseService service : servies) {
			if (service.equals(newService)) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	public void removeService(MyBaseService service) {
		servies.remove(service);
	}

	private void initSleepCount() {
		if (sleepCount == 300000) {
			sleepCount = 0;
		}
		sleepCount += sleep;
	}

	private void executServices() {
		for (MyBaseService service : servies) {
			service.execute(sleepCount);
		}
	}

	public String toStringServices() {
		StringBuilder sb = new StringBuilder();
		for (MyBaseService service : servies) {
			sb.append(service.getName() + "\n");
		}
		return sb.toString();
	}
}
