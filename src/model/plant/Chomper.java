package model.plant;

import model.GameModel;
import model.zombie.Zombie;

import java.util.List;

public class Chomper extends Plant {
    private int timer;

    public Chomper() {
        super(300, 42000);
        setState(State.IDLE);
        timer = 0;
    }

    @Override
    public void update(GameModel gameModel, int row, int col) {
        if (isDead()) {
            gameModel.setPlant(row, col, null);
            return;
        }
        if (getState() == State.IDLE) {
            List<Zombie> zombies = gameModel.getZombies(row);
            for (Zombie zombie : zombies) {
                if (zombie.getX() <= (col + 2) * gameModel.getBlockWidth()) {
                    setState(State.ATTACK);
                    timer = 1000;
                    break;
                }
            }
        } else {
            timer -= gameModel.getUpdateGap();
            if (timer <= 0) {
                if (getState() == State.ATTACK) {
                    List<Zombie> zombies = gameModel.getZombies(row);
                    for (Zombie zombie : zombies) {
                        if (zombie.getX() <= (col + 2) * gameModel.getBlockWidth()) {
                            zombie.takeDamage(zombie.getHealth());
                            setState(State.CHEW);
                            timer = getPerformGap() - 2000;
                            break;
                        }
                    }
                    if (getState() == State.ATTACK)
                        setState(State.IDLE);
                } else if (getState() == State.CHEW) {
                    setState(State.SWALLOW);
                    timer = 1000;
                } else if (getState() == State.SWALLOW) {
                    setState(State.IDLE);
                }
            }
        }
    }

    @Override
    protected void setState(State state) {
        super.setState(state);
        if (state == State.IDLE)
            setCurrentImagePath("images/Plant/Chomper/idle.gif");
        else if (state == State.ATTACK)
            setCurrentImagePath("images/Plant/Chomper/attack.gif");
        else if (state == State.CHEW)
            setCurrentImagePath("images/Plant/Chomper/chew.gif");
        else if (state == State.SWALLOW)
            setCurrentImagePath("images/Plant/Chomper/swallow.gif");
    }
}
