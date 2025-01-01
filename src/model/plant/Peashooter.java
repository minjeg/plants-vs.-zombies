package model.plant;

import model.GameModel;
import model.bullet.Pea;
import model.zombie.Zombie;
import view.ingame.AudioPlayer;

import java.io.File;
import java.util.List;
import java.util.Random;

public class Peashooter extends Plant {
    private int timer;

    private AudioPlayer[] shootPlayer = new AudioPlayer[2];

    {
        shootPlayer[0] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/throw.wav"), AudioPlayer.NORMAL);
        shootPlayer[1] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/throw2.wav"), AudioPlayer.NORMAL);
    }

    public Peashooter() {
        super(300, 1500);
        setState(State.IDLE);
        timer = (int) (Math.random() * getPerformGap());
    }

    @Override
    public void update(GameModel gameModel, int row, int col) {
        if (isDead()) {
            gameModel.setPlant(row, col, null);
            return;
        }
        List<Zombie> zombies = gameModel.getZombies(row);
        Zombie zombie = GameModel.binarySearchFrontZombie(zombies, 0, zombies.size() - 1,
                (col + 0.5) * gameModel.getBlockWidth());
        if (getState() == State.IDLE) {
            if (zombie != null && zombie.getX() < gameModel.getWidth() * 1.1) {
                setState(State.SHOOTING);
            }
        } else if (getState() == State.SHOOTING) {
            timer -= gameModel.getUpdateGap();
            if (zombie == null || zombie.getX() > gameModel.getWidth() * 1.1) {
                setState(State.IDLE);
            } else if (timer <= 0) {
                shootPlayer[new Random().nextInt(0, 2)].start();
                gameModel.addBullet(row, new Pea((col + 0.8) * gameModel.getWidth() / gameModel.getCols()));
                timer = getPerformGap();
            }
        }
    }

    @Override
    protected void setState(State state) {
        super.setState(state);
        if (state == State.IDLE)
            setCurrentImagePath("images/Plant/Peashooter/idle.gif");
        else
            setCurrentImagePath("images/Plant/Peashooter/shooting.gif");
    }

    @Override
    public int getImageX(int col) {
        return super.getImageX(col) + 5;
    }

    @Override
    public int getImageY(int row) {
        return super.getImageY(row) - 10;
    }
}
