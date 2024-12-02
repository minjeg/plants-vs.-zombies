package seed;

import model.plant.Peashooter;
import model.plant.Plant;

import java.awt.*;

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
