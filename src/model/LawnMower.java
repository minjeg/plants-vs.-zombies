package model;

import model.zombie.Zombie;

import java.io.Serializable;

public class LawnMower implements Serializable {
    private double x;
    private final int speed = 2000;//从初始位置到边界耗费的毫秒数
    private String currentImagePath;
    State state;

    public enum State {ON, OFF}

    public LawnMower(GameModel gameModel) {
        this.x = (double) -gameModel.getWidth() / 15;
        setState(State.OFF);
    }

    public void update(GameModel gameModel, int row) {
        if (x > gameModel.getWidth())
            gameModel.setLawnMower(row, null);
        else if (state == State.ON) {
            x += 1.0 * gameModel.getUpdateGap() * gameModel.getWidth() / speed;
            for (Zombie zombie : gameModel.getZombies(row)) {
                if (Math.abs(this.x - zombie.getX()) < 20) {
                    zombie.takeDamage(10000);
                }
            }
        }
    }

    public int getX() {
        return (int) x;
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    public void setState(State state) {
        this.state = state;
        if (state == State.OFF)
            currentImagePath = "images/LawnMower/idle.png";
        else if (state == State.ON)
            currentImagePath = "images/LawnMower/moving.gif";
    }
}
