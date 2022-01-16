
package counter;
import api.ApiObject;
import dataBase.mySql.JibeConnectionPool;

public class Main {
	/**
	 * @throws InterruptedException 
	 * @wbp.parser.entryPoint
	 */
	
	public static void main(String[] args) throws InterruptedException {
		JibeConnectionPool.getConnectionsPoolInstance();
//		SloConnectionPool.getConnectionsPoolInstance();
		ApiObject apiObject = ApiObject.getInstance();

		Thread.sleep(1000);
		new WindowTA35();
	}
}
