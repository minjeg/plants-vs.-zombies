package view.ingame;

import javax.swing.*;
import java.awt.event.ActionListener;

public class LoadGameConfirmButton extends JButton {
    private ImageIcon icon = new ImageIcon("images/Buttons/ContinueGameButton.png");
    private ImageIcon pressedIcon = new ImageIcon("images/Buttons/ContinueGameButton_down.png");

    public LoadGameConfirmButton(ActionListener l) {
        this.setBounds(50, 220, 209, 46);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setPressedIcon(pressedIcon);
        this.addActionListener(l);
    }
}
