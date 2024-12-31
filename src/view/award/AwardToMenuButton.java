package view.award;

import javax.swing.*;
import java.awt.event.ActionListener;

public class AwardToMenuButton extends JButton {
    private ImageIcon icon = new ImageIcon("images/Buttons/AwardToMenuButton.png");
    private ImageIcon rolloverIcon = new ImageIcon("images/Buttons/AwardToMenuButton_rollover.png");

    public AwardToMenuButton(ActionListener l) {
        this.setBounds(330, 525, 154, 42);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setRolloverIcon(rolloverIcon);
        this.setPressedIcon(rolloverIcon);
        this.addActionListener(l);
    }
}
