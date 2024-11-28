package model.plant;

import model.GameModel;

public abstract class Plant {
    private String name;
    private int health;
    private int state;
    private int damage;
    private int performGap;

    static int NORMAL =0, DEAD =1;

    public Plant(int health) {
        this.health = health;
        this.state= NORMAL;
    }

    public abstract void update(GameModel gameModel);

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