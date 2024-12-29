package view;

import model.Level;
import view.ingame.GamePanel;
import view.intromenu.ExitGameButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayFrame extends JFrame implements ActionListener {
//    private MenuPanel menuPanel = new MenuPanel(this);
    private GamePanel gamePanel = new GamePanel(new Level(5000, 20));

    public PlayFrame() {
        this.setVisible(true);
        this.setTitle("Plants vs. Zombies");
        this.setSize(835, 635);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("images/Icon.png").getImage());

        this.setLayout(null);
        this.setResizable(false);

//        this.add(menuPanel);
        this.add(gamePanel);
//        mainPanel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        if(source instanceof ExitGameButton)
            System.exit(0);
    }
}