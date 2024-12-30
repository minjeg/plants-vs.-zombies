package view.intromenu;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ExitGameButton extends JButton {
    private ImageIcon icon = new ImageIcon("images/Buttons/ExitGameButton.png");
    private ImageIcon rolloverIcon = new ImageIcon("images/Buttons/ExitGameButton_rollover.png");

    public ExitGameButton(ActionListener l) {
        this.setBounds(755, 545, 46, 27);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setRolloverIcon(rolloverIcon);
        this.setPressedIcon(rolloverIcon);
        this.setDisabledIcon(icon);
        this.addActionListener(l);
    }
}
