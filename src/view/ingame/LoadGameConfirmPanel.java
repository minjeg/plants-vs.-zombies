package view.ingame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoadGameConfirmPanel extends JPanel {

    public LoadGameConfirmPanel(ActionListener l) {
        super();
        this.setOpaque(false);
        this.setBounds(150, 120, 500, 341);
        this.setLayout(null);
        this.add(new LoadGameConfirmButton(l));
        this.add(new NewGameButton(l));
        this.add(new MenuButton(l));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("images/Panels/BigBottomDialog.png").getImage(),
                0, 0, null);
        g.setFont(DialogPanel.STANDARD);
        g.setColor(Color.ORANGE);
        g.drawString("检测到有未完成的游戏进度", 100, 135);
        g.drawString("您想要继续当前进度还是重新开始？", 80, 160);
    }
}
