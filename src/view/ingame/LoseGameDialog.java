package view.ingame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoseGameDialog extends JPanel {

    public LoseGameDialog(ActionListener l) {
        super();
        this.setBounds(200, 100, 389, 372);
        this.setOpaque(false);
        this.setLayout(null);
        this.add(new LoseConfirmButton(l));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("images/Panels/SmallDialog.png").getImage(),
                0, 0, null);
        g.setFont(DialogPanel.STANDARD);
        g.setColor(Color.ORANGE);
        g.drawString("游戏结束", 150, 200);
    }
}
