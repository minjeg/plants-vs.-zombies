package model.plant;

import model.GameModel;

public class PeaShooter extends Plant {

    private long lastShootTime;

    public PeaShooter() {
        super(300);
        lastShootTime =System.currentTimeMillis();
    }

    @Override
    public void update(GameModel gameModel) {

    }

    public long getLastShootTime() {
        return lastShootTime;
    }

    public void setLastShootTime(long lastShootTime) {
        this.lastShootTime = lastShootTime;
    }
}
