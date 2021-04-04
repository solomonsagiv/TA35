package gui.popupsFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import charts.MyChartPanel;
import charts.gui.ChartSettingWindow;
import charts.myChart.MyChart;

public class PopupsMenuFactory {

    public static JPopupMenu chartMenu( MyChartPanel chartPanel, MyChart myChart ) {
        // Main menu
        JPopupMenu menu = new JPopupMenu();
        
        // Setting
        JMenuItem setting = new JMenuItem("Setting");
        setting.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed( ActionEvent e ) {
				ChartSettingWindow settingWindow = new ChartSettingWindow(myChart);
			}
		});
        
        menu.add(setting);
        return menu;
    }
    
}	
