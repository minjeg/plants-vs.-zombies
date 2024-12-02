package view;

import javax.swing.*;

public class PlayFrame extends JFrame {


    public PlayFrame() {
        this.setVisible(true);
        this.setTitle("Plants vs. Zombies");
        this.setSize(835, 635);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("images/Icon.png").getImage());

        this.setLayout(null);
        this.setResizable(true);

        this.add(new BankPanel());
        this.add(new LawnPanel());
        this.add(new MainPanel());

    }
}
