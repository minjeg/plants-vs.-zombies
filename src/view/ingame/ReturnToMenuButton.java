package view.ingame;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ReturnToMenuButton extends JButton {
    public ReturnToMenuButton(ActionListener l) {
        this.setBounds(190, 400, 205, 46);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setIcon(new ImageIcon("images/Buttons/ReturnToMenuButton.png"));
        this.setPressedIcon(new ImageIcon("images/Buttons/ReturnToMenuButton_down.png"));
        this.setDisabledIcon(new ImageIcon("images/Buttons/ReturnToMenuButton.png"));
        this.addActionListener(l);
    }
}
