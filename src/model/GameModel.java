package model;

import model.bullet.Bullet;
import model.plant.Plant;
import model.seed.*;
import model.zombie.Zombie;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameModel implements Serializable {
    private final List<List<Plant>> plants = new ArrayList<>();
    private final List<List<Zombie>> zombies = new ArrayList<>();
    private final List<List<Bullet>> bullets = new ArrayList<>();
    private final List<LawnMower> lawnMowers = new ArrayList<>();
    private final List<PlantSeed> seedBank = new ArrayList<>();
    private final List<Sun> suns = new ArrayList<>();

    {
        seedBank.add(new PeashooterSeed());
        seedBank.add(new SunflowerSeed());
        seedBank.add(new CherryBombSeed());
        seedBank.add(new WallNutSeed());
        seedBank.add(new PotatoMineSeed());
        seedBank.add(new ChomperSeed());
        seedBank.add(new RepeaterSeed());
    }

    private final Level level;
    private int sun;
    private int fallenSunNumber = 0;
    private long sunTimer = (long) (4250 + Math.random() * 2740);
    private int totalZombieHealth;
    private int numOfZombies;
    private final int updateGap;
    private State state = State.READY;

    private final int rows, cols;
    private final int width, height;
    private final int blockWidth, blockHeight;

    private boolean grabShovel = false;
    private PlantSeed seedInHand = null;

    public enum State {READY, PAUSED, RUNNING, WIN, LOSE}

    public GameModel(int width, int height, int updateGap, Level level) {
        this.width = width;
        this.height = height;
        this.updateGap = updateGap;
        this.level = level;
        this.rows = level.getRows();
        this.cols = level.getCols();
        this.sun = level.getInitialSun();
        this.blockWidth = width / cols;
        this.blockHeight = height / rows;

        for (int i = 0; i < rows; ++i) {
            zombies.add(new CopyOnWriteArrayList<>());
            plants.add(new CopyOnWriteArrayList<>());
            bullets.add(new CopyOnWriteArrayList<>());
            lawnMowers.add(new LawnMower());
            for (int j = 0; j < cols; ++j)
                plants.get(i).add(null);
        }
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (state == State.RUNNING)
                    update();
                else if (state == State.WIN || state == State.LOSE)
                    this.cancel();
            }
        }, updateGap, updateGap);
    }

    public static void save(GameModel gameModel, String name) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(name);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(gameModel);
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameModel load(String name) {
        try {
            FileInputStream fileInputStream = new FileInputStream(name);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            GameModel gameModel = (GameModel) objectInputStream.readObject();
            objectInputStream.close();
            return gameModel;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /// 更新植物、僵尸、子弹、太阳、种子的数据
    private synchronized void update() {
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
        //种子更新
        Thread seedThread = new Thread(() -> {
            for (PlantSeed seed : seedBank)
                seed.update(this);
        });
        seedThread.start();
        //割草机更新
        Thread lawnMowerThread = new Thread(() -> {
            for (int row = 0; row < rows; ++row) {
                LawnMower lawnMower = lawnMowers.get(row);
                if (lawnMower != null)
                    lawnMower.update(this, row);
            }
        });
        lawnMowerThread.start();
        plantThread.start();
        //子弹更新
        Thread bulletThread = new Thread(() -> {
            for (int row = 0; row < rows; ++row) {
                List<Bullet> rowBullets = bullets.get(row);
                for (int index = 0; index < rowBullets.size(); ++index) {
                    Bullet bullet = rowBullets.get(index);
                    if (bullet.update(this, row, index))
                        --index;
                }
            }
        });
        bulletThread.start();
        //太阳更新
        Thread sunThread = new Thread(() -> {
            sunTimer -= updateGap;
            if (state == State.RUNNING && sunTimer <= 0) {
                addSun(new Sun(GameModel.this));
                ++fallenSunNumber;
                sunTimer = (long) (Math.min(100L * fallenSunNumber + 4250, 9500) + Math.random() * 2740);
            }
            for (int index = 0; index < suns.size(); ++index) {
                Sun sun = suns.get(index);
                if (sun.update(this, index))
                    --index;
            }
        });
        sunThread.start();
        //僵尸更新
        numOfZombies = 0;
        totalZombieHealth = 0;
        for (int row = 0; row < rows; ++row) {
            List<Zombie> rowZombies = zombies.get(row);
            for (int index = 0; index < rowZombies.size(); ++index) {
                Zombie zombie = rowZombies.get(index);
                if (zombie.update(this, row, index))
                    --index;
                else {
                    ++numOfZombies;
                    totalZombieHealth += zombie.getHealth();
                }
            }
        }
        //关卡更新
        level.update(this);
        //保证数据都完成更新
        try {
            plantThread.join();
            bulletThread.join();
            sunThread.join();
            seedThread.join();
            lawnMowerThread.join();
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
        if(state == State.LOSE || state == State.WIN)
            save(null, "gamesave/save");
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
        if (col >= cols)
            return null;
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

    public int getTotalZombieHealth() {
        return totalZombieHealth;
    }

    public List<Bullet> getBullets(int row) {
        return bullets.get(row);
    }

    public void addBullet(int row, Bullet bullet) {
        bullets.get(row).add(bullet);
    }

    public LawnMower getLawnMower(int row) {
        return lawnMowers.get(row);
    }

    public void setLawnMower(int row, LawnMower lawnMower) {
        lawnMowers.set(row, lawnMower);
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

    public int getHeight() {
        return height;
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
        return (int) ret;
    }

    public int getCol(Point pos) {
        double ret = (pos.x - 80.0) / getBlockWidth();
        if (ret < 0 || ret >= cols) return -1;
        return (int) ret;
    }

    public List<PlantSeed> getSeeds() {
        return seedBank;
    }

    public int getNumOfZombies() {
        return numOfZombies;
    }

    public Level getLevel() {
        return level;
    }
}
