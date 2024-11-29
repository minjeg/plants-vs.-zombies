package model.plant;

import model.GameModel;
import model.bullet.Pea;

public class PeaShooter extends Plant {

    private int timer = 0;

    public PeaShooter() {
        super(300, 1500);
        timer = getPerformGap();
    }

    @Override
    public void update(GameModel gameModel, int row, int col) {
        timer += gameModel.getUpdateGap();
        if (timer >= getPerformGap()) {
            gameModel.addBullet(row, new Pea((col + 1) * gameModel.getWidth() / gameModel.getCols()));
            timer -= getPerformGap();
        }
    }
}
