package model.seed;

import model.plant.Peashooter;
import model.plant.Plant;

public class SunflowerSeed extends PlantSeed {
    public SunflowerSeed() {
        super(7500, 4100, 50, "images/PlantSeeds/SunflowerSeed.png");
    }

    @Override
    public Plant getPlant() {
        return new Peashooter();
    }
}
