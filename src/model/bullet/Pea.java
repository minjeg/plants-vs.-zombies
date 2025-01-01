package model.bullet;

import view.ingame.AudioPlayer;

import java.io.File;
import java.util.Random;

public class Pea extends Bullet {
    public Pea(double x) {
        super(x, 1500, 20);
        setCurrentImagePath("images/Bullet/pea.gif");
        setSoundPlayer(AudioPlayer
                .getAudioPlayer(new File(switch (new Random().nextInt(0, 3)) {
                    case 0 -> "sounds/audio/splat.wav";
                    case 1 -> "sounds/audio/splat2.wav";
                    default -> "sounds/audio/splat3.wav";
                }), AudioPlayer.NORMAL));
        setIronShieldHitPlayer(AudioPlayer
                .getAudioPlayer(new File(switch (new Random().nextInt(0, 2)) {
                    case 0 -> "sounds/audio/shieldhit.wav";
                    default -> "sounds/audio/shieldhit2.wav";
                }), AudioPlayer.NORMAL));
    }
}
