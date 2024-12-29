package model.seed;

import model.plant.Chomper;
import model.plant.Plant;

public class ChomperSeed extends PlantSeed {
    public ChomperSeed() {
        super(7500, 0, 150,
                "images/PlantSeeds/ChomperSeed.png");
    }

    @Override
    public Plant getPlant() {
        return new Chomper();
    }
}
