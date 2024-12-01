package model;

import model.bullet.Bullet;
import model.plant.Peashooter;
import model.plant.Plant;
import model.zombie.BasicZombie;
import model.zombie.Zombie;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestComponent extends JComponent {
    private final int updateGap = 20;
    private final GameModel gameModel = new GameModel(5, 9, 800, 600,
            updateGap, 1000);

    public TestComponent() {
        for (int row = 0; row < gameModel.getRows(); ++row) {
            gameModel.setPlant(row, 4, new Peashooter());
        }

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            private int row = 0;

            @Override
            public void run() {
                gameModel.addZombie(row, new BasicZombie(gameModel));
                row = (row + 1) % gameModel.getRows();
            }
        }, 0, 2000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (gameModel.getState() == GameModel.State.RUNNING)
                    repaint();
                else if (gameModel.getState() == GameModel.State.WIN) {
                    showMessageDialog("You win!");
                    this.cancel();
                } else if (gameModel.getState() == GameModel.State.LOSE) {
                    showMessageDialog("You lose!");
                    this.cancel();
                }
            }
        }, 0, updateGap);
    }

    private void showMessageDialog(Object message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int blockWidth = gameModel.getBlockWidth();
        int blockHeight = gameModel.getBlockHeight();

        g.setColor(Color.RED);
        for (int row = 1; row < gameModel.getRows(); ++row) {
            g.drawLine(0, row * blockHeight, gameModel.getWidth(), row * blockHeight);
        }
        for (int col = 1; col < gameModel.getCols(); ++col) {
            g.drawLine(col * blockWidth, 0, col * blockWidth, gameModel.getHeight());
        }

        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getCols(); col++) {
                Plant plant = gameModel.getPlant(row, col);
                if (plant == null)
                    continue;
                Image image = new ImageIcon(plant.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) ((col + 0.5) * blockWidth - image.getWidth(null) / 2.0),
                        (int) ((row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
            }
        }

        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Zombie> zombies = gameModel.getZombies(row);
            for (Zombie zombie : zombies) {
                Image image = new ImageIcon(zombie.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) (zombie.getX() - image.getWidth(null) / 2.0),
                        (int) ((row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
            }
        }

        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Bullet> bullets = gameModel.getBullets(row);
            for (Bullet bullet : bullets) {
                Image image = new ImageIcon(bullet.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) (bullet.getX() - image.getWidth(null) / 2.0),
                        (int) ((row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
            }
        }
    }
}
