package model;

public class Sun {
    private int x, y;
    /// 阳光数量
    private final int amount = 25;
    private State state;
    /// 从起点到目的地耗费的时间，单位ms
    private final int speed = 5000;
    /// 阳光停止下落的临界y值
    private int destination;
    private final String currentImagePath = "images/Sun.gif";
    private int timer = 0;

    public enum State {IDLE, MOVING}

    /// 移动下落的阳光
    public Sun(GameModel gameModel) {
        this.x = (int) (Math.random() * gameModel.getWidth());
        this.y = 0;
        this.state = State.MOVING;
        this.destination = (int) ((Math.random() + 1) * gameModel.getRows() / 2) * gameModel.getBlockHeight();
    }

    /// 位于row行col列的静止阳光
    public Sun(GameModel gameModel, int row, int col) {
        this.x = (int) ((col + 0.5) * gameModel.getBlockWidth());
        this.y = (int) ((row + 0.5) * gameModel.getBlockHeight());
        this.state = State.IDLE;
    }

    public void update(GameModel gameModel) {
        timer += gameModel.getUpdateGap();
        if (state == State.MOVING) {
            y += gameModel.getUpdateGap() * destination / speed;
            if (Math.abs(y - destination) < 10)
                state = State.IDLE;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    public boolean isTimeout() {
        return timer >= 8000;
    }
}
