package dataBase.mySql.tables.jsonTables;

import java.time.LocalDate;

import dataBase.mySql.myBaseTables.MySumTable;
import dataBase.mySql.mySqlComps.MyColumnSql;
import dataBase.mySql.mySqlComps.MyLoadAbleColumn;
import dataBase.mySql.mySqlComps.MySqlColumnEnum;
import myJson.JsonStrings;
import myJson.MyJson;
import options.Options;

public class SumJsonTable extends MySumTable {
	
	public SumJsonTable(String name) {
		super(name);
	}
	
	@Override
	public void initColumns() {
		
		Options optionsMonth = apiObject.getExpMonth().getOptions();
		Options optionsWeek = apiObject.getExpWeek().getOptions();
		
		addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.date) {
			@Override
			public String getObject() {
				return LocalDate.now().toString();
			}
		});
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.data) {
			@Override
			public void setLoadedObject(String object) {
				apiObject.loadFromJson(new MyJson(object));
			}
			@Override
			public String getResetObject() {
				return apiObject.getResetJson().toString();
			}
			@Override
			public String getObject() {
				MyJson json = apiObject.getAsJson();
				json.put(JsonStrings.rando, apiObject.getRando());
				json.put(JsonStrings.open, apiObject.getOpen());
				json.put(JsonStrings.high, apiObject.getHigh());
				json.put(JsonStrings.low, apiObject.getLow());
				json.put(JsonStrings.tomorrow_future, optionsWeek.getContract() - apiObject.getIndex());
				return json.toString();
			}
		});
	}
}
