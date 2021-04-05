package counter;

import java.awt.Color;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JTextField;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.select.Elements;

import api.ApiObject;
import exp.Exp;
import locals.L;
import locals.Themes;
import options.Options;
import threads.MyThread;

public class Updater extends MyThread implements Runnable {
	
	ApiObject apiObject = ApiObject.getInstance();

	// local variables
	int count = 0;
	double avgDay;
	double avg;
	double madadPresentDay;

	LocalTime pre_trading_time = LocalTime.parse("09:31:00");
	LocalTime start_trading_time = LocalTime.parse("09:45:00");
	LocalTime current_time;

	// Avg list
	public ArrayList<Double> avg_day = new ArrayList<>();

	// test
	double efresh;
	JSONArray madadNetunim;
	double text;
	boolean run = true;

	Color lightGreen = new Color(12, 135, 0);
	Color lightRed = new Color(229, 19, 0);

	Elements pay;
	Elements company_for_israel;

	WindowTA35 window;

	long startTime;
	long endTime;

	int sleepCounter = 0;
	int sleep = 200;

	// Constructor
	public Updater(WindowTA35 window) {
		super();
		this.window = window;
		setRunnable(this);
	}

	@Override
	public void run() {
		while (run) {
			try {
				// Write the data to the window
				write();
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				run = false;
				System.out.println("Updater is stopped ");
			}
		}
	}

	// Write the data to the window
	private void write() throws InterruptedException {

		String streamMarket = "stream";

		Exp expMonth = apiObject.getExpMonth();
		Exp expWeek = apiObject.getExpWeek();
		Options optionsMonth = expMonth.getOptions();
		Options optionsWeek = expWeek.getOptions();

		try {
			count++;
			current_time = LocalTime.now();

			// OP
			efresh = expMonth.getOptions().getContract() - apiObject.getIndex();
			text = floor(efresh, 100);

			// After start trading
			if (apiObject.isStarted()) {
				// AVG OP
				text = floor(avg(), 10);
				setColor(window.op_avg, text, lightGreen);

				// RACES
				window.conUpField.setText(String.valueOf(apiObject.getConUp()));
				window.conDownField.setText(String.valueOf(apiObject.getConDown()));
				window.indUpField.setText(String.valueOf(apiObject.getIndUp()));
				window.indDownField.setText(String.valueOf(apiObject.getIndDown()));

				// Future counter
				setColorInt(window.conBidAskCounterMonthField, optionsWeek.getConBidAskCounter());
				setColorInt(window.conBidAskCounterWeekField, optionsMonth.getConBidAskCounter());

				// counts sum
				setColorInt(window.conSumField, apiObject.getConUp() - apiObject.getConDown());
				setColorInt(window.indSumField, apiObject.getIndUp() - apiObject.getIndDown());

				// Optimi Pesimi move
				window.pesimiBasketField.setText(str(apiObject.getPesimiLiveMove()));

				setColor(window.optimiMoveField, floor(apiObject.getOptimiLiveMove(), 10), lightGreen);
				setColor(window.pesimiMoveField, floor(apiObject.getPesimiLiveMove(), 10), lightGreen);

				// Baskets
				window.optimiBasketField.setText(str(apiObject.getBasketUp()));
				window.pesimiBasketField.setText(str(apiObject.getBasketDown()));
				setColorInt(window.basketsSumField, (apiObject.getBasketUp() - apiObject.getBasketDown()));

				// EqualMove
				setColor(window.equalMoveField, floor(apiObject.getEqualMove(), 10), lightGreen);

				// Delta calc
				// Month
				colorForge(window.monthDeltaField, (int) optionsMonth.getDelta(), L.df());
				// Week
				colorForge(window.weekDeltaField, (int) optionsWeek.getDelta(), L.df());

				// Ind baskets
				double indDelta = apiObject.getStocksHandler().getDelta();
				int baskets = apiObject.getBasketUp() - apiObject.getBasketDown();
				double indeDeltaNoBakets = indDelta + (baskets * -1000);
				
				// Ind delta
				colorForge(window.indexDeltaField, (int) indDelta, L.df());

				// Ind delta no baskets
				colorForge(window.indDeltaNoBasketsField, (int) indeDeltaNoBakets, L.df());

				// Exp
				// Week
				colorForge(window.expDeltaWeekField, (int) expWeek.getExpData().getTotalDelta(), L.df());
				colorForge(window.expIndDeltaWeekField, (int) expWeek.getExpData().getTotalIndDelta(), L.df());
				colorForge(window.expBasketsWeekField, expWeek.getExpData().getTotalBaskets(), L.df());
				text = floor(
						((apiObject.getIndex() - expWeek.getExpData().getStart()) / expWeek.getExpData().getStart())
								* 100,
						100);
				setColorPresent(window.weekStartExpField, text);

				// Month
				colorForge(window.expDeltaMonthField, (int) expMonth.getExpData().getTotalDelta(), L.df());
				colorForge(window.expIndDeltaMonthField, (int) expMonth.getExpData().getTotalIndDelta(), L.df());
				colorForge(window.expBasketsMonthField, expMonth.getExpData().getTotalBaskets(), L.df());
				text = floor(
						((apiObject.getIndex() - expMonth.getExpData().getStart()) / expMonth.getExpData().getStart())
								* 100,
						100);
				setColorPresent(window.monthStartExpField, text);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		run = false;
	}

	private double avg() {
		efresh = apiObject.getExpMonth().getOptions().getContract() - apiObject.getIndex();
		avg_day.add(efresh);
		double f = 0;
		for (int i = 0; i < avg_day.size(); i++) {
			f += avg_day.get(i);
		}
		return f / avg_day.size();
	}

	// avg
	public void avgFunction(JTextField avgMax, ArrayList<Double> list, int listLength) {
		if (list.size() < listLength) {
			list.add(efresh);
		} else {
			list.remove(0);
			list.add(efresh);
		}

		// for loop
		double sum = 0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i);
		}
		avg = sum / list.size();

		if (list.size() >= listLength) {
			text = floor(avg, 10);
			setColor(avgMax, text, Color.blue);
		}
	}

