package gui.listeners;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import gui.MyGuiComps;

public class MyListeners {

    public static MouseListener onOverMyButton(MyGuiComps.MyButton myButton) {
        return new MouseAdapter() {
            Color color;
            @Override
            public void mouseEntered(MouseEvent e) {
                color = myButton.getBackground();
                myButton.setBackground(color.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                myButton.setBackground(color);
            }
        };

    }
}
