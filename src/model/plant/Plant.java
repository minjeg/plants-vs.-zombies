package model.plant;

import model.GameModel;

public abstract class Plant {
    private int health;
    private final int performGap;
    private State state = State.IDLE;
    private String currentImagePath;

    public enum State {IDLE, SHOOTING}

    public Plant(int health, int performGap) {
        this.health = health;
        this.performGap = performGap;
    }

    public abstract void update(GameModel gameModel, int row, int col);

    protected int getHealth() {
        return health;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    protected int getPerformGap() {
        return performGap;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    protected void setCurrentImagePath(String currentImagePath) {
        this.currentImagePath = currentImagePath;
    }
}