package backTest;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelReader {
	
	// Constructor 
	public ExcelReader() {}
	
	// Get excel file sheet 
	public Sheet getExcelSheet(String excelFileLocation, int sheetNum) {
		try {
			
			Workbook workbook = null;
			workbook = Workbook.getWorkbook(new File(excelFileLocation));
			Sheet sheet = workbook.getSheet(sheetNum);
			
			return sheet;
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch blocks
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
}
