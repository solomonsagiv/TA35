package dataBase;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import dataBase.mySql.IConnectionPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Prod implements IConnectionPool {

    // Singleton instance of the connection pool
    private static volatile Prod instance;
    private final HikariDataSource dataSource;
    private String url;
    private String user;
    private String password;

    // Private constructor to prevent instantiation
    private Prod() {
        HikariConfig config = new HikariConfig();
        // Configure the connection pool here
        config.setJdbcUrl("jdbc:postgresql://34.203.91.131:5432/jibe?ssl=true&sslmode=require"); // Replace with your JDBC URL
        config.setUsername("sagiv");
        config.setPassword("f19add32-1141-4af5-9abd-4744487f3b51");

        // Optional: Set additional HikariCP properties
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // Create the HikariDataSource (the connection pool)
        dataSource = new HikariDataSource(config);
    }

    // Public method to get the singleton instance of the connection pool
    public static Prod getInstance() {
        // Double-checked locking to ensure thread-safe lazy initialization
        if (instance == null) {
            synchronized (Prod.class) {
                if (instance == null) {
                    instance = new Prod();
                }
            }
        }
        return instance;
    }

    // Expose the DataSource to the application
    public DataSource getDataSource() {
        return dataSource;
    }

    // Close the connection pool
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public HikariPoolMXBean get_statistic() {
        HikariDataSource dataSource = (HikariDataSource) getInstance().getDataSource();
        return dataSource.getHikariPoolMXBean();
    }


    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        return false;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }
}










