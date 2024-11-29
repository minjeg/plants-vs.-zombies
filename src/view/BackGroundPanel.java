package view;

import javax.swing.*;
import java.awt.*;

public class BackGroundPanel extends JPanel {
    private final Image background =
            new ImageIcon("images/Background.jpg").getImage();

    public BackGroundPanel() {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, -200, 0, null);
    }
}
