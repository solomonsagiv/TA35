package yogi;

import java.util.Arrays;

public class Macros {
	
	int[] point;
	String key;
	
	public Macros() {}
	
	// Constructor 
	public Macros(int[] point, String key) {
		this.point = point;
		this.key = key;
	}

	@Override
	public String toString() {
		return "KeysMacros [point=" + Arrays.toString(point) + ", key=" + key + "]";
	}

	public int[] getPoint() {
		return point;
	}

	public void setPoint(int[] point) {
		this.point = point;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
	
}	
