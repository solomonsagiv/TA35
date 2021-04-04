package dataBase;

import api.ApiObject;
import api.Manifest;
import dataBase.mySql.tables.ArraysTable;
import dataBase.mySql.tables.BoundsTable;
import dataBase.mySql.tables.jsonTables.DayJsonTable;
import dataBase.mySql.tables.jsonTables.StatusJsonTable;
import dataBase.mySql.tables.jsonTables.SumJsonTable;
import service.MyBaseService;

public class DataBaseService extends MyBaseService {
	
	public static void main(String[] args) {
		ApiObject apiObject = ApiObject.getInstance();
		SumJsonTable jsonTable = new SumJsonTable("ta35Sum");
		jsonTable.insert();
	}
	
	ArraysTable arraysTable;
	DayJsonTable dayTable;
	StatusJsonTable statusTable;
	SumJsonTable sumTable;
	BoundsTable boundsTable;
	
	public DataBaseService() {
		super();
		arraysTable = new ArraysTable("ta35Arrays");
		dayTable = new DayJsonTable("ta35Day");
		statusTable = new StatusJsonTable("ta35Status");
		sumTable = new SumJsonTable("ta35Sum");
		boundsTable = new BoundsTable("bounds");
	}
	
	@Override
	public void go() {
		
		if (Manifest.DB) {
			// Day
			dayTable.insert();
		}
		
		if (Manifest.DB_UPDATER) {
			// Status
			statusTable.update();
		}
		
		// Arrays
		if (sleepCount % 5000 == 0) {
			arraysTable.insert();
		}
	}
	
	@Override
	public String getName() {
		return "DataBaseService";
	}
	
	@Override
	public int getSleep() {
		return 1000;
	}

	public ArraysTable getArraysTable() {
		return arraysTable;
	}

	public BoundsTable getBoundsTable() {
		return boundsTable;
	}

	public StatusJsonTable getStatusTable() {
		return statusTable;
	}
	
	public SumJsonTable getSumTable() {
		return sumTable;
	}
	
	public DayJsonTable getDayTable() {
		return dayTable;
	}
	
}
