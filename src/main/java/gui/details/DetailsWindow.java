package gui.details;

import api.TA35;
import dataBase.mySql.JibeConnectionPool;
import threads.MyThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DetailsWindow {

	public JFrame frame;
	public static JTextArea textArea;
	public static JTextArea optionsWeekArea;
	public static JTextArea optionsMonthArea;
	public static JTextArea indStocksArea;

	Updater updater;

	TA35 client;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DetailsWindow window = new DetailsWindow();
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
	public DetailsWindow() {
		this.client = TA35.getInstance();
		initialize();
		updater = new Updater();
		updater.getHandler().start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				updater.getHandler().close();
			}
		});
		frame.setBounds(100, 100, 1026, 612);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 1071, 649);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Details");
		lblNewLabel.setForeground(new Color(0, 0, 102));
		lblNewLabel.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblNewLabel.setBounds(10, 11, 84, 37);
		panel.add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 46, 322, 517);
		panel.add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JLabel lblOptions = new JLabel("Options");
		lblOptions.setForeground(new Color(0, 0, 102));
		lblOptions.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblOptions.setBounds(342, 11, 84, 37);
		panel.add(lblOptions);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(342, 46, 322, 253);
		panel.add(scrollPane_1);

		optionsWeekArea = new JTextArea();
		scrollPane_1.setViewportView(optionsWeekArea);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(342, 310, 322, 253);
		panel.add(scrollPane_2);

		optionsMonthArea = new JTextArea();
		scrollPane_2.setViewportView(optionsMonthArea);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(674, 46, 322, 517);
		panel.add(scrollPane_3);

		indStocksArea = new JTextArea();
		scrollPane_3.setViewportView(indStocksArea);

		JLabel lblStocks = new JLabel("Stocks");
		lblStocks.setForeground(new Color(0, 0, 102));
		lblStocks.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		lblStocks.setBounds(674, 11, 84, 37);
		panel.add(lblStocks);
	}

	// Text update
	class Updater extends MyThread implements Runnable {

		int sleep = 1000;

		public Updater() {
			setRunnable(this);
		}

		@Override
		public void run() {

			while (isRun()) {
				try {

					// Sleep
					Thread.sleep(sleep);

					// Update text
					updateText();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private void updateText() {
			
			StringBuilder text = new StringBuilder();
			text.append(client.getAsJson().toString(4));
			text.append("/n Connection pool /n");
			text.append(JibeConnectionPool.getAsJson().toString(4));
			
			textArea.setText(text.toString());
			optionsWeekArea.setText(client.getExps().getWeek().getOptions().getOptionsWithDataAsJson().toString(4));
			optionsMonthArea.setText(client.getExps().getMonth().getOptions().getOptionsWithDataAsJson().toString(4));
			indStocksArea.setText(client.getStocksHandler().getData().toString(4));
		}

		@Override
		public void initRunnable() {
			setRunnable(this);
		}
	}
}
