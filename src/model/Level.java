package model;

import model.zombie.BasicZombie;

import java.io.Serializable;
import java.util.*;

public class Level implements Serializable {
    private final int rows = 5, cols = 9;
    private final int initialSun;
    private int currentWave = 0;
    private final int totalWave;
    private long time = 18000;

    private final List<Double> rowWeights;
    private final List<Integer> lastPicked;
    private final List<Integer> secondLastPicked;

    public Level(int initialSun, int totalWave) {
        this.initialSun = initialSun;
        this.totalWave = totalWave;
        rowWeights = new ArrayList<>(rows);
        lastPicked = new ArrayList<>(rows);
        secondLastPicked = new ArrayList<>(rows);
        for (int row = 0; row < rows; ++row) {
            rowWeights.add(0.2);
            lastPicked.add(0);
            secondLastPicked.add(0);
        }
    }

    public void update(GameModel gameModel) {
        if (time <= 0) {
            if (currentWave < totalWave) {
                new Timer().schedule(new TimerTask() {
                    private int count = 0;

                    @Override
                    public void run() {
                        if (count < getMaxNumber()) {
                            gameModel.addZombie(decideRow(), new BasicZombie());
                            ++count;
                        } else {
                            ++currentWave;
                            this.cancel();
                        }
                    }
                }, 0, 2000);
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
            pLast.add((6 * lastPicked.get(row) * rowWeights.get(row) + 6 * rowWeights.get(row) - 3) / 4);
            pSecondLast.add((secondLastPicked.get(row) * rowWeights.get(row) + rowWeights.get(row) - 1) / 4);
            if (row == 0)
                smoothWeight.add(rowWeights.get(row) * Math.min(Math.max(pLast.get(row) + pSecondLast.get(row), 0.01), 100));
            else
                smoothWeight.add(smoothWeight.get(row - 1) + rowWeights.get(row) * Math.min(Math.max(pLast.get(row) + pSecondLast.get(row), 0.01), 100));
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

    private int getMaxNumber() {
        int number = currentWave / 3 + 1;
        if (currentWave % 10 == 9)
            number = (int) (number * 2.5);
        return number;
    }
}
