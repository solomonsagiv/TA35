package counter;

import api.ApiObject;
import api.dde.DDE.DDEConnection;
import book.BookWindow;
import charts.charts.FullCharts2;
import charts.charts.MainMonthChart;
import charts.charts.MainMonthWeekChart;
import charts.charts.MainWeekChart;
import dataBase.DataBaseService;
import gui.MyGuiComps;
import gui.details.DetailsWindow;
import logic.Logic;
import options.OptionsDataUpdater;
import setting.Setting;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class WindowTA35 extends MyGuiComps.MyFrame {

	Color lightGreen = new Color(12, 135, 0);
	Color lightRed = new Color(229, 19, 0);

	static int h;
	static int m;
	static int s;
	
	public JTextField op_avg;
	public static JTextField rando;
	public static JButton start;
	private JPanel bottomPanel;
	public JTextField monthStartExpField;
	public JTable table;
	public JTable optionsCalcTable;
	public static JTextArea log;
	public JTextField monthDeltaField;
	public JTextField weekDeltaField;

	public int updater_id = 0;

	ApiObject apiObject = ApiObject.getInstance();
	DataBaseService dataBaseService;

	// Threads
	Updater updater;
	BackGroundRunner backGroundRunner;
	Logic logic;
	JTextField pesimiBasketField;
	JTextField optimiBasketField;
	public JTextField basketsSumField;
	DDEConnection ddeConnection;
	private JPanel panel_19;
	private JScrollPane optionsCalcScrollPane;
	public JTextField weekStartExpField;
	public JTextField indexDeltaField;
	private JButton btnDetails;
	public JTextField expDeltaWeekField;
	public JTextField expDeltaMonthField;
	public JTextField expIndDeltaWeekField;
	public JTextField expIndDeltaMonthField;
	public JTextField expBasketsWeekField;
	public JTextField expBasketsMonthField;
	public JTextField indDeltaNoBasketsField;
	

	// Constructor
	public WindowTA35() {
		super("TA35");
		updater = new Updater(this);
		updater.getHandler().start();
//		initialize();
//		frame.setVisible(true);

		load_on_startup();
	}

	// Load on startup
	private void load_on_startup() {
		try {
			// DDE connection
			ddeConnection = new DDEConnection(apiObject);
			ddeConnection.start();
			
			// Data base service
			dataBaseService = new DataBaseService();

			// Back ground runner
			backGroundRunner = new BackGroundRunner();
			backGroundRunner.getHandler().start();

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showConfirmDialog(this, e.getMessage() + "\n" + e.getCause());
		}
	}


	@Override
	public void onClose() {
		super.onClose();
		System.exit(0);
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



	@Override
	public void initListeners() {

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void initialize() {

		bottomPanel = new JPanel();
		bottomPanel.setBackground(SystemColor.menu);
		bottomPanel.setBounds(0, 129, 801, 38);
		getContentPane().add(bottomPanel);
		bottomPanel.setLayout(null);

		start = new JButton("Start");
		start.setBorder(null);
		start.setBounds(248, 7, 72, 23);
		bottomPanel.add(start);
		start.setForeground(new Color(0, 0, 51));
		start.setBackground(new Color(211, 211, 211));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				apiObject.start();
				start.setEnabled(false);
			}
		});

		start.setFont(new Font("Arial", Font.BOLD, 12));

		JButton options = new JButton("Options");
		options.setBorder(null);
		options.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean b;
				if (updater_id % 2 == 0) {
					b = true;
				} else {
					b = false;
				}
				BookWindow window = new BookWindow(updater_id, b);
				window.frame.setVisible(true);
				updater_id++;
			}
		});
		options.setForeground(new Color(0, 0, 51));
		options.setFont(new Font("Arial", Font.BOLD, 12));
		options.setBackground(new Color(211, 211, 211));
		options.setBounds(166, 7, 78, 23);
		bottomPanel.add(options);

		JButton settingBtn = new JButton("Setting");
		settingBtn.setBorder(null);
		settingBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Setting setting = new Setting();
				setting.setVisible();
			}
		});
		settingBtn.setForeground(new Color(0, 0, 51));
		settingBtn.setFont(new Font("Arial", Font.BOLD, 12));
		settingBtn.setBackground(new Color(211, 211, 211));
		settingBtn.setBounds(10, 7, 72, 23);
		bottomPanel.add(settingBtn);

		btnDetails = new JButton("Details");
		btnDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DetailsWindow detailsWindow = new DetailsWindow();
				detailsWindow.frame.setVisible(true);
			}
		});
		btnDetails.setBorder(null);
		btnDetails.setForeground(new Color(0, 0, 51));
		btnDetails.setFont(new Font("Arial", Font.BOLD, 12));
		btnDetails.setBackground(new Color(211, 211, 211));
		btnDetails.setBounds(88, 7, 72, 23);
		bottomPanel.add(btnDetails);

		@SuppressWarnings("unchecked")
		JComboBox chartsCombo = new JComboBox(new String[] { "Week", "Month", "Full chart 2", "Main month week" });
		chartsCombo.setBounds(start.getX() + start.getWidth() + 5, 8, 182, 23);
		bottomPanel.add(chartsCombo);
		chartsCombo.setBorder(null);
		chartsCombo.setBackground(SystemColor.menu);
		chartsCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (chartsCombo.getSelectedItem().toString()) {
				case "Month":
					try {
						MainMonthChart mainMonthChart = new MainMonthChart(apiObject);
						mainMonthChart.createChart();
					} catch (CloneNotSupportedException exception) {
						exception.printStackTrace();
					}
					break;
				case "Week":
					try {
						MainWeekChart mainWeekChart = new MainWeekChart(apiObject);
						mainWeekChart.createChart();
					} catch (CloneNotSupportedException exception) {
						exception.printStackTrace();
					}
					break;
				case "Full chart 2":
					try {
						FullCharts2 chart = new FullCharts2(apiObject);
						chart.createChart();
					} catch (CloneNotSupportedException exception) {
						exception.printStackTrace();
					}
					break;
				case "Main month week":
					try {
						MainMonthWeekChart chart = new MainMonthWeekChart(apiObject);
						chart.createChart();
					} catch (CloneNotSupportedException exception) {
						exception.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		});
		chartsCombo.setForeground(new Color(0, 0, 51));
		chartsCombo.setFont(new Font("Dubai Medium", Font.PLAIN, 15));

		indDeltaNoBasketsField = new JTextField();
		indDeltaNoBasketsField.setHorizontalAlignment(SwingConstants.CENTER);
		indDeltaNoBasketsField.setFont(new Font("Arial", Font.PLAIN, 15));
		indDeltaNoBasketsField.setBorder(null);
		indDeltaNoBasketsField.setBounds(365, 7, 65, 23);
		bottomPanel.add(indDeltaNoBasketsField);

		((JLabel) chartsCombo.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		JPanel basketsPanel = new JPanel();
		basketsPanel.setLayout(null);
		basketsPanel.setBackground(SystemColor.menu);
		basketsPanel.setBounds(0, 26, 55, 102);
		getContentPane().add(basketsPanel);

		pesimiBasketField = new JTextField();
		pesimiBasketField.setBorder(null);
		pesimiBasketField.setHorizontalAlignment(SwingConstants.CENTER);
		pesimiBasketField.setForeground(lightRed);
		pesimiBasketField.setFont(new Font("Arial", Font.PLAIN, 15));
		pesimiBasketField.setColumns(10);
		pesimiBasketField.setBounds(5, 35, 45, 25);
		basketsPanel.add(pesimiBasketField);

		optimiBasketField = new JTextField();
		optimiBasketField.setBorder(null);
		optimiBasketField.setHorizontalAlignment(SwingConstants.CENTER);
		optimiBasketField.setForeground(lightGreen);
		optimiBasketField.setFont(new Font("Arial", Font.PLAIN, 15));
		optimiBasketField.setColumns(10);
		optimiBasketField.setBounds(5, 5, 45, 25);
		basketsPanel.add(optimiBasketField);

		basketsSumField = new JTextField();
		basketsSumField.setBorder(null);
		basketsSumField.setHorizontalAlignment(SwingConstants.CENTER);
		basketsSumField.setForeground(new Color(229, 19, 0));
		basketsSumField.setFont(new Font("Arial", Font.PLAIN, 15));
		basketsSumField.setColumns(10);
		basketsSumField.setBounds(5, 70, 45, 25);
		basketsPanel.add(basketsSumField);

		JPanel basket_header_panel = new JPanel();
		basket_header_panel.setLayout(null);
		basket_header_panel.setBackground(SystemColor.menu);
		basket_header_panel.setBounds(0, 0, 55, 25);
		getContentPane().add(basket_header_panel);

		JLabel baskets_lbl = new JLabel("סלים");
		baskets_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		baskets_lbl.setForeground(new Color(0, 0, 51));
		baskets_lbl.setFont(new Font("Arial", Font.BOLD, 15));
		baskets_lbl.setBounds(0, 0, 55, 26);
		basket_header_panel.add(baskets_lbl);

		JPanel panel_7 = new JPanel();
		panel_7.setLayout(null);
		panel_7.setBackground(new Color(176, 196, 222));
		panel_7.setBounds(67, 359, 165, 128);
		getContentPane().add(panel_7);

		String[] header = { "Call", "Strike", "Put" };
		Object[][] data = new Object[5][3];

		// Table
		table = myTable(data, header);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(5, 5, 155, 115);
		panel_7.add(scrollPane);




		JPanel op_avg_header_panel = new JPanel();
		op_avg_header_panel.setLayout(null);
		op_avg_header_panel.setBackground(SystemColor.menu);
		op_avg_header_panel.setBounds(basket_header_panel.getX() + basket_header_panel.getWidth() + 1, 0, 64, 25);
		getContentPane().add(op_avg_header_panel);

		JLabel move_lbl = new JLabel("ממוצע");
		move_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		move_lbl.setForeground(new Color(0, 0, 51));
		move_lbl.setFont(new Font("Arial", Font.BOLD, 15));
		move_lbl.setBounds(0, 0, 68, 25);
		op_avg_header_panel.add(move_lbl);

		JPanel op_avg_panel = new JPanel();
		op_avg_panel.setBackground(SystemColor.menu);
		op_avg_panel.setBounds(op_avg_header_panel.getX(), op_avg_header_panel.getY() + op_avg_header_panel.getHeight() + 1, 64, 102);
		getContentPane().add(op_avg_panel);
		op_avg_panel.setLayout(null);

		op_avg = new JTextField();
		op_avg.setBorder(null);
		op_avg.setBounds(7, 5, 49, 25);
		op_avg_panel.add(op_avg);
		op_avg.setHorizontalAlignment(SwingConstants.CENTER);
		op_avg.setForeground(Color.WHITE);
		op_avg.setFont(new Font("Arial", Font.PLAIN, 15));
		op_avg.setColumns(10);

		JPanel panel_18 = new JPanel();
		panel_18.setLayout(null);
		panel_18.setBackground(SystemColor.menu);
		panel_18.setBounds(0, 35, 64, 25);
		op_avg_panel.add(panel_18);

		JLabel label_10 = new JLabel("רנדומלי");
		label_10.setHorizontalAlignment(SwingConstants.CENTER);
		label_10.setForeground(new Color(0, 0, 51));
		label_10.setFont(new Font("Arial", Font.BOLD, 15));
		label_10.setBounds(0, 0, 64, 25);
		panel_18.add(label_10);

		rando = new JTextField();
		rando.setBorder(null);
		rando.setBounds(7, 66, 49, 25);
		op_avg_panel.add(rando);
		rando.setHorizontalAlignment(SwingConstants.CENTER);
		rando.setForeground(new Color(255, 255, 255));
		rando.setFont(new Font("Arial", Font.BOLD, 15));
		rando.setColumns(10);

		JPanel deltaHeaderPanel = new JPanel();
		deltaHeaderPanel.setLayout(null);
		deltaHeaderPanel.setBounds(op_avg_header_panel.getX() + op_avg_header_panel.getWidth() + 1, op_avg_header_panel.getY(), 80, 25);
		deltaHeaderPanel.setBackground(SystemColor.menu);

		JLabel deltaLbl = new JLabel("דלתא");
		deltaLbl.setFont(new Font("Arial", Font.BOLD, 15));
		deltaLbl.setBounds(0, 0, deltaHeaderPanel.getWidth(), deltaHeaderPanel.getHeight());
		deltaLbl.setHorizontalAlignment(JLabel.CENTER);
		deltaLbl.setForeground(new Color(0, 0, 51));
		deltaHeaderPanel.add(deltaLbl);
		getContentPane().add(deltaHeaderPanel);

		// Delta panel
		JPanel deltaPanel = new JPanel();
		deltaPanel.setBackground(SystemColor.menu);
		deltaPanel.setLayout(null);
		deltaPanel.setBounds(deltaHeaderPanel.getX(), deltaHeaderPanel.getY() + deltaHeaderPanel.getHeight() + 1, 80, 102);
		getContentPane().add(deltaPanel);

		// Week delta
		weekDeltaField = new JTextField();
		weekDeltaField.setBorder(null);
		weekDeltaField.setBounds(5, 5, 65, 25);
		weekDeltaField.setHorizontalAlignment(JTextField.CENTER);
		weekDeltaField.setFont(new Font("Arial", Font.PLAIN, 15));
		deltaPanel.add(weekDeltaField);

		// Month delta
		monthDeltaField = new JTextField();
		monthDeltaField.setBorder(null);
		monthDeltaField.setBounds(5, 35, 65, 25);
		monthDeltaField.setHorizontalAlignment(JTextField.CENTER);
		monthDeltaField.setFont(new Font("Arial", Font.PLAIN, 15));
		deltaPanel.add(monthDeltaField);

		indexDeltaField = new JTextField();
		indexDeltaField.setBorder(null);
		indexDeltaField.setHorizontalAlignment(SwingConstants.CENTER);
		indexDeltaField.setFont(new Font("Arial", Font.PLAIN, 15));
		indexDeltaField.setBounds(5, 66, 65, 25);
		deltaPanel.add(indexDeltaField);




		panel_19 = new JPanel();
		panel_19.setLayout(null);
		panel_19.setBackground(new Color(176, 196, 222));
		panel_19.setBounds(1249, 0, 165, 128);
		getContentPane().add(panel_19);

		String[] header2 = { "Call", "Strike", "Put" };
		Object[][] data2 = new Object[5][3];

		optionsCalcTable = myTable(data2, header2);

		optionsCalcScrollPane = new JScrollPane(optionsCalcTable);
		optionsCalcScrollPane.setBounds(5, 5, 155, 115);
		panel_19.add(optionsCalcScrollPane);

		JPanel logPanel = new JPanel();
		logPanel.setBackground(new Color(176, 196, 222));
		logPanel.setBounds(897, 0, 147, 102);
		getContentPane().add(logPanel);
		logPanel.setLayout(null);

		log = new JTextArea();
		log.setBounds(10, 11, 127, 80);
		logPanel.add(log);

		JPanel exp_header_panel = new JPanel();
		exp_header_panel.setLayout(null);
		exp_header_panel.setBackground(SystemColor.menu);
		exp_header_panel.setBounds(deltaHeaderPanel.getX() + deltaHeaderPanel.getWidth() + 1, 0, 362, 25);
		getContentPane().add(exp_header_panel);

		JLabel label_2 = new JLabel("תנועה");
		label_2.setBounds(66, 0, 68, 25);
		exp_header_panel.add(label_2);
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setForeground(new Color(0, 0, 51));
		label_2.setFont(new Font("Arial", Font.BOLD, 15));

		JLabel label_13 = new JLabel("סלים");
		label_13.setBounds(139, 0, 72, 25);
		exp_header_panel.add(label_13);
		label_13.setHorizontalAlignment(SwingConstants.CENTER);
		label_13.setForeground(new Color(0, 0, 51));
		label_13.setFont(new Font("Arial", Font.BOLD, 15));

		JLabel label_11 = new JLabel("דלתא");
		label_11.setBounds(212, 0, 68, 25);
		exp_header_panel.add(label_11);
		label_11.setHorizontalAlignment(SwingConstants.CENTER);
		label_11.setForeground(new Color(0, 0, 51));
		label_11.setFont(new Font("Arial", Font.BOLD, 15));

		JLabel label_1 = new JLabel("מניות");
		label_1.setBounds(284, 0, 68, 25);
		exp_header_panel.add(label_1);
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setForeground(new Color(0, 0, 51));
		label_1.setFont(new Font("Arial", Font.BOLD, 15));

		JLabel exp_lbl = new JLabel("פקיעה");
		exp_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		exp_lbl.setForeground(new Color(0, 0, 51));
		exp_lbl.setFont(new Font("Arial", Font.BOLD, 15));
		exp_lbl.setBounds(0, 0, 68, 25);
		exp_header_panel.add(exp_lbl);

		JPanel exp_panel = new JPanel();
		exp_panel.setBackground(SystemColor.menu);
		exp_panel.setBounds(exp_header_panel.getX(), exp_header_panel.getY() + exp_header_panel.getHeight() + 1, 362, 102);
		getContentPane().add(exp_panel);
		exp_panel.setLayout(null);

		expDeltaWeekField = new JTextField();
		expDeltaWeekField.setHorizontalAlignment(SwingConstants.CENTER);
		expDeltaWeekField.setForeground(Color.WHITE);
		expDeltaWeekField.setFont(new Font("Arial", Font.PLAIN, 15));
		expDeltaWeekField.setColumns(10);
		expDeltaWeekField.setBorder(null);
		expDeltaWeekField.setBounds(212, 11, 68, 25);
		exp_panel.add(expDeltaWeekField);

		expDeltaMonthField = new JTextField();
		expDeltaMonthField.setHorizontalAlignment(SwingConstants.CENTER);
		expDeltaMonthField.setForeground(Color.WHITE);
		expDeltaMonthField.setFont(new Font("Arial", Font.PLAIN, 15));
		expDeltaMonthField.setColumns(10);
		expDeltaMonthField.setBorder(null);
		expDeltaMonthField.setBounds(212, 41, 68, 25);
		exp_panel.add(expDeltaMonthField);

		expBasketsWeekField = new JTextField();
		expBasketsWeekField.setHorizontalAlignment(SwingConstants.CENTER);
		expBasketsWeekField.setForeground(Color.WHITE);
		expBasketsWeekField.setFont(new Font("Arial", Font.PLAIN, 15));
		expBasketsWeekField.setColumns(10);
		expBasketsWeekField.setBorder(null);
		expBasketsWeekField.setBounds(139, 11, 68, 25);
		exp_panel.add(expBasketsWeekField);

		expBasketsMonthField = new JTextField();
		expBasketsMonthField.setHorizontalAlignment(SwingConstants.CENTER);
		expBasketsMonthField.setForeground(Color.WHITE);
		expBasketsMonthField.setFont(new Font("Arial", Font.PLAIN, 15));
		expBasketsMonthField.setColumns(10);
		expBasketsMonthField.setBorder(null);
		expBasketsMonthField.setBounds(139, 41, 68, 25);
		exp_panel.add(expBasketsMonthField);

		weekStartExpField = new JTextField();
		weekStartExpField.setBounds(66, 11, 68, 25);
		exp_panel.add(weekStartExpField);
		weekStartExpField.setBorder(null);
		weekStartExpField.setHorizontalAlignment(SwingConstants.CENTER);
		weekStartExpField.setForeground(Color.WHITE);
		weekStartExpField.setFont(new Font("Arial", Font.PLAIN, 15));
		weekStartExpField.setColumns(10);

		monthStartExpField = new JTextField();
		monthStartExpField.setBounds(66, 41, 68, 25);
		exp_panel.add(monthStartExpField);
		monthStartExpField.setBorder(null);
		monthStartExpField.setHorizontalAlignment(SwingConstants.CENTER);
		monthStartExpField.setForeground(Color.WHITE);
		monthStartExpField.setFont(new Font("Arial", Font.PLAIN, 15));
		monthStartExpField.setColumns(10);

		expIndDeltaWeekField = new JTextField();
		expIndDeltaWeekField.setBounds(284, 11, 68, 25);
		exp_panel.add(expIndDeltaWeekField);
		expIndDeltaWeekField.setHorizontalAlignment(SwingConstants.CENTER);
		expIndDeltaWeekField.setForeground(Color.WHITE);
		expIndDeltaWeekField.setFont(new Font("Arial", Font.PLAIN, 15));
		expIndDeltaWeekField.setColumns(10);
		expIndDeltaWeekField.setBorder(null);

		expIndDeltaMonthField = new JTextField();
		expIndDeltaMonthField.setBounds(284, 41, 68, 25);
		exp_panel.add(expIndDeltaMonthField);
		expIndDeltaMonthField.setHorizontalAlignment(SwingConstants.CENTER);
		expIndDeltaMonthField.setForeground(Color.WHITE);
		expIndDeltaMonthField.setFont(new Font("Arial", Font.PLAIN, 15));
		expIndDeltaMonthField.setColumns(10);
		expIndDeltaMonthField.setBorder(null);

		JLabel label_4 = new JLabel("שבועי");
		label_4.setHorizontalAlignment(SwingConstants.CENTER);
		label_4.setForeground(new Color(0, 0, 51));
		label_4.setFont(new Font("Arial", Font.BOLD, 15));
		label_4.setBounds(0, 11, 68, 25);
		exp_panel.add(label_4);

		JLabel label_14 = new JLabel("חודשי");
		label_14.setHorizontalAlignment(SwingConstants.CENTER);
		label_14.setForeground(new Color(0, 0, 51));
		label_14.setFont(new Font("Arial", Font.BOLD, 15));
		label_14.setBounds(0, 41, 68, 25);
		exp_panel.add(label_14);


	}

	// -------------------- function -------------------- //

	// Floor
	public static double floor(double d) {
		return Math.floor(d * 100) / 100;
	};

	// Open setting window if 2 clicks
	private void open_setting_window(MouseEvent e) {
		if (e.getClickCount() == 2) {
			Setting setting = new Setting();
			setting.setVisible();
		}
	}

	// Popup
	public static void popup(String message, Exception e) {
		JOptionPane.showMessageDialog(null, message + "\n" + e.getMessage());
	}

	// Create xls file with the date of today
	public FileOutputStream getFile(String name) {
		try {
			return new FileOutputStream(apiObject.getExport_dir() + name);
		} catch (FileNotFoundException e) {
			WindowTA35.popup("Creating file error ", e);
			e.printStackTrace();
		}
		return null;
	}

	// Getters and Setters

	public Updater getUpdater() {
		return updater;
	}

	public void setUpdater(Updater updater) {
		this.updater = updater;
	}

	private JTable myTable(Object[][] rowsData, String[] cols) {
		Color darkBlue = new Color(0, 51, 102);

		// Table
		JTable table = new JTable(rowsData, cols) {

			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

				Component c = super.prepareRenderer(renderer, row, col);

				String strike = String.valueOf(getValueAt(row, 1));

				Color originalColor = c.getBackground();

				int cell_val = 0;
				try {
					if (!getValueAt(row, col).equals("")) {
						try {
							cell_val = (int) getValueAt(row, col);
						} catch (ClassCastException e) {
							cell_val = Integer.parseInt((String) getValueAt(row, col));
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {

					// Call
					if (col == 0) {
						// Color forf
						if (cell_val > 0) {
							c.setForeground(lightGreen);
						} else {
							c.setForeground(lightRed);
						}

					} else

					// Put
					if (col == 2) {

						// Color forf
						if (cell_val > 0) {
							c.setForeground(lightGreen);
						} else {
							c.setForeground(lightRed);
						}

					} else

					// Strike
					if (col == 1) {
						c.setFont(c.getFont().deriveFont(Font.BOLD));
						c.setForeground(Color.BLACK);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				return c;
			}
		};
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getModifiers() == MouseEvent.BUTTON3_MASK) {

					// Main menu
					JPopupMenu menu = new JPopupMenu();

					JMenuItem setting = new JMenuItem("Setting");
					setting.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Setting setting = new Setting();
							setting.setVisible();
						}
					});

					JMenuItem strike = new JMenuItem("Set strike");
					strike.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								int strike = Integer.parseInt(JOptionPane.showInputDialog("Enter middle strike"));
								OptionsDataUpdater.updateStrikes(strike);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}
					});

					menu.add(strike);
					menu.add(setting);
					// Show the menu
					menu.show(event.getComponent(), event.getX(), event.getY());
				}

				if (event.getClickCount() == 2) {
					table.clearSelection();
				}
			}
		});
		table.setBounds(0, 0, 300, 235);

		// Header
		JTableHeader header = table.getTableHeader();
		header.setVisible(false);
		table.setTableHeader(null);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.setDefaultRenderer(Object.class, centerRenderer);
		table.setFillsViewportHeight(true);
		table.setRowHeight(22);
		table.setFont(new Font("Arial", Font.PLAIN, 15));
		table.setShowGrid(true);
		table.setSelectionBackground(Color.YELLOW);
		return table;
	}

	public BackGroundRunner getBackGroundRunner() {
		return backGroundRunner;
	}
}
