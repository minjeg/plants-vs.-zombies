package model;

import model.bullet.Bullet;
import model.plant.Plant;
import model.zombie.Zombie;

import java.util.*;

public class GameModel {
    private final List<List<Plant>> plants=new ArrayList<>();
    private final List<List<Zombie>>zombies=new ArrayList<>();
    private final List<List<Bullet>>bullets=new ArrayList<>();
    private final List<Boolean> lawnMowers=new ArrayList<>();

    private int sun=50;
    private final int rows,cols;
    private int width,height;
    private int state=RUNNING;

    public static int PAUSED=0,RUNNING=1;

    public GameModel(int rows,int cols){
        this.rows=rows;
        this.cols=cols;
        for(int i=0;i<rows;++i){
            zombies.add(new ArrayList<>());
            plants.add(new ArrayList<>());
            bullets.add(new ArrayList<>());
            lawnMowers.add(true);
            for(int j=0;j<cols;++j)
                plants.get(i).add(null);
        }
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(state==RUNNING)
                    update();
            }
        },0,20);
    }

    private void update(){
        for(List<Plant>rowPlants:plants)
            for(Plant plant:rowPlants)
                if(plant!=null)
                    plant.update(this);
        for(List<Zombie>rowZombies:zombies)
            for(Zombie zombie:rowZombies)
                zombie.update(this);
        for(List<Bullet>rowBullets:bullets)
            for(Bullet bullet:rowBullets)
                bullet.update(this);
        check();
    }

    private void check() {

    }

    public int getState() {
        return state;
    }

    public void pauseGame(){
        state=PAUSED;
    }

    public void continueGame(){
        state=RUNNING;
    }

    public boolean addPlant(int row, int col, Plant plant){
        if(row <0|| row >=rows|| col <0|| col >=cols||plants.get(row).get(col)!=null)
            return false;
        plants.get(row).set(col,plant);
        return true;
    }

    public Plant getPlant(int row,int col){
        if(row<0||row>=rows||col<0||col>=cols)
            return null;
        return plants.get(row).get(col);
    }

    public boolean addZombie(int row,Zombie zombie){
        if(row<0||row>=rows)
            return false;
        zombies.get(row).add(zombie);
        return true;
    }

    public List<Zombie> getZombies(int row){
        if(row<0||row>=rows)
            return new ArrayList<>();
        return zombies.get(row);
    }

    public List<Bullet> getBullets(int row) {
        if(row<0||row>=rows)
            return new ArrayList<>();
        return bullets.get(row);
    }

    public boolean getLawnMowers(int row){
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
}
