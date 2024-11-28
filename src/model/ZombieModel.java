package model;

public abstract class ZombieModel {
    private int health;
    public ZombieModel(int health){
        this.health=health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
