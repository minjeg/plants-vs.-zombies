package model.plant;

import model.GameModel;
import model.zombie.Zombie;
import view.ingame.AudioPlayer;

import java.io.File;
import java.util.List;

public class Chomper extends Plant {
    private int timer;

    private AudioPlayer chompPlayer = AudioPlayer.getAudioPlayer(
            new File("sounds/audio/bigchomp.wav"), AudioPlayer.NORMAL);

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
            Zombie zombie = GameModel.binarySearchFrontZombie(zombies, 0, zombies.size() - 1,
                    (col + 0.5) * gameModel.getBlockWidth());
            if (zombie != null && zombie.getX() < (col + 2) * gameModel.getBlockWidth()
                    && zombie.getX() < gameModel.getWidth() * 1.05) {
                setState(State.ATTACK);
                timer = 1000;
            }
        } else {
            timer -= gameModel.getUpdateGap();
            if (timer <= 0) {
                if (getState() == State.ATTACK) {
                    chompPlayer.start();
                    List<Zombie> zombies = gameModel.getZombies(row);
                    Zombie zombie = GameModel.binarySearchFrontZombie(zombies, 0, zombies.size() - 1, (col + 0.5) * gameModel.getBlockWidth());
                    if (zombie != null && zombie.getX() <= (col + 2) * gameModel.getBlockWidth()) {
                        zombie.setState(Zombie.State.TOTALLY_DEAD);
                        setState(State.CHEW);
                        timer = getPerformGap() - 2000;
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

    @Override
    public int getImageX(int col) {
        return super.getImageX(col) + 20;
    }

    @Override
    public int getImageY(int row) {
        return super.getImageY(row) - 20;
    }
}
