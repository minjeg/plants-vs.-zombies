package view.intromenu;

import view.PlayFrame;
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
    private StartGameButton startGameButton = new StartGameButton(this);
    private ExitGameButton exitGameButton = new ExitGameButton(this);
    private ExitConfirmPanel exitConfirmPanel = new ExitConfirmPanel(this);
    private PlayFrame frame;

    private int state;

    public static final int NORMAL = 0, READY_TO_PLAY = 1, READY_TO_EXIT = 2, START_PLAY = 3;

    private static final AudioPlayer BGM_PLAYER = AudioPlayer.getAudioPlayer(
            new File("sounds/bgm/CrazyDave (Intro Theme).wav"), AudioPlayer.LOOP);
    private static final AudioPlayer EVIL_LAUGH_PLAYER = AudioPlayer.getAudioPlayer(
            new File("sounds/audio/evillaugh.wav"), AudioPlayer.NORMAL);
    private static final AudioPlayer START_GAME_PLAYER = AudioPlayer.getAudioPlayer(
            new File("sounds/audio/losemusic.wav"), AudioPlayer.NORMAL);

    private int performTimer = 0;

    public MenuPanel(PlayFrame frame) {
        super();
        this.frame = frame;
        this.setBounds(0, 0, 835, 635);
        this.setLayout(null);
        this.add(titleScreen);
        this.add(exitConfirmPanel);
        this.add(startGameButton);
        this.add(exitGameButton);
        setState(NORMAL);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(new ImageIcon("images/Panels/MainMenu.png").getImage(),
                0, 0, 835, 635, null);
        if(performTimer > 0 && performTimer < 3500) {
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
            setState(READY_TO_PLAY);
        } else if(source instanceof ExitGameButton) {
            setState(READY_TO_EXIT);
        } else if(source instanceof ExitConfirmButton) {
            System.exit(0);
        } else if(source instanceof ExitCancelButton) {
            setState(NORMAL);
        }
    }

    public void setState(int state) {
        this.state = state;
        if(state == NORMAL) {
            this.setVisible(true);
            if(BGM_PLAYER.isStop())
                BGM_PLAYER.start();
            startGameButton.setEnabled(true);
            exitGameButton.setEnabled(true);
            exitConfirmPanel.setVisible(false);
        } else if(state == READY_TO_PLAY) {
            BGM_PLAYER.stop();
            startGameButton.setEnabled(false);
            exitGameButton.setEnabled(false);
            exitConfirmPanel.setVisible(false);
            START_GAME_PLAYER.start();
            EVIL_LAUGH_PLAYER.start();
            java.util.Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    performTimer += 20;
                    repaint();
                    if(performTimer % 80 == 0) {
                        startGameButton.switchIcon();
                    }
                    if(performTimer >= 3500) {
                        performTimer = 0;
                        startGameButton.resetDisabledIcon();
                        setState(START_PLAY);
                        this.cancel();
                        t.cancel();
                    }
                }
            }, 0, 20);
        } else if(state == READY_TO_EXIT) {
            startGameButton.setEnabled(false);
            exitGameButton.setEnabled(false);
            exitConfirmPanel.setVisible(true);
        } else if(state == START_PLAY) {
            this.setVisible(false);
            frame.startPlay();
        }
    }
}
