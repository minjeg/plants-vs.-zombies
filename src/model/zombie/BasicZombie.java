package model.zombie;

public class BasicZombie extends Zombie {
    private static int count = 0;

    public BasicZombie() {
        super(270, defaultX, 50000, 100);
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
        } else if (state == State.EATING)
            setCurrentImagePath("images/Zombie/BasicZombie/eat.gif");
    }
}
