package dataBase.mySql.myBaseTables;

import dataBase.mySql.mySqlComps.MySqlTable;

public abstract class MyStatusTable extends MySqlTable {

    public MyStatusTable(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	// Constructor

    @Override
    public void insert() {}

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void update() {
        super.update( );
    }

    @Override
    public void reset() {super.reset();}

}
