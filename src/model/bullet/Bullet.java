package model.bullet;

import model.GameModel;
import model.zombie.BucketheadZombie;
import model.zombie.Zombie;
import view.ingame.AudioPlayer;

import javax.swing.*;
import java.io.Serializable;
import java.util.List;

public abstract class Bullet implements Serializable {
    private double x;
    private final int speed;//从发射位置到边界耗费时间
    private final int damage;
    private String currentImagePath;

    private AudioPlayer soundPlayer;
    private AudioPlayer coneHitPlayer;
    private AudioPlayer ironShieldHitPlayer;

    public Bullet(double x, int speed, int damage) {
        this.x = x;
        this.speed = speed;
        this.damage = damage;
    }

    /// 返回子弹是否应被移除
    public boolean update(GameModel gameModel, int row, int index) {
        //子弹超出范围
        if (x < 0 || x > gameModel.getWidth() * 1.2) {
            gameModel.getBullets(row).remove(index);
            return true;
        }
        //子弹击中僵尸
        List<Zombie> zombies = gameModel.getZombies(row);
        Zombie zombie = GameModel.binarySearchFrontZombie(zombies, 0, zombies.size() - 1, x);
        if (zombie != null && !zombie.isDead() && Math.abs(zombie.getX() - this.getX()) < 20) {
            zombie.takeDamage(this.getDamage());
            gameModel.getBullets(row).remove(index);
            if (zombie instanceof BucketheadZombie
                    && ((BucketheadZombie) zombie).withBucket())
                ironShieldHitPlayer.start();
            else
                soundPlayer.start();
            return true;
        }
        //更新子弹位置
        x += 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
        return false;
    }

    public double getX() {
        return x;
    }

    public int getDamage() {
        return damage;
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    protected void setCurrentImagePath(String imagePath) {
        this.currentImagePath = imagePath;
    }

    public void setSoundPlayer(AudioPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    public void setConeHitPlayer(AudioPlayer coneHitPlayer) {
        this.coneHitPlayer = coneHitPlayer;
    }

    public void setIronShieldHitPlayer(AudioPlayer ironShieldHitPlayer) {
        this.ironShieldHitPlayer = ironShieldHitPlayer;
    }

    public int getImageX() {
        return (int) (60 + getX()
                - new ImageIcon(currentImagePath).getImage().getWidth(null) / 2.0);
    }

    public int getImageY(int row) {
        return (int) (60 + (row + 0.5) * 100
                - new ImageIcon(currentImagePath).getImage().getHeight(null) / 2.0) - 10;
    }

    public int getShadeX() {
        return (int) (60 + getX() - 9);
    }

    public int getShadeY(int row) {
        return (int) (60 + (row + 0.5) * 100 + 28);
    }
}
