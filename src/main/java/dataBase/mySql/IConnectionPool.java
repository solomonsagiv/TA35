package dataBase.mySql;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionPool {

    Connection getConnection() throws SQLException;

    boolean releaseConnection( Connection connection );

    String getUrl();

    String getUser();

    String getPassword();
}
