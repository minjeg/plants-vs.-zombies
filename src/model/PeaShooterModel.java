package model;

public class PeaShooterModel extends PlantModel {

    private long lastShootTime;

    public PeaShooterModel() {
        super(300);
        lastShootTime =System.currentTimeMillis();
    }

    public long getLastShootTime() {
        return lastShootTime;
    }

    public void setLastShootTime(long lastShootTime) {
        this.lastShootTime = lastShootTime;
    }
}
