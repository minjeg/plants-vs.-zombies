package model.plant;

import model.GameModel;

public abstract class Plant {
    private int health;
    private final int performGap;
//    private final int cost;
    private State state = State.IDLE;
    private String currentImagePath;

    public enum State {IDLE, SHOOTING}

    public Plant(int health, /*int cost, */int performGap) {
        this.health = health;
//        this.cost = cost;
        this.performGap = performGap;
    }

    public abstract void update(GameModel gameModel, int row, int col);

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    protected int getPerformGap() {
        return performGap;
    }

//    public int getCost() {
//        return cost;
//    }

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