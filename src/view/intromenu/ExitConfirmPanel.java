package view.intromenu;

import view.ingame.DialogPanel;

import java.awt.*;
import java.awt.event.ActionListener;

public class ExitConfirmPanel extends DialogPanel {
    public ExitConfirmPanel(ActionListener l) {
        super(l);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.ORANGE);
        g.setFont(STANDARD);
        g.drawString("您确定要退出吗？", 170, 180);
    }
}
