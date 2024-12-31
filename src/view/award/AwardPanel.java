package view.award;

import view.PlayFrame;
import view.ingame.AudioPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AwardPanel extends JPanel implements ActionListener {
    PlayFrame frame;

    private static final AudioPlayer BGM_PLAYER = AudioPlayer.getAudioPlayer(
            new File("sounds/bgm/Zen Garden.wav"), AudioPlayer.LOOP);

    private int state = OFF;

    public static final int ON = 0, OFF = 1;

    public AwardPanel(PlayFrame frame) {
        super();
        this.frame = frame;
        this.setBounds(0, 0, 835, 635);
        this.setLayout(null);
        this.add(new AwardToMenuButton(this));
        setState(OFF);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(new ImageIcon("images/Panels/AwardScreen.png").getImage(),
                0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setState(OFF);
        frame.returnToMenu();
    }

    public void setState(int state) {
        this.state = state;
        if(state == ON) {
            BGM_PLAYER.start();
            this.setVisible(true);
        } else {
            this.setVisible(false);
            BGM_PLAYER.stop();
        }
    }
}
