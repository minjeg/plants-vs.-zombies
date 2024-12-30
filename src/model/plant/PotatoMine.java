package model.plant;

import model.GameModel;
import model.zombie.Zombie;
import view.ingame.AudioPlayer;

import java.io.File;
import java.util.List;

public class PotatoMine extends Plant {
    private int timer;

    private AudioPlayer riseSoundPlayer = AudioPlayer.getAudioPlayer(
            new File("sounds/audio/dirt_rise.wav"), AudioPlayer.NORMAL);
    private AudioPlayer explosionPlayer = AudioPlayer.getAudioPlayer(
            new File("sounds/audio/potato_mine.wav"), AudioPlayer.NORMAL);

    private boolean riseSoundEnabled = true;
    private boolean explodeSoundEnabled = true;

    public PotatoMine() {
        super(300, 20000);
        setState(State.IDLE);
        timer = getPerformGap();
    }

    @Override
    public void takeDamage(int damage) {
        if(getState() == State.IDLE)
            super.takeDamage(damage);
    }

    @Override
    public void update(GameModel gameModel, int row, int col) {
        if(isDead()) {
            gameModel.setPlant(row, col, null);
            return;
        }
        if(getState() == State.IDLE) {
            timer -= gameModel.getUpdateGap();
            if(timer <= 0) {
                timer = 1500;
                setState(State.RISE);
            }
        } else if(getState() == State.RISE) {
            if(riseSoundEnabled) {
                riseSoundPlayer.start();
                riseSoundEnabled = false;
            }
            timer -= gameModel.getUpdateGap();
            if(timer <= 0)
                setState(State.ARMED);
        } else if(getState() == State.ARMED) {
            List<Zombie> zombies = gameModel.getZombies(row);
            for (Zombie zombie : zombies) {
                if(Math.abs(zombie.getX() - (col + 0.5) * gameModel.getBlockWidth()) <= 20) {
                    for(Zombie z : zombies) {
                        if(Math.abs(z.getX() - (col + 0.5) * gameModel.getBlockWidth()) <= 20)
                            z.setState(Zombie.State.TOTALLY_DEAD);
                    }
                    setState(State.EXPLODING);
                    timer = 1000;
                    return;
                }
            }
        } else if(getState() == State.EXPLODING) {
            if(explodeSoundEnabled) {
                explosionPlayer.start();
                explodeSoundEnabled = false;
            }
            timer -= gameModel.getUpdateGap();
            if(timer <= 0)
                gameModel.setPlant(row, col, null);
        }
    }

    @Override
    protected void setState(State state) {
        super.setState(state);
        if(state == State.IDLE)
            setCurrentImagePath("images/Plant/PotatoMine/under.gif");
        else if(state == State.RISE)
            setCurrentImagePath("images/Plant/PotatoMine/rise.gif");
        else if(state == State.ARMED)
            setCurrentImagePath("images/Plant/PotatoMine/armed.gif");
        else if(state == State.EXPLODING)
            setCurrentImagePath("images/Plant/PotatoMine/Explode.png");
    }
}
