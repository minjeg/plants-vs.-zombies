package view.ingame;

import javax.sound.sampled.*;
import java.io.IOException;

public class AudioPlayer {
    private Clip audioClip;
    private boolean isLoop;

    public static final boolean NORMAL = false, LOOP = true;

    public AudioPlayer(AudioInputStream ais, boolean isLoop) throws LineUnavailableException, IOException {
        DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
        audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.open(ais);
        this.isLoop = isLoop;
    }

    public void start(int frame) throws LineUnavailableException, IOException {
        audioClip.setFramePosition(frame);
        audioClip.start();
        if(isLoop) audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void start() throws LineUnavailableException, IOException {
        start(audioClip.getFramePosition());
    }

    public void stop() {
        audioClip.stop();
    }

    public int getCurrentFrame() {
        return audioClip.getFramePosition();
    }
}
