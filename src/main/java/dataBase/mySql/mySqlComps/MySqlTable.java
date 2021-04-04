package dataBase.mySql.mySqlComps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import api.ApiObject;
import arik.Arik;
import dataBase.mySql.MySql;

public abstract class MySqlTable implements IMyTableSql {

	// Variables
	protected ApiObject apiObject = ApiObject.getInstance();
	protected String name;

	protected String schemaName = "stocks";

	protected Map<MySqlColumnEnum, MyColumnSql> columns = new HashMap<>();
	protected Map<MySqlColumnEnum, MyLoadAbleColumn> loadAbleColumns = new HashMap<>();

	public void updateColumn(MySqlColumnEnum columnEnum, MyColumnSql myColumnSql) {
		columns.put(columnEnum, myColumnSql);

		if (myColumnSql instanceof MyLoadAbleColumn) {
			loadAbleColumns.put(columnEnum, (MyLoadAbleColumn) myColumnSql);
		}
	}

	// Constructor
	public MySqlTable(String name) {
		this.name = name;
		initColumns();
	}

	protected void addColumn(MyColumnSql column) {

		// Is loadable
		if (column instanceof MyLoadAbleColumn) {
			loadAbleColumns.put(column.getType(), (MyLoadAbleColumn) column);
		}

		columns.put(column.getType(), column);

	}

	public String getName() {
		return name;
	}

	protected void updateSpecificCols(ArrayList<MyColumnSql> columns) {
		StringBuilder query = new StringBuilder(String.format("UPDATE `%s`.`%s` SET ", schemaName, name));

		int i = 0;

		for (MyColumnSql column : columns) {
			try {
				if (i < columns.size() - 1) {
					query.append("`" + column.getType().getName() + "`='" + column.getObject() + "',");
				} else {
					query.append("`" + column.getType().getName() + "`='" + column.getObject() + "'");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}

		String endQuery = String.format("WHERE `id`='%s';", 1);

		query.append(endQuery);

		MySql.update(query.toString());
	}

	@Override
	public void insert() {

		String query = String.format("INSERT INTO `%s`.`%s` ", schemaName, getName());
		StringBuilder insertQuery = new StringBuilder(query);
		StringBuilder insertColumns = new StringBuilder();

		String values = " VALUES ";
		StringBuilder valuesColumns = new StringBuilder();

		int i = 0;

		for (Map.Entry<MySqlColumnEnum, MyColumnSql> entry : columns.entrySet()) {
			try {
				MyColumnSql column = entry.getValue();

				if (i < columns.size() - 1) {
					// Columns
					insertColumns.append("`" + column.getType().getName() + "`,");
					// Values
					valuesColumns.append("'" + column.getObject() + "',");
				} else {
					// Columns
					insertColumns.append("`" + column.getType().getName() + "`");
					// Values
					valuesColumns.append("'" + column.getObject() + "'");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}

		String columns = "(" + insertColumns + ")";
		String vColumns = "(" + valuesColumns + ")";

		insertQuery.append(columns).append(values).append(vColumns);

		// Insert
		MySql.insert(insertQuery.toString());

	}

	@Override
	public void update() {
		StringBuilder query = new StringBuilder(String.format("UPDATE `%s`.`%s` SET ", schemaName, getName()));
		int i = 0;
		for (Map.Entry<MySqlColumnEnum, MyColumnSql> entry : columns.entrySet()) {
			try {
				MyColumnSql column = entry.getValue();

				if (i < columns.size() - 1) {
					query.append("`" + column.getType().getName() + "`='" + column.getObject() + "',");
				} else {
					query.append("`" + column.getType().getName() + "`='" + column.getObject() + "'");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}

		String endQuery = String.format("WHERE `id`='%s';", 1);

		query.append(endQuery);

		MySql.update(query.toString());

	}

	@Override
	public void load() {
		try {

			String query = String.format("SELECT * FROM %s.%s WHERE id ='%S'", schemaName, getName(), 1);

			ResultSet rs = MySql.select(query);

			while (rs.next()) {
				for (Map.Entry<MySqlColumnEnum, MyLoadAbleColumn> entry : loadAbleColumns.entrySet()) {
					MyLoadAbleColumn column = entry.getValue();

					if (column.getType().getDataType() == MySqlDataTypeEnum.DOUBLE) {
						double d = rs.getDouble(column.getType().getName());
						System.out.println(column.getType().getName());
						column.setLoadedObject(d);
						continue;
					}

					if (column.getType().getDataType() == MySqlDataTypeEnum.INT) {
						int i = rs.getInt(column.getType().getName());
						column.setLoadedObject(i);
						continue;
					}

					if (column.getType().getDataType() == MySqlDataTypeEnum.STRING) {
						String s = rs.getString(column.getType().getName());
						if (s != null && !s.equals("")) {
							column.setLoadedObject(s);
						}
						continue;
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			Arik.getInstance().sendErrorMessage(e);
		}
	}

	public void loadAll() {
		try {

			String query = String.format("SELECT * FROM %s.%s", schemaName, getName(), 1);

			ResultSet rs = MySql.select(query);

			while (rs.next()) {

				for (Map.Entry<MySqlColumnEnum, MyLoadAbleColumn> entry : loadAbleColumns.entrySet()) {
					MyLoadAbleColumn column = entry.getValue();

					if (column.getType().getDataType() == MySqlDataTypeEnum.DOUBLE) {
						double d = rs.getDouble(column.getType().getName());
						column.setLoadedObject(d);
						continue;
					}

					if (column.getType().getDataType() == MySqlDataTypeEnum.INT) {
						int i = rs.getInt(column.getType().getName());
						column.setLoadedObject(i);
						continue;
					}

					if (column.getType().getDataType() == MySqlDataTypeEnum.STRING) {
						String s = rs.getString(column.getType().getName());
						column.setLoadedObject(s);
						continue;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Arik.getInstance().sendErrorMessage(e);
		}
	}

	@Override
	public void reset() {
		StringBuilder query = new StringBuilder(String.format("UPDATE `%s`.`%s` SET ", schemaName, getName()));
		int i = 0;

		for (Map.Entry<MySqlColumnEnum, MyLoadAbleColumn> entry : loadAbleColumns.entrySet()) {
			try {
				MyLoadAbleColumn column = entry.getValue();

				column.setLoadedObject(column.getResetObject());

				if (i < loadAbleColumns.size() - 1) {
					query.append("`" + column.getType().getName() + "`='" + column.getResetObject() + "',");
				} else {
					query.append("`" + column.getType().getName() + "`='" + column.getResetObject() + "'");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}

		String endQuery = String.format(" WHERE `id`='%s';", 1);

		query.append(endQuery);

		MySql.update(query.toString());
	}

	public void setName(String name) {
		this.name = name;
	}
}
