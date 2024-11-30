package model;

import model.bullet.Bullet;
import model.plant.PeaShooter;
import model.zombie.BasicZombie;
import model.zombie.Zombie;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestComponent extends JComponent {
    GameModel gameModel=new GameModel(5,9,800,600);

    public TestComponent(){
        gameModel.setPlant(0,0,new PeaShooter());
        gameModel.addZombie(0,new BasicZombie(gameModel));
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        },0,20);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int blockWidth=gameModel.getWidth()/ gameModel.getCols();
        int blockHeight=gameModel.getHeight()/ gameModel.getRows();
        g.setColor(Color.RED);
        for(int row = 1; row <gameModel.getRows(); ++row){
            g.drawLine(0,row*blockHeight,gameModel.getWidth(),row*blockHeight);
        }
        for(int col = 1; col <gameModel.getCols(); ++col){
            g.drawLine(col*blockWidth,0,col*blockWidth,gameModel.getHeight());
        }
        g.setColor(Color.GREEN);
        for(int row = 0; row <gameModel.getRows(); ++row){
            for(int col = 0; col <gameModel.getCols(); ++col){
                if(gameModel.getPlant(row,col)==null)
                    continue;
                g.fillRect(col*blockWidth,
                        row*blockHeight,
                        blockWidth,
                        blockHeight
                );
            }
        }
        g.setColor(Color.BLACK);
        for(int row=0;row< gameModel.getRows();++row){
            List<Zombie>rowZombies=gameModel.getZombies(row);
            for(Zombie zombie:rowZombies){
                g.fillRect(zombie.getX()-blockWidth/2,
                        row*blockHeight,
                        blockWidth,
                        blockHeight
                );
            }
        }
        g.setColor(Color.YELLOW);
        for(int row=0;row<gameModel.getRows();++row){
            List<Bullet>rowBullet=gameModel.getBullets(row);
            for(Bullet bullet:rowBullet){
                g.fillOval(bullet.getX()-blockWidth/4,
                        (int) ((row+0.25)*blockHeight),
                        blockWidth/2,
                        blockHeight/2
                );
            }
        }
    }
}
