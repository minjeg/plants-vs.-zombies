package view;

import javax.swing.*;
import java.awt.event.ActionListener;

public class PauseButton extends JButton {
    public PauseButton(ActionListener l) {
        this.setBounds(700, 0, 113, 47);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(new ImageIcon("images/Buttons/PauseButton.png"));
        this.setPressedIcon(new ImageIcon("images/Buttons/PauseButton_down.png"));
        this.addActionListener(l);
    }
}
