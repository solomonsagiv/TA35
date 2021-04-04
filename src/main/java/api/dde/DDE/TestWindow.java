package api.dde.DDE;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TestWindow {

	public static TestWindow window;

	JFrame frame;
	JTextField optimiMoveField;
	JTextField pesimiMoveField;
	JTextField lastStateField;
	JTextField optimiLiveMoveField;
	private JLabel label;
	private JLabel label_1;
	JTextField pesimiLiveMoveField;
	JTextField preStateField;
	private JLabel lblPreState;
	private JLabel lblStartPrice;
	JTextField startPriceField;
	private JLabel lblEndPrice;
	JTextField endPriceField;
	private JLabel lblFuture;
	JTextField futureField;
	private JLabel lblIndex;
	JTextField indexField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new TestWindow();
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
	public TestWindow() {
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
		optimiMoveField.setBounds(10, 43, 86, 20);
		frame.getContentPane().add(optimiMoveField);
		optimiMoveField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Optimi move sum");
		lblNewLabel.setBounds(10, 18, 83, 14);
		frame.getContentPane().add(lblNewLabel);

		pesimiMoveField = new JTextField();
		pesimiMoveField.setColumns(10);
		pesimiMoveField.setBounds(113, 43, 86, 20);
		frame.getContentPane().add(pesimiMoveField);

		JLabel lblPesimiMove = new JLabel("Pesimi move  sum");
		lblPesimiMove.setBounds(113, 18, 86, 14);
		frame.getContentPane().add(lblPesimiMove);

		lastStateField = new JTextField();
		lastStateField.setColumns(10);
		lastStateField.setBounds(319, 40, 86, 20);
		frame.getContentPane().add(lastStateField);

		JLabel lblNewLabel_1 = new JLabel("Last state");
		lblNewLabel_1.setBounds(319, 18, 105, 14);
		frame.getContentPane().add(lblNewLabel_1);

		optimiLiveMoveField = new JTextField();
		optimiLiveMoveField.setColumns(10);
		optimiLiveMoveField.setBounds(10, 99, 86, 20);
		frame.getContentPane().add(optimiLiveMoveField);

		label = new JLabel("Optimi move");
		label.setBounds(10, 74, 83, 14);
		frame.getContentPane().add(label);

		label_1 = new JLabel("Pesimi move ");
		label_1.setBounds(113, 74, 79, 14);
		frame.getContentPane().add(label_1);

		pesimiLiveMoveField = new JTextField();
		pesimiLiveMoveField.setColumns(10);
		pesimiLiveMoveField.setBounds(113, 99, 86, 20);
		frame.getContentPane().add(pesimiLiveMoveField);

		preStateField = new JTextField();
		preStateField.setColumns(10);
		preStateField.setBounds(319, 93, 86, 20);
		frame.getContentPane().add(preStateField);

		lblPreState = new JLabel("Pre state ");
		lblPreState.setBounds(319, 71, 105, 14);
		frame.getContentPane().add(lblPreState);

		lblStartPrice = new JLabel("Start price");
		lblStartPrice.setBounds(10, 149, 83, 14);
		frame.getContentPane().add(lblStartPrice);

		startPriceField = new JTextField();
		startPriceField.setColumns(10);
		startPriceField.setBounds(10, 174, 86, 20);
		frame.getContentPane().add(startPriceField);

		lblEndPrice = new JLabel("End price");
		lblEndPrice.setBounds(113, 149, 83, 14);
		frame.getContentPane().add(lblEndPrice);

		endPriceField = new JTextField();
		endPriceField.setColumns(10);
		endPriceField.setBounds(113, 174, 86, 20);
		frame.getContentPane().add(endPriceField);

		lblFuture = new JLabel("Future");
		lblFuture.setBounds(235, 149, 83, 14);
		frame.getContentPane().add(lblFuture);

		futureField = new JTextField();
		futureField.setText("1570.5");
		futureField.setColumns(10);
		futureField.setBounds(235, 174, 86, 20);
		frame.getContentPane().add(futureField);

		lblIndex = new JLabel("Index");
		lblIndex.setBounds(338, 149, 86, 14);
		frame.getContentPane().add(lblIndex);

		indexField = new JTextField();
		indexField.setText("1571.0");
		indexField.setColumns(10);
		indexField.setBounds(338, 174, 86, 20);
		frame.getContentPane().add(indexField);
	}
}
