package TA_35.ta_35;

import java.time.Duration;
import java.time.Instant;

public class TestStuff {

	public static void main(String[] args) throws Exception {
		
		Instant start = Instant.now();
		// Do the code here...
		
	      System.out.println();
		
		// End...
		Instant end = Instant.now();
		
		System.out.println("Completed in: " + Duration.between(start, end).toMillis());
	}
	
}

