package dataBase.mySql.tables.jsonTables;

import java.time.LocalDate;
import java.time.LocalTime;

import dataBase.mySql.myBaseTables.MyDayTable;
import dataBase.mySql.mySqlComps.MyColumnSql;
import dataBase.mySql.mySqlComps.MySqlColumnEnum;

public class DayJsonTable extends MyDayTable {
	
    public DayJsonTable(String name) {
		super(name);
	}
    
    @Override
    public void initColumns() {
        addColumn(new MyColumnSql<String>(this,  MySqlColumnEnum.date) {
            @Override
            public String getObject() {
                return LocalDate.now().toString();
            }
        });
        addColumn(new MyColumnSql<String>(this,  MySqlColumnEnum.time) {
            @Override
            public String getObject() {
                return LocalTime.now().toString();
            }
        });
        addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.data) {
            @Override
            public String getObject() {
                return apiObject.getAsJson().toString();
            }
        });
    }
}
