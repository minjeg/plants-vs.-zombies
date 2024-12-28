package view.ingame;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/// 音频播放器类, 通过包装Clip接口来完成音频播放
public class AudioPlayer {
    private Clip audioClip;
    private boolean isLoop;

    public static final boolean NORMAL = false, LOOP = true;

    private AudioPlayer(AudioInputStream ais, boolean isLoop) throws LineUnavailableException, IOException {
        DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
        audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.open(ais);
        this.isLoop = isLoop;
    }

    public static AudioPlayer getAudioPlayer(File audioFile, boolean playerMode) {
        AudioPlayer ret;
        try {
            ret = new AudioPlayer(AudioSystem.getAudioInputStream(audioFile), playerMode);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    // 从指定的某一帧开始播放
    public void startFrom(int frame) {
        audioClip.setFramePosition(frame % audioClip.getFrameLength());
        audioClip.start();
        if(isLoop) audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // 从头开始播放
    public void start() {
        startFrom(0);
    }

    // 继续播放 (一般用于循环播放器被暂停)
    public void continuePlay() {
        if(isLoop) startFrom(getCurrentFrame());
    }

    // 停止播放
    public void stop() {
        audioClip.stop();
    }

    // 获取当前帧
    public int getCurrentFrame() {
        return audioClip.getFramePosition();
    }
}
