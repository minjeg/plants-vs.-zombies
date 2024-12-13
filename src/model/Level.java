package model;

import model.zombie.BasicZombie;
import model.zombie.Zombie;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Level implements Serializable {
    private final int rows, cols;
    private final int initialSun;
    private transient int timer = 0;
    //<毫秒数,Pair<行号,僵尸>>
    private final TreeMap<Long, Pair<Integer, Zombie>> levelZombies = new TreeMap<>();

    public Level(int rows, int cols, int initialSun) {
        this.rows = rows;
        this.cols = cols;
        this.initialSun = initialSun;
    }

    public void addZombie(long time, int row, Zombie zombie) {
        levelZombies.put(time, new Pair<>(row, zombie));
    }

    public void update(GameModel gameModel) {
        timer += gameModel.getUpdateGap();
        Iterator<Map.Entry<Long, Pair<Integer, Zombie>>> iterator = levelZombies.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Pair<Integer, Zombie>> entry = iterator.next();
            if (entry.getKey() * 1000 <= timer) {
                gameModel.getZombies(entry.getValue().first).add(entry.getValue().second);
                iterator.remove();
            }
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

    //创建并保存关卡示例
    public static void main(String[] args) throws IOException {
        Level level = new Level(5, 9, 50);
        level.addZombie(20, 3, new BasicZombie());
        level.addZombie(45, 1, new BasicZombie());
        level.addZombie(70, 2, new BasicZombie());
        level.addZombie(95, 0, new BasicZombie());
        level.addZombie(120, 4, new BasicZombie());
        save(level, "level.lv");
    }

    public static void save(Level level, String name) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(name);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(level);
        objectOutputStream.close();
    }

    public static Level load(String name) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(name);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Level level = (Level) objectInputStream.readObject();
        objectInputStream.close();
        return level;
    }
}
