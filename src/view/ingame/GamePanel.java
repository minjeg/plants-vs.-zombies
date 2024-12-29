package view.ingame;

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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static javax.swing.JOptionPane.showMessageDialog;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    private Image imageFollowMouse = null;
    private Point mousePos = new Point(410, 5);
    private GameModel gameModel;
    private final int deltaX = 60, deltaY = 60;

    private PauseMenuPanel pauseMenu;
    private Level level;

    private static final AudioPlayer COMMON_BGM_PLAYER;
    private static final AudioPlayer FAST_BGM_PLAYER;
    private AudioPlayer currentBGMPlayer;
    private static final AudioPlayer FIRST_ARRIVE_SIREN_PLAYER;
    private static final AudioPlayer SIREN_PLAYER;
    private static final AudioPlayer PAUSE_SOUND_PLAYER;
    private static final AudioPlayer[] PLANT_PLAYER = new AudioPlayer[2];
    private static final AudioPlayer CHOOSE_SEED_PLAYER;
    private static final AudioPlayer BUZZ_PLAYER;
    private static final AudioPlayer SHOVEL_PLAYER;
    private static final AudioPlayer[] TAP_PLAYER = new AudioPlayer[2];

    private static final Font STANDARD = new Font("Standard", Font.PLAIN, 15);

    static {
        COMMON_BGM_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/bgm/GrassWalk.wav"),
                AudioPlayer.LOOP);
        FAST_BGM_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/bgm/GrassWalk_fast.wav"),
                AudioPlayer.LOOP);
        PAUSE_SOUND_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/pause.wav"),
                AudioPlayer.NORMAL);
        FIRST_ARRIVE_SIREN_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/awooga.wav"),
                AudioPlayer.NORMAL);
        SIREN_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/siren.wav"),
                AudioPlayer.NORMAL);
        PLANT_PLAYER[0] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/plant.wav"),
                AudioPlayer.NORMAL);
        PLANT_PLAYER[1] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/plant2.wav"),
                AudioPlayer.NORMAL);
        CHOOSE_SEED_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/seedlift.wav"),
                AudioPlayer.NORMAL);
        BUZZ_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/buzzer.wav"),
                AudioPlayer.NORMAL);
        SHOVEL_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/shovel.wav"),
                AudioPlayer.NORMAL);
        TAP_PLAYER[0] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/tap.wav"),
                AudioPlayer.NORMAL);
        TAP_PLAYER[1] = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/tap2.wav"),
                AudioPlayer.NORMAL);
    }

    private int previousWave = 0;

    public GamePanel(Level level) {
        super(true);
        this.setBounds(0, 0, 835, 635);
        this.setLayout(null);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.add(new PauseButton(this));


        this.level = level;
        gameModel = new GameModel(720, 500, 30, level);

        pauseMenu = new PauseMenuPanel(this);
        pauseMenu.setVisible(false);
        this.add(pauseMenu);

        currentBGMPlayer = COMMON_BGM_PLAYER;
        currentBGMPlayer.start();

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (gameModel.getState() == GameModel.State.RUNNING) {
                    synchronized (gameModel) {
                        repaint();
                    }
                } else if (gameModel.getState() == GameModel.State.WIN) {
                    repaint();
                    showMessageDialog(GamePanel.this, "You win!");
                    this.cancel();
                } else if (gameModel.getState() == GameModel.State.LOSE) {
                    showMessageDialog(GamePanel.this, "You lose!");
                    this.cancel();
                }
            }
        }, 0, gameModel.getUpdateGap());
    }

    private void paintBackground(Graphics g) {
        g.drawImage(new ImageIcon("images/Background.jpg").getImage(),
                -200, 0, null);
    }

    private void paintBanks(Graphics g) {
        // 绘制种子槽和铲子槽本身
        g.drawImage(new ImageIcon("images/SeedBank.png").getImage(),
                10, 10, null);
        g.drawImage(new ImageIcon("images/ShovelBank.png").getImage(),
                466, 10, null);

        // 绘制当前阳光数
        g.setFont(STANDARD);
        String numOfSun = Integer.toString(gameModel.getSun());
        g.drawString(numOfSun,
                44 - numOfSun.length() * g.getFont().getSize() / 4, 89);

        // 绘制种子卡片
        List<PlantSeed> seeds = gameModel.getSeeds();
        for (int i = 0; i < seeds.size(); i++) {
            PlantSeed seed = seeds.get(i);
            if (seed == null) continue;

            g.drawImage(new ImageIcon(seed.getImagePath()).getImage(),
                    85 + i * 53, 15, null);


            if (!seed.goodToPlant(gameModel)) {
                g.setColor(new Color(128, 128, 128, 128));
                g.fillRect(85 + i * 53, 15, 53, 75);
                if (seed.getCoolDown() != 0) {
                    g.setColor(new Color(0, 0, 0, 128));
                    g.fillRect(85 + i * 53, 15,
                            53, (int) (75 * seed.getCoolDown()));
                    g.setColor(null);
                }
            } else if (gameModel.getSeedInHand() == seed) {
                g.setColor(new Color(0, 0, 0, 128));
                g.fillRect(85 + i * 53, 15, 53, 75);
                g.setColor(null);
            }
        }

        // 绘制铲子槽里的铲子
        if (!gameModel.isGrabShovel())
            g.drawImage(new ImageIcon("images/Shovel.png").getImage(),
                    463, 5, null);
    }

    private void paintShadows(Graphics g) {
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
            List<Zombie> zombies = gameModel.getZombies(row);
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
                        (int) (deltaY + (row + 0.5) * blockHeight + image.getHeight(null) * 3.0), null);
            }
        }
    }

    private Image brightenImage(Image image) {
        BufferedImage ret =
                new BufferedImage(image.getWidth(null),
                        image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = ret.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        for(int x = 0; x < ret.getWidth(); x++) {
            for(int y = 0; y < ret.getHeight(null); y++) {
                int rgb = ret.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int newRgb = rgb;
                if(alpha != 0) {
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;

                    r = Math.min(255, r + 50);
                    g = Math.min(255, g + 50);
                    b = Math.min(255, b + 50);

                    newRgb = (alpha << 24) | (r << 16) | (g << 8) | b;
                }
                ret.setRGB(x, y, newRgb);
            }
        }

        return ret;
    }

    private void paintPlants(Graphics g) {
        int blockWidth = gameModel.getBlockWidth();
        int blockHeight = gameModel.getBlockHeight();

        Graphics2D g2d = (Graphics2D) g;

        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getCols(); col++) {
                Plant plant = gameModel.getPlant(row, col);
                Image image;
                if (plant == null) {
                    PlantSeed seedInHand = gameModel.getSeedInHand();
                    if (seedInHand != null && gameModel.getRow(mousePos) == row && gameModel.getCol(mousePos) == col) {
                        image = new ImageIcon(seedInHand.getPlant().getCurrentImagePath()).getImage();
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    } else {
                        continue;
                    }
                } else {
                    image = new ImageIcon(plant.getCurrentImagePath()).getImage();
                    if(gameModel.isGrabShovel() && gameModel.getRow(mousePos) == row && gameModel.getCol(mousePos) == col)
                        image = brightenImage(image);
                }
                g2d.drawImage(image, (int) (deltaX + (col + 0.5) * blockWidth - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }

    private void paintZombies(Graphics g) {
        int blockHeight = gameModel.getBlockHeight();

        //绘制僵尸
        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Zombie> zombies = gameModel.getZombies(row);
            for (Zombie zombie : zombies) {
                Image image = new ImageIcon(zombie.getCurrentImagePath()).getImage();
                if(zombie.isHit()) {
                    image = brightenImage(image);
                    zombie.resetHitState();
                }
                g.drawImage(image, (int) (deltaX + zombie.getX() - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
            }
        }
    }

    private void paintBullets(Graphics g) {
        int blockHeight = gameModel.getBlockHeight();

        //绘制子弹
        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Bullet> bullets = gameModel.getBullets(row);
            for (Bullet bullet : bullets) {
                Image image = new ImageIcon(bullet.getCurrentImagePath()).getImage();
                g.drawImage(image, (int) (deltaX + bullet.getX() - image.getWidth(null) / 2.0),
                        (int) (deltaY + (row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
            }
        }
    }

    private void paintLawnMowers(Graphics g) {
        int blockHeight = gameModel.getBlockHeight();

        //绘制割草机
        for (int row = 0; row < gameModel.getRows(); ++row) {
            LawnMower lawnMower = gameModel.getLawnMower(row);
            if (lawnMower == null)
                continue;
            Image image = new ImageIcon(lawnMower.getCurrentImagePath()).getImage();
            g.drawImage(image, (int) (deltaX + lawnMower.getX() - image.getWidth(null) / 2.0),
                    (int) (deltaY + (row + 0.5) * blockHeight - image.getHeight(null) / 2.0), null);
        }
    }

    private void paintLoadBar(Graphics g) {
        double rate = (double) level.getCurrentWave() / level.getTotalWave();
        g.drawImage(new ImageIcon("images/Panels/FlagMeter_Empty.png").getImage(),
                600, 575, 758, 602, 0, 0, 158, 27, null);
        g.drawImage(new ImageIcon("images/Panels/FlagMeter.png").getImage(),
                600 + (int) (158 * (1 - rate)), 575, 758, 602,
                (int) (158 * (1 - rate)), 0, 158, 27, null);
        for (double i = 0; i <= level.getTotalWave(); i += 10) {
            if (i == 0) continue;
            g.drawImage(new ImageIcon("images/Panels/FlagMeterParts_FlagPole.png").getImage(),
                    607 + (int) (145 * (1 - i / level.getTotalWave())), 575, null);
            g.drawImage(new ImageIcon("images/Panels/FlagMeterParts_Flag.png").getImage(),
                    607 + (int) (145 * (1 - i / level.getTotalWave())),
                    (level.getCurrentWave() >= i ? 566 : 575), null);
        }
        g.drawImage(new ImageIcon("images/Panels/FlagMeterLevelProgress.png").getImage(),
                640, 588, null);
        g.drawImage(new ImageIcon("images/Panels/FlagMeterParts_Head.png").getImage(),
                600 + (int) (145 * (1 - (double) level.getCurrentWave() / level.getTotalWave())),
                575, null);
    }

    private void paintSun(Graphics g) {
        for (Sun sun : gameModel.getSuns()) {
            Image image = new ImageIcon(sun.getCurrentImagePath()).getImage();
            g.drawImage(image, (int) (deltaX / 2.0 + sun.getX() - image.getWidth(null) / 2.0),
                    (int) (deltaY + deltaY + sun.getY() - image.getHeight(null) / 2.0), null);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        paintBackground(g);
        paintBanks(g);
        paintShadows(g);
        paintPlants(g);
        paintZombies(g);
        paintBullets(g);
        paintLawnMowers(g);
        if (level.getCurrentWave() != 0)
            paintLoadBar(g);
        paintSun(g);

        if (imageFollowMouse != null)
            g.drawImage(imageFollowMouse,
                    mousePos.x - imageFollowMouse.getWidth(null) / 2,
                    mousePos.y - imageFollowMouse.getHeight(null) / 2, null);

        // 根据僵尸数量切换bgm
        int numOfZombies = gameModel.getNumOfZombies();
        if (currentBGMPlayer == COMMON_BGM_PLAYER && numOfZombies >= 4) {
            currentBGMPlayer = FAST_BGM_PLAYER;
            FAST_BGM_PLAYER.startFrom(COMMON_BGM_PLAYER.getCurrentFrame());
            COMMON_BGM_PLAYER.stop();
        } else if (currentBGMPlayer == FAST_BGM_PLAYER && numOfZombies < 4) {
            currentBGMPlayer = COMMON_BGM_PLAYER;
            COMMON_BGM_PLAYER.startFrom(FAST_BGM_PLAYER.getCurrentFrame());
            FAST_BGM_PLAYER.stop();
        }

        if (previousWave == level.getCurrentWave() - 1) {
            if (level.getCurrentWave() == 1)
                FIRST_ARRIVE_SIREN_PLAYER.start();
            else if (level.getCurrentWave() % 10 == 0
                    || level.getCurrentWave() == level.getTotalWave())
                SIREN_PLAYER.start();
        }
        previousWave = level.getCurrentWave();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mousePos = e.getPoint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        mousePos = e.getPoint();
        if (gameModel.getState() == GameModel.State.LOSE ||
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
                    temp.playSound();
                    sunList.remove(i);
                    return;
                }
            }
            int x_diff = p.x - 85;
            int y_diff = p.y - 15;
            if (x_diff >= 0 && x_diff <= 371 && y_diff >= 0 && y_diff <= 75) {
                int i = x_diff / 53;
                if (i < gameModel.getSeeds().size()) {
                    if (gameModel.getSeeds().get(i).goodToPlant(gameModel)) {
                        gameModel.setSeedInHand(gameModel.getSeeds().get(i));
                        imageFollowMouse = new ImageIcon(gameModel.getSeedInHand()
                                .getPlant()
                                .getCurrentImagePath())
                                .getImage();
                        CHOOSE_SEED_PLAYER.start();
                    } else {
                        BUZZ_PLAYER.start();
                    }
                }
            } else if (x_diff > 371 && x_diff <= 441 && y_diff >= 0 && y_diff <= 72) {
                gameModel.setGrabShovel(true);
                imageFollowMouse = new ImageIcon("images/Shovel.png")
                        .getImage();
                SHOVEL_PLAYER.start();
            }
        } else if (gameModel.isGrabShovel()) {
            int row = gameModel.getRow(mousePos);
            int col = gameModel.getCol(mousePos);
            if (row != -1 && col != -1 && gameModel.getPlant(row, col) != null) {
                gameModel.setPlant(row, col, null);
                PLANT_PLAYER[0].start();
            } else {
                TAP_PLAYER[new Random().nextInt(0, 2)].start();
            }
            gameModel.setGrabShovel(false);
            imageFollowMouse = null;
        } else if (gameModel.getSeedInHand() != null) {
            int row = gameModel.getRow(mousePos);
            int col = gameModel.getCol(mousePos);
            if (row != -1 && col != -1 && gameModel.getPlant(row, col) == null) {
                gameModel.getSeedInHand().plant(gameModel, row, col);
                PLANT_PLAYER[new Random().nextInt(0, 2)].start();
            } else {
                TAP_PLAYER[new Random().nextInt(0, 2)].start();
            }
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
                PAUSE_SOUND_PLAYER.start();
                gameModel.pauseGame();
                pauseMenu.setVisible(true);
                FAST_BGM_PLAYER.stop();
                COMMON_BGM_PLAYER.stop();
            }
        } else if (source instanceof BackToGameButton) {
            if (gameModel.getState() == GameModel.State.PAUSED) {
                gameModel.continueGame();
                pauseMenu.setVisible(false);
                currentBGMPlayer.continuePlay();
            }
        } else if (source instanceof RestartButton) {
            level = new Level(level.getInitialSun(), level.getTotalWave());
            gameModel = new GameModel(720, 500, 30, level);
            pauseMenu.setVisible(false);
            currentBGMPlayer = COMMON_BGM_PLAYER;
            currentBGMPlayer.start();
        }
    }
}
