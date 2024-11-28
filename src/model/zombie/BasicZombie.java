package model.zombie;

import model.GameModel;

public class BasicZombie extends Zombie{
    public BasicZombie() {
        super(270);
    }

    @Override
    public void update(GameModel gameModel) {
        setY(getY()-getSpeed());
    }
}
