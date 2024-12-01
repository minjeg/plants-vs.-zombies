package model.plant;

import model.GameModel;
import model.bullet.Pea;

public class Peashooter extends Plant {
    private int timer;

    public Peashooter() {
        super(300, 100, 1500);
        setState(State.IDLE);
        timer = getPerformGap();
    }

    @Override
    public void update(GameModel gameModel, int row, int col) {
        if (getState() == State.IDLE && !gameModel.getZombies(row).isEmpty()) {
            setState(State.SHOOTING);
            timer = getPerformGap();
        } else if (getState() == State.SHOOTING) {
            timer += gameModel.getUpdateGap();
            if (gameModel.getZombies(row).isEmpty()) {
                setState(State.IDLE);
            } else if (timer >= getPerformGap()) {
                gameModel.addBullet(row, new Pea((col + 1) * gameModel.getWidth() / gameModel.getCols()));
                timer -= getPerformGap();
            }
        }
    }

    @Override
    public void setState(State state) {
        super.setState(state);
        if (state == State.IDLE)
            setCurrentImagePath("images/Plant/Peashooter/idle.gif");
        else
            setCurrentImagePath("images/Plant/Peashooter/shooting.gif");
    }
}
