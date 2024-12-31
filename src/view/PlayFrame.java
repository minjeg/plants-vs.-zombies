package view;

import model.Level;
import view.ingame.GamePanel;
import view.intromenu.ExitGameButton;
import view.intromenu.MenuPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayFrame extends JFrame {
    private MenuPanel menuPanel = new MenuPanel(this);
    private GamePanel gamePanel = new GamePanel(this, new Level(50, 20));

    public PlayFrame() {
        this.setVisible(true);
        this.setTitle("Plants vs. Zombies");
        this.setSize(835, 635);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("images/Icon.png").getImage());

        this.setLayout(null);
        this.setResizable(false);

        this.add(menuPanel);
        this.add(gamePanel);
        gamePanel.setVisible(false);
    }

    public void startPlay() {
        gamePanel.setState(GamePanel.READY);
    }

    public void returnToMenu() {
        menuPanel.setState(MenuPanel.NORMAL);
    }
}