package view.ingame;

import javax.swing.*;
import java.awt.*;

public class PauseMenuPanel extends JPanel {
    private RestartButton restartButton;
    private ReturnToMenuButton returnToMenuButton;
    private BackToGameButton backToGameButton;

    public PauseMenuPanel(GamePanel gamePanel) {
        this.setOpaque(false);
        this.setBounds(120, -10, 490, 580);
        this.setLayout(null);
        restartButton = new RestartButton(gamePanel);
        returnToMenuButton = new ReturnToMenuButton(gamePanel);
        backToGameButton = new BackToGameButton(gamePanel);
        this.add(restartButton);
        this.add(returnToMenuButton);
        this.add(backToGameButton);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("images/Panels/PausePanel.png").getImage(),
                80, 80, null);
    }

    public void disableAll() {
        restartButton.setEnabled(false);
        returnToMenuButton.setEnabled(false);
        backToGameButton.setEnabled(false);
    }

    public void enableAll() {
        restartButton.setEnabled(true);
        returnToMenuButton.setEnabled(true);
        backToGameButton.setEnabled(true);
    }
}
