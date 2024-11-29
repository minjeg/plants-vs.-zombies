package model.zombie;

import model.GameModel;

public class BasicZombie extends Zombie {
    public BasicZombie(GameModel gameModel) {
        super(270, gameModel.getWidth(), 50000);
    }
}
