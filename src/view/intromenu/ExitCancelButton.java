package view.intromenu;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ExitCancelButton extends JButton {
    private ImageIcon icon = new ImageIcon("images/Buttons/CancelButton.png");
    private ImageIcon pressedIcon = new ImageIcon("images/Buttons/CancelButton_down.png");

    public ExitCancelButton(ActionListener l) {
        this.setBounds(50, 260, 196, 78);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(icon);
        this.setPressedIcon(pressedIcon);
        this.addActionListener(l);
    }
}
