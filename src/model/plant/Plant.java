package model.plant;

import model.GameModel;

import javax.swing.*;
import java.io.Serializable;

public abstract class Plant implements Serializable {
    private int health;
    private final int performGap;
    private State state = State.IDLE;
    private String currentImagePath;

    public enum State {IDLE, SHOOTING, RISE, ARMED, ATTACK, CHOMP, CHEW, SWALLOW, EXPLODING, EXPLODE}

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

    protected void setState(State state) {
        this.state = state;
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    protected void setCurrentImagePath(String currentImagePath) {
        this.currentImagePath = currentImagePath;
    }

    public int getImageX(int col) {
        return (int) (60 + (col + 0.5) * 80
                - new ImageIcon(currentImagePath).getImage().getWidth(null) / 2.0);
    }

    public int getImageY(int row) {
        return (int) (60 + (row + 0.5) * 100
                - new ImageIcon(currentImagePath).getImage().getHeight(null) / 2.0);
    }

    public int getShadeX(int col) {
        return (int) (60 + (col + 0.5) * 80 - 43);
    }

    public int getShadeY(int row) {
        return (int) (60 + (row + 0.5) * 100 + 18);
    }
}