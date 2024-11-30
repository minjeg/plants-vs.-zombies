package model.zombie;

import model.GameModel;

public abstract class Zombie {
    private int health;
    private double x;
    private int speed;
    private int damage;
    private State state = State.ADVANCING;

    public enum State {ADVANCING, ATTACKING}

    public Zombie(int health, int x, int speed, int damage) {
        this.health = health;
        this.x = x;
        this.speed = speed;
        this.damage = damage;
    }

    public void update(GameModel gameModel) {
        if (state == State.ADVANCING)
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


    public int getDamage() {
        return damage;
    }

    public int getX() {
        return (int) x;
    }

    public int getSpeed() {
        return speed;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getClosestColumn(GameModel gameModel) {
        return (int) (x * gameModel.getCols() / gameModel.getWidth());
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
