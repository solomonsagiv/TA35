package tables;

import java.util.ArrayList;

public class Program {

	// Variables
	ArrayList<ArrayList<Integer>> positions = new ArrayList<>();

	// Main
	public static void main(String[] args) {
		Program program = new Program();
		program.run();
	}

	public void run() {
		// Create an alphabet to work with
		String[] alphabet = new String[] { "-1", "0", "1", "0", "0", "0", "0", "0", "0", "0" };
		// Find all possible combinations of this alphabet in the string size of 3
		possibleStrings(10, alphabet, "");

		for (ArrayList<Integer> position : positions) {
			System.out.println(position);
		}

		System.out.println(positions.size());
	}

	// Find all the possible strings
	public void possibleStrings(int maxLength, String[] alphabet, String curr) {
		int length;
		if (curr.contains("-")) {
			length = curr.replace("-", "").length();
		} else {
			length = curr.length();
		}

		// If the current string has reached it's maximum length
		if (length == maxLength) {
			ArrayList<Integer> position = string_to_list(curr);
			System.out.println(position);
			if (is_legal(position)) {
				positions.add(position);
			}

			// Else add each letter from the alphabet to new strings and process these new
			// strings again
		} else {
			for (int i = 0; i < alphabet.length; i++) {
				String oldCurr = curr;
				curr += alphabet[i];
				possibleStrings(maxLength, alphabet, curr);
				curr = oldCurr;
			}
		}
	}

	// Convert string to list of integers
	public ArrayList<Integer> string_to_list(String s) {
		ArrayList<Integer> pos = new ArrayList<>();
		boolean was_minus = false;
		String minus = null;

		for (char c : s.toCharArray()) {
			try {
				if (was_minus) {
					minus += Character.toString(c);
					int num = Integer.parseInt(minus);
					pos.add(num);
					was_minus = false;
				} else {
					int num = Integer.parseInt(Character.toString(c));
					pos.add(num);
				}
			} catch (NumberFormatException e) {
				minus = Character.toString(c);
				was_minus = true;
			}
		}
		return pos;
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
