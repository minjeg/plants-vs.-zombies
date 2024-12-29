package view.intromenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestFrame extends JFrame implements ActionListener {
    public TestFrame() {
        this.setVisible(true);
        this.setTitle("Plants vs. Zombies");
        this.setSize(835, 635);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("images/Icon.png").getImage());

        this.setLayout(null);
        this.setResizable(false);

        this.add(new MenuPanel(this));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.dispose();
    }
}
