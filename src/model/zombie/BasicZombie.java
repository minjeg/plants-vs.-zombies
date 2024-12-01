package model.zombie;

import model.GameModel;

public class BasicZombie extends Zombie {
    private static int count = 0;

    public BasicZombie(GameModel gameModel) {
        super(270, gameModel.getWidth(), 50000, 100);
        setState(State.WALKING);
    }

    @Override
    public void setState(State state) {
        super.setState(state);
        if (state == State.WALKING) {
            count = (count + 1) % 2;
            if (count == 0)
                setCurrentImagePath("images/Zombie/BasicZombie/walk.gif");
            else
                setCurrentImagePath("images/Zombie/BasicZombie/walk2.gif");
        } else
            setCurrentImagePath("images/Zombie/BasicZombie/eat.gif");
    }
}
