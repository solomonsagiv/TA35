package dataBase.mySql.myBaseTables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;

import arik.Arik;
import dataBase.mySql.MySql;
import dataBase.mySql.mySqlComps.MyLoadAbleColumn;
import dataBase.mySql.mySqlComps.MySqlColumnEnum;
import dataBase.mySql.mySqlComps.MySqlDataTypeEnum;
import dataBase.mySql.mySqlComps.MySqlTable;

public abstract class MyArraysTable extends MySqlTable {

	public MyArraysTable(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void insert() {
		super.insert();
	}

	@Override
	public void load() {
		try {
			String query = String.format("SELECT * FROM %s.%s;",schemaName, name);
			
			ResultSet rs = MySql.select(query);

			if (rs != null) {

				while (rs.next()) {
					for (Map.Entry<MySqlColumnEnum, MyLoadAbleColumn> entry : loadAbleColumns.entrySet()) {

						try {

							MyLoadAbleColumn column = entry.getValue();

							if (column.getType().getDataType() == MySqlDataTypeEnum.DOUBLE) {
								double d = rs.getDouble(column.getType().getName());
								column.setLoadedObject(d);
								continue;
							}
							
							if (column.getType().getDataType() == MySqlDataTypeEnum.STRING) {
								String s = rs.getString(column.getType().getName());
								if (s != null && !s.isEmpty()) {
									column.setLoadedObject(s);
								}
								continue;
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Arik.getInstance().sendErrorMessage(e);
		}
	}

	@Override
	public void update() {
	}
	
	@Override
	public void reset() {
		try {
			MySql.trunticate(name);
		} catch (Exception e) {
			e.printStackTrace();
			Arik.getInstance().sendErrorMessage(e);
		}
	}

	// Convert json array to arrayList<Double>
	public void convertJsonArrayToDoubleArray(JSONArray jsonArray, ArrayList<Double> list) {

		for (int i = 0; i < jsonArray.length(); i++) {

			list.add(jsonArray.getDouble(i));

		}

	}

}
