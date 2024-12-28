package view.intromenu;

import javax.swing.*;
import java.awt.event.ActionListener;

public class IntroButton extends JButton {
    public IntroButton(ActionListener l) {
        this.setBounds(255, 500, 319, 69);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(new ImageIcon("images/Buttons/IntroButton.png"));
        this.setRolloverIcon(new ImageIcon("images/Buttons/IntroButton_selected.png"));
        this.setPressedIcon(new ImageIcon("images/Buttons/IntroButton_selected.png"));
        this.addActionListener(l);
    }
}
