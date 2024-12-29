package model.seed;

import model.plant.CherryBomb;
import model.plant.Plant;

public class CherryBombSeed extends PlantSeed {
    public CherryBombSeed() {
        super(50000, 35000, 150,
                "images/PlantSeeds/CherryBombSeed.png");
    }

    @Override
    public Plant getPlant() {
        return new CherryBomb();
    }
}