package model.seed;

import model.plant.Plant;
import model.plant.PotatoMine;

public class PotatoMineSeed extends PlantSeed {
    public PotatoMineSeed() {
        super(30000, 15000, 25,
                "images/PlantSeeds/PotatoMineSeed.png");
    }

    @Override
    public Plant getPlant() {
        return new PotatoMine();
    }
}
