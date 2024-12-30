package model.plant;

import model.GameModel;
import model.bullet.Pea;
import view.ingame.AudioPlayer;

import java.io.File;
import java.util.Random;

public class Repeater extends Plant {
    private int timer;
    private boolean flag = false;

    private AudioPlayer[] shootPlayer = new AudioPlayer[2];

    {
        shootPlayer[0] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/throw.wav"), AudioPlayer.NORMAL);
        shootPlayer[1] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/throw2.wav"), AudioPlayer.NORMAL);
    }

    public Repeater() {
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
        if (getState() == State.IDLE && !gameModel.getZombies(row).isEmpty()) {
            setState(State.SHOOTING);
            timer = getPerformGap();
        } else if (getState() == State.SHOOTING) {
            timer -= gameModel.getUpdateGap();
            if (gameModel.getZombies(row).isEmpty()) {
                setState(State.IDLE);
            } else if (timer <= 0) {
                shootPlayer[new Random().nextInt(0, 2)].start();
                gameModel.addBullet(row, new Pea((col + 1) * gameModel.getWidth() / gameModel.getCols()));
                if (flag) {
                    timer = getPerformGap() - 100;
                    flag = false;
                } else {
                    timer = 100;
                    flag = true;
                }
            }
        }
    }

    @Override
    protected void setState(State state) {
        super.setState(state);
        if (state == State.IDLE)
            setCurrentImagePath("images/Plant/Repeater/idle.gif");
        else
            setCurrentImagePath("images/Plant/Repeater/shooting.gif");
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
