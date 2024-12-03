package model.seed;

import model.plant.Plant;
import model.plant.WallNut;

public class WallNutSeed extends PlantSeed {
    public WallNutSeed() {
        super(30000, 20000, 50, "images/PlantSeeds/WallNutSeed.png");
    }

    @Override
    public Plant getPlant() {
        return new WallNut();
    }
}
