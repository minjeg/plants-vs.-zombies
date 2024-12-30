package view.intromenu;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ExitConfirmButton extends JButton {
    private ImageIcon icon = new ImageIcon("images/Buttons/ConfirmButton.png");
    private ImageIcon pressedIcon = new ImageIcon("images/Buttons/ConfirmButton_down.png");

    public ExitConfirmButton(ActionListener l) {
        this.setBounds(250, 260, 196, 78);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setPressedIcon(pressedIcon);
        this.addActionListener(l);
    }
}
