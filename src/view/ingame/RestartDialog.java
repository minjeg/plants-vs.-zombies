package view.ingame;

import javax.swing.*;
import java.awt.*;

public class RestartDialog extends DialogPanel {

    public RestartDialog(GamePanel gamePanel) {
        super(gamePanel);
        this.setVisible(false);
        this.add(new RestartCancelButton(gamePanel));
        this.add(new RestartConfirmButton(gamePanel));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.ORANGE);
        g.setFont(STANDARD);
        g.drawString("确定要重新开始吗？", 170, 180);
        g.drawString("这会导致您丢失当前的游戏进度！", 100, 210);
    }
}
