package model.plant;

import model.GameModel;

public class WallNut extends Plant {
    public WallNut() {
        super(4000, 0);
        setCurrentImagePath("images/Plant/Wall-Nut/idle.gif");
    }

    @Override
    public void update(GameModel gameModel, int row, int col) {
        if (isDead()) {
            gameModel.setPlant(row, col, null);
            return;
        }
        if (getHealth() < 1333)
            setCurrentImagePath("images/Plant/Wall-Nut/break2.gif");
        else if (getHealth() < 2666)
            setCurrentImagePath("images/Plant/Wall-Nut/break1.gif");
    }
}
