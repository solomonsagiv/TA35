package stocks;

import api.BASE_CLIENT_OBJECT;
import gui.MyGuiComps;
import locals.L;
import locals.Themes;
import threads.MyThread;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SingleStockSummeryPanel extends MyGuiComps.MyPanel {

    // Variables
    BASE_CLIENT_OBJECT client;

    Updater updater;

    MyGuiComps.MyLabel name_lbl;
    MyGuiComps.MyPanel color_panel;

    MyGuiComps.MyTextField
            open,
            last,
            last_px,
            index_race_field,
            roll_race_field;

    // Constructor
    public SingleStockSummeryPanel(BASE_CLIENT_OBJECT client) {
        super();
        this.client = client;
        initialize(client);
        initListeners();

        updater = new Updater();
        updater.getHandler().start();

    }

    private void initListeners() {
        // Right click
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getModifiers() == MouseEvent.BUTTON3_MASK) {
                    showPopUpMenu(event);
                }
            }
        });
    }

    public void showPopUpMenu(MouseEvent event) {
//        JPopupMenu menu = PopupsMenuFactory.indexPanel(client);
//         Show the menu
//        menu.show(event.getComponent(), event.getX(), event.getY());
    }


    private void initialize(BASE_CLIENT_OBJECT client) {

        this.client = client;

        // This
        setWidth(500);
        setHeight(30);
        setBackground(Themes.GREY_2);

        color_panel = new MyGuiComps.MyPanel();
        color_panel.setBackground(client.get_index_race_serie_color());
        color_panel.setBounds(10, 10, 15, 15);
        add(color_panel);

        // Name
        name_lbl = new MyGuiComps.MyLabel(L.capitalizeFirstLetter(client.getName()));
        name_lbl.setXY(color_panel.getX() + color_panel.getWidth() + 3, 3);
        name_lbl.setFont(name_lbl.getFont().deriveFont(Font.BOLD));
        name_lbl.setForeground(Themes.BLUE_DARK);
        add(name_lbl);

        // Last price
        last_px = new MyGuiComps.MyTextField();
        last_px.setXY(name_lbl.getX() + name_lbl.getWidth() + 3, name_lbl.getY());
        add(last_px);

        // ------------------------------ Text fields ------------------------------ //

        // Open
        open = new MyGuiComps.MyTextField();
        open.setXY(last_px.getX() + last_px.getWidth() + 3, last_px.getY());
        add(open);

        // Last
        last = new MyGuiComps.MyTextField();
        last.setXY(open.getX() + open.getWidth() + 3, open.getY());
        add(last);

        // Index race
        index_race_field = new MyGuiComps.MyTextField();
        index_race_field.setXY(last.getX() + last.getWidth() + 3, last.getY());
        add(index_race_field);

        // Roll race
        roll_race_field = new MyGuiComps.MyTextField();
        roll_race_field.setXY(index_race_field.getX() + index_race_field.getWidth() + 3, index_race_field.getY());
        add(roll_race_field);

    }

    public void close() {
        updater.getHandler().close();
    }

    private class Updater extends MyThread implements Runnable {

        public Updater() {
            super();
        }

        @Override
        public void initRunnable() {
            setRunnable(this);
        }

        @Override
        public void run() {
            go();
        }

        private void go() {
            while (isRun()) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                set_text();
            }
        }

        int index_race = 0, roll_race = 0, df_4 = 0, df_8 = 0;


        private void set_text() {
            // Data
            if (client instanceof api.TA35) {
                index_race = (int) ((api.TA35) client).get_index_races_iw();
                roll_race = (int) ((api.TA35) client).get_week_races_wi();
            } else {
                index_race = (int) client.get_main_race().get_r_one_points();
                roll_race = (int) client.get_main_race().get_r_two_points();
            }

            // Ticker
            open.colorBack(client.getOpenPresent(), L.format100(), "%");
            last.colorBack(client.getLastPresent(), L.format100(), "%");
            last_px.setText(client.getLast_price(), L.format100());

            // Races
            index_race_field.colorForge(index_race);
            roll_race_field.colorForge(roll_race);
        }
    }
}


