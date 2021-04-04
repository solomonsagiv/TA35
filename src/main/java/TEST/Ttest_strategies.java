package TEST;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Ttest_strategies {

	static String url = "http://apitest.bizportal.co.il/DataApi/RequestHandler?ClientId=181&RequestId=105&paperid=11111027";

	public static void main(String[] args) throws Exception {
		Ttest_strategies strategies = new Ttest_strategies();

		// Start
		Instant start = Instant.now();

		strategies.run();

		// End
		Instant end = Instant.now();

		System.out.println("Completed in: " + Duration.between(start, end).toMillis());
	}

	public void run() {

		ArrayList<ArrayList<Integer>> positions = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> strikes = new ArrayList<>();

		for (int strike = 1570; strike < 1680; strike += 10) {
			strikes.add(strike);
		}

		for (int i = 0; i < 500000; i++) {
			boolean know_this_pos = false;
			ArrayList<Integer> position = new ArrayList<>();
			position.add(random());
			position.add(random());
			position.add(random());
			position.add(0);
			position.add(0);
			position.add(0);
			position.add(0);
			position.add(0);
			position.add(0);
			position.add(0);

			Collections.shuffle(position);

			// Is it legal position
			if (is_legal(position)) {

				// Check if know this position
				for (ArrayList<Integer> p : positions) {
					if (compare(p, position)) {
						know_this_pos = true;
						break;
					}
				}
				// If dont know this position add the position
				if (!know_this_pos) {
					positions.add(position);
					System.out.println(position);
				}
			} else {
				// System.out.println(i);
			}
		}
		System.out.println("Total position found: " + positions.size());
	}

	// Get random int -1 to 1
	public int random() {
		Random ran = new Random();
		int x = ran.nextInt(5) - 2;

		return x;
	}

	// Compare two lists
	public boolean compare(ArrayList<Integer> first, ArrayList<Integer> seconde) {
		if (first.size() == seconde.size()) {
			for (int i = 0; i < first.size(); i++) {
				if (first.get(i) != seconde.get(i)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	// Is legal
	public boolean is_legal(ArrayList<Integer> strategy) {
		boolean positive = false;
		boolean negative = false;

		for (int i : strategy) {
			if (i > 0) {
				positive = true;
			}
			if (i < 0) {
				negative = true;
			}
		}
		if (positive && negative) {
			return true;
		} else {
			return false;
		}
	}
}
