
package counter;

import api.TA35;
import dataBase.mySql.JibeConnectionPool;

public class Main {
	/**
	 * @throws InterruptedException 
	 * @wbp.parser.entryPoint
	 */

	public static void main(String[] args) throws InterruptedException {
//		JibeConnectionPool.getConnectionsPoolInstance();
		JibeConnectionPool.getConnectionsPoolInstance();
		TA35 client = TA35.getInstance();

		Thread.sleep(1000);
		new WindowTA35();
	}
}
