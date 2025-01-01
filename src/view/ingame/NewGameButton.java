package view.ingame;

import javax.swing.*;
import java.awt.event.ActionListener;

public class NewGameButton extends JButton {
    private ImageIcon icon = new ImageIcon("images/Buttons/NewGameButton.png");
    private ImageIcon pressedIcon = new ImageIcon("images/Buttons/NewGameButton_down.png");

    public NewGameButton(ActionListener l) {
        this.setBounds(260, 220, 209, 46);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setPressedIcon(pressedIcon);
        this.addActionListener(l);
    }
}
