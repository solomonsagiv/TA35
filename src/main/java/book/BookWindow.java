package book;

import api.ApiObject;
import charts.OptionChartWindow;
import counter.WindowTA35;
import options.Option;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.time.LocalTime;

public class BookWindow {

	String[] columns = { "כמות ", "מחיר ", "מחיר ", "כמות " };
	Object[][] data = new Object[5][4];
	boolean type;

	int bidAskCounter = 0;
	
	public JFrame frame;
	private JTable table;
	private JPanel panel;
	private JLabel label;
	private JLabel label_1;
	private int updater_id;
	private JTextField last;
	private JTextField high;
	private JTextField low;
	private JTextField base;
	private JLabel lblBase;
	JScrollPane scrollPane;
	String option;

	// The book updater thread
	BookUpdater bookUpdater;
	OptionChartWindow chartWindow;

	// Colors
	Color lightGreen = new Color(12, 135, 0);
	Color lightRed = new Color(229, 19, 0);

	// Fonts
	Font ariel_bold = new Font("Arial", Font.BOLD, 15);
	Font ariel = new Font("Arial", Font.PLAIN, 15);
	private JTextField lastPresent;
	private JTextField bidAskCounterField;

	public static void main(String[] args) {
		BookWindow window = new BookWindow(55, true);
		window.frame.setVisible(true);
	}

	/**
	 * Create the application.
	 * 
	 * @param type
	 */
	public BookWindow(int updater_id, boolean type) {
		this.updater_id = updater_id;
		this.type = type;
		// UI manager
		UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(Color.black));
		UIManager.put("InternalFrame.activeTitleForeground", new ColorUIResource(Color.WHITE));
		UIManager.put("InternalFrame.titleFont", new Font("Dialog", Font.PLAIN, 11));

		initialize();
		
