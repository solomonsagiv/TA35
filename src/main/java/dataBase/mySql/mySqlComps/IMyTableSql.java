package dataBase.mySql.mySqlComps;

public interface IMyTableSql {

    void initColumns();
    void insert();
    void load();
    void update();
    void reset();

}
