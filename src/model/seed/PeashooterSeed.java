package model.seed;

import model.plant.Peashooter;
import model.plant.Plant;

public class PeashooterSeed extends PlantSeed {
    public PeashooterSeed() {
        super(7500, 0, 100,
                "images/PlantSeeds/PeashooterSeed.png");
    }

    @Override
    public Plant getPlant() {
        return new Peashooter();
    }
}
