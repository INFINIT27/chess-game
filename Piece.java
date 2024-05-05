public abstract class Piece {
    private int i;
    private int j;

    public Piece(int i, int j) {
        this.i = i;
        this.j = j;
    }
    
    public abstract void move(int i, int j);
    public abstract String getPieceName();
    public abstract String getPath();
    public abstract String getColor();

    public int getI() {
        return this.i;
    }

    public int getJ() {
        return this.j;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setJ(int j) {
        this.j = j;
    }
}
