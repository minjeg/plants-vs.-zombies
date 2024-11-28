package model;

public abstract class PlantModel {
    private int health;
    private int state;

    static int NORMAL =0, DEAD =1;

    public PlantModel(int health) {
        this.health = health;
        this.state= NORMAL;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        if(health<=0)
            state= DEAD;
    }

    public int getState() {
        return state;
    }
}