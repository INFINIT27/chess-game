import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();

        DrawBoard frame = new DrawBoard(board);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
