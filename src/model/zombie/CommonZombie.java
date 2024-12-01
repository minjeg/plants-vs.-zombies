package model.zombie;

import model.GameModel;

public class CommonZombie extends Zombie {
    public CommonZombie(GameModel gameModel) {
        super(270, gameModel.getWidth(), 50000,100);
    }
}
