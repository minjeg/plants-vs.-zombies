package model.plant;

import model.GameModel;

public abstract class Plant {
    private int health;
    private final int performGap;

    public Plant(int health, int performGap) {
        this.health = health;
        this.performGap = performGap;
    }

    public abstract void update(GameModel gameModel, int row, int col);

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public int getPerformGap() {
        return performGap;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "health=" + health +
                ", performGap=" + performGap +
                '}';
    }
}