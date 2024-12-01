package model.zombie;

import model.GameModel;

public abstract class Zombie {
    private int health;
    private double x;
    private final int speed;
    private final int damage;
    private State state = State.WALKING;
    private String currentImagePath;

    public enum State {WALKING, EATING}

    protected Zombie(int health, int x, int speed, int damage) {
        this.health = health;
        this.x = x;
        this.speed = speed;
        this.damage = damage;
    }

    public void update(GameModel gameModel) {
        if (state == State.WALKING)
            x -= 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public int getDamage() {
        return damage;
    }

    public int getX() {
        return (int) x;
    }

    public State getState() {
        return state;
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
}
