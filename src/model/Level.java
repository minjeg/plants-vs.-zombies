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
    private long time = 18000;

    private final List<Double> rowWeight;
    private final List<Integer> lastPicked;
    private final List<Integer> secondLastPicked;

    private final int zombieTypeNumber = 3;
    private final List<ZombieType> zombieTypes = new ArrayList<>(zombieTypeNumber);

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
        if (time <= 0) {
            if (currentWave < totalWave) {
                System.out.println("第" + (currentWave + 1) + "波");
                if (currentWave % 10 == 9 || totalWave < 10 && currentWave == totalWave - 1) {
                    System.out.println("一大波僵尸正在接近！");
                    if (currentWave == totalWave - 1)
                        System.out.println("最后一波！");
                }
                new Timer().schedule(new TimerTask() {
                    private final int[] levelSum = new int[1];

                    @Override
                    public void run() {
                        if (levelSum[0] < getLevelUpperLimit()) {
                            gameModel.addZombie(decideRow(), decideZombie(levelSum));
                        } else {
                            ++currentWave;
                            if (currentWave > 4 && currentWave < 25) {
                                zombieTypes.getFirst().setWeight(zombieTypes.getFirst().getWeight() - 180);
                                zombieTypes.get(1).setWeight(zombieTypes.get(1).getWeight() - 150);
                            }
                            this.cancel();
                        }
                    }
                }, 0, 1000);
                time = (long) ((25 + Math.random() * 6) * 1000);
            } else {
                if (gameModel.hasNoZombie())
                    gameModel.setState(GameModel.State.WIN);
            }
        } else {
            if (currentWave != 0 && gameModel.hasNoZombie() && time > 2000)
                time = 2000;
            time -= gameModel.getUpdateGap();
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

    /// 确定出现的僵尸类型
    private Zombie decideZombie(int[] levelSum) {
        List<ZombieType> allowedZombieTypes = new ArrayList<>(zombieTypeNumber);
        List<Integer> zombieWeight = new ArrayList<>(zombieTypeNumber);
        for (int i = 0; i < zombieTypeNumber; ++i) {
            ZombieType zombieType = zombieTypes.get(i);
            if (levelSum[0] + zombieType.getLevel() <= getLevelUpperLimit()) {
                allowedZombieTypes.add(zombieType);
                if (zombieWeight.isEmpty())
                    zombieWeight.add(zombieType.getWeight());
                else
                    zombieWeight.add(zombieType.getWeight() + zombieWeight.getLast());
            }
        }
        int rand = (int) (Math.random() * zombieWeight.getLast());
        int i;
        for (i = 0; i < zombieWeight.size(); ++i) {
            if (rand < zombieWeight.get(i))
                break;
        }
        levelSum[0] += allowedZombieTypes.get(i).getLevel();
        return allowedZombieTypes.get(i).getZombie();
    }

    public int getTotalWave() {
        return totalWave;
    }

    /// 获取波次级别上限
    private int getLevelUpperLimit() {
        int number = currentWave / 3 + 1;
        if (currentWave % 10 == 9 || totalWave < 10 && currentWave == totalWave - 1)
            number = (int) (number * 2.5);
        return number;
    }
}
