package gui;

import api.BASE_CLIENT_OBJECT;
import dataBase.IDataBaseHandler;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import gui.listeners.MyListeners;
import locals.L;
import locals.Themes;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.HashMap;

public class MyGuiComps {

    // ---------- JFrame ---------- //
    public static abstract class MyFrame extends JFrame {

        public BASE_CLIENT_OBJECT client;

        public MyFrame(BASE_CLIENT_OBJECT client, String title) throws HeadlessException {
            super(title);
            this.client = client;
            init();
            initialize();
            initListeners();
            packAndFinish();
            initOnClose();
        }

        private void packAndFinish() {
            pack();
            loadBounds(Queries.get_bounds(client, getTitle(), MySql.JIBE_PROD_CONNECTION));
            setVisible(true);
        }

        private void loadBounds(HashMap<String, Integer> map) {
            new Thread(() -> {
                int x = 100;
                int y = 100;
                int width = 300;
                int height = 300;
                try {
                    setPreferredSize(new Dimension(map.get(IDataBaseHandler.x), map.get(IDataBaseHandler.y)));
                    x = map.get(IDataBaseHandler.x);
                    y = map.get(IDataBaseHandler.y);
                    width = map.get(IDataBaseHandler.width);
                    height = map.get(IDataBaseHandler.height);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setBounds(x, y, width, height);
            }).start();
        }

        private void init() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setBackground(Themes.LIGHT_BLUE);
            getContentPane().setLayout(null);
            setLayout(null);
            
            // Add right-click context menu for dark mode
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                        showDarkModeMenu(e);
                    }
                }
            });
        }
        
        private void showDarkModeMenu(MouseEvent e) {
            JPopupMenu popup = new JPopupMenu();
            String menuText = Themes.isDarkMode() ? "מצב בהיר" : "מצב כהה";
            JMenuItem menuItem = new JMenuItem(menuText);
            menuItem.addActionListener(ev -> {
                Themes.toggleDarkMode();
                applyDarkMode();
            });
            popup.add(menuItem);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
        
        public void applyDarkMode() {
            // This will be overridden in subclasses to apply dark mode
            updateDarkMode();
        }
        
        protected void updateDarkMode() {
            // Update frame background using Themes helper methods
            setBackground(Themes.getBackgroundColor());
            getContentPane().setBackground(Themes.getBackgroundColor());
            
            // Update all components recursively
            updateComponentDarkMode(getContentPane());
        }
        
        private void updateComponentDarkMode(Container container) {
            for (Component comp : container.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    panel.setBackground(Themes.getPanelBackgroundColor());
                    // Recursively update components inside the panel
                    updateComponentDarkMode(panel);
                } else if (comp instanceof MyTextField) {
                    MyTextField field = (MyTextField) comp;
                    field.setBackground(Themes.getTextFieldBackgroundColor());
                    field.setForeground(Themes.getTextFieldForegroundColor());
                } else if (comp instanceof JTextField && !(comp instanceof MyTextField)) {
                    // Handle regular JTextFields too
                    JTextField field = (JTextField) comp;
                    field.setBackground(Themes.getTextFieldBackgroundColor());
                    field.setForeground(Themes.getTextFieldForegroundColor());
                } else if (comp instanceof MyLabel) {
                    MyLabel label = (MyLabel) comp;
                    if (Themes.isDarkMode()) {
                        label.setForeground(Themes.getTextColor());
                    } else {
                        label.setForeground(Themes.BLUE);
                    }
                } else if (comp instanceof JLabel && !(comp instanceof MyLabel)) {
                    // Handle regular JLabels too (but not MyLabel which is handled above)
                    JLabel label = (JLabel) comp;
                    if (Themes.isDarkMode()) {
                        // Only change to white if it's a default/dark color
                        Color currentFg = label.getForeground();
                        if (currentFg.equals(Themes.STANDARD_BLACK) || 
                            currentFg.equals(Themes.BLUE) ||
                            currentFg.equals(Themes.LIGHT_TEXT_HEADER)) {
                            label.setForeground(Themes.getTextColor());
                        }
                        // Keep green/red colors as they are (for indicators)
                    } else {
                        // Restore default colors when exiting dark mode
                        Color currentFg = label.getForeground();
                        if (currentFg.equals(Themes.BRIGHT_WHITE_TEXT) || 
                            currentFg.equals(Themes.WHITE_TEXT)) {
                            label.setForeground(Themes.STANDARD_BLACK);
                        }
                    }
                } else if (comp instanceof MyButton) {
                    MyButton button = (MyButton) comp;
                    button.setBackground(Themes.getButtonBackgroundColor());
                    button.setForeground(Themes.getButtonForegroundColor());
                } else if (comp instanceof JTextArea) {
                    JTextArea textArea = (JTextArea) comp;
                    textArea.setBackground(Themes.getLogBackgroundColor());
                    textArea.setForeground(Themes.getTextFieldForegroundColor());
                } else if (comp instanceof Container) {
                    updateComponentDarkMode((Container) comp);
                }
            }
        }

        public void setXY(int x, int y) {
            setBounds(x, y, getWidth(), getHeight());
        }

        public void setWidth(int width) {
            setBounds(getX(), getY(), width, getHeight());
        }

        public void setHeight(int height) {
            setBounds(getX(), getY(), getWidth(), height);
        }

        public void setSize(int width, int height) {
            setPreferredSize(new Dimension(width, height));
        }

        public abstract void initListeners();

        public abstract void initialize();

        protected void insetOrUpdateBounds() {
            try {
                String query = String.format("SELECT sagiv.update_bounds('%s', '%s', %s, %s, %s, %s);", client.getName(), getTitle(), getX(), getY(), getWidth(), getHeight());
                MySql.select(query, MySql.JIBE_PROD_CONNECTION);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void initOnClose() {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    onClose();
                }
            });
        }

        public void onClose() {
            insetOrUpdateBounds();
            dispose();
        }
    }

    // ---------- JPanel ---------- //
    public static class MyPanel extends JPanel {

        public MyPanel() {
            init();
        }

        protected void init() {
            setFont(Themes.ARIEL_17);
            setBackground(Themes.getPanelBackgroundColor());
            setBorder(null);
            setLayout(null);
        }

        public void setXY(int x, int y) {
            setBounds(x, y, getWidth(), getHeight());
        }

        public void setWidth(int width) {
            setBounds(getX(), getY(), width, getHeight());
        }

        public void setHeight(int height) {
            setBounds(getX(), getY(), getWidth(), height);
        }

    }

    // ---------- JTextField ---------- //
    public static class MyTextField extends JTextField {

        public MyTextField() {
            super();
            init();
        }

        public MyTextField(int columns) {
            super(columns);
            init();
        }

        public void setWidth(int width) {
            setBounds(getX(), getY(), width, getHeight());
        }

        public void setHeight(int height) {
            setBounds(getX(), getY(), getWidth(), height);
        }

        private void init() {
            setBounds(new Rectangle(65, 25));
            setFont(Themes.VERDANA_PLAIN_12);
            setHorizontalAlignment(JTextField.CENTER);
            setBorder(null);
//            setEnabled(false);
            
            // Apply dark mode colors if dark mode is active
            setBackground(Themes.getTextFieldBackgroundColor());
            setForeground(Themes.getTextFieldForegroundColor());
        }

        public void setFontSize(int size) {
            setFont(getFont().deriveFont((float) size));
        }

        public void setXY(int x, int y) {
            setBounds(x, y, getWidth(), getHeight());
        }

        public void setText(double val, DecimalFormat format) {
            if (format != null) {
                setText(format.format(val));
            } else {
                setText(L.str(val));
            }
        }

        public void colorForge(double val, DecimalFormat format) {
            if (val >= 0) {
                setForeground(Themes.GREEN);
            } else {
                setForeground(Themes.RED);
            }

            setText(format.format(val));
        }

        public void colorForgeRound(double val) {
            val /= 1000;

            if (val >= 0) {
                setForeground(Themes.GREEN);
            } else {
                setForeground(Themes.RED);
            }

            setText(L.str((int) val));
        }

        public void colorForge(double val, DecimalFormat format, String sign) {
            if (val >= 0) {
                setForeground(Themes.GREEN);
            } else {
                setForeground(Themes.RED);
            }

            setText(format.format(val) + sign);
        }

        public void colorForge(int val) {
            if (val >= 0) {
                setForeground(Themes.GREEN);
            } else {
                setForeground(Themes.RED);
            }

            setText(L.str(val));
        }

        public void colorBack(double val, DecimalFormat format) {

            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.BOLD));

            if (val >= 0) {
                setBackground(Themes.GREEN);
            } else {
                setBackground(Themes.RED);
            }

            setText(format.format(val));
        }

        public void colorBack(double val, DecimalFormat format, String sign) {

            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.BOLD));

            if (val >= 0) {
                setBackground(Themes.GREEN);
            } else {
                setBackground(Themes.RED);
            }

            setText(format.format(val) + sign);
        }

    }

    // ---------- JLabel ---------- //
    public static class MyLabel extends JLabel {

        public MyLabel(String text) {
            super(text);
            init();
        }

        private void init() {

            setBounds(new Rectangle(60, 25));
            setFont(Themes.ARIEL_BOLD_12);
            setForeground(Themes.BLUE);
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);

        }

        public void setWidth(int width) {
            setBounds(getX(), getY(), width, getHeight());
        }

        public void setHeight(int height) {
            setBounds(getX(), getY(), getWidth(), height);
        }

        public void setXY(int x, int y) {
            setBounds(x, y, getWidth(), getHeight());
        }

        public void setText(double val, DecimalFormat format) {
            if (format != null) {
                setText(format.format(val));
            } else {
                setText(L.str(val));
            }
        }

        public void colorForge(double val, DecimalFormat format) {
            if (val >= 0) {
                setForeground(Themes.GREEN);
            } else {
                setForeground(Themes.RED);
            }

            setText(format.format(val));
        }

        public void colorForge(int val) {
            if (val >= 0) {
                setForeground(Themes.GREEN);
            } else {
                setForeground(Themes.RED);
            }

            setText(L.coma(val));
        }

        public void colorForge(int val, Color green) {
            if (val >= 0) {
                setForeground(green);
            } else {
                setForeground(Themes.RED);
            }

            setText(L.coma(val));
        }

        public void colorBack(double val, DecimalFormat format) {
            if (val >= 0) {
                setBackground(Themes.GREEN);
            } else {
                setBackground(Themes.RED);
            }

            setText(format.format(val));
        }
    }

    // ---------- JButton ---------- //
    public static class MyButton extends JButton {

        public MyButton(String text) {
            super(text);

            init();
        }

        private void init() {
            setWidth(80);
            setHeight(25);
            setFont(Themes.VEDANA_12);
            setForeground(Themes.BLUE);
            setHorizontalAlignment(JLabel.CENTER);
            setBackground(Themes.GREY_LIGHT);
            setOpaque(true);
            addMouseListener(MyListeners.onOverMyButton(this));
        }

        public void complete() {
            Color backGroundColor = getBackground();
            setBackground(Themes.GREEN);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setBackground(backGroundColor);

        }

        public void setWidth(int width) {
            setBounds(getX(), getY(), width, getHeight());
        }

        public void setHeight(int height) {
            setBounds(getX(), getY(), getWidth(), height);
        }

        public void setSize(int width, int height) {
            setPreferredSize(new Dimension(width, height));
        }

        public void setXY(int x, int y) {
            setBounds(x, y, getWidth(), getHeight());
        }
    }


    // ---------- GridPanel ---------- //
    public static class MyBoardPanel extends JPanel {

        public Field[][] fields;

        int rows, cols;
        Dimension panelMinDimension, fieldsMinDimension;

        public MyBoardPanel(int rows, int cols, Dimension panelMinDimension, Dimension fieldsMinDimension) {
            this.rows = rows;
            this.cols = cols;
            fields = new Field[rows][cols];
            this.panelMinDimension = panelMinDimension;
            this.fieldsMinDimension = fieldsMinDimension;

            setLayout(new GridLayout(rows, cols));
            setMinimumSize(new Dimension(panelMinDimension));
            setPreferredSize(new Dimension(panelMinDimension));
            setBackground(Themes.GREY_LIGHT);
            fillBoard();
        }

        private void fillBoard() {
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < cols; ++j) {
                    fields[i][j] = new Field(fieldsMinDimension);
                    add(fields[i][j]);
                }
            }
        }

        public void setLabel(JLabel label, int row, int col) {
            fields[row][col].add(label);
        }

        class Field extends JPanel {

            public Field(Dimension minDimension) {
                setMinimumSize(minDimension);
                setPreferredSize(minDimension);
            }

        }
    }

    public static abstract class MyTable extends JTable {

        public MyTable(Object[][] data, Object[] headers) {
            super(data, headers);
            init();
        }

        private void init() {
            setFont(Themes.VEDANA_12);

            // Cell renderer
            DefaultTableCellRenderer cell_renderer = get_cell_renderer();
            cell_renderer.setHorizontalAlignment( JLabel.CENTER );
            setDefaultRenderer(MyTable.class, cell_renderer);
            for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
                getColumnModel().getColumn(i).setCellRenderer(cell_renderer);
            }

        }

        public DefaultTableCellRenderer get_cell_renderer() {
            return new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                    //Cells are by default rendered as a JLabel.
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    cell_change_listener(c, table, value, isSelected, hasFocus, row, col);
                    return c;
                }

            };
        }

        protected abstract void cell_change_listener(Component c, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col);
    }

}

abstract class AFrame extends JFrame {

    public AFrame(String title) throws HeadlessException {
        super(title);
    }

    public abstract void onClose();
}