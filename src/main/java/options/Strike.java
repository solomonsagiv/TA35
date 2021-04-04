package options;

public class Strike {
	
	private Option call;
	private Option put;
	private double strike;
	
	// Constructor 
	public Strike() {}
	
	public Strike(Option call, Option put, double strike) {
		this.call = call;
		this.put = put;
		this.strike = strike;
		
	}
	
	// Getters and Setters 
	public Option getCall() {
		return call;
	}
	public void setCall(Option call) {
		this.call = call;
	}
	public Option getPut() {
		return put;
	}
	public void setPut(Option put) {
		this.put = put;
	}
	public double getStrike() {
		return strike;
	}
	public void setStrike(double strike) {
		this.strike = strike;
	}

	@Override
	public String toString() {
		
		String call = "null";
		String put = "null";
		
		if (getCall() != null) {
			call = getCall().toString();
		}
		
		if (getPut() != null) {
			put = getPut().toString();
		}
		
		return strike + "\n" + call + ", \n" + put;
	}
	
}
