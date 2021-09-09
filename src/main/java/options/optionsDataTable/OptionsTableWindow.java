package options.optionsDataTable;

import api.ApiObject;
import exp.Exp;
import gui.MyGuiComps;
import locals.L;
import locals.Themes;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsTableWindow extends MyGuiComps.MyFrame {

    public static void main(String[] args) {
        new OptionsTableWindow("Options window", ApiObject.getInstance().getExpMonth());
    }

    MyGuiComps.MyPanel main_panel;
    JScrollPane scrollPane;
    OptionsTable table;
    Exp exp;
    ApiObject apiObject;
    MyGuiComps.MyTextField start_strike_field;

    // Constructor
    public OptionsTableWindow(String title, Exp exp) throws HeadlessException {
        super(title);
        apiObject = ApiObject.getInstance();
        this.exp = exp;
    }

    @Override
    public void initListeners() {
        start_strike_field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                set_strikes_in_table();
            }
        });
    }

    @Override
    public void initialize() {

        String[] headers = {
                "Pricing",
                "Position",
                "Delta",
                "Strike",
                "Delta",
                "Position",
                "Pricing",
        };

        // This
        setLayout(null);
        getContentPane().setLayout(null);
        setBackground(Themes.GREY_VERY_LIGHT);

        // Main panel
        main_panel = new MyGuiComps.MyPanel();
        main_panel.setLayout(null);
        main_panel.setBounds(0, 0, 650, 400);
        add(main_panel);

        // Start strike
        start_strike_field = new MyGuiComps.MyTextField();
        start_strike_field.setText("1720");
        start_strike_field.setBounds(300, 0, 50, 25);
        main_panel.add(start_strike_field);

        // My options table
        table = new OptionsTable(get_data_table(), headers, exp);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, start_strike_field.getY() + start_strike_field.getHeight() + 5, main_panel.getWidth(), main_panel.getHeight());
        scrollPane.setBackground(Themes.BLUE);
        table.setFillsViewportHeight(true);
        main_panel.add(scrollPane);
    }

    @Override
    public void onClose() {
        super.onClose();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        table.close();
    }

    private Object[][] get_data_table() {
        Object[][] data = new Object[14][7];

        // Set strikes in table for base data
        for (int i = 0; i < 14; i++) {
            data[i][0] = 0;
            data[i][1] = 0;
            data[i][2] = 0;
            data[i][3] = 0;
            data[i][4] = 0;
            data[i][5] = 0;
            data[i][6] = 0;
        }
        return data;
    }

    public void set_strikes_in_table() {
        int start_strike = L.INT(start_strike_field.getText());
        int end_strike = start_strike + 140;
        int row = 0;
        for (int i = start_strike; i < end_strike; i+= 10) {
           table.setValueAt(i, row, OptionsTable.strike_col);
           row++;
        }
    }
}