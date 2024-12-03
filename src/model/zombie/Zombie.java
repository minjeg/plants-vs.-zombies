package model.zombie;

import model.GameModel;
import model.plant.Plant;

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

    public boolean update(GameModel gameModel, int row, int index) {
        if (!isAlive()) {
            gameModel.getZombies(row).remove(index);
            return true;
        }
        if (state == State.WALKING)
            x -= 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
        int col = getClosestColumn(gameModel);
        //僵尸到达小推车
        if (col < 0) {
            if (gameModel.hasLawnMower(row)) {
                gameModel.getZombies(row).clear();
                gameModel.setLawnMower(row,false);
                return true;
            }
            gameModel.setState(GameModel.State.LOSE);
            return false;
        }
        //根据僵尸状态、前方是否有植物进行数据、状态更新
        Plant plant = gameModel.getPlant(row, col);
        if (state == Zombie.State.WALKING && plant != null
                && Math.abs(x - (col + 0.5) * gameModel.getBlockWidth()) < 10)
            setState(Zombie.State.EATING);
        else if (state == Zombie.State.EATING) {
            if (plant == null)
                setState(Zombie.State.WALKING);
            else {
                plant.takeDamage(gameModel.getUpdateGap() * getDamage() / 1000);
                if (plant.isDead())
                    setState(Zombie.State.WALKING);
            }
        }
        return false;
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
