package view.ingame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DialogPanel extends JPanel {
    public static final Font STANDARD = new Font("Standard", Font.PLAIN, 20);

    public DialogPanel(ActionListener l) {
        super();
        this.setBounds(150, 100, 600, 446);
        this.setOpaque(false);
        this.setLayout(null);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("images/Panels/DialogPanel.png").getImage(),
                0, 0, null);
    }
}
