package dataBase.mySql;

import myJson.JsonStrings;
import myJson.MyJson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JibeConnectionPool implements IConnectionPool {
	
	private static final int MAX_POOL_SIZE = 25;
	
	// Instance
	private static JibeConnectionPool jibeConnectionPool;
	private static int INITIAL_POOL_SIZE = 15;
	private static String url;
	private static String user;
	private static String password;
	private static List<Connection> connections;
	private static List<Connection> usedConnections = new ArrayList<>();
	
	private JibeConnectionPool(String url, String user, String password, List<Connection> connections) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.connections = connections;
	}
	
	public static JibeConnectionPool getConnectionsPoolInstance() {
		if (jibeConnectionPool == null) {
			try {
				// String url =
				// "jdbc:mysql://sagivwork.ca16ljkfxgo3.us-east-2.rds.amazonaws.com:3306/ta35";
				// String user = "sagivAwsMaster";
				// String password = "Solomonsagivawsmaster12";

//				String url = "jdbc:mysql://parisdb.chuxlqcvlex2.eu-west-3.rds.amazonaws.com:3306/";
//				String user = "sagivMasterUser";
//				String password = "Solomonsagivawsmaster12";

				String url = "jdbc:postgresql://52.4.58.207:5432/jibe";
				String user = "jibe_admin";
				String password = "160633a0cd2ab5a9b82f088a77240cb68f9232a8";

				jibeConnectionPool = JibeConnectionPool.create(url, user, password);
			} catch (Exception e) {
				// Arik.getInstance( ).sendMessage( e.getMessage( ) + "\n" + e.getCause( ) );
			}
		}
		return jibeConnectionPool;
	}

	@SuppressWarnings("finally")
	public static JibeConnectionPool create(String url, String user, String password) throws SQLException {

		List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
		try {
			for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
				new Thread(() -> {
					try {
						pool.add(createConnection(url, user, password));
					} catch (SQLException throwables) {
						throwables.printStackTrace();
					}
				}).start();
			}
		} finally {
			return new JibeConnectionPool(url, user, password, pool);
		}
	}

	// standard constructors
	private static Connection createConnection(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	public static void addNewConnection() throws SQLException {
		connections.add(createConnection(url, user, password));
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		if (connections.isEmpty()) {
			if (usedConnections.size() < MAX_POOL_SIZE) {
				connections.add(createConnection(url, user, password));
			} else {
				throw new RuntimeException("Maximum pool size reached, no available connections!");
			}
		}
		
		Connection connection = connections.remove(connections.size() - 1);
		usedConnections.add(connection);
		return connection;
	}

	public void shutdown() throws SQLException {
		usedConnections.forEach(this::releaseConnection);
		for (Connection c : connections) {
			c.close();
		}
		connections.clear();
	}
	
	@Override
	public boolean releaseConnection(Connection connection) {
		connections.add(connection);
		return usedConnections.remove(connection);
	}
	
	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public int getSize() {
		return connections.size() + usedConnections.size();
	}
	
	public static MyJson getAsJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.connections, connections.size());
		json.put(JsonStrings.usedConnections, usedConnections.size());
		json.put(JsonStrings.connectionsLimit, MAX_POOL_SIZE);
		return json;
	}

}
