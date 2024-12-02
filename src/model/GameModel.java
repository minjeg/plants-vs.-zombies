package model;

import model.bullet.Bullet;
import model.plant.Plant;
import model.zombie.Zombie;
import model.seed.PlantSeed;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameModel {
    private final List<List<Plant>> plants = new ArrayList<>();
    private final List<List<Zombie>> zombies = new ArrayList<>();
    private final List<List<Bullet>> bullets = new ArrayList<>();
    private final List<Boolean> lawnMowers = new ArrayList<>();
    private final List<PlantSeed> seedBank = new ArrayList<>();

    private int sun;
    private final int rows, cols;
    private int width, height;
    private int blockWidth, blockHeight;
    private int updateGap;
    private State state = State.RUNNING;

    public enum State {PAUSED, RUNNING, WIN, LOSE}

    public GameModel(int rows, int cols, int width, int height, int updateGap, int sun) {
        this.rows = rows;
        this.cols = cols;
        this.width = width;
        this.height = height;
        this.updateGap = updateGap;
        this.sun = sun;
        this.blockWidth = width / cols;
        this.blockHeight = height / rows;

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
                if (state == State.RUNNING)
                    update();
            }
        }, 0, updateGap);
    }

    /// 更新植物、僵尸和子弹的数据
    private void update() {
        check();

        //植物更新
        Thread plantThread = new Thread(() -> {
            for (int row = 0; row < rows; ++row) {
                for (int col = 0; col < cols; ++col) {
                    Plant plant = plants.get(row).get(col);
                    if (plant != null)
                        plant.update(this, row, col);
                }
            }
        });
        plantThread.start();
        //僵尸更新
        Thread zombieThread = new Thread(() -> {
            for (int row = 0; row < rows; ++row) {
                List<Zombie> rowZombies = zombies.get(row);
                for (Zombie zombie : rowZombies)
                    zombie.update(this);
            }
        });
        zombieThread.start();
        //子弹更新
        Thread bulletThread = new Thread(() -> {
            for (int row = 0; row < rows; ++row) {
                List<Bullet> rowBullets = bullets.get(row);
                for (int i = 0; i < rowBullets.size(); ++i) {
                    Bullet bullet = rowBullets.get(i);
                    bullet.update(this);
                    if (bullet.getX() > width) {
                        rowBullets.remove(i);
                        --i;
                    }
                }
            }
        });
        bulletThread.start();
        try {
            plantThread.join();
            zombieThread.join();
            bulletThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for(var seed : seedBank)
            seed.update(this);

//        check();
    }

    /// 检查子弹击中僵尸、僵尸攻击植物、更新状态
    private void check() {
        for (int i = 0; i < rows; ++i) {
            List<Zombie> rowZombies = zombies.get(i);
            List<Bullet> rowBullets = bullets.get(i);
            List<Plant> rowPlants = plants.get(i);
            for (int j = 0; j < rowZombies.size(); ++j) {
                Zombie zombie = rowZombies.get(j);
                for (int k = 0; k < rowBullets.size(); ++k) {
                    Bullet bullet = rowBullets.get(k);
                    if (Math.abs(zombie.getX() - bullet.getX()) < 10) {
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
                    if (col < 0) {
                        state = State.LOSE;
                        return;
                    }
                    Plant plant = rowPlants.get(col);
                    if (zombie.getState() == Zombie.State.WALKING && plant != null
                            && Math.abs(zombie.getX() - (col + 0.5) * blockWidth) < 10) {
                        zombie.setState(Zombie.State.EATING);
                    } else if (zombie.getState() == Zombie.State.EATING) {
                        assert plant != null;
                        plant.takeDamage(updateGap * zombie.getDamage() / 1000);
                        if (!plant.isAlive()) {
                            zombie.setState(Zombie.State.WALKING);
                            rowPlants.set(col, null);
                        }
                    }
                }
            }
        }
    }

    public State getState() {
        return state;
    }

    public void pauseGame() {
        state = State.PAUSED;
    }

    public void continueGame() {
        state = State.RUNNING;
    }

    public Plant getPlant(int row, int col) {
        return plants.get(row).get(col);
    }

    public void setPlant(int row, int col, Plant plant) {
        plants.get(row).set(col, plant);
//        sun -= plant.getCost();
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

    public int getBlockWidth() {
        return blockWidth;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public int getUpdateGap() {
        return updateGap;
    }

    public int getRow(Point pos) {
        int ret = (pos.y - 60) / getBlockHeight();
        if(ret < 0 || ret > rows) return -1;
        return ret;
    }

    public int getCol(Point pos) {
        int ret = (pos.x - 80) / getBlockWidth();
        if(ret < 0 || ret > cols) return -1;
        return ret;
    }

    public List<PlantSeed> getSeeds() {
        return seedBank;
    }

    public void addSeed(PlantSeed seed) {
        seedBank.add(seed);
    }
}
