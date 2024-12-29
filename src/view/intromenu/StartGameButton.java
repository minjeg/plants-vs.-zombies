package view.intromenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class StartGameButton extends JButton {
    private boolean disabledIconState = true;
    private ImageIcon icon = new ImageIcon("images/Buttons/StartGameButton.png");
    private ImageIcon rolloverIcon = new ImageIcon("images/Buttons/StartGameButton_rollover.png");

    public StartGameButton(ActionListener l) {
        this.setBounds(445, 100, 330, 146);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setRolloverIcon(rolloverIcon);
        this.setPressedIcon(rolloverIcon);
        this.addActionListener(l);
    }

    public void switchIcon() {
        this.setDisabledIcon(disabledIconState ? icon : rolloverIcon);
        disabledIconState ^= true;
    }
}
