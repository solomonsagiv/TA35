package backTest;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class BackTestWindow {

	public static BackTestWindow window;

	public static JFrame frame;
	public static JTextField optimiMoveField;
	public static JTextField pesimiMoveField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BackTestWindow window = new BackTestWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public BackTestWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		optimiMoveField = new JTextField();
		optimiMoveField.setFont(new Font("Dubai Medium", Font.PLAIN, 13));
		optimiMoveField.setBounds(10, 28, 71, 27);
		frame.getContentPane().add(optimiMoveField);
		optimiMoveField.setColumns(10);

		pesimiMoveField = new JTextField();
		pesimiMoveField.setFont(new Font("Dubai Medium", Font.PLAIN, 13));
		pesimiMoveField.setBounds(10, 84, 71, 27);
		frame.getContentPane().add(pesimiMoveField);
		pesimiMoveField.setColumns(10);

		JLabel lblGetstdev = new JLabel("Optimi");
		lblGetstdev.setFont(new Font("Dubai Medium", Font.PLAIN, 15));
		lblGetstdev.setBounds(10, 11, 71, 14);
		frame.getContentPane().add(lblGetstdev);

		JLabel lblPesimi = new JLabel("Pesimi");
		lblPesimi.setFont(new Font("Dubai Medium", Font.PLAIN, 15));
		lblPesimi.setBounds(10, 66, 71, 14);
		frame.getContentPane().add(lblPesimi);
	}
}
