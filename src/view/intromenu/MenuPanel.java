package view.intromenu;

import view.ingame.AudioPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MenuPanel extends JPanel implements ActionListener {
    private TitleScreenPanel titleScreen = new TitleScreenPanel(this);

    private static final AudioPlayer BGM_PLAYER = AudioPlayer.getAudioPlayer(
            new File("sounds/bgm/CrazyDave (Intro Theme).wav"), AudioPlayer.LOOP);
    private static final AudioPlayer EVIL_LAUGH_PLAYER = AudioPlayer.getAudioPlayer(
            new File("sounds/audio/evillaugh.wav"), AudioPlayer.LOOP);

    public MenuPanel() {
        super();
        this.setBounds(0, 0, 835, 635);
        this.setLayout(null);
        this.add(titleScreen);
        BGM_PLAYER.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(new ImageIcon("images/Panels/Menu_TombStone.png").getImage(),
                0, 0, 835, 635, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        if(source instanceof IntroButton) {
            titleScreen.setVisible(false);
            source.setVisible(false);
        }
    }
}
