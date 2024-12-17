package view.ingame;

import javax.swing.*;
import java.awt.event.ActionListener;

public class RestartButton extends JButton {
    public RestartButton(ActionListener l) {
        this.setBounds(190, 350, 205, 46);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setIcon(new ImageIcon("images/Buttons/RestartButton.png"));
        this.setPressedIcon(new ImageIcon("images/Buttons/RestartButton_down.png"));
        this.addActionListener(l);
    }
}
