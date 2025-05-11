package stocks;

import api.BASE_CLIENT_OBJECT;
import api.Manifest;
import api.Poli;
import gui.MyGuiComps;
import locals.L;
import locals.Themes;
import java.awt.*;
import java.util.ArrayList;

public class SummeryWindow extends MyGuiComps.MyFrame {

    ArrayList<SingleStockSummeryPanel> panels;

    // Constructor
    public SummeryWindow(String title) throws HeadlessException {
        super(null, title);
    }

    // Main
    public static void main(String[] args) {
        new SummeryWindow(" My main window ");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void appendClients() {
        L.stocks.add(Poli.getInstance());
    }

    @Override
    public void onClose() {

        // close singles
        for (SingleStockSummeryPanel panel: panels) {
            panel.close();
        }

        // Super close
        super.onClose();
    }

    @Override
    public void initListeners() {
    }

    @Override
    public void initialize() {

        panels = new ArrayList<>();


        // Append clients
        appendClients();

        // Load data from DB
        if (Manifest.DB_UPLOAD) {
            loadOnStartUp();
        }

        // This
        setXY(100, 100);
        setSize(500, 500);
        setLayout(null);

        // Header panel
        MyGuiComps.MyPanel header = header_panel();
        header.setBounds(0,0,500,35);
        header.setBackground(Themes.GREY_2);
        add(header);

        // Append single stock panels
        append_single_stock_panels(header.getX(), header.getY() + header.getHeight() + 1);
    }

    private MyGuiComps.MyPanel header_panel() {
        MyGuiComps.MyPanel panel = new MyGuiComps.MyPanel();

        MyGuiComps.MyLabel
                name_lbl,
                open_lbl,
                last_lbl,
                last_px_lbl,
                index_race_lbl,
                roll_race_lbl;

        // ------------------------------ LBL ------------------------------ //
        // Name
        name_lbl = new MyGuiComps.MyLabel(L.capitalizeFirstLetter("Name"));
        name_lbl.setXY(23, 3);
        name_lbl.setFont(name_lbl.getFont().deriveFont(Font.BOLD));
        name_lbl.setForeground(Themes.BLUE_DARK);
        panel.add(name_lbl);

        // Last px
        last_px_lbl = new MyGuiComps.MyLabel("Last px");
        last_px_lbl.setFont(last_px_lbl.getFont().deriveFont(Font.BOLD));;
        last_px_lbl.setXY(name_lbl.getX() + name_lbl.getWidth() + 3, name_lbl.getY());
        panel.add(last_px_lbl);

        // Open
        open_lbl = new MyGuiComps.MyLabel("Open");
        open_lbl.setFont(open_lbl.getFont().deriveFont(Font.BOLD));
        open_lbl.setXY(last_px_lbl.getX() + last_px_lbl.getWidth() + 3, last_px_lbl.getY());
        panel.add(open_lbl);

        // Last
        last_lbl = new MyGuiComps.MyLabel("Last");
        last_lbl.setFont(last_lbl.getFont().deriveFont(Font.BOLD));
        last_lbl.setXY(open_lbl.getX() + open_lbl.getWidth() + 3, open_lbl.getY());
        panel.add(last_lbl);

        // Index race
        index_race_lbl = new MyGuiComps.MyLabel("Ind race");
        index_race_lbl.setFont(index_race_lbl.getFont().deriveFont(Font.BOLD));
        index_race_lbl.setXY(last_lbl.getX() + last_lbl.getWidth() + 3, last_lbl.getY());
        panel.add(index_race_lbl);

        // Roll race
        roll_race_lbl = new MyGuiComps.MyLabel("Roll race");
        roll_race_lbl.setFont(roll_race_lbl.getFont().deriveFont(Font.BOLD));
        roll_race_lbl.setXY(index_race_lbl.getX() + index_race_lbl.getWidth() + 3, index_race_lbl.getY());
        panel.add(roll_race_lbl);

        return panel;
    }

    private void append_single_stock_panels(int _x, int _y) {

        int y = _y;
        int x = _x;

        for (BASE_CLIENT_OBJECT client : L.stocks) {
            // Create panel
            SingleStockSummeryPanel singleStockSummeryPanel = new SingleStockSummeryPanel(client);
            singleStockSummeryPanel.setXY(x, y);

            // Add the panel to the container
            add(singleStockSummeryPanel);
            panels.add(singleStockSummeryPanel);

            // Update currentY: move it down by the panel's height plus a margin (e.g., 1 pixel)
            y = singleStockSummeryPanel.getY() + singleStockSummeryPanel.getHeight() + 1;
        }

    }

    private void loadOnStartUp() {
        // TODO: 23/02/2025  Load on startup
    }
}