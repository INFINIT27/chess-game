public class Square {
    
    private Piece piece;
    private int i;
    private int j;

    /**
     * Define the default constructor.
     * @param i
     * @param j
     */
    public Square(int i, int j) {
        this.i = i;
        this.j = j;
        this.piece = null;
    }

    /**
     * Set piece of this square
     * @param piece
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public int getI() {
        return this.i;
    }

    public int getJ() {
        return this.j;
    }

}
