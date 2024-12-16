package model.zombie;

import model.GameModel;

public class BucketheadZombie extends Zombie {
    private int count = 0;
    private boolean withBucket = true;

    public BucketheadZombie() {
        super(1370, defaultX, 50000, 100);
        setState(State.WALKING);
    }

    @Override
    public boolean update(GameModel gameModel, int row, int index) {
        if (withBucket && getHealth() <= 270) {
            withBucket = false;
            setState(State.WALKING);
        }
        return super.update(gameModel, row, index);
    }

    @Override
    public void setState(State state) {
        super.setState(state);
        String s = withBucket ? "BucketheadZombie" : "BasicZombie";
        if (state == State.WALKING) {
            count = (count + 1) % 2;
            if (count == 0)
                setCurrentImagePath("images/Zombie/" + s + "/walk.gif");
            else
                setCurrentImagePath("images/Zombie/" + s + "/walk2.gif");
        } else if (state == State.EATING)
            setCurrentImagePath("images/Zombie/" + s + "/eat.gif");
    }
}
