package TEST;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TestWindow {

	private JFrame frame;
	private JTextField call;
	private JTextField put;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestWindow window = new TestWindow();
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
		
		call = new JTextField();
		call.setFont(new Font("Tahoma", Font.PLAIN, 18));
		call.setHorizontalAlignment(SwingConstants.CENTER);
		call.setBounds(64, 50, 175, 105);
		frame.getContentPane().add(call);
		call.setColumns(10);
		
		put = new JTextField();
		put.setHorizontalAlignment(SwingConstants.CENTER);
		put.setFont(new Font("Tahoma", Font.PLAIN, 18));
		put.setColumns(10);
		put.setBounds(249, 50, 175, 105);
		frame.getContentPane().add(put);
		
		JButton start = new JButton("New button");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					@Override
					public void run() {
						while (true) {
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				};
				t.start();
			}
		});
		start.setBounds(181, 197, 89, 23);
		frame.getContentPane().add(start);
	}
	
	// floor 
	private static double floor(double d) {
		return Math.floor(d * 100) / 100;
	}

}
