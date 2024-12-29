package model.seed;

import model.plant.Plant;
import model.plant.Repeater;

public class RepeaterSeed extends PlantSeed {
    public RepeaterSeed() {
        super(7500, 0, 200, "images/PlantSeeds/RepeaterSeed.png");
    }

    @Override
    public Plant getPlant() {
        return new Repeater();
    }
}
