package model.zombie;

import model.GameModel;

public abstract class Zombie {
    private int health;
    private int y;
    private int speed;

    public Zombie(int health){
        this.health=health;
    }

    public abstract void update(GameModel gameModel);

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
