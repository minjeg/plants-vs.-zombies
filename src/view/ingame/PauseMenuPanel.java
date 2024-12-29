package view.ingame;

import javax.swing.*;
import java.awt.*;

public class PauseMenuPanel extends JPanel {
    public PauseMenuPanel(GamePanel gamePanel) {
        this.setOpaque(false);
        this.setBounds(120, -10, 490, 580);
        this.setLayout(null);
        this.add(new RestartButton(gamePanel));
        this.add(new ReturnToMenuButton(gamePanel));
        this.add(new BackToGameButton(gamePanel));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("images/Panels/PausePanel.png").getImage(),
                80, 80, null);
    }
}
