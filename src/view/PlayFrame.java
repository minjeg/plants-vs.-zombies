package view;

import model.Level;

import javax.swing.*;
import java.io.IOException;

public class PlayFrame extends JFrame {

    public PlayFrame() {
        this.setVisible(true);
        this.setTitle("Plants vs. Zombies");
        this.setSize(835, 635);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("images/Icon.png").getImage());

        this.setLayout(null);
        this.setResizable(false);

        Level level;
        try {
            level = Level.load("level.lv");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.add(new MainPanel(level));
    }
}
