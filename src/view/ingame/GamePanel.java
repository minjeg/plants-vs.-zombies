package view.ingame;

import model.GameModel;
import model.LawnMower;
import model.Level;
import model.Sun;
import model.bullet.Bullet;
import model.plant.Plant;
import model.seed.*;
import model.zombie.Zombie;
import view.PlayFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    private Image imageFollowMouse = null;
    private Point mousePos = new Point(410, 5);
    private GameModel gameModel;
    private final int updateGap = 30;
    private final int deltaX = 60, deltaY = 60;

    private final BufferedImage bufferedImage = new BufferedImage(835, 635, BufferedImage.TYPE_INT_RGB);
    private boolean panelImmobile = false;

    private final PauseMenuPanel pauseMenu;
    private final BackToMenuDialog backToMenuDialog;
    private final RestartDialog restartDialog;
    private final PauseButton pauseButton;
    private final LoseGameDialog loseGameDialog;
    private final LoadGameConfirmPanel loadGameConfirmPanel;

    private boolean grabShovel = false;
    private PlantSeed seedInHand = null;

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
    private static final AudioPlayer READY_SET_PLANT_PLAYER;
    private static final AudioPlayer HUGE_WAVE_APPROACHING_PLAYER;
    private static final AudioPlayer FINAL_WAVE_APPROACHING_PLAYER;
    private static final AudioPlayer LOSE_PLAYER;
    private static final AudioPlayer SCREAM_PLAYER;
    private static final AudioPlayer WIN_PLAYER;
    private static final AudioPlayer PRIZE_PLAYER;

    private final PlayFrame frame;

    private int state = WAIT;
    public static final int WAIT = 0, READY = 1, START = 2, SETTLE = 3, LOADING = 4, PAUSE = 5;

    private int readyTimer = 0;

    private boolean hugeWaveSoundPlayable = false;
    private boolean hugeWaveSoundPlayed = false;
    private int finalWaveShowTimer = 0;

    private int loseAnimationPerformTimer = 0;

    private int winAnimationPerformTimer = 0;
    private boolean trophyGenerated = false;

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
        READY_SET_PLANT_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/readysetplant.wav"), AudioPlayer.NORMAL);
        HUGE_WAVE_APPROACHING_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/hugewave.wav"), AudioPlayer.NORMAL);
        FINAL_WAVE_APPROACHING_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/finalwave.wav"), AudioPlayer.NORMAL);
        LOSE_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/losemusic.wav"), AudioPlayer.NORMAL);
        WIN_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/winmusic.wav"), AudioPlayer.NORMAL);
        SCREAM_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/scream.wav"), AudioPlayer.NORMAL);
        PRIZE_PLAYER = AudioPlayer.getAudioPlayer(
                new File("sounds/audio/prize.wav"), AudioPlayer.NORMAL);
    }

    private int previousWave = 0;

    public GamePanel(PlayFrame frame) {
        super(true);
        this.frame = frame;
        this.setBounds(0, 0, 835, 635);
        this.setLayout(null);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        backToMenuDialog = new BackToMenuDialog(this);
        backToMenuDialog.setVisible(false);
        this.add(backToMenuDialog);
        restartDialog = new RestartDialog(this);
        restartDialog.setVisible(false);
        this.add(restartDialog);
        pauseMenu = new PauseMenuPanel(this);
        pauseMenu.setVisible(false);
        this.add(pauseMenu);
        pauseButton = new PauseButton(this);
        this.add(pauseButton);
        loseGameDialog = new LoseGameDialog(this);
        loseGameDialog.setVisible(false);
        this.add(loseGameDialog);
        loadGameConfirmPanel = new LoadGameConfirmPanel(this);
        loadGameConfirmPanel.setVisible(false);
        this.add(loadGameConfirmPanel);

        currentBGMPlayer = COMMON_BGM_PLAYER;
        setState(WAIT);

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (state == WAIT) {
                    repaint();
                    panelUpdate();
                } else if (state == READY) {
                    readyTimer += gameModel.getUpdateGap();
                    if (readyTimer >= 2400) {
                        setState(START);
                        readyTimer = 0;
                    }
                    repaint();
                    panelUpdate();
                } else if (state == LOADING || state == PAUSE) {
                    if (gameModel != null) {
                        panelUpdate();
                        repaint();
                        if (gameModel.getState() == GameModel.State.LOSE)
                            loseAnimationPerformTimer =
                                    Math.min(loseAnimationPerformTimer + gameModel.getUpdateGap(), 8000);
                    }
                } else if (state == SETTLE) {
                    winAnimationPerformTimer += gameModel.getUpdateGap();
                    repaint();
                    panelUpdate();
                    if (winAnimationPerformTimer >= 8000) {
                        setState(WAIT);
                        frame.getAward();
                    }
                } else {
                    if (panelImmobile)
                        panelImmobile = false;
                    if (gameModel == null)
                        return;
                    if (gameModel.getState() == GameModel.State.LOSE) {
                        GameModel.save(null, "gamesave/save");
                        setState(PAUSE);
                    } else {
                        if (gameModel.getState() == GameModel.State.WIN)
                            GameModel.save(null, "gamesave/save");
                        gameModel.update();
                        panelUpdate();
                        repaint();
                    }
                }
            }
        }, updateGap, updateGap);
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
        g.setColor(Color.BLACK);
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

            if (state == READY || seedInHand == seed) {
                g.setColor(new Color(0, 0, 0, 128));
                g.fillRect(85 + i * 53, 15, 53, 75);
                g.setColor(null);
            } else if (!seed.goodToPlant(gameModel)) {
                g.setColor(new Color(128, 128, 128, 128));
                g.fillRect(85 + i * 53, 15, 53, 75);
                if (seed.getCoolDown() != 0) {
                    g.setColor(new Color(0, 0, 0, 128));
                    g.fillRect(85 + i * 53, 15,
                            53, (int) (75 * seed.getCoolDown()));
                    g.setColor(null);
                }
            }
        }

        // 绘制铲子槽里的铲子
        if (!grabShovel)
            g.drawImage(new ImageIcon("images/Shovel.png").getImage(),
                    463, 5, null);
    }

    private void paintShadows(Graphics g) {
        // 绘制植物底下的影子
        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getCols(); col++) {
                Plant plant = gameModel.getPlant(row, col);
                if (plant == null || plant.getState() == Plant.State.EXPLODING)
                    continue;
                Image image = new ImageIcon("images/Plant/PlantShadow.png").getImage();
                g.drawImage(image, plant.getShadeX(col), plant.getShadeY(row), null);
            }
        }
        // 绘制僵尸底下的影子
        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Zombie> zombies = gameModel.getZombies(row);
            for (Zombie zombie : zombies) {
                if (zombie.isDead()) continue;
                Image image = new ImageIcon("images/Zombie/ZombieShadow.png").getImage();
                g.drawImage(image, zombie.getShadeX(), zombie.getShadeY(row), null);
            }
        }
        // 绘制子弹下方的影子
        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Bullet> bullets = gameModel.getBullets(row);
            for (Bullet bullet : bullets) {
                Image image = new ImageIcon("images/Bullet/bulletShadow.png").getImage();
                g.drawImage(image, bullet.getShadeX(), bullet.getShadeY(row), null);
            }
        }
        // 绘制小推车的影子
        for (int row = 0; row < gameModel.getRows(); ++row) {
            LawnMower lawnMower = gameModel.getLawnMower(row);
            if (lawnMower == null)
                continue;
            Image image = new ImageIcon("images/LawnMower/LawnMowerShadow.png").getImage();
            g.drawImage(image, lawnMower.getShadeX(), lawnMower.getShadeY(row), null);
        }
    }

    private void paintLawnMowers(Graphics g) {
        //绘制割草机
        for (int row = 0; row < gameModel.getRows(); ++row) {
            LawnMower lawnMower = gameModel.getLawnMower(row);
            if (lawnMower == null)
                continue;
            Image image = new ImageIcon(lawnMower.getCurrentImagePath()).getImage();
            g.drawImage(image, lawnMower.getImageX(), lawnMower.getImageY(row), null);
        }
    }

    private Image brightenImage(Image image) {
        BufferedImage ret =
                new BufferedImage(image.getWidth(null),
                        image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = ret.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        for (int x = 0; x < ret.getWidth(); x++) {
            for (int y = 0; y < ret.getHeight(null); y++) {
                int rgb = ret.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int newRgb = rgb;
                if (alpha != 0) {
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
        Graphics2D g2d = (Graphics2D) g;

        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getCols(); col++) {
                Plant plant = gameModel.getPlant(row, col);
                Image image;
                if (plant == null) {
                    if (seedInHand != null && gameModel.getRow(mousePos) == row && gameModel.getCol(mousePos) == col) {
                        image = new ImageIcon(seedInHand.getPlant().getCurrentImagePath()).getImage();
                        plant = seedInHand.getPlant();
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    } else {
                        continue;
                    }
                } else {
                    image = new ImageIcon(plant.getCurrentImagePath()).getImage();
                    if (grabShovel && gameModel.getRow(mousePos) == row && gameModel.getCol(mousePos) == col)
                        image = brightenImage(image);
                }
                if (image != null)
                    g2d.drawImage(image, plant.getImageX(col), plant.getImageY(row), null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }

    private void paintZombies(Graphics g) {
        //绘制僵尸
        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Zombie> zombies = gameModel.getZombies(row);
            for (Zombie zombie : zombies) {
                Image image = new ImageIcon(zombie.getCurrentImagePath()).getImage();
                if (zombie.isHit()) {
                    image = brightenImage(image);
                    zombie.resetHitState();
                }
                g.drawImage(image, zombie.getImageX(), zombie.getImageY(row), null);
            }
        }
    }

    private void paintBullets(Graphics g) {
        //绘制子弹
        for (int row = 0; row < gameModel.getRows(); row++) {
            List<Bullet> bullets = gameModel.getBullets(row);
            for (Bullet bullet : bullets) {
                Image image = new ImageIcon(bullet.getCurrentImagePath()).getImage();
                g.drawImage(image, bullet.getImageX(), bullet.getImageY(row), null);
            }
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

    private void paintImages(Graphics g) {
        super.paintComponent(g);

        if (gameModel == null)
            return;

        paintBackground(g);
        if (gameModel.getState() != GameModel.State.LOSE)
            paintBanks(g);
        paintShadows(g);
        paintPlants(g);
        paintZombies(g);

        paintBullets(g);
        paintLawnMowers(g);
        if (level.getCurrentWave() != 0 && gameModel.getState() != GameModel.State.LOSE)
            paintLoadBar(g);
        paintSun(g);

        if (imageFollowMouse != null && gameModel.getState() != GameModel.State.LOSE)
            g.drawImage(imageFollowMouse,
                    mousePos.x - imageFollowMouse.getWidth(null) / 2,
                    mousePos.y - imageFollowMouse.getHeight(null) / 2, null);

        if (state == READY) {
            if (readyTimer <= 600)
                g.drawImage(new ImageIcon("images/ready1.png").getImage(),
                        300, 270, null);
            else if (readyTimer <= 1200)
                g.drawImage(new ImageIcon("images/ready2.png").getImage(),
                        300, 300, null);
            else if (readyTimer <= 1800)
                g.drawImage(new ImageIcon("images/ready3.png").getImage(),
                        200, 200, null);
        } else {
            if (level.isShowingWords()) {
                g.drawImage(new ImageIcon("images/Approaching.png").getImage(),
                        120, 300, null);
                hugeWaveSoundPlayable = true;
            } else {
                hugeWaveSoundPlayable = false;
                hugeWaveSoundPlayed = false;
            }
        }

        if (level.getCurrentWave() == level.getTotalWave()) {
            if (finalWaveShowTimer < 1200)
                g.drawImage(new ImageIcon("images/FinalWave.png").getImage(),
                        220, 250, null);
            finalWaveShowTimer += gameModel.getUpdateGap();
        }
    }

    private void paintImmobileImage(Graphics g) {
        if (!panelImmobile) {
            paintImages(bufferedImage.getGraphics());
            panelImmobile = true;
        }
        g.drawImage(bufferedImage, 0, 0, null);
    }

    private void paintLoseAnimation(Graphics g) {
        if (loseAnimationPerformTimer >= 5000 && loseAnimationPerformTimer < 8000)
            g.drawImage(new ImageIcon("images/ZombiesAteYourBrain.png").getImage(),
                    120, 80, null);
    }

    private void paintWinAnimation(Graphics g) {
        if (gameModel == null)
            return;

        if (gameModel.getState() == GameModel.State.WIN) {
            int x = (int) (400 - Math.min(winAnimationPerformTimer, 1200) * (5.0 / 120));
            g.drawImage(new ImageIcon("images/trophy.png").getImage(),
                    x, 4 * x - 1200, null);
            if (winAnimationPerformTimer == 0)
                g.drawImage(new ImageIcon("images/PointerDown.gif").getImage(),
                        434, 370, null);
            if (winAnimationPerformTimer >= 1800 && winAnimationPerformTimer <= 8000) {
                g.setColor(Color.WHITE);
                Graphics2D g2d = (Graphics2D) g;
                float alpha = Math.min((winAnimationPerformTimer - 1800) / 5400f, 1.0f);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.fillRect(0, 0, 835, 635);
                g2d.setColor(null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (state == PAUSE || state == LOADING) {
            paintImmobileImage(g);
            paintLoseAnimation(g);
        } else {
            paintImages(g);
            paintWinAnimation(g);
        }
    }

    private void panelUpdate() {
        if (gameModel == null)
            return;

        if (hugeWaveSoundPlayable && !hugeWaveSoundPlayed) {
            HUGE_WAVE_APPROACHING_PLAYER.start();
            hugeWaveSoundPlayed = true;
        }

        // 根据僵尸数量切换bgm
        int numOfZombies = gameModel.getNumOfZombies();
        if (currentBGMPlayer == COMMON_BGM_PLAYER && numOfZombies >= 10) {
            currentBGMPlayer = FAST_BGM_PLAYER;
            FAST_BGM_PLAYER.startFrom(COMMON_BGM_PLAYER.getCurrentFrame());
            COMMON_BGM_PLAYER.stop();
        } else if (currentBGMPlayer == FAST_BGM_PLAYER && numOfZombies < 10) {
            currentBGMPlayer = COMMON_BGM_PLAYER;
            COMMON_BGM_PLAYER.startFrom(FAST_BGM_PLAYER.getCurrentFrame());
            FAST_BGM_PLAYER.stop();
        }

        if (previousWave == level.getCurrentWave() - 1) {
            if (level.getCurrentWave() == 1)
                FIRST_ARRIVE_SIREN_PLAYER.start();
            else if (level.isFlagWave()) {
                SIREN_PLAYER.start();
                if (level.getCurrentWave() == level.getTotalWave())
                    FINAL_WAVE_APPROACHING_PLAYER.start();
            }
        }
        previousWave = level.getCurrentWave();

        if (gameModel.getState() == GameModel.State.LOSE) {
            COMMON_BGM_PLAYER.stop();
            FAST_BGM_PLAYER.stop();
            pauseButton.setVisible(false);
            if (loseAnimationPerformTimer == 0) {
//                paintImages(bufferedImage.createGraphics());
                LOSE_PLAYER.start();
            } else if (loseAnimationPerformTimer == 5010)
                SCREAM_PLAYER.start();
            else if (loseAnimationPerformTimer == 8000)
                loseGameDialog.setVisible(true);
        }

        if (gameModel.getState() == GameModel.State.WIN) {
            if (!trophyGenerated) {
                PRIZE_PLAYER.start();
                trophyGenerated = true;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mousePos = e.getPoint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        mousePos = e.getPoint();
        if (gameModel.getState() != GameModel.State.RUNNING &&
                gameModel.getState() != GameModel.State.WIN)
            return;

        if (!grabShovel && seedInHand == null) {
            if (gameModel.getState() == GameModel.State.WIN && state != SETTLE) {
                if (mousePos.x >= 400 && mousePos.x <= 500
                        && mousePos.y >= 400 && mousePos.y <= 481) {
                    setState(SETTLE);
                    return;
                }
            }
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
                        seedInHand = gameModel.getSeeds().get(i);
                        imageFollowMouse = new ImageIcon(seedInHand
                                .getPlant()
                                .getCurrentImagePath())
                                .getImage();
                        CHOOSE_SEED_PLAYER.start();
                    } else {
                        BUZZ_PLAYER.start();
                    }
                }
            } else if (x_diff > 371 && x_diff <= 441 && y_diff >= 0 && y_diff <= 72) {
                grabShovel = true;
                imageFollowMouse = new ImageIcon("images/Shovel.png")
                        .getImage();
                SHOVEL_PLAYER.start();
            }
        } else if (grabShovel) {
            int row = gameModel.getRow(mousePos);
            int col = gameModel.getCol(mousePos);
            if (row != -1 && col != -1 && gameModel.getPlant(row, col) != null) {
                gameModel.setPlant(row, col, null);
                PLANT_PLAYER[0].start();
            } else {
                TAP_PLAYER[new Random().nextInt(0, 2)].start();
            }
            grabShovel = false;
            imageFollowMouse = null;
        } else if (seedInHand != null) {
            int row = gameModel.getRow(mousePos);
            int col = gameModel.getCol(mousePos);
            if (row != -1 && col != -1 && gameModel.getPlant(row, col) == null) {
                seedInHand.plant(gameModel, row, col);
                PLANT_PLAYER[new Random().nextInt(0, 2)].start();
            } else {
                TAP_PLAYER[new Random().nextInt(0, 2)].start();
            }
            seedInHand = null;
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
            this.setState(PAUSE);
            pauseMenu.setVisible(true);
            pauseMenu.enableAll();
        } else if (source instanceof BackToGameButton) {
            this.setState(START);
        } else if (source instanceof ReturnToMenuButton) {
            backToMenuDialog.setVisible(true);
            pauseMenu.disableAll();
        } else if (source instanceof BackToMenuCancelButton) {
            backToMenuDialog.setVisible(false);
            pauseMenu.enableAll();
        } else if (source instanceof BackToMenuConfirmButton || source instanceof MenuButton) {
            this.setState(WAIT);
            GameModel.save(gameModel, "gamesave/save");
            frame.returnToMenu();
        } else if (source instanceof RestartButton || source instanceof NewGameButton) {
            restartDialog.setVisible(true);
            pauseMenu.disableAll();
        } else if (source instanceof RestartCancelButton) {
            restartDialog.setVisible(false);
            pauseMenu.enableAll();
        } else if (source instanceof RestartConfirmButton
                || source instanceof LoseConfirmButton) {
            gameModel.reset();
            level = gameModel.getLevel();
            GameModel.save(gameModel, "gamesave/save");
            this.setState(READY);
        } else if (source instanceof LoadGameConfirmButton) {
            this.setState(START);
        }
    }

    public void setState(int state) {
        if (state != LOADING)
            this.state = state;
        if (state == WAIT) {
            waitForActivate();
        } else if (state == READY) {
            readyToStart();
        } else if (state == START) {
            startGame();
        } else if (state == SETTLE) {
            settleForAward();
        } else if (state == LOADING) {
            if (!loadGame())
                setState(READY);
        } else if (state == PAUSE) {
            pauseGame();
        }
    }

    // 初始化动画播放数值
    private void initialize() {
        readyTimer = 0;
        hugeWaveSoundPlayable = false;
        hugeWaveSoundPlayed = false;
        finalWaveShowTimer = 0;
        loseAnimationPerformTimer = 0;
        winAnimationPerformTimer = 0;
        trophyGenerated = false;
    }

    // 隐藏所有界面组件
    private void hideComponents() {
        pauseMenu.setVisible(false);
        backToMenuDialog.setVisible(false);
        restartDialog.setVisible(false);
        loseGameDialog.setVisible(false);
        loadGameConfirmPanel.setVisible(false);
        pauseButton.setVisible(false);
    }

    private void waitForActivate() {
        initialize();
        COMMON_BGM_PLAYER.stop();
        FAST_BGM_PLAYER.stop();
        this.setVisible(false);
    }

    private void readyToStart() {
        initialize();
        hideComponents();
        this.setVisible(true);
        gameModel.setState(GameModel.State.READY);
        READY_SET_PLANT_PLAYER.start();
    }

    private boolean loadGame() {
        this.setVisible(true);
        gameModel = GameModel.load("gamesave/save");
        if (gameModel == null) {
            gameModel = new GameModel(720, 500, 30, new Level(50, 20));
            level = gameModel.getLevel();
            return false;
        }
        setState(PAUSE);
        level = gameModel.getLevel();
        loadGameConfirmPanel.setVisible(true);
        gameModel.setState(GameModel.State.PAUSED);
        return true;
    }

    private void startGame() {
        hideComponents();
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
        currentBGMPlayer.continuePlay();
        gameModel.setState(GameModel.State.RUNNING);
    }

    private void settleForAward() {
        hideComponents();
        COMMON_BGM_PLAYER.stop();
        FAST_BGM_PLAYER.stop();
        WIN_PLAYER.start();
    }

    private void pauseGame() {
        PAUSE_SOUND_PLAYER.start();
        if (gameModel.getState() != GameModel.State.LOSE)
            gameModel.setState(GameModel.State.PAUSED);
        hideComponents();
        pauseButton.setVisible(true);
        pauseButton.setEnabled(false);
        FAST_BGM_PLAYER.stop();
        COMMON_BGM_PLAYER.stop();
    }
}
