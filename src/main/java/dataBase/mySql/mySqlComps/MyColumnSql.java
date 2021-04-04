package dataBase.mySql.mySqlComps;

import java.net.UnknownHostException;

public abstract class MyColumnSql< T > {

    public static final int STRING = 0;
    public static final int DOUBLE = 1;
    public static final int INT = 2;

    // Variables
    protected MySqlTable myTableSql;
    protected MySqlColumnEnum type;
    
    // Constructor
    public MyColumnSql(MySqlTable myTableSql, MySqlColumnEnum type) {
        this.myTableSql = myTableSql;
        this.type = type;
    }

    // Abstracts functions
    public abstract T getObject() throws UnknownHostException;

    // Getters and setters
    public MySqlColumnEnum getType() {
        return type;
    }
}

