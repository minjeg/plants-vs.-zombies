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
        timer = getPerformGap();
    }

    @Override
    public void update(GameModel gameModel, int row, int col) {
        if (isDead()) {
            gameModel.setPlant(row, col, null);
            return;
        }
        if (getState() == State.IDLE) {
            for(Zombie zombie : gameModel.getZombies(row)) {
                if(zombie.isDead()) continue;
                int x = zombie.getX();
                if(x >= (col + 0.5) * gameModel.getBlockWidth()
                        && x < gameModel.getWidth() * 1.1) {
                    setState(State.SHOOTING);
                    timer = getPerformGap();
                    break;
                }
            }
        } else if (getState() == State.SHOOTING) {
            boolean isDone = true;
            for(Zombie zombie : gameModel.getZombies(row)) {
                if(zombie.isDead()) continue;
                int x = zombie.getX();
                if(x >= (col + 0.5) * gameModel.getBlockWidth()
                        && x < gameModel.getWidth() * 1.1) {
                    isDone = false;
                    break;
                }
            }
            timer -= gameModel.getUpdateGap();
            if (isDone) {
                setState(State.IDLE);
            } else if (timer <= 0) {
                shootPlayer[new Random().nextInt(0, 2)].start();
                gameModel.addBullet(row, new Pea((col + 1) * gameModel.getWidth() / gameModel.getCols()));
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