	// min
	public void lastMin(JTextField madadTEXT, ArrayList<Double> list, int listLength) throws JSONException {
		double madad = 0;

		if (list.size() < listLength) {
			list.add(apiObject.getIndex());
		} else {
			madad = list.get(0);
			list.remove(0);
			list.add(apiObject.getIndex());
			double madadPresent = ((apiObject.getIndex() / madad) * 100) - 100;

			text = floor(madadPresent, 100);
			setColorPresent(madadTEXT, text);
		}
	}

	// color setting function();
	public void setColor(JTextField textField, double text, Color color) {

		if (text >= 0.0) {
			textField.setForeground(color);
			textField.setText(String.valueOf(text));
		} else {
			textField.setForeground(Color.red);
			textField.setText(String.valueOf(text));
		}
	}

	// color setting function();
	public void setColorInt(JTextField textField, int text) {
		if (text >= 0.0) {
			textField.setForeground(lightGreen);
			textField.setText(String.valueOf(text));
		} else {
			textField.setForeground(lightRed);
			textField.setText(String.valueOf(text));
		}
	}

	// color setting function();
	public void setColorPresent(JTextField textField, double text) {

		if (text >= 0.0) {
			textField.setBackground(lightGreen);
			textField.setText(String.valueOf(text) + "% ");
		} else {
			textField.setBackground(lightRed);
			textField.setText(String.valueOf(text) + "% ");
		}
	}

	public void colorForge(JTextField textField, int val, DecimalFormat format) {
		if (val >= 0) {
			textField.setForeground(Themes.GREEN);
		} else {
			textField.setForeground(Themes.RED);
		}

		textField.setText(format.format(val));
	}

	// pars double function();
	public double dbl(String string) {
		return Double.parseDouble(string);
	}

	// floor function();
	public double floor(double d, int zeros) {
		return Math.floor(d * zeros) / zeros;
	}

	// To string
	public String str(Object o) {
		return String.valueOf(o);
	}

}
