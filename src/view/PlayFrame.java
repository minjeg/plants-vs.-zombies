package view;

import view.award.AwardPanel;
import view.ingame.GamePanel;
import view.intromenu.MenuPanel;

import javax.swing.*;

public class PlayFrame extends JFrame {
    private final MenuPanel menuPanel = new MenuPanel(this);
    private final GamePanel gamePanel = new GamePanel(this);
    private final AwardPanel awardPanel = new AwardPanel(this);

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
        menuPanel.setState(MenuPanel.NORMAL);
        this.add(gamePanel);
        gamePanel.setState(GamePanel.WAIT);
        this.add(awardPanel);
        awardPanel.setState(AwardPanel.OFF);
    }

    public void startPlay() {
        gamePanel.setState(GamePanel.LOADING);
    }

    public void returnToMenu() {
        menuPanel.setState(MenuPanel.NORMAL);
    }

    public void getAward() {
        awardPanel.setState(AwardPanel.ON);
    }
}