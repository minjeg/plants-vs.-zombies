package model;

import model.bullet.Bullet;
import model.plant.Plant;
import model.zombie.Zombie;
import model.seed.PlantSeed;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GameModel implements Serializable {
    private final List<List<Plant>> plants = new ArrayList<>();
    private final List<List<Zombie>> zombies = new ArrayList<>();
    private final List<List<Bullet>> bullets = new ArrayList<>();
    private final List<LawnMower> lawnMowers = new ArrayList<>();
    private final List<PlantSeed> seedBank = new ArrayList<>();
    private final List<Sun> suns = new ArrayList<>();

    private final Level level;
    private int sun;
    private int fallenSunNumber = 0;
    private final int rows, cols;
    private int width, height;
    private int blockWidth, blockHeight;
    private final int updateGap;
    private State state = State.RUNNING;

    private boolean grabShovel = false;
    private PlantSeed seedInHand = null;

    public enum State {PAUSED, RUNNING, WIN, LOSE}

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
            zombies.add(new ArrayList<>());
            plants.add(new ArrayList<>());
            bullets.add(new ArrayList<>());
            lawnMowers.add(new LawnMower(this));
            for (int j = 0; j < cols; ++j)
                plants.get(i).add(null);
        }
        Timer timer = new Timer();

        //阳光自然出现线程
        new Thread(() -> {
            while (true) {
                if (state == State.RUNNING) {
                    long gap = (long) (Math.min(100L * fallenSunNumber + 4250, 9500) + Math.random() * 2740);
                    System.out.println("Sun" + (++fallenSunNumber) + " wait " + gap + "ms");
                    try {
                        Thread.sleep(gap);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    addSun(new Sun(GameModel.this));
                } else if (state == State.WIN || state == State.LOSE) {
                    break;
                }
            }
        }).start();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                long t = System.nanoTime();
                if (state == State.RUNNING)
                    update();
                else if (state == State.WIN || state == State.LOSE)
                    this.cancel();
//                t = System.nanoTime() - t;
//                System.out.println("model:" + t + "ns");
            }
        }, 0, updateGap);
    }

    public static void save(GameModel gameModel, String name) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(name);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(gameModel);
        objectOutputStream.close();
    }

    public static GameModel load(String name) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(name);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        GameModel gameModel = (GameModel) objectInputStream.readObject();
        objectInputStream.close();
        return gameModel;
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
        //割草机更新
        Thread lawnMowerThread = new Thread(() -> {
            for (int row = 0; row < rows; ++row) {
                LawnMower lawnMower = lawnMowers.get(row);
                if (lawnMower != null)
                    lawnMower.update(this, row);
            }
        });
        lawnMowerThread.start();
        //关卡更新
        Thread levelThread = new Thread(() -> level.update(this));
        levelThread.start();
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
            lawnMowerThread.join();
            levelThread.join();
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

    public boolean hasNoZombie() {
        boolean result = true;
        for (int row = 0; row < rows; ++row) {
            if (!getZombies(row).isEmpty()) {
                result = false;
                break;
            }
        }
        return result;
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

    public void addSeed(PlantSeed seed) {
        seedBank.add(seed);
    }
}
