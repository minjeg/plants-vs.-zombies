package model.seed;

import model.GameModel;
import model.plant.Plant;

import java.io.Serializable;

public abstract class PlantSeed implements Serializable {
    private int coolDown;
    private int currentCoolDown;
    private int cost;
    private boolean chosen = false;
    private String imagePath;

    public PlantSeed(int coolDown, int currentCoolDown, int cost, String imagePath) {
        this.coolDown = coolDown;
        this.currentCoolDown = currentCoolDown;
        this.cost = cost;
        this.imagePath = imagePath;
    }

    public boolean goodToPlant(GameModel model) {
        return currentCoolDown == 0 && model.getSun() >= cost;
    }

//    boolean isChosen() {
//        return chosen;
//    }

    public String getImagePath() {
        return imagePath;
    }

    abstract public Plant getPlant();

    public void plant(GameModel model, int row, int col) {
        currentCoolDown = coolDown;
        model.setSun(model.getSun() - cost);
        model.setPlant(row, col, getPlant());
    }

    public void update(GameModel model) {
        currentCoolDown = Math.max(currentCoolDown - model.getUpdateGap(), 0);
    }

    // 获取当前冷却时间和最大冷却时间的商, 用于界面设计
    public double getCoolDown() {
        return (double) currentCoolDown / coolDown;
    }
}
