package model.zombie;

import model.GameModel;
import model.LawnMower;
import model.plant.Plant;
import view.ingame.AudioPlayer;

import javax.swing.*;
import java.io.File;
import java.io.Serializable;
import java.util.Random;

public abstract class Zombie implements Serializable {
    private int health;
    private double x;
    public static final int defaultX = -1000;
    private final int speed;//从右边界到左边界耗费毫秒数
    private final int damage;//每秒伤害
    private State state = State.WALKING;
    private String currentImagePath;

    private final AudioPlayer[] eatSoundPlayer = new AudioPlayer[3];
    private final AudioPlayer gulpSoundPlayer;

    private int soundPlayTimer = 0;

    private int deadAnimationPlayTimer = 0;

    private boolean isHit = false;

    {
        eatSoundPlayer[0] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/chomp.wav"), AudioPlayer.NORMAL);
        eatSoundPlayer[1] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/chomp2.wav"), AudioPlayer.NORMAL);
        eatSoundPlayer[2] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/chompsoft.wav"), AudioPlayer.NORMAL);
        gulpSoundPlayer = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/gulp.wav"), AudioPlayer.NORMAL);
    }

    public enum State {WALKING, EATING, DEAD, BOOMED, TOTALLY_DEAD}

    protected Zombie(int health, double x, int speed, int damage) {
        this.health = health;
        this.x = x;
        this.speed = speed;
        this.damage = damage;
    }

    public boolean update(GameModel gameModel, int row, int index) {
        if (state == State.TOTALLY_DEAD) {
            gameModel.getZombies(row).remove(index);
            return true;
        }
        if (state == State.WALKING) {
            if (x == defaultX)
                x = gameModel.getWidth() * (1.05 + Math.random() / 10);
            x -= 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
        }
        int col = getClosestColumn(gameModel);
        //僵尸到达小推车
        if (x < -30) {
            if (gameModel.getLawnMower(row) != null) {
                gameModel.getLawnMower(row).setState(LawnMower.State.ON);
                takeDamage(health);
            } else if (col < -1) {
                gameModel.setState(GameModel.State.LOSE);
                return false;
            }
        }
        //根据僵尸状态、前方是否有植物进行数据、状态更新
        if (health <= 0)
            setState(State.DEAD);
        Plant plant = col >= 0 ? gameModel.getPlant(row, col) : null;
        if (state == State.WALKING && plant != null
                && Math.abs(x - (col + 0.5) * gameModel.getBlockWidth()) < 20)
            setState(State.EATING);
        else if (state == State.EATING) {
            if (plant == null)
                setState(State.WALKING);
            else {
                plant.takeDamage(gameModel.getUpdateGap() * getDamage() / 1000);
                soundPlayTimer += 20;
                if (soundPlayTimer == 1000) {
                    eatSoundPlayer[new Random().nextInt(0, 3)].start();
                    soundPlayTimer = 0;
                }
                if (plant.isDead()) {
                    gulpSoundPlayer.start();
                    setState(State.WALKING);
                }
            }
        } else if (isDead()) {
            deadAnimationPlayTimer += 30;
            if (deadAnimationPlayTimer >= 2300)
                setState(State.TOTALLY_DEAD);
        }
        return false;
    }

    public boolean isDead() {
        return state == State.DEAD || state == State.BOOMED || state == State.TOTALLY_DEAD;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (!isDead()) isHit = true;
    }

    public int getDamage() {
        return damage;
    }

    public double getX() {
        return (int) x;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getClosestColumn(GameModel gameModel) {
        return (int) (x / (gameModel.getBlockWidth() + 1));
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    protected void setCurrentImagePath(String currentImagePath) {
        this.currentImagePath = currentImagePath;
    }

    public boolean isHit() {
        return isHit;
    }

    public void resetHitState() {
        isHit = false;
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
        return (int) (60 + getX() - 43) + 20;
    }

    public int getShadeY(int row) {
        return (int) (60 + (row + 0.5) * 100 + 18);
    }
}
