package view.intromenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TitleScreenPanel extends JPanel {
    private Image titleScreen = new ImageIcon("images/Panels/titlescreen.png").getImage();

    public TitleScreenPanel(ActionListener l) {
        super();
        this.setBounds(0, 0, 835, 635);
        this.setLayout(null);
        this.add(new IntroButton(l));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(titleScreen, 0, 0, 835, 635,  null);
    }
}
