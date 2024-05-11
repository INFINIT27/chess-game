public class Rook extends Piece {

    private final String name = "Rook";
    private String path;
    private String color;
    private final int pieceValue = 5;

    public Rook(int i, int j, String color) {
        super(i, j);
        this.color = color;
        setImagePath(color);
    }

    @Override
    public void move(int i, int j) {
        this.setI(i);
        this.setJ(j);
    }

    public String getColor() {
        return this.color;
    }

    @Override
    public String getPieceName() {
        return this.name;
    }

    private void setImagePath(String color) {
        if(color.compareTo("Light") == 0) {
            this.path = "images/light_rook.png";
        }
        else {
            this.path = "images/dark_rook.png";
        }
    }

    public String getPath() {
        return this.path;
    }

    public int getPieceValue() {
        return this.pieceValue;
    }
}
