package model.zombie;

import model.GameModel;

import java.util.Random;

public class ConeheadZombie extends Zombie {
    private boolean withCone = true;

    private final int randNum = new Random().nextInt(0, 2);

    public ConeheadZombie() {
        super(640, defaultX, 50000, 100);
        setState(State.WALKING);
    }

    @Override
    public boolean update(GameModel gameModel, int row, int index) {
        if (withCone && getHealth() <= 270) {
            withCone = false;
            setState(State.WALKING);
        }
        return super.update(gameModel, row, index);
    }

    @Override
    public void setState(State state) {
        super.setState(state);
        String s = withCone ? "ConeheadZombie" : "BasicZombie";
        if (state == State.WALKING) {
            if (randNum == 0)
                setCurrentImagePath("images/Zombie/" + s + "/walk.gif");
            else
                setCurrentImagePath("images/Zombie/" + s + "/walk2.gif");
        } else if (state == State.EATING) {
            setCurrentImagePath("images/Zombie/" + s + "/eat.gif");
        } else if (state == State.DEAD) {
            setCurrentImagePath("images/Zombie/BasicZombie/death.gif");
        } else if (state == State.BOOMED) {
            setCurrentImagePath("images/Zombie/BoomDie.gif");
        }
    }

    public boolean withCone() {
        return withCone;
    }

    @Override
    public int getImageY(int row) {
        return super.getImageY(row) - 15;
    }
}
