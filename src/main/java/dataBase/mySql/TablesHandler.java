package dataBase.mySql;

import java.util.HashMap;
import java.util.Map;

import dataBase.mySql.mySqlComps.MySqlTable;
import dataBase.mySql.mySqlComps.TablesEnum;

public class TablesHandler {
	
    Map tables = new HashMap< TablesEnum, MySqlTable>();

    public MySqlTable getTable( TablesEnum tablesEnum ) {
        return ( MySqlTable ) tables.get( tablesEnum );
    }
    
    public void addTable( TablesEnum tablesEnum, MySqlTable myTableSql ) {
        tables.put( tablesEnum,  myTableSql );
    }

    public String getStatusName() {
        return "status";
    }

    public String getSettingName() {
        return "settings";
    }

    public String getArraysName() {
        return "arrays";
    }

    public Map getTables() {
        return tables;
    }

}
