package model.seed;

import model.plant.Plant;
import model.plant.Sunflower;

public class SunflowerSeed extends PlantSeed {
    public SunflowerSeed() {
        super(7500, 0, 50, "images/PlantSeeds/SunflowerSeed.png");
    }

    @Override
    public Plant getPlant() {
        return new Sunflower();
    }
}
