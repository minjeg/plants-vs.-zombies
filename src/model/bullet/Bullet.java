package model.bullet;

import model.GameModel;
import model.zombie.Zombie;

import java.io.Serializable;
import java.util.List;

public abstract class Bullet implements Serializable {
    private double x;
    private final int speed;//从发射位置到边界耗费时间
    private final int damage;
    private String currentImagePath;

    public Bullet(int x, int speed, int damage) {
        this.x = x;
        this.speed = speed;
        this.damage = damage;
    }

    //返回子弹是否被移除
    public boolean update(GameModel gameModel, int row, int index) {
        //子弹超出范围
        if (x < 0 || x > gameModel.getWidth()) {
            gameModel.getBullets(row).remove(index);
            return true;
        }
        //子弹击中僵尸
        List<Zombie> zombies = gameModel.getZombies(row);
        for (Zombie zombie : zombies) {
            if (Math.abs(zombie.getX() - this.getX()) < 10) {
                zombie.takeDamage(this.getDamage());
                gameModel.getBullets(row).remove(index);
                return true;
            }
        }
        //更新子弹位置
        x += 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
        return false;
    }

    public int getX() {
        return (int) x;
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
}
