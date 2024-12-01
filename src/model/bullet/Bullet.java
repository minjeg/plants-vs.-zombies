package model.bullet;

import model.GameModel;

public abstract class Bullet {
    private double x;
    private final int speed;
    private final int damage;
    private String currentImagePath;

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

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    protected void setCurrentImagePath(String imagePath) {
        this.currentImagePath = imagePath;
    }
}
