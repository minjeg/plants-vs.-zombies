package view;

import model.GameModel;
import model.LawnMower;
import model.Level;
import model.Sun;
import model.bullet.Bullet;
import model.plant.Plant;
import model.seed.*;
import model.zombie.Zombie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static javax.swing.JOptionPane.showMessageDialog;

public class MainPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    private Image imageFollowMouse = null;
    private Point mousePos = new Point(410, 5);
    private GameModel gameModel;
    private final int deltaX = 60, deltaY = 60;

    private PauseMenuPanel pauseMenu;
    private Level level;

    private static final Font STANDARD = new Font("Standard", Font.PLAIN, 15);

    public MainPanel(Level level) {
        super();
        this.setBounds(0, 0, 835, 635);
        this.setLayout(null);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.add(new PauseButton(this));


        this.level = level;
        gameModel = new GameModel(720, 500, 30, level);

        synchronized (gameModel) {
            gameModel.addSeed(new PeashooterSeed());
            gameModel.addSeed(new SunflowerSeed());
            gameModel.addSeed(new WallNutSeed());
            gameModel.addSeed(new PotatoMineSeed());
        }

        pauseMenu = new PauseMenuPanel(this);
        pauseMenu.setVisible(false);
        this.add(pauseMenu);

        java.util.Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (gameModel.getState() == GameModel.State.RUNNING) {
                    synchronized (gameModel) {
                        repaint();
                    }
                } else if (gameModel.getState() == GameModel.State.WIN) {
                    repaint();
                    showMessageDialog(MainPanel.this, "You win!");
                    this.cancel();
                } else if (gameModel.getState() == GameModel.State.LOSE) {
                    showMessageDialog(MainPanel.this, "You lose!");
                    this.cancel();
                }
            }
        }, 0, gameModel.getUpdateGap());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制背景
        g.drawImage(new ImageIcon("images/Background.jpg").getImage(),
                -200, 0, null);

        // 绘制种子槽和铲子槽
        g.drawImage(new ImageIcon("images/SeedBank.png").getImage(),
                10, 10, null);
        g.drawImage(new ImageIcon("images/ShovelBank.png").getImage(),
                413, 10, null);

        // 绘制阳光数
        g.setFont(STANDARD);
        String numOfSun = Integer.toString(gameModel.getSun());
        g.drawString(numOfSun,
                44 - numOfSun.length() * g.getFont().getSize() / 4, 89);

        // 绘制种子卡片
        List<PlantSeed> seeds = gameModel.getSeeds();
        for (int i = 0; i < seeds.size(); i++) {
            PlantSeed seed = seeds.get(i);
            g.drawImage(new ImageIcon(seed.getImagePath()).getImage(),
                    85 + i * 53, 15, null);
            // 绘制种子阳光不足和冷却未完成时覆盖的阴影
            Graphics2D g2d = (Graphics2D) g;
            if (!seed.goodToPlant(gameModel)) {
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

            // 绘制种子被选中时覆盖的阴影
            if (gameModel.getSeedInHand() == seed) {
                g2d.setComposite(AlphaComposite
                        .getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(85 + i * 53, 15, 53, 75);
                g2d.setComposite(AlphaComposite
                        .getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setColor(null);
            }
        }

        // 绘制铲子槽里的铲子
        if (!gameModel.isGrabShovel())
            g.drawImage(new ImageIcon("images/Shovel.png").getImage(),
                    410, 5, null);

        int blockWidth = gameModel.getBlockWidth();
        int blockHeight = gameModel.getBlockHeight();
        // 绘制植物底下的影子
        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getCols(); col++) {
                Plant plant = gameModel.getPlant(row, col);
                if (plant == null)
                    continue;
                Image image = new ImageIcon("images/shadow.png").getImage();
                g.drawImage(image, (int) (deltaX + (col + 0.5) * blockWidth - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight + image.getHeight(null) / 2.0), null);
            }
        }
        // 绘制僵尸底下的影子
        for (int row = 0; row < gameModel.getRows(); row++) {
            java.util.List<Zombie> zombies = gameModel.getZombies(row);
            for (Zombie zombie : zombies) {
                Image image = new ImageIcon("images/shadow.png").getImage();
                g.drawImage(image, (int) (deltaX + zombie.getX() - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight + image.getHeight(null) / 2.0), null);
            }
        }
        // 绘制子弹下方的影子
        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Bullet> bullets = gameModel.getBullets(row);
            for (Bullet bullet : bullets) {
                Image image = new ImageIcon("images/Bullet/bulletShadow.png").getImage();
                g.drawImage(image, (int) (deltaX + bullet.getX() - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight + image.getHeight(null) / 2.0), null);
            }
        }
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
            for (Zombie zombie : zombies) {
                Image image = new ImageIcon(zombie.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) (deltaX + zombie.getX() - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
            }
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

        //绘制割草机
        for (int row = 0; row < gameModel.getRows(); ++row) {
            LawnMower lawnMower = gameModel.getLawnMower(row);
            if (lawnMower == null)
                continue;
            Image image = new ImageIcon(lawnMower.getCurrentImagePath()).getImage();
            g.drawImage(image, (int) (deltaX + lawnMower.getX() - image.getWidth(null) / 2.0),
                    (int) (deltaY + (row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
        }

        // 绘制关卡进度条
        double rate = (double)level.getCurrentWave() / level.getTotalWave();
        g.drawImage(new ImageIcon("images/Panels/FlagMeter1.png").getImage(),
                600, 575, 758, 602, 0, 0, 158, 27, null);
        g.drawImage(new ImageIcon("images/Panels/FlagMeter2.png").getImage(),
                600 + (int)(158 * (1 - rate)), 575, 758, 602,
                (int)(158 * (1 - rate)), 0, 158, 27, null);


        //绘制阳光
        for (Sun sun : gameModel.getSuns()) {
            Image image = new ImageIcon(sun.getCurrentImagePath()).getImage();
            g.drawImage(image, (int) (deltaX / 2.0 + sun.getX() - image.getWidth(null) / 2.0),
                    (int) (deltaY + deltaY + sun.getY() - image.getHeight(null) / 2.0), null);
        }

        //绘制玩家持有物
        if (imageFollowMouse != null)
            g.drawImage(imageFollowMouse,
                    mousePos.x - imageFollowMouse.getWidth(null) / 2,
                    mousePos.y - imageFollowMouse.getHeight(null) / 2, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mousePos = e.getPoint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        mousePos = e.getPoint();
        if(gameModel.getState() == GameModel.State.LOSE ||
                gameModel.getState() == GameModel.State.PAUSED)
            return;
        if (!gameModel.isGrabShovel() && gameModel.getSeedInHand() == null) {
            List<Sun> sunList = gameModel.getSuns();
            for (int i = 0; i < sunList.size(); i++) {
                Sun temp = sunList.get(i);
                int x_diff = p.x - (temp.getX() + deltaX / 2);
                int y_diff = p.y - (temp.getY() + deltaY * 2);
                if (x_diff >= -96 && x_diff <= 96 && y_diff >= -96 && y_diff <= 96) {
                    gameModel.setSun(gameModel.getSun() + temp.getAmount());
                    sunList.remove(i);
                    return;
                }
            }
            int x_diff = p.x - 85;
            int y_diff = p.y - 15;
            if (x_diff >= 0 && x_diff <= 318 && y_diff >= 0 && y_diff <= 75) {
                int i = x_diff / 53;
                if (i < gameModel.getSeeds().size()
                        && gameModel.getSeeds().get(i).goodToPlant(gameModel)) {
                    gameModel.setSeedInHand(gameModel.getSeeds().get(i));
                    imageFollowMouse = new ImageIcon(gameModel.getSeedInHand()
                            .getPlant()
                            .getCurrentImagePath())
                            .getImage();
                }
            } else if (x_diff > 318 && x_diff <= 388 && y_diff >= 0 && y_diff <= 72) {
                gameModel.setGrabShovel(true);
                imageFollowMouse = new ImageIcon("images/Shovel.png")
                        .getImage();
            }
        } else if (gameModel.isGrabShovel()) {
            int row = gameModel.getRow(mousePos);
            int col = gameModel.getCol(mousePos);
            if (row != -1 && col != -1)
                gameModel.setPlant(row, col, null);
            gameModel.setGrabShovel(false);
            imageFollowMouse = null;
        } else if (gameModel.getSeedInHand() != null) {
            int row = gameModel.getRow(mousePos);
            int col = gameModel.getCol(mousePos);
            if (row != -1 && col != -1 && gameModel.getPlant(row, col) == null)
                gameModel.getSeedInHand().plant(gameModel, row, col);
            gameModel.setSeedInHand(null);
            imageFollowMouse = null;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePos = e.getPoint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mousePos = e.getPoint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mousePos = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePos = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        if (source instanceof PauseButton) {
            if (gameModel.getState() == GameModel.State.RUNNING) {
                gameModel.pauseGame();
                pauseMenu.setVisible(true);
            }
        } else if(source instanceof BackToGameButton) {
            if(gameModel.getState() == GameModel.State.PAUSED) {
                gameModel.continueGame();
                pauseMenu.setVisible(false);
            }
        } else if(source instanceof RestartButton) {
            gameModel = new GameModel(720, 500, 30,
                    new Level(level.getInitialSun(), level.getTotalWave()));
            synchronized (gameModel) {
                gameModel.addSeed(new PeashooterSeed());
                gameModel.addSeed(new SunflowerSeed());
                gameModel.addSeed(new WallNutSeed());
                gameModel.addSeed(new PotatoMineSeed());
            }
            pauseMenu.setVisible(false);
        }
    }
}
