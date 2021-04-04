package charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import gui.MyGuiComps.MyLabel;
import gui.popupsFactory.PopupsMenuFactory;

public class MyChartPanel extends ChartPanel {

	MyLabel highLbl;
	MyLabel lowLbl;
	MyLabel lastLbl;

	public MyChartPanel(JFreeChart chart, boolean includeTicker) {
		super(chart);
		setLayout(null);

		// Right click
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getModifiers() == MouseEvent.BUTTON3_MASK) {
					showPopUpMenu(event);
				}
			}
		});
		
		if (includeTicker) {

			lastLbl = new MyLabel("Last");
			lastLbl.setXY(20, 20);
			add(lastLbl);

			highLbl = new MyLabel("High");
			highLbl.setXY(70, 20);
			add(highLbl);

			lowLbl = new MyLabel("Low");
			lowLbl.setXY(120, 20);
			add(lowLbl);

		}
	}
	
	// Create basic lbl
	public JLabel createLbl(String name, Color color, int x, int y) {
		final JLabel lbl = new JLabel(name);
		lbl.setBounds(x, y, 50, 50);
		lbl.setFont(new Font("Arial", Font.BOLD, 14));
		return lbl;
	}

	public MyLabel getHighLbl() {
		return highLbl;
	}

	public MyLabel getLowLbl() {
		return lowLbl;
	}

	public MyLabel getLastLbl() {
		return lastLbl;
	}

	public void showPopUpMenu(MouseEvent event) {
		JPopupMenu menu = PopupsMenuFactory.chartMenu(this, null);
		// Show the menu
		menu.show(event.getComponent(), event.getX(), event.getY());
	}

}
