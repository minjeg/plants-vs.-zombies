package model.zombie;

import model.GameModel;

public abstract class Zombie {
    private int health;
    private double x;
    private int speed;

    public Zombie(int health, int x, int speed) {
        this.health = health;
        this.x = x;
        this.speed = speed;
    }

    public void update(GameModel gameModel) {
        x -= 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public int getX() {
        return (int) x;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "Zombie{" +
                "health=" + health +
                ", x=" + x +
                ", speed=" + speed +
                '}';
    }
}
