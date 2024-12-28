package model.bullet;

import view.ingame.AudioPlayer;

import java.io.File;
import java.util.Random;

public class Pea extends Bullet {
    public Pea(int x) {
        super(x, 1500, 20);
        setCurrentImagePath("images/Bullet/pea.gif");
        int randomNum = new Random().nextInt(0, 3);
        setSoundPlayer(AudioPlayer
                .getAudioPlayer(new File(switch (randomNum) {
                    case 0 -> "sounds/audio/splat.wav";
                    case 1 -> "sounds/audio/splat2.wav";
                    default -> "sounds/audio/splat3.wav";
                }), AudioPlayer.NORMAL));
    }
}
