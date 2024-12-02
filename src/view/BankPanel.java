package view;

import javax.swing.*;
import java.awt.*;

public class BankPanel extends JPanel {
    public BankPanel() {
        super();
        this.setLayout(new FlowLayout());
        this.setBounds(10, 10, 446, 87);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("images/SeedBank.png").getImage(),
                10, 10, null);
    }
}
