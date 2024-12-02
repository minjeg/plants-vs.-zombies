package view;

import model.GameModel;
import model.Sun;
import model.bullet.Bullet;
import model.plant.Peashooter;
import model.plant.Plant;
import model.plant.Sunflower;
import model.zombie.BasicZombie;
import model.zombie.Zombie;
import model.seed.PeashooterSeed;
import model.seed.PlantSeed;
import model.seed.SunflowerSeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainPanel extends JPanel implements MouseListener, MouseMotionListener {
    private final Image background =
            new ImageIcon("images/Background.jpg").getImage();
    private final GameModel gameModel = new GameModel(5, 9, 720, 500,
            30, 50);
    private int deltaX = 60, deltaY = 60;

    private static Font STANDARD = new Font("Standard", Font.PLAIN, 15);

    private boolean isShovel = false;
    private PlantSeed seedInHand = null;


    public MainPanel() {
        super();
        this.setBounds(0, 0, 835, 635);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        gameModel.addSeed(new PeashooterSeed());
        gameModel.addSeed(new SunflowerSeed());

        for (int row = 0; row < gameModel.getRows(); ++row) {
            gameModel.setPlant(row, 0, new Sunflower());
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
        }, 0, 30);
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
        for (int i = 0; i < seeds.size(); i++) {
            PlantSeed seed = seeds.get(i);
            g.drawImage(new ImageIcon(seed.getImagePath()).getImage(),
                    85 + i * 53, 15, null);
            if (!seed.goodToPlant(gameModel)) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite
                        .getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.setColor(Color.GRAY);
                g2d.fillRect(85 + i * 53, 15, 53, 75);
                if (seed.getCoolDown() != 0) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(85 + i * 53, 15,
                            53, (int) (75 * seed.getCoolDown()));
                }
                g2d.setComposite(AlphaComposite
                        .getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setColor(null);
            }
        }

        int blockWidth = gameModel.getBlockWidth();
        int blockHeight = gameModel.getBlockHeight();
        //绘制植物
        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getCols(); col++) {
                Plant plant = gameModel.getPlant(row, col);
                if (plant == null)
                    continue;
                Image image = new ImageIcon(plant.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) (deltaX + (col + 0.5) * blockWidth - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
            }
        }
        //绘制僵尸
        for (int row = 0; row < gameModel.getRows(); row++) {
            java.util.List<Zombie> zombies = gameModel.getZombies(row);
//            try {
            for (Zombie zombie : zombies) {
                Image image = new ImageIcon(zombie.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) (deltaX + zombie.getX() - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
            }
//            } catch (Exception ignored) {
//
//            }
        }
        //绘制子弹
        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Bullet> bullets = gameModel.getBullets(row);
            for (Bullet bullet : bullets) {
                Image image = new ImageIcon(bullet.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) (deltaX + bullet.getX() - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
            }
        }
        //绘制阳光
        for (Sun sun : gameModel.getSuns()) {
            Image image = new ImageIcon(sun.getCurrentImagePath()).getImage();
            g.drawImage(image, (int) (deltaX / 2.0 + sun.getX() - image.getWidth(null) / 2.0),
                    (int) (deltaY + deltaY + sun.getY() - image.getHeight(null) / 2.0), null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        List<PlantSeed> seeds = gameModel.getSeeds();

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
