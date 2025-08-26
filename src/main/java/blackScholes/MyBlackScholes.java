package blackScholes;


import api.TA35;
import options.Option;

public class MyBlackScholes {


	static TA35 client = TA35.getInstance();

	// Get stDev
	public static double[] getStDev(Option option) {
		try {
			// Find stDev
			if (option.getStDev() == 0) {
				double stdv = findSTDEV(option)[4];
				option.setStDev(stdv);
			}

			// Update stDev

			// Current option
			double currentOptionStDev = getStDevByOption(option, option.getStDev())[4];

			// Oposite Option
			Option opositeOption = getOpositeOption(option);
			return getStDevByOption(opositeOption, currentOptionStDev);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	static double[] findSTDEV(Option option) throws Exception {
		
		// TEST DATA -- trade 106005
		double targetPrice = client.getExps().getMonth().getOptions().getContract();
//		double timeToExp = TA35.getDaysToExp() / 365.0;
		double timeToExp = 1;
		double stDev = 0.001;
		double bid = option.getBid() / 100;
		double ask = option.getAsk() / 100;
		double interest = 1.045;
		Boolean callPut = option.getSide().toLowerCase().contains("c") ? true : false;

		// run till find the right price
		while (true) {
			double calcPrice = BlackScholesFormula.calculate(callPut, targetPrice, option.getStrike(), interest,
					timeToExp, stDev);

			double avgTarget = (bid + ask) / 2;

			if (calcPrice > avgTarget) {

				// Oposite option sides
				stDev = testOpositeOption(option, targetPrice, interest, stDev, timeToExp)[4];

				OptionDetails optionDetails = new OptionDetails(callPut, targetPrice, option.getStrike(), interest,
						timeToExp, stDev);
				optionDetails = BlackScholesFormula.calculateWithGreeks(optionDetails);
				double[] greeks = optionDetails.getGreeaks();

				return greeks;
			}

			stDev += 0.0001;
			if (stDev > 1) {
				double[] d = new double[6];
				return d;
			}
		}
	}

	static double[] testOpositeOption(Option option, double targetPrice, double interest, double stDev,
			double timeToExp) {

		// Oposite Option
		Option opositeOption = getOpositeOption(option);
		return getStDevByOption(opositeOption, stDev);
	}

	static Option getOpositeOption(Option option) {

		if (option.getName().toLowerCase().contains("c")) {

			String opositeName = option.getName().replace("c", "p");

			return client.getExps().getMonth().getOptions().getOption(opositeName);

		} else {

			String opositeName = option.getName().replace("p", "c");

			return  client.getExps().getMonth().getOptions().getOption(opositeName);

		}
	}

	static double[] getStDevByOption(Option option, double stDev) {

		double targetPrice = client.getExps().getMonth().getOptions().getContract();
		double interest = 1.045;
		double timeToExp = 1 / 365.0;

		Boolean callPut = option.getSide().toLowerCase().contains("c") ? true : false;
		double calcPrice = (getGreeaks(option.getStrike(), callPut, targetPrice, interest, timeToExp, stDev)[0]) * 100;
		double avgPrice = (option.getBid() + option.getAsk()) / 2;

		// If bigger than ask
		if (calcPrice > avgPrice) {

			while (calcPrice > avgPrice) {

				calcPrice = (getGreeaks(option.getStrike(), callPut, targetPrice, interest, timeToExp, stDev)[0]) * 100;
				stDev -= 0.0001;

				if (calcPrice < avgPrice) {
					double[] greeks = getGreeaks(option.getStrike(), callPut, targetPrice, interest, timeToExp, stDev);
					return greeks;
				}
			}
		}

		// If smaller than bid
		if (calcPrice < avgPrice) {

			while (calcPrice < avgPrice) {

				calcPrice = (getGreeaks(option.getStrike(), callPut, targetPrice, interest, timeToExp, stDev)[0]) * 100;
				stDev += 0.0001;

				if (calcPrice > avgPrice) {
					double[] greeks = getGreeaks(option.getStrike(), callPut, targetPrice, interest, timeToExp, stDev);
					return greeks;
				}
			}
		}

		return getGreeaks(option.getStrike(), callPut, targetPrice, interest, timeToExp, stDev);
	}

	// Greeaks
	@SuppressWarnings("static-access")
	public static double[] getGreeaks(double strike, boolean CallPut, double price, double interest, double timeToExp,
			double stDev) {
		OptionDetails optionDetails = new OptionDetails(CallPut, price, strike, interest, timeToExp, stDev);

		optionDetails = BlackScholesFormula.calculateWithGreeks(optionDetails);
		double[] d = optionDetails.getGreeaks();
		return d;
	}

}
