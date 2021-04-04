package backTest;

import java.io.File;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.read.biff.BiffException;

public class FlowHandler {

	// Variables
	ExcelReader excelReader;
	Algoritem algoritem;
	MyObject myObject;

	// Constructor
	public FlowHandler() {
		excelReader = new ExcelReader();
		algoritem = new Algoritem();
		myObject = MyObject.getInstance();
	}

	// Start
	public void start() {

		try {
			startBackTestMultiDays();
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Loop for each excel file day
	public void startBackTestOneDay() {
		Thread oneDayRunner = new Thread(() -> {
			// backTestSingelday("C://Users/user/Desktop/Work/Data history/TA35");
		});
		oneDayRunner.start();
	}

	public void startBackTestMultiDays() throws BiffException {

		Thread multiDaysRunner = new Thread(() -> {

			SingleDayRunner singleDayRunner;

			int daysCount = 0;
			
			for (File file : getFilesInDir("C://Users/user/Desktop/Work/Data history/TA35")) {
				try {
					
					System.out.println(file.getName());
					
					String path = file.getAbsolutePath().replace("\\", "/");

					Sheet sheet = excelReader.getExcelSheet(path, 0);
					
					singleDayRunner = new SingleDayRunner(sheet);
					singleDayRunner.run();
					
					
//					if (daysCount == 30) {
//						break;
//					}
					
					daysCount++;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			System.out.println();
			System.out.println("Positions");
			myObject.getPositionHandler().printPositions();
			
			System.out.println();
			System.out.println("Total pnl");
			System.out.println(myObject.getPositionHandler().getATotalPnl());
			
			System.out.println();
			System.out.println("Avg pnl");
			System.out.println(myObject.getPositionHandler().getAvgPnl());
			
			System.exit(0);
			
		});
		multiDaysRunner.start();
		
	}

	private static ArrayList<File> getFilesInDir(String location) {
		File folder = new File(location);
		ArrayList<File> listOfFiles = new ArrayList<>();
		File[] allFiles = folder.listFiles();

		for (File file : allFiles) {
			if (file.isFile()) {
				listOfFiles.add(file);
			}
		}
		return listOfFiles;
	}

}
