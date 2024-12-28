package model;

import model.zombie.BasicZombie;
import model.zombie.BucketheadZombie;
import model.zombie.ConeheadZombie;
import model.zombie.Zombie;

import java.io.Serializable;
import java.util.*;

public class Level implements Serializable {
    private final int rows = 5, cols = 9;
    private final int initialSun;
    private int currentWave = 0;
    private final int totalWave;
    private long currentTime = 0, totalTime = 18000;
    private int currentWaveTotalZombieHealth, nextWaveTotalZombieHealth;
    private List<Zombie> nextWaveZombies;

    private final List<Double> rowWeight;
    private final List<Integer> lastPicked;
    private final List<Integer> secondLastPicked;

    private final List<ZombieType> zombieTypes = new ArrayList<>(3);

    {
        //普通僵尸
        zombieTypes.add(new ZombieType(4000, 1, 1) {
            @Override
            public Zombie getZombie() {
                return new BasicZombie();
            }
        });
        //路障僵尸
        zombieTypes.add(new ZombieType(4000, 1, 2) {
            @Override
            public Zombie getZombie() {
                return new ConeheadZombie();
            }
        });
        //铁桶僵尸
        zombieTypes.add(new ZombieType(3000, 1, 4) {
            @Override
            public Zombie getZombie() {
                return new BucketheadZombie();
            }
        });
    }

    public Level(int initialSun, int totalWave) {
        this.initialSun = initialSun;
        this.totalWave = totalWave;
        rowWeight = new ArrayList<>(rows);
        lastPicked = new ArrayList<>(rows);
        secondLastPicked = new ArrayList<>(rows);
        for (int row = 0; row < rows; ++row) {
            rowWeight.add(0.2);
            lastPicked.add(0);
            secondLastPicked.add(0);
        }
        nextWaveZombies = decideZombies(currentWave);
        nextWaveTotalZombieHealth = 0;
        for (Zombie zombie : nextWaveZombies)
            nextWaveTotalZombieHealth += zombie.getHealth();
    }

