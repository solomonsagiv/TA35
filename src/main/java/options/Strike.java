package options;

import java.util.Objects;

public class Strike {

	private Option call;
	private Option put;
	private double strike;
	private double iv;
	// Constructors
	public Strike() {}

	public Strike(double strike) {
		this.strike = strike;
	}

	public Strike(Option call, Option put, double strike) {
		this.call = call;
		this.put = put;
		this.strike = strike;
	}

	// --- Getters / Setters ---
	public Option getCall() { return call; }
	public void setCall(Option call) { this.call = call; }

	public Option getPut() { return put; }
	public void setPut(Option put) { this.put = put; }

	public double getStrike() { return strike; }
	public void setStrike(double strike) { this.strike = strike; }

	public double getIv() { return iv; }
	public void setIv(double volatility) { 
		this.iv = volatility;
		call.setIv(iv);
		put.setIv(iv);
	 }

	// --- Helpers ---
	public boolean hasCall() { return call != null; }
	public boolean hasPut()  { return put  != null; }
	public boolean isComplete() { return hasCall() && hasPut(); }
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Strike " + strike);
		if (hasCall()) {
			sb.append("\n  Call: ")
					.append(call.getName())
					.append(" Δ=").append(call.getDelta())
					.append(" BA=").append(call.getBidAskCounter());
		} else {
			sb.append("\n  Call: null");
		}

		if (hasPut()) {
			sb.append("\n  Put : ")
					.append(put.getName())
					.append(" Δ=").append(put.getDelta())
					.append(" BA=").append(put.getBidAskCounter());
		} else {
			sb.append("\n  Put : null");
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Strike)) return false;
		Strike strike1 = (Strike) o;
		return Double.compare(strike1.strike, strike) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(strike);
	}
}