		bidAskCounterField = new JTextField();
		bidAskCounterField.setHorizontalAlignment(SwingConstants.CENTER);
		bidAskCounterField.setFont(new Font("Arial", Font.PLAIN, 14));
		bidAskCounterField.setColumns(10);
		bidAskCounterField.setBounds(83, 2, 60, 20);
		panel.add(bidAskCounterField);
		
		
		// Create the book updater thread
		bookUpdater = new BookUpdater(high, low, last, base, table, lastPresent, bidAskCounterField, this);
		bookUpdater.start();
	}

	public void openOption(String strike, boolean callOrPut) {
		frame.setTitle(option);
		table = createTable(scrollPane, data, columns, option);

		bookUpdater.setTable(table);
		bookUpdater.setOption(strike);
		bookUpdater.setRun(true);

		// Create the window
		chartWindow = new OptionChartWindow(strike, callOrPut);
		chartWindow.pack();
		chartWindow.setVisible(true);
	}

	// Convert to string
	private String str(Object o) {
		return String.valueOf(o);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().setBackground(new Color(176, 196, 222));

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					bookUpdater.interrupt();
					chartWindow.onClose(e);
				} catch (NullPointerException exception) {

				}
			}
		});

		if (updater_id == 0) {
			frame.setBounds(778, 832, 238, 221);
		} else if (updater_id == 1) {
			frame.setBounds(1008, 832, 238, 221);
		} else {
			frame.setBounds(808, 832, 238, 221);
		}

		frame.getContentPane().setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 5, 224, 104);
		frame.getContentPane().add(scrollPane);

		panel = new JPanel();
		panel.setBackground(new Color(176, 196, 222));
		panel.setBounds(0, 113, 224, 71);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		label_1 = new JLabel("נמוך");
		label_1.setForeground(new Color(0, 0, 51));
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		label_1.setBounds(99, 25, 46, 20);
		panel.add(label_1);

		last = new JTextField();
		last.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					String option = JOptionPane.showInputDialog("Enter option").toLowerCase();
					frame.setTitle(option);
					table = createTable(scrollPane, data, columns, option);

					bookUpdater.setTable(table);
					bookUpdater.setOption(option);
					bookUpdater.setRun(true);

				} catch (NullPointerException e) {

				}
			}
		});
		last.setHorizontalAlignment(SwingConstants.CENTER);
		last.setFont(new Font("Arial", Font.PLAIN, 14));
		last.setColumns(10);
		last.setBounds(10, 2, 60, 20);
		panel.add(last);

		low = new JTextField();
		low.setHorizontalAlignment(SwingConstants.CENTER);
		low.setFont(new Font("Arial", Font.PLAIN, 14));
		low.setColumns(10);
		low.setBounds(83, 44, 60, 20);
		panel.add(low);

		base = new JTextField();
		base.setHorizontalAlignment(SwingConstants.CENTER);
		base.setFont(new Font("Arial", Font.PLAIN, 14));
		base.setColumns(10);
		base.setBounds(154, 44, 60, 20);
		panel.add(base);

		lblBase = new JLabel("בסיס");
		lblBase.setForeground(new Color(0, 0, 51));
		lblBase.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBase.setBounds(168, 25, 46, 20);
		panel.add(lblBase);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 25, 224, 9);
		panel.add(separator);

		lastPresent = new JTextField();
		lastPresent.setForeground(new Color(255, 255, 255));
		lastPresent.setHorizontalAlignment(SwingConstants.CENTER);
		lastPresent.setFont(new Font("Arial", Font.BOLD, 14));
		lastPresent.setColumns(10);
		lastPresent.setBounds(154, 2, 60, 20);
		panel.add(lastPresent);

		label = new JLabel("גבוה");
		label.setBounds(26, 25, 30, 20);
		panel.add(label);
		label.setForeground(new Color(0, 0, 51));
		label.setFont(new Font("Tahoma", Font.PLAIN, 12));

		high = new JTextField();
		high.setBounds(10, 44, 60, 20);
		panel.add(high);
		high.setHorizontalAlignment(SwingConstants.CENTER);
		high.setFont(new Font("Arial", Font.PLAIN, 14));
		high.setColumns(10);
	}

	// Show on screen
	public static void showOnScreen(int screen, JFrame frame) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		if (screen > -1 && screen < gd.length) {
			frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x + frame.getX(), frame.getY());
		} else if (gd.length > 0) {
			frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x + frame.getX(), frame.getY());
		} else {
			throw new RuntimeException("No Screens Found");
		}
	}

	// Get trade by name
	public Thread getThreadByName(String threadName) {
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().equals(threadName))
				return t;
		}
		return null;
	}

	// Get trade by name
	public boolean isThreadLive(String threadName) {
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().equals(threadName))
				return true;
		}
		return false;
	}

	// Get trade by name
	public void killThread(String threadName) {
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().equals(threadName)) {
				t.interrupt();
			}
		}
	}

	// Create table
	public JTable createTable(JScrollPane scrollPane, Object[][] data, String[] columns, String option) {

		// Create the table
		JTable table = new JTable(data, columns) {
			int call_bid = 0;
			int call_ask = 100000;
			int put_bid = 0;
			int put_ask = 0;
			int cell_val = 0;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

				try {
					Component component = super.prepareRenderer(renderer, row, col);
					cell_val = (int) (Double.parseDouble((String) getValueAt(row, col)));

					// Call
					if (option.contains("c")) {
						if (col == 0 || col == 1) {
							component.setForeground(lightGreen);
						} else if (col == 2 || col == 3) {
							component.setForeground(lightRed);
						} else {
							component.setForeground(Color.BLACK);
						}
					}

					// Put
					if (option.contains("p")) {
						if (col == 0 || col == 1) {
							component.setForeground(lightRed);
						} else if (col == 2 || col == 3) {
							component.setForeground(lightGreen);
						} else {
							component.setForeground(Color.BLACK);
						}
					}

					// ------- Algo ------- //
					// Call
					if (option.contains("c") && row == 0) {

						// Ask
						if (col == 2) {
							if (cell_val < call_ask) {
								component.setFont(ariel_bold);
								call_ask = cell_val;
								bidAskCounter--;
							} else {
								component.setFont(ariel);
								call_ask = cell_val;
							}
						}

						// Bid
						if (col == 1) {
							if (cell_val > call_bid) {
								component.setFont(ariel_bold);
								call_bid = cell_val;
								bidAskCounter++;
							} else {
								component.setFont(ariel);
								call_bid = cell_val;
							}
						}
					}

					// Puts
					if (option.contains("p") && row == 0) {

						// Bid
						if (col == 2) {
							if (cell_val > put_bid) {
								component.setFont(ariel_bold);
								put_bid = cell_val;
								bidAskCounter++;
							} else {
								component.setFont(ariel);
								put_bid = cell_val;
							}
						}

						// Ask
						if (col == 1) {
							if (cell_val < put_ask) {
								component.setFont(ariel_bold);
								put_ask = cell_val;
								bidAskCounter--;
							} else {
								component.setFont(ariel);
								put_ask = cell_val;
							}
						}
					}
					return component;
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO: handle exception
				}
				return editorComp;
			};
		};

		table.setEnabled(false);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setBackground(UIManager.getColor("control"));
		table.setTableHeader(null);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.setDefaultRenderer(Object.class, centerRenderer);
		table.setFillsViewportHeight(true);
		table.setRowHeight(20);
		table.setFont(new Font("Arial", Font.PLAIN, 15));
		table.setShowGrid(true);
		scrollPane.setViewportView(table);

		return table;
	}
	
	

	// Sleep
	public void sleep(long milisecondes) {
		try {
			Thread.sleep(milisecondes);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class BookUpdater extends Thread {

	ApiObject apiObject = ApiObject.getInstance();

	JSONObject option_object = new JSONObject();
	String optionName;
	LocalTime nine_half = LocalTime.parse("09:30:30");
	JTextField high;
	JTextField low;
	JTextField last;
	JTextField base;
	JTextField lastPresent;
	JTextField bidAskCounterField;
	JTable table;
	BookWindow bookWindow;

	boolean run = false;
	// TextToSpeechConvertor ttsc = new TextToSpeechConvertor();

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	// Colors
	Color lightGreen = new Color(0, 128, 0);
	Color lightRed = new Color(206, 0, 30);

	// Constructor
	public BookUpdater(JTextField high, JTextField low, JTextField last, JTextField base, JTable table,
			JTextField lastPresent, JTextField bidAskCounterField, BookWindow bookWindow) {
		this.high = high;
		this.low = low;
		this.last = last;
		this.base = base;
		this.table = table;
		this.lastPresent = lastPresent;
		this.bidAskCounterField = bidAskCounterField;
		this.bookWindow = bookWindow;
	}

	@Override
	public void run() {
		update();
	}

	// Update the book window in data
	public void update() {
		do {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				run = false;
				break;
			}
		} while (!run);

		while (run) {
			try {
				// Update the window with the data
				update_window();

				// Sleep
				Thread.sleep(500);
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				run = false;
				break;
			} catch (Exception e) {
				WindowTA35.popup("Error ", e);
				e.printStackTrace();
				break;
			}
		}
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	// Update the book window with the data
	private void update_window() {

		Option option = apiObject.getExpMonth().getOptions().getOption(optionName);

		if (!last.getText().isEmpty()) {
			int text_last = Integer.parseInt(last.getText());

			// Color the last
			if (option.getLast() > text_last) {
				last.setBackground(lightGreen);
				last.setForeground(Color.WHITE);
				last.setFont(last.getFont().deriveFont(Font.BOLD));
			} else if (option.getLast() < text_last) {
				last.setBackground(lightRed);
				last.setForeground(Color.WHITE);
				last.setFont(last.getFont().deriveFont(Font.BOLD));
			} else {
				last.setFont(last.getFont().deriveFont(Font.PLAIN));
				last.setBackground(Color.WHITE);
				last.setForeground(Color.BLACK);
			}
		}

		// Option data
		high.setText(str(option.getHigh()));
		low.setText(str(option.getLow()));
		base.setText(str(option.getBase()));

		if (!last.getText().isEmpty() && !high.getText().isEmpty() && !low.getText().isEmpty()) {
			// New high or new Low
			if (option.getLast() == Integer.parseInt(high.getText())) {
				// Color or say some text
				high.setBackground(lightGreen);
				high.setForeground(Color.WHITE);
				high.setFont(high.getFont().deriveFont(Font.BOLD));
			} else if (option.getLast() == Integer.parseInt(low.getText())) {
				// Color or say some text
				low.setBackground(lightRed);
				low.setForeground(Color.WHITE);
				low.setFont(high.getFont().deriveFont(Font.BOLD));
			} else {
				high.setBackground(Color.WHITE);
				high.setFont(high.getFont().deriveFont(Font.PLAIN));
				high.setForeground(Color.BLACK);
				low.setBackground(Color.WHITE);
				low.setFont(low.getFont().deriveFont(Font.PLAIN));
				low.setForeground(Color.BLACK);
			}
		}
		
		// Bid Ask counter 
		set_forf_text_with_color_int(bidAskCounterField, option.getBidAskCounter(), "");
		
		last.setText(str(option.getLast()));

		double base = option.getBase();
		double last = option.getLast();
		double last_present = floor(((last - base) / base) * 100);
		set_text_with_color(lastPresent, last_present, "%");

		// Call
		if (optionName.contains("c")) {
			// Update the table
			for (int row = 0; row < 5; row++) {
				table.getModel().setValueAt(str(option.getLevels().get(row + 1).getBidQuantity()), row, 0);
				table.getModel().setValueAt(str(option.getLevels().get(row + 1).getBidPrice()), row, 1);
				table.getModel().setValueAt(str(option.getLevels().get(row + 1).getAskPrice()), row, 2);
				table.getModel().setValueAt(str(option.getLevels().get(row + 1).getAskQuantity()), row, 3);
			}
		}
		// Put
		if (optionName.contains("p")) {
			// Update the table
			for (int row = 0; row < 5; row++) {
				table.getModel().setValueAt(str(option.getLevels().get(row + 1).getAskQuantity()), row, 0);
				table.getModel().setValueAt(str(option.getLevels().get(row + 1).getAskPrice()), row, 1);
				table.getModel().setValueAt(str(option.getLevels().get(row + 1).getBidPrice()), row, 2);
				table.getModel().setValueAt(str(option.getLevels().get(row + 1).getBidQuantity()), row, 3);
			}
		}
	}

	
	// Set text with background color
	private void set_text_with_color(JTextField field, double d, String text) {
		if (d > 0) {
			field.setText(str(d) + text);
			field.setBackground(lightGreen);
		} else if (d < 0) {
			field.setText(str(d) + text);
			field.setBackground(lightRed);
		} else {
			field.setText(str(d) + text);
			field.setBackground(Color.WHITE);
		}
	}
	
	
	// Set text with background color
	private void set_forf_text_with_color(JTextField field, double d, String text) {
		if (d > 0) {
			field.setText(str(d) + text);
			field.setForeground(lightGreen);
		} else {
			field.setText(str(d) + text);
			field.setForeground(lightRed);
		}
	}
	
	
	// Set text with background color
		private void set_forf_text_with_color_int(JTextField field, int i, String text) {
			if (i > 0) {
				field.setText(str(i) + text);
				field.setForeground(lightGreen);
			} else {
				field.setText(str(i) + text);
				field.setForeground(lightRed);
			}
		}
	

	// Color thread
	public void colorThread(JTextField textField, boolean up_down, String option) {
		Runnable thread = () -> {
			try {
				if (up_down) {
					textField.setBackground(lightGreen);
					textField.setForeground(Color.WHITE);
					sleep(3000);

				} else {
					textField.setBackground(lightRed);
					textField.setForeground(Color.WHITE);
					sleep(3000);
				}
				textField.setBackground(Color.WHITE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		thread.run();
	}

	public String getOption() {
		return optionName;
	}

	public void setOption(String option) {
		this.optionName = option;
	}

	// To string str
	private String str(Object o) {
		return String.valueOf(o);
	}

	// Floor
	public double floor(double d) {
		return Math.floor(d * 10) / 10;
	}

	// To double
	private double dbl(String string) {
		return Double.parseDouble(string);
	}

}
