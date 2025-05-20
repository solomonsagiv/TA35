package dataBase;

import api.BASE_CLIENT_OBJECT;
import api.Manifest;
import service.MyBaseService;
import service.ServiceEnum;

//MySql class
public class DataBaseService extends MyBaseService {

    BASE_CLIENT_OBJECT client;
    IDataBaseHandler dataBaseHandler;

    public DataBaseService(BASE_CLIENT_OBJECT client, IDataBaseHandler dataBaseHandler) {
        super(client);
        this.client = client;
        this.dataBaseHandler = dataBaseHandler;
    }

    @Override
    public void go() {
        // DB runner
        if (Manifest.DB_UPLOAD) {
            dataBaseHandler.insert_data(getSleep());
        }
    }

    public IDataBaseHandler getDataBaseHandler() {
        return dataBaseHandler;
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public int getSleep() {
        return 100;
    }

    @Override
    public ServiceEnum getType() {
        return ServiceEnum.MYSQL_RUNNER;
    }
}
