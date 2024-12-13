package model;

import model.bullet.Bullet;
import model.plant.Plant;
import model.zombie.Zombie;
import model.seed.PlantSeed;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameModel {
    private final List<List<Plant>> plants = new ArrayList<>();     // 存储植物信息
    private final List<List<Zombie>> zombies = new ArrayList<>();   // 存储场上僵尸信息
    private final List<List<Bullet>> bullets = new ArrayList<>();   // 存储场上子弹信息
    private final List<Boolean> lawnMowers = new ArrayList<>();     // 存储小推车信息
    private final List<PlantSeed> seedBank = new ArrayList<>();     // 存储植物卡槽信息
    private final List<Sun> suns = new ArrayList<>();               // 存储场上阳光信息

    private int sun;                        // 阳光数量
    private final int rows, cols;           // 行数和列数
    private int width, height;              // 草坪的宽和高
    private int blockWidth, blockHeight;    // 单个格子的宽和高

    private boolean grabShovel = false;     // 是否持有铲子
    private PlantSeed seedInHand = null;    // 正在使用的植物种子, null则代表手上不持有任何种子


    private int updateGap;                  // 数据更新间隔
    private State state = State.RUNNING;    // 关卡状态

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
                suns.add(new Sun(GameModel.this));
            }
        }, 5000, 10000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == State.RUNNING)
                    update();
            }
        }, 0, updateGap);
    }

    /// 更新植物、僵尸、子弹、太阳、种子的数据
    private void update() {
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
        //子弹更新
        Thread bulletThread = new Thread(() -> {
            for (int row = 0; row < rows; ++row) {
                List<Bullet> rowBullets = bullets.get(row);
                for (int index = 0; index < rowBullets.size(); ++index) {
                    Bullet bullet = rowBullets.get(index);
                    if (bullet.update(this, row, index)) {
                        --index;
                    }
                }
            }
        });
        bulletThread.start();
        //太阳更新
        Thread sunThread = new Thread(() -> {
            for (int index = 0; index < suns.size(); ++index) {
                Sun sun = suns.get(index);
                if (sun.update(this, index)) {
                    --index;
                }
            }
        });
        sunThread.start();
        //种子更新
        Thread seedThread = new Thread(() -> {
            for (PlantSeed seed : seedBank)
                seed.update(this);
        });
        seedThread.start();
        //僵尸更新
        for (int row = 0; row < rows; ++row) {
            List<Zombie> rowZombies = zombies.get(row);
            for (int index = 0; index < rowZombies.size(); ++index) {
                Zombie zombie = rowZombies.get(index);
                if (zombie.update(this, row, index)) {
                    --index;
                }
            }
        }
        //保证数据都完成更新
        try {
            plantThread.join();
            bulletThread.join();
            sunThread.join();
            seedThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /// 获取游戏状态
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void pauseGame() {
        state = State.PAUSED;
    }

    public void continueGame() {
        state = State.RUNNING;
    }

    public boolean isGrabShovel() {
        return grabShovel;
    }

    public void setGrabShovel(boolean grabShovel) {
        this.grabShovel = grabShovel;
    }

    public PlantSeed getSeedInHand() {
        return seedInHand;
    }

    public void setSeedInHand(PlantSeed seedInHand) {
        this.seedInHand = seedInHand;
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

    public Boolean hasLawnMower(int row) {
        return lawnMowers.get(row);
    }

    public void setLawnMower(int row, boolean whether) {
        lawnMowers.set(row, whether);
    }

    /// 获取阳光总数
    public int getSun() {
        return sun;
    }

    /// 设置阳光总数
    public void setSun(int sun) {
        this.sun = sun;
    }

    /// 获取阳光列表
    public List<Sun> getSuns() {
        return suns;
    }

    /// 添加阳光
    public void addSun(Sun sun) {
        suns.add(sun);
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

    /// 获取更新间隔的毫秒数
    public int getUpdateGap() {
        return updateGap;
    }

    public int getRow(Point pos) {
        double ret = (pos.y - 60.0) / getBlockHeight();
        if (ret < 0 || ret >= rows) return -1;
        return (int)ret;
    }

    public int getCol(Point pos) {
        double ret = (pos.x - 80.0) / getBlockWidth();
        if (ret < 0 || ret >= cols) return -1;
        return (int)ret;
    }

    public List<PlantSeed> getSeeds() {
        return seedBank;
    }

    public void addSeed(PlantSeed seed) {
        seedBank.add(seed);
    }
}
