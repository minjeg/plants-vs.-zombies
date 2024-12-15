package model;

import model.zombie.Zombie;

public abstract class ZombieType {
    private int weight;
    private final int rank;
    private final int level;

    public ZombieType(int weight, int rank, int level) {
        this.weight = weight;
        this.rank = rank;
        this.level = level;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getRank() {
        return rank;
    }

    public int getLevel() {
        return level;
    }

    public abstract Zombie getZombie();
}
