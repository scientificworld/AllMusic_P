package Color_yr.AllMusic.musicPlay.sendHud;

public class PosOBJ {
    private int x;
    private int y;

    public PosOBJ() {

    }

    public PosOBJ(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public PosOBJ copy() {
        return new PosOBJ(this.x, this.y);
    }
}
