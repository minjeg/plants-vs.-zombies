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
    private boolean isFlagWave = false;
    private long currentTime = 0, totalTime = 18000;
    private int waveTotalZombieHealth;

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
    }

    public void update(GameModel gameModel) {
        if (currentTime >= totalTime) {
            currentTime = 0;
            isFlagWave = currentWave % 10 == 9 || totalWave < 10 && currentWave == totalWave - 1;
            if (currentWave > 4 && currentWave < 25) {
                zombieTypes.getFirst().setWeight(zombieTypes.getFirst().getWeight() - 180);
                zombieTypes.get(1).setWeight(zombieTypes.get(1).getWeight() - 150);
            }
            if (isFlagWave)
                totalTime = 55000;
            else if (currentWave % 10 == 8 || currentWave == totalWave - 2)
                totalTime = 45000;
            else
                totalTime = (long) ((25 + Math.random() * 6) * 1000);
            if (currentWave < totalWave) {
                System.out.println("第" + (currentWave + 1) + "波");
                if (isFlagWave) {
                    System.out.println("一大波僵尸正在接近！");
                    if (currentWave == totalWave - 1)
                        System.out.println("最后一波！");
                }
                new Timer().schedule(new TimerTask() {
                    private List<Zombie> zombies;
                    private int index = 0;

                    @Override
                    public void run() {
                        if (zombies == null) {
                            zombies = decideZombies();
                            waveTotalZombieHealth = 0;
                            for (Zombie zombie : zombies)
                                waveTotalZombieHealth += zombie.getHealth();
                        } else if (index == zombies.size()) {
                            ++currentWave;
                            this.cancel();
                        } else {
                            gameModel.addZombie(decideRow(), zombies.get(index));
                            ++index;
                        }
                    }
                }, 0, 500);
            } else {
                if (gameModel.getTotalZombieHealth() == 0)
                    gameModel.setState(GameModel.State.WIN);
            }
        } else {
            if (currentTime > 4000 && currentWave != 0 && 2 * gameModel.getTotalZombieHealth() <= waveTotalZombieHealth && totalTime - currentTime > 2000)
                currentTime = totalTime - 2000;
            currentTime += gameModel.getUpdateGap();
        }
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
    private List<Zombie> decideZombies() {
        int levelSum = 0;
        int zombieTypeNumber = zombieTypes.size();
        List<Zombie> zombies = new ArrayList<>(getLevelUpperLimit());
        int i;
        if (isFlagWave) {
            levelSum = 1 + Math.min(getOriginalLevelUpperLimit(), 8);
            for (i = 1; i < levelSum; ++i)
                zombies.add(new BasicZombie());
        }
        while (levelSum < getLevelUpperLimit()) {
            List<Integer> allowedZombieTypes = new ArrayList<>(zombieTypeNumber);
            List<Integer> zombieWeight = new ArrayList<>(zombieTypeNumber);
            for (i = 1; i < zombieTypeNumber; ++i) {
                ZombieType zombieType = zombieTypes.get(i);
                if (levelSum + zombieType.getLevel() <= getLevelUpperLimit()) {
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

    public int getTotalWave() {
        return totalWave;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    /// 获得当前波次的原始级别上限
    private int getOriginalLevelUpperLimit() {
        int number;
        if (currentWave < 20)
            number = currentWave / 3 + 1;
        else
            number = (int) (0.4 * currentWave + 1);
        return number;
    }

    /// 获取当前波次的级别上限
    private int getLevelUpperLimit() {
        int number = getOriginalLevelUpperLimit();
        if (isFlagWave)
            number = (int) (number * 2.5);
        return number;
    }
}
