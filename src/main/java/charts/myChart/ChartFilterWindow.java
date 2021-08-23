package charts.myChart;

import gui.MyGuiComps;
import locals.Themes;
import org.apache.commons.lang3.StringUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ChartFilterWindow extends MyGuiComps.MyFrame {

    public static void main(String[] args) {
    }

    // Variables from constructor
    MyChart myChart;

    // Constructor
    MyGuiComps.MyPanel mainPanel;
    MyGuiComps.MyLabel titleLbl;

    // Constructors
    public ChartFilterWindow(String title) throws HeadlessException {
        super(title);
    }

    // Constructors
    public ChartFilterWindow(String title, MyChart myChart) throws HeadlessException {
        super(title);
        this.myChart = myChart;
        initParams(myChart);
    }

    private void initParams(MyChart myChart) {

        MyTimeSeries[] series = myChart.getSeries();

        int x = 10;
        int y = 40;
        int width = 300;
        int height = 30;

        for (MyTimeSeries serie : series) {

            // Create
            JCheckBox checkbox = new CheckBoxSeries(serie, myChart);
            checkbox.setBounds(x, y, width, height);

            // Append
            mainPanel.add(checkbox);

            // Update y
            y += height;
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initialize() {

        // This
        setXY(500, 400);
        setSize(300, 500);
        setLayout(null);

        // Main panel
        mainPanel = new MyGuiComps.MyPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, 300, 500);
        mainPanel.setBackground(Themes.GREY_VERY_LIGHT);

        add(mainPanel);

        // Title
        titleLbl = new MyGuiComps.MyLabel("Filter");
        titleLbl.setXY(10, 10);
        titleLbl.setForeground(Themes.BLUE);
        titleLbl.setFont(Themes.ARIEL_BOLD_15);
        mainPanel.add(titleLbl);
    }

    private class CheckBoxSeries extends JCheckBox {

        MyTimeSeries serie;
        MyChart myChart;

        public CheckBoxSeries(MyTimeSeries serie, MyChart myChart) {
            super();
            this.serie = serie;
            this.myChart = myChart;
            setSelected(serie.isVisible());
            setText(StringUtils.capitalize(serie.getName()));
            init();
        }

        private void init() {
            // This
            setForeground(Themes.BLUE);
            setBackground(Themes.GREY_VERY_LIGHT);

            addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    // taggle visibility
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                        serie.setVisible(true);
                    } else {
                        serie.setVisible(false);
                    }
                    // Update the chart
                    myChart.updateSeriesVisibility();
                }
            });
        }
    }
}
