package dataBase.mySql.tables;

import java.sql.ResultSet;

import arik.Arik;
import dataBase.mySql.MySql;
import dataBase.mySql.mySqlComps.MySqlTable;

public class BoundsTable extends MySqlTable {

	// Constructor
	public BoundsTable(String tableName) {
		super(tableName);
	}

	public ResultSet getBound(String stockName, String name) {
		try {
			String query = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s'", "bounds.bounds", "stockName",
					stockName, "name", name);
			return MySql.select(query);
		} catch (Exception e) {
			e.printStackTrace();
			Arik.getInstance().sendErrorMessage(e);
		}
		return null;
	}

	private void updateBound(String stockName, String name, int x, int y, int width, int height) {
		try {
			String query = String.format(
					"UPDATE `bounds`.`bounds` SET `stockName`='%s', `name`='%s', `x`='%s', `y`='%s', "
							+ "`width`='%s', `height`='%s' WHERE `name`='%s';",
					stockName, name, x, y, width, height, name);
			// Update the new bound
			MySql.update(query);
		} catch (Exception e) {
			e.printStackTrace();
			Arik.getInstance().sendErrorMessage(e);
		}
	}
	
	public void updateBoundOrCreateNewOne(String stockName, String name, int x, int y, int width, int height) {
		try {
			boolean exist = false;
			
			String selectQuery = "SELECT * FROM bounds.bounds;";

			ResultSet rs = MySql.select(selectQuery);
			while (rs.next()) {
				
				String nameFromDb = rs.getString("name");
				String stockNameFromDb = rs.getString("stockName");

				// For each check if exist
				if (stockNameFromDb.equals(stockName) && nameFromDb.equals(name)) {
					exist = true;
					break;
				}
			}
			
			// If not exist -> create new one
			if (!exist) {
				String query = String.format(
						"INSERT INTO `bounds`.`bounds` (`stockName`, `name`, `x`, `y`, `width`, `height`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
						stockName, name, x, y, width, height);
				MySql.insert(query);
			} else {
				updateBound(stockName, name, x, y, width, height);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void initColumns() {
	}

	@Override
	public void insert() {

	}

	@Override
	public void load() {

	}

	@Override
	public void update() {

	}

	@Override
	public void reset() {

	}
}