    public void update(GameModel gameModel) {
        if (currentWave != 0 && gameModel.getTotalZombieHealth() == 0) {
            gameModel.setState(GameModel.State.WIN);
            return;
        }
        if (currentTime >= totalTime) {
            currentTime -= totalTime;
            if (currentWave > 4 && currentWave < 25) {
                zombieTypes.getFirst().setWeight(zombieTypes.getFirst().getWeight() - 180);
                zombieTypes.get(1).setWeight(zombieTypes.get(1).getWeight() - 150);
            }
            if (isFlagWave(currentWave))
                totalTime = 55000;
            else if (currentWave % 10 == 8 || currentWave == totalWave - 2)
                totalTime = 45000;
            else
                totalTime = (long) ((25 + Math.random() * 6) * 1000);
            if (currentWave < totalWave) {
                System.out.println("第" + (currentWave + 1) + "波");
                if (isFlagWave(currentWave)) {
                    System.out.println("一大波僵尸正在接近！");
                    if (currentWave == totalWave - 1)
                        System.out.println("最后一波！");
                }
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    private List<Zombie> zombies;
                    private int index = 0;

                    @Override
                    public void run() {
                        if (zombies == null) {
                            zombies = nextWaveZombies;
                            currentWaveTotalZombieHealth = nextWaveTotalZombieHealth;
                            new Thread(() -> {
                                nextWaveZombies = decideZombies(currentWave + 1);
                                nextWaveTotalZombieHealth = 0;
                                for (Zombie zombie : nextWaveZombies)
                                    nextWaveTotalZombieHealth += zombie.getHealth();
                            }).start();
                        } else if (index == zombies.size()) {
                            ++currentWave;
                            this.cancel();
                        } else {
                            gameModel.addZombie(decideRow(), zombies.get(index));
                            ++index;
                        }
                    }
                }, 0, 500);
            }
        } else {
            if (currentTime > 4000 && currentWave != 0 && 2 * gameModel.getTotalZombieHealth() <= currentWaveTotalZombieHealth && totalTime - currentTime > 2000)
                currentTime = totalTime - 2000;
            currentTime += gameModel.getUpdateGap();
        }
    }

    /// 确定僵尸出现的行
    private int decideRow() {
        List<Double> pLast = new ArrayList<>(5);
        List<Double> pSecondLast = new ArrayList<>(5);
        List<Double> smoothWeight = new ArrayList<>(5);
        int row;
        for (row = 0; row < rows; ++row) {
            pLast.add((6 * lastPicked.get(row) * rowWeight.get(row) + 6 * rowWeight.get(row) - 3) / 4);
            pSecondLast.add((secondLastPicked.get(row) * rowWeight.get(row) + rowWeight.get(row) - 1) / 4);
            if (row == 0)
                smoothWeight.add(rowWeight.get(row) * Math.min(Math.max(pLast.get(row) + pSecondLast.get(row), 0.01), 100));
            else
                smoothWeight.add(smoothWeight.get(row - 1) + rowWeight.get(row) * Math.min(Math.max(pLast.get(row) + pSecondLast.get(row), 0.01), 100));
        }
        for (row = 0; row < rows; ++row) {
            secondLastPicked.set(row, secondLastPicked.get(row) + 1);
            lastPicked.set(row, lastPicked.get(row) + 1);
        }
        double rand = Math.random() * smoothWeight.getLast();
        for (row = 0; row < rows; ++row)
            if (rand < smoothWeight.get(row))
                break;
        secondLastPicked.set(row, lastPicked.get(row));
        lastPicked.set(row, 0);
        return row;
    }

    /// 确定一波的僵尸
    private List<Zombie> decideZombies(int wave) {
        int levelSum = 0;
        int zombieTypeNumber = zombieTypes.size();
        List<Zombie> zombies = new ArrayList<>(getLevelUpperLimit(wave));
        int i;
        if (isFlagWave(wave)) {
            levelSum = 1 + Math.min(getOriginalLevelUpperLimit(wave), 8);
            for (i = 1; i < levelSum; ++i)
                zombies.add(new BasicZombie());
        }
        while (levelSum < getLevelUpperLimit(wave)) {
            List<Integer> allowedZombieTypes = new ArrayList<>(zombieTypeNumber);
            List<Integer> zombieWeight = new ArrayList<>(zombieTypeNumber);
            for (i = 1; i < zombieTypeNumber; ++i) {
                ZombieType zombieType = zombieTypes.get(i);
                if (levelSum + zombieType.getLevel() <= getLevelUpperLimit(wave)) {
                    allowedZombieTypes.add(i);
                    if (zombieWeight.isEmpty())
                        zombieWeight.add(zombieType.getWeight());
                    else
                        zombieWeight.add(zombieType.getWeight() + zombieWeight.getLast());
                }
            }
            if (allowedZombieTypes.isEmpty()) {
                ZombieType zombieType = zombieTypes.getFirst();
                zombies.add(zombieType.getZombie());
                levelSum += zombieType.getLevel();
                continue;
            }
            int rand = (int) (Math.random() * zombieWeight.getLast());
            for (i = 0; i < zombieWeight.size(); ++i) {
                if (rand < zombieWeight.get(i))
                    break;
            }
            ZombieType zombieType = zombieTypes.get(allowedZombieTypes.get(i));
            zombies.add(zombieType.getZombie());
            levelSum += zombieType.getLevel();
        }
        return zombies;
    }

    /// 获得波次的原始级别上限
    private int getOriginalLevelUpperLimit(int wave) {
        int number;
        if (wave < 20)
            number = wave / 3 + 1;
        else
            number = (int) (0.4 * wave + 1);
        return number;
    }

    /// 获取波次的级别上限
    private int getLevelUpperLimit(int wave) {
        int number = getOriginalLevelUpperLimit(wave);
        if (isFlagWave(wave))
            number = (int) (number * 2.5);
        return number;
    }

    private boolean isFlagWave(int wave) {
        return wave % 10 == 9 || wave < 10 && wave == totalWave - 1;
    }

    public int getInitialSun() {
        return initialSun;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getTotalWave() {
        return totalWave;
    }

    public int getCurrentWave() {
        return currentWave;
    }
}
