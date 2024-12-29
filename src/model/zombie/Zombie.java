package model.zombie;

import model.GameModel;
import model.LawnMower;
import model.plant.Plant;
import view.ingame.AudioPlayer;

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

    private AudioPlayer[] eatSoundPlayer = new AudioPlayer[3];
    private AudioPlayer gulpSoundPlayer;
    private AudioPlayer[] hitSoundPlayer = new AudioPlayer[3];
    int soundPlayTimer = 0;

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
        hitSoundPlayer[0] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/splat.wav"), AudioPlayer.NORMAL);
        hitSoundPlayer[1] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/splat2.wav"), AudioPlayer.NORMAL);
        hitSoundPlayer[2] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/splat3.wav"), AudioPlayer.NORMAL);
    }

    public enum State {WALKING, EATING}

    protected Zombie(int health, int x, int speed, int damage) {
        this.health = health;
        this.x = x;
        this.speed = speed;
        this.damage = damage;
    }

    public boolean update(GameModel gameModel, int row, int index) {
        if (isDead()) {
            gameModel.getZombies(row).remove(index);
            return true;
        }
        if (state == State.WALKING) {
            if (x == defaultX)
                x = gameModel.getWidth();
            x -= 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
        }
        int col = getClosestColumn(gameModel);
        //僵尸到达小推车
        if (col < 0) {
            if (gameModel.getLawnMower(row) != null) {
                gameModel.getLawnMower(row).setState(LawnMower.State.ON);
                gameModel.getZombies(row).remove(index);
                return true;
            } else {
                gameModel.setState(GameModel.State.LOSE);
                return false;
            }
        }
        //根据僵尸状态、前方是否有植物进行数据、状态更新
        Plant plant = gameModel.getPlant(row, col);
        if (state == State.WALKING && plant != null
                && Math.abs(x - (col + 0.5) * gameModel.getBlockWidth()) < 20)
            setState(State.EATING);
        else if (state == State.EATING) {
            if (plant == null)
                setState(State.WALKING);
            else {
                plant.takeDamage(gameModel.getUpdateGap() * getDamage() / 1000);
                soundPlayTimer += 20;
                if(soundPlayTimer == 1000) {
                    eatSoundPlayer[new Random().nextInt(0, 3)].start();
                    soundPlayTimer = 0;
                }
                if (plant.isDead()) {
                    gulpSoundPlayer.start();
                    setState(State.WALKING);
                }
            }
        }
        return false;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        playHitSound();
        if(!isDead()) isHit = true;
    }

    public int getDamage() {
        return damage;
    }

    public int getX() {
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

    protected void playHitSound() {
        hitSoundPlayer[new Random().nextInt(0, 3)].start();
    }
}
