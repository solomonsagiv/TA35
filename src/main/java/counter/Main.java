
package counter;
import api.ApiObject;

import java.util.Scanner;

public class Main {
	/**
	 * @throws InterruptedException 
	 * @wbp.parser.entryPoint
	 */

	public static void main(String[] args) throws InterruptedException {
		ApiObject apiObject = ApiObject.getInstance();
		Thread.sleep(1000);
		new WindowTA35();
	}
}
