package blackScholes;

import java.util.Date;

/**
 * Data class used to manage the specific data items for one option transaction.
 *
 * @author mblackford mBret Bret Blackford
 *
 */
public class OptionDetails {

	Boolean callOption;
	double spotPriceOfUnderlying;
	double strikePrice;
	double riskFreeInterestRate;
	double timeToExpire;
	double volatility;
	Date valuationDate;
	Date ExpDate;

	double optionValue;
	double delta;
	double theta;
	double rho;
	double gamma;
	double vega;

	public OptionDetails(Boolean call, double s, double k, double r, double t, double v) {

		callOption = call;
		spotPriceOfUnderlying = s;
		strikePrice = k;
		riskFreeInterestRate = r;
		timeToExpire = t;
		volatility = v;
	}

	// double[] greeks
	public double[] getGreeaks() {
		double[] greeks = new double[5];
		greeks[0] = optionValue;
		greeks[1] = delta * 100;
		greeks[2] = gamma;
		greeks[3] = vega;
		greeks[4] = volatility * 10;
		return greeks;
	}

	public String toString() {

		String out = "spot price [" + spotPriceOfUnderlying + "] strike [";
		out += strikePrice + "] int rate [" + riskFreeInterestRate + "] expire [";
		out += timeToExpire + "] vol [" + volatility + "] \n \n";

		out += "option value-[" + floor(optionValue * 100) + "] \n delta-[" + floor(delta * 100) + "] \n theta-[";
		out += floor(theta) + "] \n gamma-[" + floor(gamma) + "] \n vega-[" + floor(vega) + "]";

		return out;
	}

	// floor .00
	public static double floor(double d) {
		return Math.floor(d * 100) / 100;
	}
}
