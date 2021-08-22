package counter;

import api.ApiObject;
import api.Manifest;
import api.dde.DDE.DDEConnection;
import book.BookWindow;
import charts.charts.FullCharts2;
import charts.charts.MainMonthChart;
import charts.charts.MainMonthWeekChart;
import charts.charts.MainWeekChart;
import dataBase.DataBaseService;
import gui.details.DetailsWindow;
import logic.Logic;
import options.OptionsDataUpdater;
import setting.Setting;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WindowTA35 {

	public JFrame frame;

	Color lightGreen = new Color(12, 135, 0);
	Color lightRed = new Color(229, 19, 0);

	static int h;
	static int m;
	static int s;
	
	public JTextField op_avg;
	public static JTextField rando;
	public static JButton start;
	public static JTextField conUpField;
	public static JTextField conDownField;
	public static JTextField indUpField;
	public static JTextField indDownField;
	public static JTextField conSumField;
	public static JTextField indSumField;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel ratioPanel;
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
	JTextField optimiMoveField;
	JTextField pesimiMoveField;
	DDEConnection ddeConnection;
	public JTextField conBidAskCounterWeekField;
	private JPanel panel_19;
	private JScrollPane optionsCalcScrollPane;
	public JTextField conBidAskCounterMonthField;
	public JTextField equalMoveField;
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
	
	// The main function
	public void startWindow() {
		try {
			// Open the window
			initialize();
			frame.setVisible(true);

			// Show on screen
			showOnScreen(Manifest.screen, frame);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Constructor
	public WindowTA35() {
		startWindow();
		updater = new Updater(this);
		updater.getHandler().start();

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
			JOptionPane.showConfirmDialog(frame, e.getMessage() + "\n" + e.getCause());
		}
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

	/**
	 * @wbp.parser.entryPoint
	 */
	public void initialize() throws InterruptedException, SQLException {
		// UI manager
		UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(Color.black));
		UIManager.put("InternalFrame.activeTitleForeground", new ColorUIResource(Color.WHITE));
		UIManager.put("InternalFrame.titleFont", new Font("Dialog", Font.PLAIN, 11));

		ImageIcon img = new ImageIcon("C:/Users/ronens/Desktop/���� �����/iconDollar.png");
		frame = new JFrame();
		frame.getContentPane().setForeground(new Color(0, 0, 102));
		frame.getContentPane().setFont(new Font("Arial", Font.PLAIN, 12));
		frame.setTitle("TA35");
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBounds(-6, 0, 811, 204);
		frame.getContentPane().setLayout(null);
		frame.setIconImage(Toolkit.getDefaultToolkit()
				.getImage("C:\\Users\\user\\Desktop\\884466c0-505d-464a-bc9e-b2265d91ea38.png"));
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				UIManager.put("OptionPane.background", new ColorUIResource(189, 208, 239));
				UIManager.put("Panel.background", new ColorUIResource(189, 208, 239));

				String[] options = new String[] { "Yes", "No", "Yes with trunticate" };
				int res = JOptionPane.showOptionDialog(null, "Are you sure you want to close the program ?", "Title",
						JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				if (res == 0) {
					System.exit(0);
				} else if (res == 1) {
					// TO nothing
				} else if (res == 2) {
					// Close the program
					System.exit(0);
				}
			}

			// Get bounds of frame
			private String getBoundsAsString(JFrame frame) {
				String bounds = String.valueOf(frame.getX()) + "," + String.valueOf(frame.getY()) + ","
						+ String.valueOf(frame.getWidth()) + "," + String.valueOf(frame.getHeight());
				return bounds;
			}
		});

		// timer
		javax.swing.Timer t = new javax.swing.Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Calendar now = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				h = now.get(Calendar.HOUR_OF_DAY);
				m = now.get(Calendar.MINUTE);
				s = now.get(Calendar.SECOND);
			}
		});
		t.start();

		panel = new JPanel();
		panel.setBackground(SystemColor.menu);
		panel.setBounds(0, 26, 106, 102);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		conUpField = new JTextField();
		conUpField.setBorder(null);
		conUpField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				open_setting_window(e);
			}
		});
		conUpField.setHorizontalAlignment(SwingConstants.CENTER);
		conUpField.setBounds(5, 5, 45, 25);
		panel.add(conUpField);
		conUpField.setForeground(lightGreen);
		conUpField.setFont(new Font("Arial", Font.PLAIN, 15));
		conUpField.setColumns(10);

		indUpField = new JTextField();
		indUpField.setBorder(null);
		indUpField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				open_setting_window(e);
			}
		});
		indUpField.setHorizontalAlignment(SwingConstants.CENTER);
		indUpField.setBounds(55, 5, 45, 25);
		panel.add(indUpField);
		indUpField.setForeground(lightGreen);
		indUpField.setFont(new Font("Arial", Font.PLAIN, 15));
		indUpField.setColumns(10);

		indDownField = new JTextField();
		indDownField.setBorder(null);
		indDownField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				open_setting_window(e);
			}
		});
		indDownField.setHorizontalAlignment(SwingConstants.CENTER);
		indDownField.setBounds(55, 35, 45, 25);
		panel.add(indDownField);
		indDownField.setForeground(lightRed);
		indDownField.setFont(new Font("Arial", Font.PLAIN, 15));
		indDownField.setColumns(10);

		conDownField = new JTextField();
		conDownField.setBorder(null);
		conDownField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				open_setting_window(e);
			}
		});
		conDownField.setHorizontalAlignment(SwingConstants.CENTER);
		conDownField.setBounds(5, 35, 45, 25);
		panel.add(conDownField);
		conDownField.setForeground(lightRed);
		conDownField.setFont(new Font("Arial", Font.PLAIN, 15));
		conDownField.setColumns(10);

		conSumField = new JTextField();
		conSumField.setBorder(null);
		conSumField.setHorizontalAlignment(SwingConstants.CENTER);
		conSumField.setBounds(5, 70, 45, 25);
		panel.add(conSumField);
		conSumField.setForeground(Color.BLACK);
		conSumField.setFont(new Font("Arial", Font.PLAIN, 15));
		conSumField.setColumns(10);

		indSumField = new JTextField();
		indSumField.setBorder(null);
		indSumField.setHorizontalAlignment(SwingConstants.CENTER);
		indSumField.setBounds(55, 70, 45, 25);
		panel.add(indSumField);
		indSumField.setForeground(Color.BLACK);
		indSumField.setFont(new Font("Arial", Font.PLAIN, 15));
		indSumField.setColumns(10);

		panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.menu);
		panel_1.setBounds(293, 0, 64, 25);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JLabel label_12 = new JLabel("חוזה");
		label_12.setBounds(0, 0, 62, 25);
		panel_1.add(label_12);
		label_12.setHorizontalAlignment(SwingConstants.CENTER);
		label_12.setForeground(new Color(0, 0, 51));
		label_12.setFont(new Font("Arial", Font.BOLD, 15));

		panel_2 = new JPanel();
		panel_2.setBackground(SystemColor.menu);
		panel_2.setBounds(0, 0, 106, 25);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(null);

		JLabel label_7 = new JLabel("\u05D7\u05D5\u05D6\u05D4 ");
		label_7.setBounds(10, 5, 45, 15);
		panel_2.add(label_7);
		label_7.setForeground(new Color(0, 0, 51));
		label_7.setFont(new Font("Arial", Font.BOLD, 15));

		JLabel label_8 = new JLabel("\u05DE\u05D3\u05D3");
		label_8.setBounds(57, 5, 42, 15);
		panel_2.add(label_8);
		label_8.setForeground(new Color(0, 0, 51));
		label_8.setFont(new Font("Arial", Font.BOLD, 15));

		ratioPanel = new JPanel();
		ratioPanel.setBackground(SystemColor.menu);
		ratioPanel.setBounds(293, 26, 64, 102);
		frame.getContentPane().add(ratioPanel);
		ratioPanel.setLayout(null);

		JLabel label_9 = new JLabel("פקיעה");
		label_9.setBounds(352, 97, 36, 18);
		ratioPanel.add(label_9);
		label_9.setFont(new Font("Arial", Font.PLAIN, 15));
		label_9.setForeground(new Color(153, 204, 255));

		conBidAskCounterWeekField = new JTextField();
		conBidAskCounterWeekField.setBorder(null);
		conBidAskCounterWeekField.setBounds(7, 36, 50, 25);
		ratioPanel.add(conBidAskCounterWeekField);
		conBidAskCounterWeekField.setHorizontalAlignment(SwingConstants.CENTER);
		conBidAskCounterWeekField.setForeground(new Color(12, 135, 0));
		conBidAskCounterWeekField.setFont(new Font("Arial", Font.PLAIN, 15));
		conBidAskCounterWeekField.setColumns(10);

		conBidAskCounterMonthField = new JTextField();
		conBidAskCounterMonthField.setBorder(null);
		conBidAskCounterMonthField.setBounds(7, 5, 50, 25);
		ratioPanel.add(conBidAskCounterMonthField);
		conBidAskCounterMonthField.setHorizontalAlignment(SwingConstants.CENTER);
		conBidAskCounterMonthField.setForeground(new Color(12, 135, 0));
		conBidAskCounterMonthField.setFont(new Font("Arial", Font.PLAIN, 15));
		conBidAskCounterMonthField.setColumns(10);

		bottomPanel = new JPanel();
		bottomPanel.setBackground(SystemColor.menu);
		bottomPanel.setBounds(0, 129, 801, 38);
		frame.getContentPane().add(bottomPanel);
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
		chartsCombo.setBounds(609, 8, 182, 23);
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

		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBackground(SystemColor.menu);
		panel_5.setBounds(163, 26, 64, 102);
		frame.getContentPane().add(panel_5);

		optimiMoveField = new JTextField();
		optimiMoveField.setBorder(null);
		optimiMoveField.setHorizontalAlignment(SwingConstants.CENTER);
		optimiMoveField.setForeground(new Color(12, 135, 0));
		optimiMoveField.setFont(new Font("Arial", Font.PLAIN, 15));
		optimiMoveField.setColumns(10);
		optimiMoveField.setBounds(5, 5, 53, 25);
		panel_5.add(optimiMoveField);

		pesimiMoveField = new JTextField();
		pesimiMoveField.setBorder(null);
		pesimiMoveField.setHorizontalAlignment(SwingConstants.CENTER);
		pesimiMoveField.setForeground(new Color(229, 19, 0));
		pesimiMoveField.setFont(new Font("Arial", Font.PLAIN, 15));
		pesimiMoveField.setColumns(10);
		pesimiMoveField.setBounds(5, 35, 53, 25);
		panel_5.add(pesimiMoveField);

		equalMoveField = new JTextField();
		equalMoveField.setBorder(null);
		equalMoveField.setHorizontalAlignment(SwingConstants.CENTER);
		equalMoveField.setForeground(new Color(229, 19, 0));
		equalMoveField.setFont(new Font("Arial", Font.PLAIN, 15));
		equalMoveField.setColumns(10);
		equalMoveField.setBounds(5, 70, 53, 25);
		panel_5.add(equalMoveField);

		JPanel panel_6 = new JPanel();
		panel_6.setLayout(null);
		panel_6.setBackground(SystemColor.menu);
		panel_6.setBounds(163, 0, 64, 25);
		frame.getContentPane().add(panel_6);

		JLabel label = new JLabel("תנועה");
		label.setBounds(0, 0, 64, 26);
		panel_6.add(label);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(new Color(0, 0, 51));
		label.setFont(new Font("Arial", Font.BOLD, 15));

		JPanel basketsPanel = new JPanel();
		basketsPanel.setLayout(null);
		basketsPanel.setBackground(SystemColor.menu);
		basketsPanel.setBounds(107, 26, 55, 102);
		frame.getContentPane().add(basketsPanel);

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

		JPanel panel_11 = new JPanel();
		panel_11.setLayout(null);
		panel_11.setBackground(SystemColor.menu);
		panel_11.setBounds(107, 0, 55, 25);
		frame.getContentPane().add(panel_11);

		JLabel label_6 = new JLabel("סלים");
		label_6.setHorizontalAlignment(SwingConstants.CENTER);
		label_6.setForeground(new Color(0, 0, 51));
		label_6.setFont(new Font("Arial", Font.BOLD, 15));
		label_6.setBounds(0, 0, 55, 26);
		panel_11.add(label_6);

		JPanel panel_7 = new JPanel();
		panel_7.setLayout(null);
		panel_7.setBackground(new Color(176, 196, 222));
		panel_7.setBounds(67, 359, 165, 128);
		frame.getContentPane().add(panel_7);

		String[] header = { "Call", "Strike", "Put" };
		Object[][] data = new Object[5][3];

		// Table
		table = myTable(data, header);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(5, 5, 155, 115);
		panel_7.add(scrollPane);

		JPanel panel_8 = new JPanel();
		panel_8.setBackground(SystemColor.menu);
		panel_8.setBounds(439, 26, 362, 102);
		frame.getContentPane().add(panel_8);
		panel_8.setLayout(null);

		expDeltaWeekField = new JTextField();
		expDeltaWeekField.setHorizontalAlignment(SwingConstants.CENTER);
		expDeltaWeekField.setForeground(Color.WHITE);
		expDeltaWeekField.setFont(new Font("Arial", Font.PLAIN, 15));
		expDeltaWeekField.setColumns(10);
		expDeltaWeekField.setBorder(null);
		expDeltaWeekField.setBounds(212, 11, 68, 25);
		panel_8.add(expDeltaWeekField);

		expDeltaMonthField = new JTextField();
		expDeltaMonthField.setHorizontalAlignment(SwingConstants.CENTER);
		expDeltaMonthField.setForeground(Color.WHITE);
		expDeltaMonthField.setFont(new Font("Arial", Font.PLAIN, 15));
		expDeltaMonthField.setColumns(10);
		expDeltaMonthField.setBorder(null);
		expDeltaMonthField.setBounds(212, 41, 68, 25);
		panel_8.add(expDeltaMonthField);

		expBasketsWeekField = new JTextField();
		expBasketsWeekField.setHorizontalAlignment(SwingConstants.CENTER);
		expBasketsWeekField.setForeground(Color.WHITE);
		expBasketsWeekField.setFont(new Font("Arial", Font.PLAIN, 15));
		expBasketsWeekField.setColumns(10);
		expBasketsWeekField.setBorder(null);
		expBasketsWeekField.setBounds(139, 11, 68, 25);
		panel_8.add(expBasketsWeekField);

		expBasketsMonthField = new JTextField();
		expBasketsMonthField.setHorizontalAlignment(SwingConstants.CENTER);
		expBasketsMonthField.setForeground(Color.WHITE);
		expBasketsMonthField.setFont(new Font("Arial", Font.PLAIN, 15));
		expBasketsMonthField.setColumns(10);
		expBasketsMonthField.setBorder(null);
		expBasketsMonthField.setBounds(139, 41, 68, 25);
		panel_8.add(expBasketsMonthField);

		weekStartExpField = new JTextField();
		weekStartExpField.setBounds(66, 11, 68, 25);
		panel_8.add(weekStartExpField);
		weekStartExpField.setBorder(null);
		weekStartExpField.setHorizontalAlignment(SwingConstants.CENTER);
		weekStartExpField.setForeground(Color.WHITE);
		weekStartExpField.setFont(new Font("Arial", Font.PLAIN, 15));
		weekStartExpField.setColumns(10);

		monthStartExpField = new JTextField();
		monthStartExpField.setBounds(66, 41, 68, 25);
		panel_8.add(monthStartExpField);
		monthStartExpField.setBorder(null);
		monthStartExpField.setHorizontalAlignment(SwingConstants.CENTER);
		monthStartExpField.setForeground(Color.WHITE);
		monthStartExpField.setFont(new Font("Arial", Font.PLAIN, 15));
		monthStartExpField.setColumns(10);

		expIndDeltaWeekField = new JTextField();
		expIndDeltaWeekField.setBounds(284, 11, 68, 25);
		panel_8.add(expIndDeltaWeekField);
		expIndDeltaWeekField.setHorizontalAlignment(SwingConstants.CENTER);
		expIndDeltaWeekField.setForeground(Color.WHITE);
		expIndDeltaWeekField.setFont(new Font("Arial", Font.PLAIN, 15));
		expIndDeltaWeekField.setColumns(10);
		expIndDeltaWeekField.setBorder(null);

		expIndDeltaMonthField = new JTextField();
		expIndDeltaMonthField.setBounds(284, 41, 68, 25);
		panel_8.add(expIndDeltaMonthField);
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
		panel_8.add(label_4);

		JLabel label_14 = new JLabel("חודשי");
		label_14.setHorizontalAlignment(SwingConstants.CENTER);
		label_14.setForeground(new Color(0, 0, 51));
		label_14.setFont(new Font("Arial", Font.BOLD, 15));
		label_14.setBounds(0, 41, 68, 25);
		panel_8.add(label_14);

		JPanel deltaHeaderPanel = new JPanel();
		deltaHeaderPanel.setLayout(null);
		deltaHeaderPanel.setBounds(358, 0, 80, 25);
		deltaHeaderPanel.setBackground(SystemColor.menu);

		JLabel deltaLbl = new JLabel("דלתא");
		deltaLbl.setFont(new Font("Arial", Font.BOLD, 15));
		deltaLbl.setBounds(0, 0, deltaHeaderPanel.getWidth(), deltaHeaderPanel.getHeight());
		deltaLbl.setHorizontalAlignment(JLabel.CENTER);
		deltaLbl.setForeground(new Color(0, 0, 51));
		deltaHeaderPanel.add(deltaLbl);
		frame.getContentPane().add(deltaHeaderPanel);

		// Delta panel
		JPanel deltaPanel = new JPanel();
		deltaPanel.setBackground(SystemColor.menu);
		deltaPanel.setLayout(null);
		deltaPanel.setBounds(358, 26, 80, 102);
		frame.getContentPane().add(deltaPanel);

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

		JPanel panel_16 = new JPanel();
		panel_16.setBackground(SystemColor.menu);
		panel_16.setBounds(228, 26, 64, 102);
		frame.getContentPane().add(panel_16);
		panel_16.setLayout(null);

		op_avg = new JTextField();
		op_avg.setBorder(null);
		op_avg.setBounds(7, 5, 49, 25);
		panel_16.add(op_avg);
		op_avg.setHorizontalAlignment(SwingConstants.CENTER);
		op_avg.setForeground(Color.WHITE);
		op_avg.setFont(new Font("Arial", Font.PLAIN, 15));
		op_avg.setColumns(10);

		JPanel panel_18 = new JPanel();
		panel_18.setLayout(null);
		panel_18.setBackground(SystemColor.menu);
		panel_18.setBounds(0, 35, 64, 25);
		panel_16.add(panel_18);

		JLabel label_10 = new JLabel("רנדומלי");
		label_10.setHorizontalAlignment(SwingConstants.CENTER);
		label_10.setForeground(new Color(0, 0, 51));
		label_10.setFont(new Font("Arial", Font.BOLD, 15));
		label_10.setBounds(0, 0, 64, 25);
		panel_18.add(label_10);

		rando = new JTextField();
		rando.setBorder(null);
		rando.setBounds(7, 66, 49, 25);
		panel_16.add(rando);
		rando.setHorizontalAlignment(SwingConstants.CENTER);
		rando.setForeground(new Color(255, 255, 255));
		rando.setFont(new Font("Arial", Font.BOLD, 15));
		rando.setColumns(10);

		JPanel panel_17 = new JPanel();
		panel_17.setLayout(null);
		panel_17.setBackground(SystemColor.menu);
		panel_17.setBounds(228, 0, 64, 25);
		frame.getContentPane().add(panel_17);

		JLabel label_5 = new JLabel("ממוצע");
		label_5.setHorizontalAlignment(SwingConstants.CENTER);
		label_5.setForeground(new Color(0, 0, 51));
		label_5.setFont(new Font("Arial", Font.BOLD, 15));
		label_5.setBounds(0, 0, 68, 25);
		panel_17.add(label_5);

		panel_19 = new JPanel();
		panel_19.setLayout(null);
		panel_19.setBackground(new Color(176, 196, 222));
		panel_19.setBounds(1249, 0, 165, 128);
		frame.getContentPane().add(panel_19);

		String[] header2 = { "Call", "Strike", "Put" };
		Object[][] data2 = new Object[5][3];

		optionsCalcTable = myTable(data2, header2);

		optionsCalcScrollPane = new JScrollPane(optionsCalcTable);
		optionsCalcScrollPane.setBounds(5, 5, 155, 115);
		panel_19.add(optionsCalcScrollPane);

		JPanel logPanel = new JPanel();
		logPanel.setBackground(new Color(176, 196, 222));
		logPanel.setBounds(897, 0, 147, 102);
		frame.getContentPane().add(logPanel);
		logPanel.setLayout(null);

		log = new JTextArea();
		log.setBounds(10, 11, 127, 80);
		logPanel.add(log);

		JPanel panel_3 = new JPanel();
		panel_3.setLayout(null);
		panel_3.setBackground(SystemColor.menu);
		panel_3.setBounds(439, 0, 362, 25);
		frame.getContentPane().add(panel_3);

		JLabel label_2 = new JLabel("תנועה");
		label_2.setBounds(66, 0, 68, 25);
		panel_3.add(label_2);
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setForeground(new Color(0, 0, 51));
		label_2.setFont(new Font("Arial", Font.BOLD, 15));

		JLabel label_13 = new JLabel("סלים");
		label_13.setBounds(139, 0, 72, 25);
		panel_3.add(label_13);
		label_13.setHorizontalAlignment(SwingConstants.CENTER);
		label_13.setForeground(new Color(0, 0, 51));
		label_13.setFont(new Font("Arial", Font.BOLD, 15));

		JLabel label_11 = new JLabel("דלתא");
		label_11.setBounds(212, 0, 68, 25);
		panel_3.add(label_11);
		label_11.setHorizontalAlignment(SwingConstants.CENTER);
		label_11.setForeground(new Color(0, 0, 51));
		label_11.setFont(new Font("Arial", Font.BOLD, 15));

		JLabel label_1 = new JLabel("מניות");
		label_1.setBounds(284, 0, 68, 25);
		panel_3.add(label_1);
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setForeground(new Color(0, 0, 51));
		label_1.setFont(new Font("Arial", Font.BOLD, 15));

		JLabel label_3 = new JLabel("פקיעה");
		label_3.setHorizontalAlignment(SwingConstants.CENTER);
		label_3.setForeground(new Color(0, 0, 51));
		label_3.setFont(new Font("Arial", Font.BOLD, 15));
		label_3.setBounds(0, 0, 68, 25);
		panel_3.add(label_3);

	}

	// -------------------- function -------------------- //
	public void kill_dispose(JFrame frame) {
		try {
			frame.dispose();
		} catch (Exception e1) {
			popup("", e1);
			e1.printStackTrace();
		}
	}

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

	public void closeLogic() {
		logic.close();
	}

	// Stop
	public void stop() {
		start.setEnabled(true);
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
