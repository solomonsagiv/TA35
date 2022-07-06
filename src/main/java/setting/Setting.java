package setting;

import api.ApiObject;
import arik.Arik;
import counter.WindowTA35;
import dataBase.DataBaseHandler;
import dataBase.mySql.JibeConnectionPool;
import exp.Exp;
import locals.L;
import options.Option;
import options.Options;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Setting {
	
	private JFrame frame;
	private JPanel panel;
	private JPanel setting_panel;
	private JLabel lblNewLabel_1;
	private JLabel lblIndex;
	private JTextField setting_f_up;
	private JTextField setting_f_down;
	private JTextField setting_index_up;
	private JTextField setting_index_down;
	private JButton set;
	private JPanel panel_2;
	ApiObject apiObject = ApiObject.getInstance();
	private JTextField futureExp;
	private JTextField indexExp;
	private JTextField textField_2;
	private JLabel lblBasketUp;
	private JLabel lblBasketDown;
	private JTextField textField_3;
	private JLabel lblExpTimer;
	private JLabel lblExpBaskets;
	private JTextField textField_4;
	private JTextField expBasketsField;

	private JLabel lblR;
	private JPanel panel_1;
	private JLabel lblDataBase;
	private JButton btnReset;
	private JButton btnLoad;
	private JButton btnSum;
	private JButton btnUpdate;
	private JTextField expField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Setting window = new Setting();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Setting() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	
	Exp expWeek, expMonth, exp;
	Options optionsMonth, optionsWeek, options;
	private JLabel lblIndDelta_1;

	private void initialize() {
		
		expWeek = apiObject.getExps().getWeek();
		expMonth = apiObject.getExps().getMonth();
		
		optionsWeek = expWeek.getOptions();
		optionsMonth = expMonth.getOptions();
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 636, 588);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Image image = (new ImageIcon("C:\\Users\\user\\Desktop\\Work\\Development\\icons\\Misc-Database-3-icon.png")
				.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
		frame.getContentPane().setLayout(null);

		panel = new JPanel();
		panel.setBounds(0, 0, 843, 640);
		frame.getContentPane().add(panel);
		panel.setBackground(new Color(0, 0, 51));
		panel.setLayout(null);

		setting_panel = new JPanel();
		setting_panel.setBounds(0, 0, 843, 643);
		panel.add(setting_panel);
		setting_panel.setBackground(Color.WHITE);

		panel_2 = new JPanel();
		panel_2.setBackground(SystemColor.menu);
		panel_2.setBounds(0, 304, 632, 153);
		panel_2.setLayout(null);

		futureExp = new JTextField();
		futureExp.setHorizontalAlignment(SwingConstants.CENTER);
		futureExp.setForeground(new Color(0, 51, 153));
		futureExp.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		futureExp.setColumns(10);
		futureExp.setBorder(null);
		futureExp.setBackground(new Color(255, 255, 255));
		futureExp.setBounds(10, 71, 57, 22);
		panel_2.add(futureExp);

		JLabel lblFuture = new JLabel("Future");
		lblFuture.setHorizontalAlignment(SwingConstants.CENTER);
		lblFuture.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		lblFuture.setBounds(10, 44, 57, 21);
		panel_2.add(lblFuture);

		indexExp = new JTextField();
		indexExp.setHorizontalAlignment(SwingConstants.CENTER);
		indexExp.setForeground(new Color(0, 51, 153));
		indexExp.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		indexExp.setColumns(10);
		indexExp.setBorder(null);
		indexExp.setBackground(new Color(255, 255, 255));
		indexExp.setBounds(87, 71, 57, 22);
		panel_2.add(indexExp);

		JLabel lblIndex_1 = new JLabel("Index");
		lblIndex_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblIndex_1.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		lblIndex_1.setBounds(87, 44, 57, 21);
		panel_2.add(lblIndex_1);

		JButton button = new JButton("Set");
		button.setBorder(null);
		button.setBackground(new Color(220, 220, 220));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int future = 0;
				int index = 0;

				try {
					future = Integer.parseInt(futureExp.getText());
					index = Integer.parseInt(indexExp.getText());
				} catch (Exception e) {
					popup("Failed", e);
				} finally {
					apiObject.setFutureExpRaces(future);
					apiObject.setIndexExpRaces(index);
				}
			}
		});
		button.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		button.setBounds(10, 118, 134, 24);
		panel_2.add(button);
		setting_panel.setLayout(null);
		setting_panel.add(panel_2);

		JLabel lblExp = new JLabel("Exp");
		lblExp.setForeground(new Color(0, 0, 128));
		lblExp.setHorizontalAlignment(SwingConstants.LEFT);
		lblExp.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblExp.setBounds(10, 11, 67, 21);
		panel_2.add(lblExp);

		textField_4 = new JTextField();
		textField_4.setBounds(167, 71, 56, 22);
		panel_2.add(textField_4);
		textField_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		textField_4.setHorizontalAlignment(SwingConstants.CENTER);
		textField_4.setForeground(new Color(0, 51, 153));
		textField_4.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		textField_4.setColumns(10);
		textField_4.setBorder(null);
		textField_4.setBackground(new Color(255, 255, 255));

		lblExpTimer = new JLabel("Exp timer ");
		lblExpTimer.setHorizontalAlignment(SwingConstants.CENTER);
		lblExpTimer.setBounds(157, 44, 71, 21);
		panel_2.add(lblExpTimer);
		lblExpTimer.setFont(new Font("Dubai Medium", Font.PLAIN, 14));

		lblExpBaskets = new JLabel("Baskets");
		lblExpBaskets.setHorizontalAlignment(SwingConstants.CENTER);
		lblExpBaskets.setBounds(238, 44, 56, 21);
		panel_2.add(lblExpBaskets);
		lblExpBaskets.setFont(new Font("Dubai Medium", Font.PLAIN, 14));

		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(SystemColor.menu);
		panel_3.setBounds(0, 181, 632, 122);
		setting_panel.add(panel_3);
		panel_3.setLayout(null);
		
		String[] optionsArr = { "", "Month", "Week" };
		JComboBox comboBoxOptions = new JComboBox(optionsArr);
		comboBoxOptions.setBorder(null);
		comboBoxOptions.setFont(new Font("Arial", Font.PLAIN, 11));
		comboBoxOptions.setBounds(385, 11, 78, 20);
		comboBoxOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (comboBoxOptions.getSelectedItem().toString()) {
				case "Month":
					exp = expMonth;
					options = optionsMonth;
					break;
				case "Week":
					exp = expWeek;
					options = optionsWeek;
					break;
				default:
					break;
				}
			}
		});
		panel_3.add(comboBoxOptions);
		
		expBasketsField = new JTextField();
		expBasketsField.setBounds(238, 71, 56, 22);
		panel_2.add(expBasketsField);
		expBasketsField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (!expBasketsField.getText().isEmpty()) {
					try {
						int baskets = L.INT(expBasketsField.getText());
						exp.getExpData().setBaskets(baskets);
					} catch (Exception exception) {
						exception.printStackTrace();
						JOptionPane.showMessageDialog(frame, exception.getMessage());
					}
				}
				
			}
		});
		expBasketsField.setHorizontalAlignment(SwingConstants.CENTER);
		expBasketsField.setForeground(new Color(0, 51, 153));
		expBasketsField.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		expBasketsField.setColumns(10);
		expBasketsField.setBorder(null);
		expBasketsField.setBackground(new Color(255, 255, 255));
		

		JLabel lblDelta_1 = new JLabel("Delta");
		lblDelta_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblDelta_1.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		lblDelta_1.setBounds(304, 44, 56, 21);
		panel_2.add(lblDelta_1);
		

		lblIndDelta_1 = new JLabel("Ind delta");
		lblIndDelta_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblIndDelta_1.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		lblIndDelta_1.setBounds(370, 43, 67, 21);
		panel_2.add(lblIndDelta_1);

		JButton resetOptionsButton = new JButton("Reset options");
		resetOptionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				for (Option option : optionsMonth.getOptions_list()) {
					option.setBidAskCounter(0);
					option.setBidAskCalcCounter(0);
					option.getBidAskCalcCounterList().clear();
					option.getBidAskCounterList().clear();
				}

				Thread res = new Thread(() -> {
					try {
						String preText = resetOptionsButton.getText();

						resetOptionsButton.setText("Cleared");

						Thread.sleep(2000);

						resetOptionsButton.setText(preText);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				res.start();
			}
		});
		resetOptionsButton.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		resetOptionsButton.setBounds(699, 11, 134, 30);
		setting_panel.add(resetOptionsButton);

		JButton btnResetAll = new JButton("Reset all");
		btnResetAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// Options
				resetOptionsButton.doClick();
				
				// Races
				apiObject.setConUp(0);
				apiObject.setConDown(0);
				apiObject.setIndUp(0);
				apiObject.setIndDown(0);

				// Baskets
				apiObject.setBasketUp(0);
				apiObject.setBasketDown(0);

				// Timers op
				apiObject.setOptimiTimer(0);
				apiObject.setPesimiTimer(0);

				// Op avg
				optionsMonth.getOpChartList().clear();

				// Index and future bid ask counters
				apiObject.setIndBidAskCounter(0);
				apiObject.getIndexBidAskCounterList().clear();
				optionsMonth.getConBidAskCounterList().clear();
				apiObject.getIndexChartList().clear();

			}
		});
		btnResetAll.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		btnResetAll.setBounds(699, 57, 134, 30);
		setting_panel.add(btnResetAll);

		JButton btnResetDb = new JButton("Reset db");
		btnResetDb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Arik.getInstance().sendMessage(Arik.sagivID, "Ta35 reset success", null);
			}
		});
		btnResetDb.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		btnResetDb.setBounds(699, 92, 134, 30);
		setting_panel.add(btnResetDb);


		JLabel lblDelta = new JLabel("Delta");
		lblDelta.setHorizontalAlignment(SwingConstants.CENTER);
		lblDelta.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		lblDelta.setBounds(91, 46, 71, 21);
		panel_3.add(lblDelta);

		JLabel lblConBaCounter = new JLabel("Con counter");
		lblConBaCounter.setHorizontalAlignment(SwingConstants.CENTER);
		lblConBaCounter.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		lblConBaCounter.setBounds(170, 46, 71, 21);
		panel_3.add(lblConBaCounter);


		JLabel lblOptions = new JLabel("Options");
		lblOptions.setForeground(new Color(0, 0, 128));
		lblOptions.setHorizontalAlignment(SwingConstants.LEFT);
		lblOptions.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblOptions.setBounds(14, 11, 67, 21);
		panel_3.add(lblOptions);
		
		JLabel lblExp_1 = new JLabel("Exp");
		lblExp_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblExp_1.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		lblExp_1.setBounds(251, 47, 71, 21);
		panel_3.add(lblExp_1);
		
		expField = new JTextField();
		expField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!expField.getText().isEmpty()) {
					try {
						double startExp = L.dbl(expField.getText());
						exp.getExpData().setStart(startExp);
					} catch (Exception exception) {
						JOptionPane.showMessageDialog(frame, exception.getMessage());
					}
				}
			}
		});
		expField.setHorizontalAlignment(SwingConstants.CENTER);
		expField.setForeground(new Color(0, 51, 153));
		expField.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		expField.setColumns(10);
		expField.setBorder(null);
		expField.setBackground(Color.WHITE);
		expField.setBounds(251, 73, 71, 22);
		panel_3.add(expField);
		
		JLabel lblTodayDelta = new JLabel("Today delta");
		lblTodayDelta.setHorizontalAlignment(SwingConstants.CENTER);
		lblTodayDelta.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		lblTodayDelta.setBounds(10, 46, 71, 21);
		panel_3.add(lblTodayDelta);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(SystemColor.menu);
		panel_4.setBounds(0, 0, 632, 180);
		setting_panel.add(panel_4);
		panel_4.setLayout(null);

		lblNewLabel_1 = new JLabel("Future");
		lblNewLabel_1.setBounds(11, 38, 45, 21);
		panel_4.add(lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Dubai Medium", Font.PLAIN, 14));

		lblIndex = new JLabel("Index");
		lblIndex.setBounds(73, 38, 45, 21);
		panel_4.add(lblIndex);
		lblIndex.setHorizontalAlignment(SwingConstants.CENTER);
		lblIndex.setFont(new Font("Dubai Medium", Font.PLAIN, 14));

		setting_index_up = new JTextField();
		setting_index_up.setBounds(69, 65, 45, 22);
		panel_4.add(setting_index_up);
		setting_index_up.setBackground(new Color(255, 255, 255));
		setting_index_up.setForeground(new Color(0, 51, 153));
		setting_index_up.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		setting_index_up.setHorizontalAlignment(SwingConstants.CENTER);
		setting_index_up.setBorder(null);
		setting_index_up.setColumns(10);

		setting_f_up = new JTextField();
		setting_f_up.setBounds(10, 65, 45, 22);
		panel_4.add(setting_f_up);
		setting_f_up.setBackground(new Color(255, 255, 255));
		setting_f_up.setForeground(new Color(0, 51, 153));
		setting_f_up.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		setting_f_up.setHorizontalAlignment(SwingConstants.CENTER);
		setting_f_up.setBorder(null);
		setting_f_up.setColumns(10);
		lblNewLabel_1.setLabelFor(setting_f_up);

		setting_f_down = new JTextField();
		setting_f_down.setBounds(10, 98, 45, 22);
		panel_4.add(setting_f_down);
		setting_f_down.setBackground(new Color(255, 255, 255));
		setting_f_down.setForeground(new Color(204, 0, 51));
		setting_f_down.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		setting_f_down.setHorizontalAlignment(SwingConstants.CENTER);
		setting_f_down.setBorder(null);
		setting_f_down.setColumns(10);

		setting_index_down = new JTextField();
		setting_index_down.setBounds(69, 98, 45, 22);
		panel_4.add(setting_index_down);
		setting_index_down.setBackground(new Color(255, 255, 255));
		setting_index_down.setForeground(new Color(204, 0, 51));
		setting_index_down.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		setting_index_down.setHorizontalAlignment(SwingConstants.CENTER);
		setting_index_down.setBorder(null);
		setting_index_down.setColumns(10);

		set = new JButton("Set");
		set.setBorder(null);
		set.setBackground(new Color(220, 220, 220));
		set.setBounds(10, 131, 104, 30);
		panel_4.add(set);
		set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					apiObject.setConUp(Integer.parseInt(setting_f_up.getText()));
					apiObject.setConDown(Integer.parseInt(setting_f_down.getText()));
					apiObject.setIndUp(Integer.parseInt(setting_index_up.getText()));
					apiObject.setIndDown(Integer.parseInt(setting_index_down.getText()));

				} catch (NumberFormatException e) {
					e.printStackTrace();
					popup("Only numbers are eccepted ", e);
				}
			}

		});
		set.setFont(new Font("Dubai Medium", Font.PLAIN, 14));

		lblBasketUp = new JLabel("Up");
		lblBasketUp.setBounds(137, 38, 23, 22);
		panel_4.add(lblBasketUp);
		lblBasketUp.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		
		textField_2 = new JTextField();
		textField_2.setBounds(174, 38, 45, 22);
		panel_4.add(textField_2);
		textField_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					apiObject.setBasketUp(Integer.parseInt(e.getActionCommand()));
				} catch (Exception e1) {
					popup("Failed", e1);
				}
			}
		});
		textField_2.setHorizontalAlignment(SwingConstants.CENTER);
		textField_2.setForeground(new Color(0, 51, 153));
		textField_2.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		textField_2.setColumns(10);
		textField_2.setBorder(null);
		textField_2.setBackground(new Color(255, 255, 255));

		lblBasketDown = new JLabel("Down");
		lblBasketDown.setBounds(137, 72, 37, 21);
		panel_4.add(lblBasketDown);
		lblBasketDown.setFont(new Font("Dubai Medium", Font.PLAIN, 14));

		textField_3 = new JTextField();
		textField_3.setBounds(174, 71, 45, 22);
		panel_4.add(textField_3);
		textField_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					apiObject.setBasketDown(Integer.parseInt(e.getActionCommand()));
				} catch (Exception e1) {
					popup("Failed", e1);
				}
			}
		});
		textField_3.setHorizontalAlignment(SwingConstants.CENTER);
		textField_3.setForeground(new Color(0, 51, 153));
		textField_3.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 15));
		textField_3.setColumns(10);
		textField_3.setBorder(null);
		textField_3.setBackground(new Color(255, 255, 255));

		JLabel lblBaskets = new JLabel("Baskets");
		lblBaskets.setForeground(new Color(0, 0, 128));
		lblBaskets.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblBaskets.setBounds(146, 11, 59, 21);
		panel_4.add(lblBaskets);

		JLabel lblMove = new JLabel("Move");
		lblMove.setForeground(new Color(0, 0, 128));
		lblMove.setHorizontalAlignment(SwingConstants.CENTER);
		lblMove.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblMove.setBounds(247, 11, 50, 21);
		panel_4.add(lblMove);

		JLabel lblCounters = new JLabel("Counters");
		lblCounters.setForeground(new Color(0, 0, 128));
		lblCounters.setHorizontalAlignment(SwingConstants.CENTER);
		lblCounters.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblCounters.setBounds(390, 11, 71, 21);
		panel_4.add(lblCounters);

		JLabel lblIndCounter = new JLabel("Ind");
		lblIndCounter.setBounds(399, 38, 23, 21);
		panel_4.add(lblIndCounter);
		lblIndCounter.setHorizontalAlignment(SwingConstants.CENTER);
		lblIndCounter.setFont(new Font("Dubai Medium", Font.PLAIN, 14));


		lblR = new JLabel("Races");
		lblR.setForeground(new Color(0, 0, 128));
		lblR.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblR.setBounds(10, 11, 59, 21);
		panel_4.add(lblR);
		
		JLabel lblIndDelta = new JLabel("Delta");
		lblIndDelta.setHorizontalAlignment(SwingConstants.CENTER);
		lblIndDelta.setForeground(new Color(0, 0, 128));
		lblIndDelta.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblIndDelta.setBounds(520, 10, 71, 21);
		panel_4.add(lblIndDelta);
		
		JLabel label_1 = new JLabel("Ind");
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		label_1.setBounds(483, 37, 50, 21);
		panel_4.add(label_1);
		
		JLabel lblIndDeltaBaskets = new JLabel("Ind no baskets");
		lblIndDeltaBaskets.setHorizontalAlignment(SwingConstants.CENTER);
		lblIndDeltaBaskets.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		lblIndDeltaBaskets.setBounds(468, 65, 83, 21);
		panel_4.add(lblIndDeltaBaskets);

		panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBackground(SystemColor.menu);
		panel_1.setBounds(0, 458, 632, 153);
		setting_panel.add(panel_1);

		lblDataBase = new JLabel("Data base");
		lblDataBase.setHorizontalAlignment(SwingConstants.LEFT);
		lblDataBase.setForeground(new Color(0, 0, 128));
		lblDataBase.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblDataBase.setBounds(10, 11, 84, 21);
		panel_1.add(lblDataBase);

		btnReset = new JButton("Reset");
		btnReset.setBorder(null);
		btnReset.setBackground(new Color(220, 220, 220));
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Arik.getInstance().sendMessage(Arik.sagivID, "Ta35 reset success", null);
				} catch (Exception exception) {
					popup("Reset failed", exception);
				}
			}
		});
		btnReset.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		btnReset.setBounds(10, 39, 76, 30);
		panel_1.add(btnReset);

		btnLoad = new JButton("Load");
		btnLoad.setBorder(null);
		btnLoad.setBackground(new Color(220, 220, 220));
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DataBaseHandler dataBaseHandler = new DataBaseHandler();
					dataBaseHandler.load_data();
					Arik.getInstance().sendMessage(Arik.sagivID, "Ta35 load success", null);
				} catch (Exception exception) {
					popup("Load failed", exception);
				}
			}
		});
		btnLoad.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		btnLoad.setBounds(96, 39, 76, 30);
		panel_1.add(btnLoad);

		btnSum = new JButton("Sum");
		btnSum.setBorder(null);
		btnSum.setBackground(new Color(220, 220, 220));
		btnSum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Arik.getInstance().sendMessage(Arik.sagivID, "Ta35 Sum line success", null);
				} catch (Exception exception) {
					popup("Insert sum line failed", exception);
				}
			}
		});
		btnSum.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		btnSum.setBounds(182, 39, 76, 30);
		panel_1.add(btnSum);

		btnUpdate = new JButton("Update");
		btnUpdate.setBorder(null);
		btnUpdate.setBackground(new Color(220, 220, 220));
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Arik.getInstance().sendMessage(Arik.sagivID, "Ta35 update status success", null);
				} catch (Exception exception) {
					popup("Update failed", exception);
				}
			
				
			}
		});
		btnUpdate.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		btnUpdate.setBounds(268, 39, 76, 30);
		panel_1.add(btnUpdate);
		
		JButton btnNewConnection = new JButton("New connection");
		btnNewConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					JibeConnectionPool.addNewConnection();
				} catch (SQLException e) {
					WindowTA35.popup("Add connection faild", e);
				}
			}
		});
		btnNewConnection.setFont(new Font("Dubai Medium", Font.PLAIN, 14));
		btnNewConnection.setBorder(null);
		btnNewConnection.setBackground(new Color(220, 220, 220));
		btnNewConnection.setBounds(354, 39, 110, 30);
		panel_1.add(btnNewConnection);
	}

	// Creating popup window alert
	private void popup(String message, Exception e) {
		JOptionPane.showMessageDialog(frame, message + "\n" + e.getMessage());
	}

	// Set the window visible
	public void setVisible() {
		frame.setVisible(true);
	}
}
