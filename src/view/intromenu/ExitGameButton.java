package view.intromenu;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ExitGameButton extends JButton {
    public ExitGameButton(ActionListener l) {
        this.setBounds(755, 545, 46, 27);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(new ImageIcon("images/Buttons/ExitGameButton.png"));
        this.setRolloverIcon(new ImageIcon("images/Buttons/ExitGameButton_rollover.png"));
        this.setPressedIcon(new ImageIcon("images/Buttons/ExitGameButton_rollover.png"));
        this.addActionListener(l);
    }
}
