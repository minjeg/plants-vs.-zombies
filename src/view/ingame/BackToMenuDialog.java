package view.ingame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BackToMenuDialog extends DialogPanel {

    public BackToMenuDialog(GamePanel gamePanel) {
        super(gamePanel);
        this.setVisible(false);
        this.add(new BackToMenuCancelButton(gamePanel));
        this.add(new BackToMenuConfirmButton(gamePanel));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.ORANGE);
        g.setFont(STANDARD);
        g.drawString("我们会为您保存未完成的游戏内容", 100, 180);
        g.drawString("确定返回主菜单？", 170, 210);
    }
}
