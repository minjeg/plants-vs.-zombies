package model.zombie;

import java.util.Random;

public class BasicZombie extends Zombie {
    private int randNum = new Random().nextInt(0, 2);

    public BasicZombie() {
        super(270, defaultX, 50000, 100);
        setState(State.WALKING);
    }

    @Override
    public void setState(State state) {
        super.setState(state);
        if (state == State.WALKING) {
            if (randNum == 0)
                setCurrentImagePath("images/Zombie/BasicZombie/walk.gif");
            else
                setCurrentImagePath("images/Zombie/BasicZombie/walk2.gif");
        } else if (state == State.EATING) {
            setCurrentImagePath("images/Zombie/BasicZombie/eat.gif");
        } else if(state == State.DEAD) {
            setCurrentImagePath("images/Zombie/BasicZombie/death.gif");
        } else if(state == State.BOOMED) {
            setCurrentImagePath("images/Zombie/BoomDie.gif");
        }
    }

    @Override
    public int getImageX() {
        return super.getImageX();
    }

    @Override
    public int getImageY(int row) {
        return super.getImageY(row) - 15;
    }
}
