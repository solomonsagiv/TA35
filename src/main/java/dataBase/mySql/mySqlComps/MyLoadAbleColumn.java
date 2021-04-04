package dataBase.mySql.mySqlComps;

public abstract class MyLoadAbleColumn<T> extends MyColumnSql {

    public MyLoadAbleColumn(MySqlTable myTableSql, MySqlColumnEnum columnType) {
        super(myTableSql, columnType);
    }
    
    public abstract void setLoadedObject(T object);
    
    public abstract T getResetObject();
}
