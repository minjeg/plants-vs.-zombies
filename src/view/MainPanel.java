package view;

import model.GameModel;
import model.bullet.Bullet;
import model.plant.Peashooter;
import model.plant.Plant;
import model.zombie.BasicZombie;
import model.zombie.Zombie;
import seed.PeashooterSeed;
import seed.PlantSeed;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainPanel extends JPanel {
    private final Image background =
            new ImageIcon("images/Background.jpg").getImage();
    private GameModel gameModel = new GameModel(5, 9, 720, 500,
            20, 500);
    private static Font STANDARD = new Font("Standard", Font.PLAIN, 15);

    public MainPanel() {
        super();
        this.setBounds(0, 0, 835, 635);
        gameModel.addSeed(new PeashooterSeed());

        for (int row = 0; row < gameModel.getRows(); ++row) {
            gameModel.setPlant(row, 4, new Peashooter());
        }

        java.util.Timer timer = new Timer();

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
        }, 0, 20);
    }

    private void showMessageDialog(Object message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, -200, 0, null);

        g.drawImage(new ImageIcon("images/SeedBank.png").getImage(),
                10, 10, null);
        g.setFont(STANDARD);
        String numOfSun = Integer.toString(gameModel.getSun());
        g.drawString(numOfSun,
                44 - numOfSun.length() * g.getFont().getSize() / 4, 89);
        List<PlantSeed> seeds = gameModel.getSeeds();
        for(int i = 0; i < seeds.size(); i++)
            g.drawImage(new ImageIcon(seeds.get(i).getImagePath())
                            .getImage()
                            .getScaledInstance(68, 48, Image.SCALE_DEFAULT),
                    85 + i * 50, 15, null);

        int blockWidth = gameModel.getBlockWidth();
        int blockHeight = gameModel.getBlockHeight();

        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getCols(); col++) {
                Plant plant = gameModel.getPlant(row, col);
                if (plant == null)
                    continue;
                Image image = new ImageIcon(plant.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) ((col + 0.5) * blockWidth - image.getWidth(null) / 2.0),
                        (int) ((row + 1) * blockHeight - image.getHeight(null) / 2.0), null);
            }
        }

        for (int row = 0; row < gameModel.getRows(); row++) {
            java.util.List<Zombie> zombies = gameModel.getZombies(row);
//            try {
                for (Zombie zombie : zombies) {
                    Image image = new ImageIcon(zombie.getCurrentImagePath()).getImage();
                    g.drawImage(image, (int) (zombie.getX() - image.getWidth(null) / 2.0),
                            (int) ((row + 1) * blockHeight - image.getHeight(null) / 2.0), null);
                }
//            } catch (Exception ignored) {
//
//            }
        }

        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Bullet> bullets = gameModel.getBullets(row);
            for (Bullet bullet : bullets) {
                Image image = new ImageIcon(bullet.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) (bullet.getX() - image.getWidth(null) / 2.0),
                        (int) ((row + 1) * blockHeight - image.getHeight(null) / 2.0), null);
            }
        }
    }
}
