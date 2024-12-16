package view;

import model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PauseMenuPanel extends JPanel {
    private MainPanel mainPanel;

    public PauseMenuPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
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
