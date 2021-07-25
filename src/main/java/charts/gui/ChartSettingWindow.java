package charts.gui;

import charts.myChart.MyChart;
import charts.myChart.MyTimeSeries;
import locals.L;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class ChartSettingWindow {

	private JFrame frame;
	private JScrollPane scrollPane;
	private JPanel mainPanel;
	private JList list;

	MyTimeSeries series;
	MyChart myChart;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChartSettingWindow window = new ChartSettingWindow(null);
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
	public ChartSettingWindow(MyChart myChart) {
		
		// This
		this.myChart = myChart;
		this.series = myChart.getSeries()[0];

		// Init
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 454, 503);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);

		mainPanel = new JPanel();
		mainPanel.setBounds(0, 0, 438, 464);
		frame.getContentPane().add(mainPanel);
		mainPanel.setLayout(null);

		JLabel titleLbl = new JLabel("New label");
		titleLbl.setForeground(new Color(0, 0, 51));
		titleLbl.setHorizontalAlignment(SwingConstants.LEFT);
		titleLbl.setFont(new Font("Dubai Medium", Font.BOLD, 15));
		titleLbl.setBounds(10, 11, 196, 26);
		mainPanel.add(titleLbl);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 35, 319, 418);
		mainPanel.add(scrollPane);
		
		// List
		initList();
		
		JButton btnNewButton = new JButton("Remove");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {

					String[] selected = list.getSelectedValue().toString().split(" - ");

					RegularTimePeriod period;

					period = new Second(L.formatter.parse(selected[0]));

					series.delete(period);
					
					initList();
					
//					myChart.getUpdater().forceReRange();
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setForeground(new Color(0, 0, 51));
		btnNewButton.setFont(new Font("Dubai Medium", Font.PLAIN, 15));
		btnNewButton.setBounds(339, 33, 89, 23);
		mainPanel.add(btnNewButton);
		
	}

	private void initList() {

		DefaultListModel<String> strings = new DefaultListModel<>();

		String date, value;

		for (int i = 0; i < series.getItemCount(); i++) {

			date = series.getDataItem(i).getPeriod().toString();
			value = series.getDataItem(i).getValue().toString();

			strings.addElement(date + " - " + value);
		}

		list = new JList(strings);
		scrollPane.setViewportView(list);
		
	}
}
