package model.plant;

import model.GameModel;
import model.Sun;

public class Sunflower extends Plant {
    private int timer;

    public Sunflower() {
        super(300, 24000);
        timer = (int) (3000 + Math.random() * 9500);
        setState(State.IDLE);
    }

    @Override
    public void update(GameModel gameModel, int row, int col) {
        if (isDead()) {
            gameModel.setPlant(row, col, null);
            return;
        }
        timer += gameModel.getUpdateGap();
        if (timer >= getPerformGap()) {
            gameModel.addSun(new Sun(gameModel, row, col));
            timer -= getPerformGap();
        }
    }

    @Override
    protected void setState(State state) {
        super.setState(state);
        setCurrentImagePath("images/Plant/Sunflower/idle.gif");
    }
}
