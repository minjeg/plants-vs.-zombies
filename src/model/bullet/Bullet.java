package model.bullet;

import model.GameModel;

public class Bullet {
    private int x;
    private final int speed;

    public Bullet(int speed){
        this.speed=speed;
    }

    public void update(GameModel gameModel){
        x -=speed*gameModel.getWidth()/gameModel.getCols()/10;
    }

    public int getSpeed() {
        return speed;
    }
}
