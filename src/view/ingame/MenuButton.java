package view.ingame;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MenuButton extends JButton {
    private ImageIcon icon = new ImageIcon("images/Buttons/MenuButton.png");
    private ImageIcon pressedIcon = new ImageIcon("images/Buttons/MenuButton_down.png");

    public MenuButton(ActionListener l) {
        this.setBounds(150, 270, 209, 46);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setPressedIcon(pressedIcon);
        this.addActionListener(l);
    }
}
