package model;

import model.zombie.Zombie;
import view.ingame.AudioPlayer;

import javax.swing.*;
import java.io.File;
import java.io.Serializable;

public class LawnMower implements Serializable {
    private double x;
    private static final int defaultX = -60;
    private final int speed = 2000;//从初始位置到边界耗费的毫秒数
    private String currentImagePath;
    State state;

    private final AudioPlayer soundPlayer = AudioPlayer.getAudioPlayer(
            new File("sounds/audio/lawnmower.wav"), AudioPlayer.NORMAL);
    private boolean soundPlayable = true;

    public enum State {ON, OFF}

    public LawnMower() {
        this.x = defaultX;
        setState(State.OFF);
    }

    public void update(GameModel gameModel, int row) {
        if (x > gameModel.getWidth() * 1.1)
            gameModel.setLawnMower(row, null);
        else if (state == State.ON) {
            x += 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
            for (Zombie zombie : gameModel.getZombies(row)) {
                if (Math.abs(this.x - zombie.getX()) < 20) {
                    zombie.takeDamage(zombie.getHealth());
                }
            }
        }
    }

    public double getX() {
        return x;
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    public void setState(State state) {
        this.state = state;
        if (state == State.OFF)
            currentImagePath = "images/LawnMower/idle.png";
        else if (state == State.ON) {
            currentImagePath = "images/LawnMower/moving.gif";
            if (soundPlayable) {
                soundPlayer.start();
                soundPlayable = false;
            }
        }
    }

    public int getImageX() {
        return (int) (60 + getX()
                - new ImageIcon(currentImagePath).getImage().getWidth(null) / 2.0);
    }

    public int getImageY(int row) {
        return (int) (60 + (row + 0.5) * 100
                - new ImageIcon(currentImagePath).getImage().getHeight(null) / 2.0);
    }

    public int getShadeX() {
        return getImageX() + 10;
    }

    public int getShadeY(int row) {
        return getImageY(row) + 85;
    }
}
