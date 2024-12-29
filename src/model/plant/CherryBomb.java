package model.plant;

import model.GameModel;
import model.zombie.Zombie;

import java.util.List;

public class CherryBomb extends Plant {
    private int timer;

    public CherryBomb() {
        super(300, 1000);
        setState(State.EXPLODING);
        timer = getPerformGap();
    }

    @Override
    public void update(GameModel gameModel, int row, int col) {
        if (isDead()) {
            gameModel.setPlant(row, col, null);
            return;
        }
        timer -= gameModel.getUpdateGap();
        if (timer <= 0) {
            if (getState() == State.EXPLODING) {
                setState(State.EXPLODE);
                timer = 1000;
                int upper = Math.min(row + 1, gameModel.getRows() - 1);
                for (int i = Math.max(row - 1, 0); i <= upper; ++i) {
                    List<Zombie> rowZombies = gameModel.getZombies(i);
                    for (Zombie zombie : rowZombies) {
                        int j = zombie.getClosestColumn(gameModel);
                        if (j >= col - 1 && j <= col + 1)
                            zombie.takeDamage(1800);
                    }
                }
            } else if (getState() == State.EXPLODE) {
                gameModel.setPlant(row, col, null);
            }
        }
    }

    @Override
    protected void setState(State state) {
        super.setState(state);
        if (getState() == State.EXPLODING)
            setCurrentImagePath("images/Plant/CherryBomb/explode.gif");
        else if (getState() == State.EXPLODE)
            setCurrentImagePath("images/Plant/CherryBomb/ExplodeEffect.gif");
    }
}
