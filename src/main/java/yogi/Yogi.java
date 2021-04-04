package yogi;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Yogi {
	
	static Yogi window;
	
	Robot robot;
	MyKeyLogger keyLogger;
	MacroKeyLogger macroKeyLogger;

	private JFrame frame;
	private JPanel panel_1;
	private JButton listen;
	private JTextField time_to_move;
	private JButton stop_listen;
	JButton startMacro;
	JButton stopMacro;
	private JLabel status;
	JTextArea textAreaLastMacro;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new Yogi();
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
	public Yogi() {
		try {
			// Initialize
			initialize();

			// Create the stay focus thread
			try {
				StayFocus focus = new StayFocus(frame, time_to_move);
				focus.setName("focus");
				focus.start();
			} catch (AWTException e1) {
				popup(frame, e1.getMessage());
				e1.printStackTrace();
			}
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws AWTException
	 */
	private void initialize() throws AWTException {
		robot = new Robot();
		frame = new JFrame();
		frame.setAlwaysOnTop(true);
		frame.setBounds(100, 500, 603, 355);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (keyLogger != null) {
					keyLogger.stop();
					System.exit(0);
				}
			}
		});
		frame.getContentPane().setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 587, 321);
		frame.getContentPane().add(tabbedPane);

		panel_1 = new JPanel();
		tabbedPane.addTab("Run", null, panel_1, null);
		panel_1.setLayout(null);

		listen = new JButton("Listen");
		listen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listen.setEnabled(false);
				stop_listen.setEnabled(true);
				try {
					keyLogger = new MyKeyLogger(mouse_x, mouse_y);
					keyLogger.start();

					status.setText("Listening");
				} catch (AWTException e) {
					status.setText("Not listening");
					popup(frame, e.getMessage());
					e.printStackTrace();
				}

			}
		});
		listen.setFont(new Font("Tahoma", Font.PLAIN, 12));
		listen.setBounds(10, 11, 93, 27);
		panel_1.add(listen);

		JButton open_shaldag = new JButton("Sivron");
		open_shaldag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open_shaldag.setEnabled(false);
				frame.setState(Frame.ICONIFIED);

				Thread thread = new Thread() {
					@Override
					public void run() {
						open_sivron();
					}
				};
				thread.start();
			}
		});
		open_shaldag.setFont(new Font("Tahoma", Font.PLAIN, 12));
		open_shaldag.setBounds(10, 49, 93, 27);
		panel_1.add(open_shaldag);

		time_to_move = new JTextField();
		time_to_move.setHorizontalAlignment(SwingConstants.CENTER);
		time_to_move.setFont(new Font("Arial", Font.PLAIN, 15));
		time_to_move.setColumns(10);
		time_to_move.setBounds(10, 131, 93, 24);
		panel_1.add(time_to_move);

		stop_listen = new JButton("Stop listen");
		stop_listen.setEnabled(false);
		stop_listen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listen.setEnabled(true);
				keyLogger.stop();
				status.setText("Not listening");
			}
		});
		stop_listen.setFont(new Font("Tahoma", Font.PLAIN, 12));
		stop_listen.setBounds(113, 11, 93, 27);
		panel_1.add(stop_listen);

		status = new JLabel("Not listening");
		status.setHorizontalAlignment(SwingConstants.CENTER);
		status.setFont(new Font("Arial", Font.PLAIN, 15));
		status.setBounds(113, 131, 93, 24);
		panel_1.add(status);

		panel = new JPanel();
		tabbedPane.addTab("Mouse", null, panel, null);
		panel.setLayout(null);

		label = new JLabel("X");
		label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		label.setBounds(10, 13, 19, 14);
		panel.add(label);

		mouse_x = new JTextField();
		mouse_x.setHorizontalAlignment(SwingConstants.CENTER);
		mouse_x.setFont(new Font("Tahoma", Font.PLAIN, 12));
		mouse_x.setColumns(10);
		mouse_x.setBounds(29, 11, 46, 20);
		panel.add(mouse_x);

		label_1 = new JLabel("Y");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		label_1.setBounds(103, 11, 19, 14);
		panel.add(label_1);

		mouse_y = new JTextField();
		mouse_y.setHorizontalAlignment(SwingConstants.CENTER);
		mouse_y.setFont(new Font("Tahoma", Font.PLAIN, 12));
		mouse_y.setColumns(10);
		mouse_y.setBounds(122, 11, 46, 20);
		panel.add(mouse_y);
		
		panel_2 = new JPanel();
		tabbedPane.addTab("Macro", null, panel_2, null);
		panel_2.setLayout(null);
		
		startMacro = new JButton("Start macro");
		startMacro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startMacro.setEnabled(false);
				stopMacro.setEnabled(true);
				try {
					macroKeyLogger = new MacroKeyLogger(window);
					macroKeyLogger.start();
				} catch (AWTException e) {
					textAreaLastMacro.append(e.getMessage());
				}
			}
		});
		startMacro.setBounds(437, 11, 135, 23);
		panel_2.add(startMacro);
		
		stopMacro = new JButton("Stop macro");
		stopMacro.setEnabled(false);
		stopMacro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startMacro.setEnabled(true);
				stopMacro.setEnabled(false);
				
				macroKeyLogger.stop();
			}
		});
		stopMacro.setBounds(437, 45, 135, 23);
		panel_2.add(stopMacro);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 254, 69);
		panel_2.add(scrollPane);
		
		textAreaLastMacro = new JTextArea();
		scrollPane.setViewportView(textAreaLastMacro);
		
		button = new JButton("Show macros");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				macroKeyLogger.appendMacroListToTextArea();
			}
		});
		button.setBounds(278, 259, 136, 23);
		panel_2.add(button);
		
		btnAppendMacro = new JButton("Append macro");
		btnAppendMacro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				macroKeyLogger.appendMacro();
			}
		});
		btnAppendMacro.setBounds(274, 45, 140, 23);
		panel_2.add(btnAppendMacro);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 91, 254, 191);
		panel_2.add(scrollPane_1);
		
		textAreaAllMacros = new JTextArea();
		scrollPane_1.setViewportView(textAreaAllMacros);
		
		btnResetMacro = new JButton("Reset macro");
		btnResetMacro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				macroKeyLogger.resetMacro();
			}
		});
		btnResetMacro.setBounds(274, 11, 140, 23);
		panel_2.add(btnResetMacro);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(424, 11, 20, 271);
		panel_2.add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(274, 85, 140, 12);
		panel_2.add(separator_1);
	}

	// Popup wondow
	public static void popup(JFrame frame, String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	int[] start_point = new int[2];
	int[] minus = { 445, 685 };
	int[] plus = { 445, 670 };
	int[] accept = { 445, 720 };
	int[] shaldag = { 35, 17 };
	int[] shaldag_user_name = { 580, 375 };
	int[] shaldag_password = { 580, 412 };
	int[] shaldag_accept = { 753, 536 };
	int[] nigzarim = { 1201, 27 };
	int[] nigzari_maof = { 1180, 46 };
	int[] choose_account = { 1193, 911 };
	int[] account = { 1181, 825 };
	int[] sell = { 772, 767 };
	int[] buy = { 702, 767 };
	int[] setting = { 722, 28 };
	int[] setting_maof = { 720, 47 };
	int[] save = { 236, 279 };
	int[] exit_setting = { 1223, 17 };
	private JPanel panel;
	private JLabel label;
	private JTextField mouse_x;
	private JLabel label_1;
	private JTextField mouse_y;
	private JPanel panel_2;
	private JButton button;
	private JButton btnAppendMacro;
	private JScrollPane scrollPane_1;
	JTextArea textAreaAllMacros;
	private JButton btnResetMacro;

	// Open the sivron
	public void open_sivron() {
		try {
			move_and_double_click(shaldag);
			Thread.sleep(5000);
			key_press_release(KeyEvent.VK_L);
			key_press_release(KeyEvent.VK_8);
			key_press_release(KeyEvent.VK_5);
			key_press_release(KeyEvent.VK_0);
			key_press_release(KeyEvent.VK_5);
			key_press_release(KeyEvent.VK_1);
			move_and_click(shaldag_password);
			key_press_release(KeyEvent.VK_X);
			key_press_release(KeyEvent.VK_2);
			key_press_release(KeyEvent.VK_X);
			key_press_release(KeyEvent.VK_2);
			key_press_release(KeyEvent.VK_X);
			key_press_release(KeyEvent.VK_2);
			move_and_click(shaldag_accept);
			Thread.sleep(90000);
			move_and_click(nigzarim);
			Thread.sleep(1500);
			move_and_click(nigzari_maof);
			Thread.sleep(5000);
			move_and_click(choose_account);
			Thread.sleep(1000);
			move_and_click(account);
			Thread.sleep(1000);
			move_and_click(sell);
			key_press_release(KeyEvent.VK_1);
			key_press_release(KeyEvent.VK_0);
			move_and_click(buy);
			key_press_release(KeyEvent.VK_1);
			key_press_release(KeyEvent.VK_0);
			move_and_click(setting);
			Thread.sleep(1000);
			move_and_click(setting_maof);
			Thread.sleep(3000);
			move_and_click(save);
			Thread.sleep(3000);
			move_and_click(exit_setting);
			Thread.sleep(3000);
			key_press_release(KeyEvent.VK_CONTROL);
			key_press_release(KeyEvent.VK_I);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Move and click
	public void move_and_click(int[] point) {
		robot.mouseMove(point[0], point[1]);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	// Move and click
	public void move_and_double_click(int[] point) {
		robot.mouseMove(point[0], point[1]);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	// Click and release
	private void key_press_release(int keyEvent) {
		robot.keyPress(keyEvent);
		robot.keyRelease(keyEvent);
	}
}
