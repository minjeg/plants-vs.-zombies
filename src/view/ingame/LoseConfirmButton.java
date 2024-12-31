package view.ingame;

import javax.swing.*;
import java.awt.event.ActionListener;

public class LoseConfirmButton extends JButton {

    private ImageIcon icon = new ImageIcon("images/Buttons/RestartConfirmButton.png");
    private ImageIcon pressedIcon = new ImageIcon("images/Buttons/RestartConfirmButton_down.png");

    public LoseConfirmButton(ActionListener l) {
        this.setBounds(100, 270, 196, 78);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setPressedIcon(pressedIcon);
        this.addActionListener(l);
    }
}
