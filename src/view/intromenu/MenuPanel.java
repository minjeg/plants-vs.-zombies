package view.intromenu;

import view.ingame.AudioPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MenuPanel extends JPanel implements ActionListener {
    private TitleScreenPanel titleScreen = new TitleScreenPanel(this);

    private static final AudioPlayer BGM_PLAYER = AudioPlayer.getAudioPlayer(
            new File("sounds/bgm/CrazyDave (Intro Theme).wav"), AudioPlayer.LOOP);
    private static final AudioPlayer EVIL_LAUGH_PLAYER = AudioPlayer.getAudioPlayer(
            new File("sounds/audio/evillaugh.wav"), AudioPlayer.NORMAL);
    private static final AudioPlayer START_GAME_PLAYER = AudioPlayer.getAudioPlayer(
            new File("sounds/audio/losemusic.wav"), AudioPlayer.NORMAL);

    private int performTimer = 0;

    public MenuPanel(ActionListener l) {
        super();
        this.setBounds(0, 0, 835, 635);
        this.setLayout(null);
        this.add(titleScreen);
        this.add(new StartGameButton(this));
        this.add(new ExitGameButton(l));
        BGM_PLAYER.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(new ImageIcon("images/Panels/MainMenu.png").getImage(),
                0, 0, 835, 635, null);
        if(performTimer > 0 && performTimer < 5500) {
            Image zombieHandImage;
            if(performTimer < 1400)
                zombieHandImage =
                        new ImageIcon("images/Panels/Zombie_hand.gif").getImage();
            else
                zombieHandImage =
                        new ImageIcon("images/Panels/Zombie_hand_last_frame.png").getImage();
            g.drawImage(zombieHandImage,
                    180, 220, null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        if(source instanceof IntroButton) {
            titleScreen.setVisible(false);
            source.setVisible(false);
        } else if(source instanceof StartGameButton) {
            source.setEnabled(false);
            BGM_PLAYER.stop();
            START_GAME_PLAYER.start();
            EVIL_LAUGH_PLAYER.start();
            java.util.Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    performTimer += 20;
                    repaint();
                    if(performTimer % 80 == 0) {
                        ((StartGameButton) source).switchIcon();
                    }
                    if(performTimer >= 5500) {
                        performTimer = 0;
                        repaint();
                        source.setEnabled(true);
                        this.cancel();
                        t.cancel();
                    }
                }
            }, 0, 20);
        }
    }
}
