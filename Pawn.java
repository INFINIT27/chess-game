public class Pawn extends Piece {

    private final String name = "Pawn";
    private String path;
    private String color;

    public Pawn(int i, int j, String color) {
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
            this.path = "images/light_pawn.png";
        }
        else {
            this.path = "images/dark_pawn.png";
        }
    }

    public String getPath() {
        return this.path;
    }
}

