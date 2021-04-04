package TEST;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jsoup.Jsoup;

public class StartWindow {
	private JFrame frame;
	private String dax_url = "http://localhost:8081/t/start";
	private String connect_url = "http://localhost:8081/t/api/connect";
	private String ta35_url = "http://localhost:8080/ta_35/start";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartWindow window = new StartWindow();
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
	public StartWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 244, 125);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton start_dax = new JButton("Dax");
		start_dax.setBackground(new Color(0, 0, 51));
		start_dax.setForeground(new Color(153, 204, 255));
		start_dax.setFont(new Font("Tahoma", Font.PLAIN, 18));
		start_dax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Jsoup.connect(connect_url).get();
					Thread.sleep(2000);
					Jsoup.connect(dax_url).get();
				} catch (Exception e) {
					popup("", e);
					e.printStackTrace();
				}
			}
		});
		start_dax.setBounds(0, 0, 116, 87);
		frame.getContentPane().add(start_dax);
		
		JButton start_ta35 = new JButton("TA35");
		start_ta35.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Jsoup.connect(ta35_url).get();
				} catch (Exception e) {
					popup("", e);
					e.printStackTrace();
				}
			}
		});
		start_ta35.setBackground(new Color(0, 0, 51));
		start_ta35.setForeground(new Color(153, 204, 255));
		start_ta35.setFont(new Font("Tahoma", Font.PLAIN, 18));
		start_ta35.setBounds(118, 0, 109, 87);
		frame.getContentPane().add(start_ta35);
	}

	// Popup
	public void popup(String message, Exception e) {
		JOptionPane.showMessageDialog(frame, message + "\n" + e.getMessage());
	}

}
