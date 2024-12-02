package seed;

import model.plant.Plant;

public abstract class PlantSeed {
    private int coolDown;
    private int currentCoolDown;
    private int sun;
    private Class<Plant> plant;

    public PlantSeed(int coolDown, int currentCoolDown, int sun, Class<Plant> plant) {
        this.coolDown = coolDown;
        this.currentCoolDown = currentCoolDown;
        this.sun = sun;
        this.plant = plant;
    }
}
