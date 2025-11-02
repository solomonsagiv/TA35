package locals;

import api.BASE_CLIENT_OBJECT;
import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class L {

	private static DecimalFormat df100;
	private static DecimalFormat df10;
	private static DecimalFormat df;

	public static Set<BASE_CLIENT_OBJECT> stocks = new LinkedHashSet<>();


	public static final double RACE_MARGIN = 0.001;

	public static DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);


	public static void main(String[] args) {
		double d = -105090;
		System.out.println(L.format_int_2(d));
	}

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

	public static double list_avg(List<Double> list) {

		double sum = 0.0;
		for (double num : list) {
			sum += num;
		}
		return list.isEmpty() ? 0.0 : sum / list.size();
	}

	public static int row_to_int(Object o) {
		int num;

		if (o instanceof Number) {
			// This covers BigDecimal, Double, Integer, etc.
			num = ((Number) o).intValue();
		} else {
			throw new IllegalArgumentException("Value for key 'p' is not a number: " + o);
		}
		return num;
	}

	public static double row_to_double(Object o) {
		double num;

		if (o instanceof Number) {
			// This covers BigDecimal, Double, Integer, etc.
			num = ((Number) o).doubleValue();
		} else {
			throw new IllegalArgumentException("Value for key 'p' is not a number: " + o);
		}

		return num;
	}

	public static java.sql.Date row_to_date(Object o) {
		if (o instanceof java.sql.Timestamp) {
			java.sql.Timestamp ts = (java.sql.Timestamp) o;
			return new java.sql.Date(ts.getTime());
		} else if (o instanceof java.util.Date) {
			return (java.sql.Date) o;
		} else {
			throw new IllegalArgumentException("Invalid type for created_at: " + o);
		}
	}

	public static double present(double val, double base) {
		if (base != 0) {
			return floor(((val - base) / base) * 100, 100);
		} else {
			return 0;
		}
	}

	public static String format_int(double val) {
		DecimalFormat df = new DecimalFormat("#,##0;-#,##0");
		df.setNegativePrefix("-");
		return df.format(val);
	}

	public static String format_int_2(double val) {
		DecimalFormat df = new DecimalFormat("#,##0;(#,##0)");
		return df.format(val);
	}

	public static String cell(int row, int col) {
		return String.format("R%sC%s", row, col);
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

	public static double max_value(List<Double> list) {
		return Collections.max(list);
	}

	public static double min_value(List<Double> list) {
		return Collections.min(list);
	}

	public static String capitalizeFirstLetter(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}


	public static class FixedSizeDoubleList {
		private final int capacity;
		private final LinkedList<Double> list;

		public FixedSizeDoubleList(int capacity) {
			if (capacity <= 0) {
				throw new IllegalArgumentException("Capacity must be greater than zero.");
			}
			this.capacity = capacity;
			this.list = new LinkedList<>();
		}

		public void add(double value) {
			if (list.size() == capacity) {
				list.removeFirst();
			}
			list.add(value);
		}

		public Double findMaxAbs() {
			if (list.isEmpty()) {
				return null;
			}
			double max = list.get(0);
			for (double val : list) {
				if (Math.abs(val) > Math.abs(max)) {
					max = val;
				}
			}
			return max;
		}

		// ✅ Method to get the average
		public Double getAverage() {
			if (list.isEmpty()) {
				return null;
			}
			return list.stream()
					.mapToDouble(Double::doubleValue)
					.average()
					.orElse(0.0);
		}

		public int size() {
			return list.size();
		}

		public Double get(int index) {
			if (index < 0 || index >= list.size()) {
				return null;
			}
			return list.get(index);
		}

		@Override
		public String toString() {
			return list.toString();
		}
	}


	public static boolean equalsIgnoreCaseAndSpaces(String s1, String s2) {
		if (s1 == null || s2 == null) return false;
		String normalized1 = s1.replaceAll("\\s+", "").toLowerCase();
		String normalized2 = s2.replaceAll("\\s+", "").toLowerCase();
		return normalized1.equals(normalized2);
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
