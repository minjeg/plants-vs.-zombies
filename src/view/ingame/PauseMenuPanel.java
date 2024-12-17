package view.ingame;

import javax.swing.*;
import java.awt.*;

public class PauseMenuPanel extends JPanel {
    public PauseMenuPanel(MainPanel mainPanel) {
        this.setOpaque(false);
        this.setBounds(120, -10, 490, 580);
        this.setLayout(null);
        this.add(new RestartButton(mainPanel));
        this.add(new ReturnToMenuButton(mainPanel));
        this.add(new BackToGameButton(mainPanel));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("images/Panels/PausePanel.png").getImage(),
                80, 80, null);
    }
}
