package view.ingame;

import javax.swing.*;
import java.awt.event.ActionListener;

public class BackToGameButton extends JButton {
    public BackToGameButton(ActionListener l) {
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setOpaque(false);
        this.setBounds(110, 460, 355, 100);
        this.setIcon(new ImageIcon("images/Buttons/BackToGameButton.png"));
        this.setPressedIcon(new ImageIcon("images/Buttons/BackToGameButton_down.png"));
        this.addActionListener(l);
    }
}
