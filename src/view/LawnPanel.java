package view;

import javax.swing.*;
import java.awt.*;

public class LawnPanel extends JPanel {
    public LawnPanel() {
        super();
        this.setBounds(60, 80, 720, 500);
        this.setOpaque(false);
        this.setLayout(new GridLayout(5, 9));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

    }
}
