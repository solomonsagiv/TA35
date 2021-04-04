package dataBase.mySql.tables;

import java.net.UnknownHostException;
import java.time.LocalTime;

import org.json.JSONArray;
import org.json.JSONObject;

import api.ApiObject;
import dataBase.mySql.myBaseTables.MyArraysTable;
import dataBase.mySql.mySqlComps.MyColumnSql;
import dataBase.mySql.mySqlComps.MyLoadAbleColumn;
import dataBase.mySql.mySqlComps.MySqlColumnEnum;
import lists.MyChartPoint;
import options.Options;

public class ArraysTable extends MyArraysTable {
	
	
	public static void main(String[] args) {
		ApiObject apiObject = ApiObject.getInstance();
		apiObject.getDataBaseService().getStatusTable().reset();
	}
	
	public ArraysTable(String name) {
		super(name);
	}
	
	@Override
	public void initColumns() {
		
		Options optionsMonth = apiObject.getExpMonth().getOptions();
		Options optionsWeek = apiObject.getExpWeek().getOptions();
		
		addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.time) {
			@Override
			public String getObject() {
				return LocalTime.now().toString();
			}
		});
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.indexList) {
			@Override
			public String getObject() throws UnknownHostException {
				return apiObject.getIndexChartList().getLast().getAsJson().toString();
			}
			
			@Override
			public void setLoadedObject(String object) {
				apiObject.getIndexChartList().add(new MyChartPoint(new JSONObject(object)));
			}
			
			@Override
			public String getResetObject() {
				return new JSONArray().toString();
			}
		});
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.conBidAskCounterWeekList) {
			@Override
			public String getObject() throws UnknownHostException {
				return optionsWeek.getConBidAskCounterList().getLast().getAsJson().toString();
			}

			@Override
			public void setLoadedObject(String object) {
				optionsWeek.getConBidAskCounterList().add(new MyChartPoint(new JSONObject(object)));
			}

			@Override
			public String getResetObject() {
				return new JSONArray().toString();
			}
		});
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.conBidAskCounterMonthList) {
			@Override
			public String getObject() throws UnknownHostException {
				return optionsMonth.getConBidAskCounterList().getLast().getAsJson().toString();
			}

			@Override
			public void setLoadedObject(String object) {
				optionsMonth.getConBidAskCounterList().add(new MyChartPoint(new JSONObject(object)));
			}
			
			@Override
			public String getResetObject() {
				return new JSONArray().toString();
			}
		});
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.opWeekList) {
			@Override
			public String getObject() throws UnknownHostException {
				return optionsWeek.getOpChartList().getLast().getAsJson().toString();
			}

			@Override
			public void setLoadedObject(String object) {
				optionsWeek.getOpChartList().add(new MyChartPoint(new JSONObject(object)));
			}

			@Override
			public String getResetObject() {
				return null;
			}
		});
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.opMonthList) {
			@Override
			public String getObject() throws UnknownHostException {
				return optionsMonth.getOpChartList().getLast().getAsJson().toString();
			}

			@Override
			public void setLoadedObject(String object) {
				optionsMonth.getOpChartList().add(new MyChartPoint(new JSONObject(object)));
			}

			@Override
			public String getResetObject() {
				return null;
			}
		});
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.deltaWeekList) {
			@Override
			public String getObject() throws UnknownHostException {
				return optionsWeek.getDeltaChartList().getLast().getAsJson().toString();
			}

			@Override
			public void setLoadedObject(String object) {
				optionsWeek.getDeltaChartList().add(new MyChartPoint(new JSONObject(object)));
			}

			@Override
			public String getResetObject() {
				return null;
			}
		});
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.deltaMonthList) {
			@Override
			public String getObject() throws UnknownHostException {
				return optionsMonth.getDeltaChartList().getLast().getAsJson().toString();
			}
			
			@Override
			public void setLoadedObject(String object) {
				optionsMonth.getDeltaChartList().add(new MyChartPoint(new JSONObject(object)));
			}

			@Override
			public String getResetObject() {
				return null;
			}
		});
		
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.indDeltaList) {
			@Override
			public String getObject() throws UnknownHostException {
				return apiObject.getStocksHandler().getIndDeltaList().getLast().getAsJson().toString();
			}

			@Override
			public void setLoadedObject(String object) {
				apiObject.getStocksHandler().getIndDeltaList().add(new MyChartPoint(new JSONObject(object)));
			}

			@Override
			public String getResetObject() {
				return null;
			}
		});
		
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.indDeltaNoBasketsList) {
			@Override
			public String getObject() throws UnknownHostException {
				return apiObject.getStocksHandler().getIndDeltaNoBasketsList().getLast().getAsJson().toString();
			}
			
			@Override
			public void setLoadedObject(String object) {
				apiObject.getStocksHandler().getIndDeltaNoBasketsList().add(new MyChartPoint(new JSONObject(object)));
			}

			@Override
			public String getResetObject() {
				return null;
			}
		});
		
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.indBasketsList) {
			@Override
			public String getObject() throws UnknownHostException {
				return apiObject.getIndBasketsList().getLast().getAsJson().toString();
			}
			
			@Override
			public void setLoadedObject(String object) {
				apiObject.getIndBasketsList().add(new MyChartPoint(new JSONObject(object)));
			}

			@Override
			public String getResetObject() {
				return null;
			}
		});
		
	}
}
