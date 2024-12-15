package view;

import javax.swing.*;

public class PauseButton extends JButton {
    public PauseButton(MainPanel mainPanel) {
        this.setBounds(700, 0, 113, 47);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setIcon(new ImageIcon("images/Buttons/PauseButton.png"));
        this.setPressedIcon(new ImageIcon("images/Buttons/PauseButton_down.png"));
        this.addActionListener(mainPanel);
    }
}
