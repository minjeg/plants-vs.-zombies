package view;

import model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PauseMenuPanel extends JPanel implements ActionListener {
    private GameModel model;

    public PauseMenuPanel(GameModel model) {
        this.model = model;
        this.setOpaque(false);
        this.setBounds(120, -10, 490, 580);
        this.setLayout(null);
        this.add(new RestartButton(this));
        this.add(new ReturnToMenuButton(this));
        this.add(new BackToGameButton(this));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("images/Panels/PausePanel.png").getImage(),
                80, 80, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton)e.getSource();
        if(source instanceof BackToGameButton) {
            model.continueGame();
            this.setVisible(false);
        }
    }
}
