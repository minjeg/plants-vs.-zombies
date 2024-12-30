package view.ingame;

import javax.swing.*;
import java.awt.event.ActionListener;

public class BackToMenuCancelButton extends JButton {
    private ImageIcon icon = new ImageIcon("images/Buttons/CancelButton.png");
    private ImageIcon pressedIcon = new ImageIcon("images/Buttons/CancelButton_down.png");

    public BackToMenuCancelButton(ActionListener l) {
        this.setBounds(50, 260, 196, 78);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setPressedIcon(pressedIcon);
        this.addActionListener(l);
    }
}
