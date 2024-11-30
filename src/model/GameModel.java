package model;

import model.bullet.Bullet;
import model.plant.Plant;
import model.zombie.Zombie;

import java.util.*;

public class GameModel {
    private final List<List<Plant>> plants = new ArrayList<>();
    private final List<List<Zombie>> zombies = new ArrayList<>();
    private final List<List<Bullet>> bullets = new ArrayList<>();
    private final List<Boolean> lawnMowers = new ArrayList<>();

    private int sun = 50;
    private final int rows, cols;
    private int width, height;
    private final int updateGap = 20;
    private int state = RUNNING;

    public static int PAUSED = 0, RUNNING = 1;

    public GameModel(int rows, int cols, int width, int height) {
        this.rows = rows;
        this.cols = cols;
        this.width = width;
        this.height = height;

        for (int i = 0; i < rows; ++i) {
            zombies.add(new ArrayList<>());
            plants.add(new ArrayList<>());
            bullets.add(new ArrayList<>());
            lawnMowers.add(true);
            for (int j = 0; j < cols; ++j)
                plants.get(i).add(null);
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == RUNNING)
                    update();
            }
        }, 0, 20);
    }

    private void update() {
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                Plant plant = plants.get(row).get(col);
                if (plant != null)
                    plant.update(this, row, col);
            }
            for (Zombie zombie : zombies.get(row))
                zombie.update(this);
            for (Bullet bullet : bullets.get(row))
                bullet.update(this);
        }
        check();
    }

    private void check() {
        for (int i = 0; i < rows; ++i) {
            List<Zombie> rowZombies = zombies.get(i);
            List<Bullet> rowBullets = bullets.get(i);
            List<Plant> rowPlants = plants.get(i);
            for (int j = 0; j < rowZombies.size(); ++j) {
                Zombie zombie = rowZombies.get(j);
                for (int k = 0; k < rowBullets.size(); ++k) {
                    Bullet bullet = rowBullets.get(k);
                    if (zombie.getX() - bullet.getX() < 10) {
                        zombie.takeDamage(bullet.getDamage());
                        rowBullets.remove(k);
                        --k;
                        if (!zombie.isAlive()) {
                            rowZombies.remove(j);
                            --j;
                        }
                    }
                }
                if (zombie.isAlive()) {
                    int col = zombie.getClosestColumn(this);
                    Plant plant = rowPlants.get(col);
                    if (zombie.getState() == Zombie.State.ADVANCING && plant != null
                            && zombie.getX()-(col+1)*width/cols<10) {
                        zombie.setState(Zombie.State.ATTACKING);
                    }
                    if (zombie.getState() == Zombie.State.ATTACKING) {
                        assert plant != null;
                        plant.takeDamage(updateGap * zombie.getDamage() / 1000);
                        if (!plant.isAlive()) {
                            zombie.setState(Zombie.State.ADVANCING);
                            rowPlants.set(col, null);
                        }
                    }
                }
            }
        }
    }

    public int getState() {
        return state;
    }

    public void pauseGame() {
        state = PAUSED;
    }

    public void continueGame() {
        state = RUNNING;
    }

    public Plant getPlant(int row, int col) {
        return plants.get(row).get(col);
    }

    public void setPlant(int row, int col, Plant plant) {
        plants.get(row).set(col, plant);
    }

    public List<Zombie> getZombies(int row) {
        return zombies.get(row);
    }

    public void addZombie(int row, Zombie zombie) {
        zombies.get(row).add(zombie);
    }

    public List<Bullet> getBullets(int row) {
        return bullets.get(row);
    }

    public void addBullet(int row, Bullet bullet) {
        bullets.get(row).add(bullet);
    }

    public boolean getLawnMowers(int row) {
        return lawnMowers.get(row);
    }

    public int getSun() {
        return sun;
    }

    public void setSun(int sun) {
        this.sun = sun;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getUpdateGap() {
        return updateGap;
    }

    public void display() {
        System.out.println("Plants");
        for (List<Plant> rowPlant : plants)
            System.out.println(rowPlant);
        System.out.println("Zombies\n" + zombies);
        System.out.println("Bullets\n" + bullets);
        System.out.println();
    }
}
