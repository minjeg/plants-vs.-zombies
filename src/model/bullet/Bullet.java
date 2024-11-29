package model.bullet;

import model.GameModel;

public class Bullet {
    private double x;
    private final int speed;
    private int damage;

    public Bullet(int x, int speed, int damage) {
        this.x = x;
        this.speed = speed;
        this.damage = damage;
    }

    public void update(GameModel gameModel) {
        x += 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
    }

    public int getX() {
        return (int) x;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public String toString() {
        return "Bullet{" +
                "x=" + x +
                ", speed=" + speed +
                ", damage=" + damage +
                '}';
    }
}
