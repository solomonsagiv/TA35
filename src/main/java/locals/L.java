package locals;

import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class L {

	private static DecimalFormat df100;
	private static DecimalFormat df10;
	private static DecimalFormat df;

	public static DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

	public static double modulu(double value) {
		while (true) {
			if (value % 10 == 0) {
				return value;
			}
			value = ((int) (value / 10)) * 10;
		}
	}

	public static DecimalFormat df() {
		if (df == null) {
			df = new DecimalFormat("#,##0;-#,##0");
			df.setNegativePrefix("-");
		}
		return df;
	}

	public static DecimalFormat format100() {
		if (df100 == null) {
			df100 = new DecimalFormat("#,##0.00;-#,##0.00");
			df100.setNegativePrefix("-");
		}
		return df100;
	}

	public static double present(double val, double base) {
		if (base != 0) {
			return floor(((val - base) / base) * 100, 100);
		} else {
			return 0;
		}
	}

	public static String coma(double d) {
		return NumberFormat.getNumberInstance(Locale.US).format(d);
	}

	public static String coma(int i) {
		return NumberFormat.getNumberInstance(Locale.US).format(i);
	}


	public static DecimalFormat format10() {
		if (df10 == null) {
			df10 = new DecimalFormat("#,##0.0;-#,##0.0");
			df10.setNegativePrefix("-");
		}
		return df10;
	}

	public static String format100(double num) {
		if (df100 == null) {
			df100 = new DecimalFormat("#,##0.00;-#,##0.00");
			df100.setNegativePrefix("-");
		}
		return df100.format(num);
	}

	public static String format10(double num) {
		if (df10 == null) {
			df10 = new DecimalFormat("#,##0.0;-#,##0.0");
			df10.setNegativePrefix("-");
		}
		return df10.format(num);
	}

	public void popUp(JFrame frame, String text) {
		JOptionPane.showMessageDialog(frame, text);
	}

	public void popUp(JFrame frame, Exception e) {
		String text = e.getMessage() + "\n" + e.getCause();
		JOptionPane.showMessageDialog(frame, text);
	}

	// noisy
	public static void noisy(JTextField textField, Color color) {
		Runnable r = () -> {
			doNois(textField, color, Themes.LIGHT_BLUE);
		};
		new Thread(r).start();
	}

	static void doNois(JTextField textField, Color color, Color light_grey_back) {
		try {
			Color forg = textField.getForeground();

			for (int i = 0; i < 200; i++) {
				textField.setBackground(color);
				textField.setForeground(Color.WHITE);
				Thread.sleep(10);
			}

			textField.setForeground(forg);
			textField.setBackground(light_grey_back);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static LocalDate parseDate(String dateStr) {
		if (dateStr.length() == 8) {
			String year = dateStr.substring(0, 4);
			String month = dateStr.substring(4, 6);
			String day = dateStr.substring(6, 8);
			return LocalDate.parse(year + "-" + month + "-" + day);
		} else {
			return LocalDate.now();
		}
	}

	public static double floor(double d, int zeros) {
		return Math.floor(d * zeros) / zeros;
	}

	public static double dbl(String s) {
		return Double.parseDouble(s);
	}

	public static String str(Object o) {
		return String.valueOf(o);
	}

	public static int INT(String s) {
		return Integer.parseInt(s);
	}

	public static double opo(double d) {
		return d * -1.0;
	}

	public static double abs(double d) {
		return Math.abs(d);
	}

}
