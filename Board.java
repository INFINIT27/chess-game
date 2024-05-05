public class Board {

    private Square[][] board = new Square[8][8];

    public Board() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                board[i][j] = new Square(i, j);
            }
        }

        setLightPieces();
        setDarkPieces();
    }

    public void setLightPieces() {
        for(int i = 0; i < 8; i++) {
            board[6][i].setPiece(new Pawn(6, i, "Light"));
        }
        board[7][0].setPiece(new Rook(7, 0, "Light"));
        board[7][1].setPiece(new Knight(7, 1, "Light"));
        board[7][2].setPiece(new Bishop(7, 2, "Light"));
        board[7][3].setPiece(new Queen(7, 3, "Light"));
        board[7][4].setPiece(new King(7, 4, "Light"));
        board[7][5].setPiece(new Bishop(7, 5, "Light"));
        board[7][6].setPiece(new Knight(7, 6, "Light"));
        board[7][7].setPiece(new Rook(7, 7, "Light"));
    }

    public void setDarkPieces() {
        for(int i = 0; i < 8; i++) {
            board[1][i].setPiece(new Pawn(1, i, "Dark"));
        }
        board[0][0].setPiece(new Rook(0, 0, "Dark"));
        board[0][1].setPiece(new Knight(0, 1, "Dark"));
        board[0][2].setPiece(new Bishop(0, 2, "Dark"));
        board[0][3].setPiece(new Queen(0, 3, "Dark"));
        board[0][4].setPiece(new King(0, 4, "Dark"));
        board[0][5].setPiece(new Bishop(0, 5, "Dark"));
        board[0][6].setPiece(new Knight(0, 6, "Dark"));
        board[0][7].setPiece(new Rook(0, 7, "Dark"));
    }

    public Square[][] getBoard() {
        return this.board;
    }
}
