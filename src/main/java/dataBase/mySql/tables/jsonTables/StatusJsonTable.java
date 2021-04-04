package dataBase.mySql.tables.jsonTables;

import java.time.LocalDate;
import java.time.LocalTime;

import dataBase.mySql.myBaseTables.MyStatusTable;
import dataBase.mySql.mySqlComps.MyColumnSql;
import dataBase.mySql.mySqlComps.MyLoadAbleColumn;
import dataBase.mySql.mySqlComps.MySqlColumnEnum;
import myJson.MyJson;

public class StatusJsonTable extends MyStatusTable {
	
	public StatusJsonTable(String name) {
		super(name);
	}
	
	@Override
	public void initColumns() {
		addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.date) {
			@Override
			public String getObject() {
				return LocalDate.now().toString();
			}
		});
		addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.time) {
			@Override
			public String getObject() {
				return LocalTime.now().toString();
			}
		});
		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.data) {
			@Override
			public void setLoadedObject(String object) {
				MyJson json = new MyJson(object);
				if (json.length() != 0) {
					apiObject.loadFromJson(json);
				}
			}
			@Override
			public String getResetObject() {
				return apiObject.getResetJson().toString();
			}
			@Override
			public String getObject() {
				return apiObject.getAsJson().toString();
			}
		});
	}
}
