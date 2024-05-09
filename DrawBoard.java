import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class DrawBoard extends JFrame {

    private class LabelCoord {
        private JLabel label;
        private Point point;
        private Piece piece;

        public LabelCoord(JLabel label, Point point, Piece piece) {
            this.point = point;
            this.label = label;
            this.piece = piece;
        }

        public Point getPoint() {
            return this.point;
        }

        public JLabel getLabel() {
            return this.label;
        }

        public void setPoint(Point p) {
            this.point = p;
        }

        public Piece getPiece() {
            return this.piece;
        }
    }

    private ArrayList<LabelCoord> labels = new ArrayList<>();
    private int labelIndex;
    private int WIDTH = 640;
    private int HEIGHT = 640;
    private Board board;
    private boolean valid;
    
    private JPanel panel;
    private LabelCoord selectedLabel;
    private Point[][] gridPositions;
    private Point prevPt = null;
    private Point currPt = null;
    private Point imageCorner = null;
    private Point lastPosition = null;
    // private DrawPanel panel = null;

    /**
     * Constructor of the DrawBoard class.
     * @param board
     */
    public DrawBoard(Board board) {

        this.pack();
        Insets insets = this.getInsets();
        int addedWidth = insets.left + insets.right;
        int addedHeight = insets.top + insets.bottom;

        this.setIconImage(new ImageIcon("images/dark_king.png").getImage());
        this.setTitle("Chess Game");
        this.board = board;
        this.setResizable(false);
        this.setSize(new Dimension(WIDTH + addedWidth, HEIGHT + addedHeight));
        this.setLayout(null);
        this.add(panel());
    }

    /**
     * This function when called resets some variables for reuse.
     */
    private void resetVariables() {
        selectedLabel = null; 
        prevPt = null;
        currPt = null;
        imageCorner = null;
        lastPosition = null;
    }

    public JPanel panel() {
        panel = new JPanel();
        panel.setBounds(0, 0, WIDTH, HEIGHT);
        panel.setLayout(null);

        ClickerListener clickerListener = new ClickerListener();
        DragListener dragListener = new DragListener();
        panel.addMouseListener(clickerListener);
        panel.addMouseMotionListener(dragListener);

        int length = WIDTH / 8;
        boolean flip = true;

        Square[][] playingBoard = board.getBoard();
        gridPositions = new Point[8][8];
        
        // Define the center of each board square.
        for(int i = 0; i < gridPositions.length; i++) {
            for(int j = 0; j < gridPositions[0].length; j++) {
                gridPositions[i][j] = new Point(i*length + length/2, j*length + length/2);
            }
        }

        // Draw the labels for each piece on the board and store them in an array list
        // so that we can change them later on.
        for(int i = 0; i < playingBoard.length; i++) {
            for(int j = 0; j < playingBoard[0].length; j++) {
                if(playingBoard[j][i].getPiece() == null) {
                    continue;
                }
                else {
                    JLabel temp2 = new JLabel();
                    
                    temp2.setBounds(i*length + 10, j*length + 10, 60, 60);
                    labels.add(new LabelCoord(temp2, new Point(i*length + 10, j*length + 10), playingBoard[j][i].getPiece()));
                    
                    ImageIcon image = new ImageIcon(playingBoard[j][i].getPiece().getPath());
                    temp2.setIcon(image);
                    panel.add(temp2);
                }
            }
        }
        
        // Draw the board colors
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                JLabel temp = new JLabel();
                temp.setOpaque(true);
                temp.setBounds(i*length, j*length, length, length);
                if(flip) {
                    temp.setBackground(new Color(237, 224, 213));
                }
                else {
                    temp.setBackground(new Color(133, 133, 133));
                }
                flip = !flip;
                panel.add(temp);
            }
            flip = !flip;
        }

        return panel;
    }

    /**
     * Given a LabelCoord object and a point p, set the location
     * of the label in that LabelCoord to the point p.
     * @param label
     * @param p
     */
    public void drawLabel(LabelCoord label, Point p) {
        label.getLabel().setLocation((int)p.getX(), (int)p.getY());
    }

    /**
     * Draw the exact location of the piece after it is released
     * @param label
     * @param p
     */
    public void drawExactLocation(LabelCoord label, Point p) {
        System.out.println(labels.size());
        int length = Integer.MAX_VALUE;
        Point curr = null;
        for(int i = 0; i < gridPositions.length; i++) {
            for(int j = 0; j < gridPositions[0].length; j++) {
                int tempLength = (int)Math.sqrt(
                                                Math.pow(p.getX()-gridPositions[i][j].getX(), 2) +
                                                Math.pow(p.getY()-gridPositions[i][j].getY(), 2)
                                            );
                if(tempLength < length) {
                    length = tempLength;
                    curr = gridPositions[i][j]; // Generate the grid position that fits the point p
                }
            }
        }

        // ------------------------------------ ERROR ---------------------------------------
        // There seems to be an issue when capturing a piece in the right 3 coloumns with the light pieces.
        // And the bottom left squares with the dark pieces.


        // Check if the piece being dragged collides with a nother piece when a move is made.
        int index = 0;
        boolean lastPos = false;
        for(LabelCoord tempLabel : labels) {
            if( // If the label selected lands on another label/piece
                tempLabel.getPoint().getX() + 30 == curr.getX() &&
                tempLabel.getPoint().getY() + 30 == curr.getY()
            ) { // Check if the piece selected and the piece landing on have the same or different colors. 
                if(tempLabel.getPiece().getColor().compareTo(label.getPiece().getColor()) != 0){
                    System.out.println(
                        "The Piece being removed\n" + 
                        "Piece Name: " + tempLabel.getPiece().getPieceName() + "\n" +
                        "Piece Color: " + tempLabel.getPiece().getColor()
                    );
                    labels.get(index).getLabel().setVisible(false);
                    labels.remove(index);
                    
                    // Light Pieces are stored in an index in the ArrayList that is larger than the
                    // Dark Pieces, thus when we capture a dark piece with a light piece, we can
                    // decrement the labelIndex since the removed piece is before the held piece,
                    // while when you capture a light piece with a dark piece, the labelIndex doesn't
                    // have to be changed since the removed index is after the dark piece.
                    if(index < labelIndex) {labelIndex--;}
                    else {/* Do Nothing */}
                    break;
                }
                else if(tempLabel.getPiece().getColor().compareTo(label.getPiece().getColor()) == 0) {
                    lastPos = true;
                }
            }
            index++;
        }
        
        // Update both the label passed to the method as well as the label in the ArrayList
        if(!valid) {/* Do nothing */}
        else {
            if(lastPos) { // Check if the piece is attempting to take a piece of the same color
                label.getLabel().setLocation((int)lastPosition.getX() - 30, (int)lastPosition.getY() - 30);
                labels.get(labelIndex).setPoint(new Point((int)lastPosition.getX() - 30, (int)lastPosition.getY() - 30));
            }
            else {
                label.getLabel().setLocation((int)curr.getX() - 30, (int)curr.getY() - 30);
                labels.get(labelIndex).setPoint(new Point((int)curr.getX() - 30, (int)curr.getY() - 30));
            }
        }
        resetVariables();
    }

    /**
     * Get the closest grid position from the mouse click.
     * @param p
     * @return
     */
    public Point getClosestGridPosition(Point p) {
        Point closestGridPosition = null;
        int minLength = 100000;

        for(int i = 0; i < gridPositions.length; i++) {
            for(int j = 0; j < gridPositions[0].length; j++) {
                int tempLength = (int)Math.sqrt(
                    Math.pow(p.getX() - gridPositions[i][j].getX(), 2) +
                    Math.pow(p.getY() - gridPositions[i][j].getY(), 2) 
                );

                if(tempLength < minLength) {
                    minLength = tempLength;
                    closestGridPosition = gridPositions[i][j];
                }
            }
        }
        return closestGridPosition;
    }

    private class ClickerListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            
            prevPt = e.getPoint();
            Point closestGridPosition = getClosestGridPosition(e.getPoint());
            lastPosition = closestGridPosition;

            labelIndex = 0;
            for(LabelCoord label : labels) {
                if(
                    label.getPoint().getX() == (closestGridPosition.getX()-30) &&
                    label.getPoint().getY() == (closestGridPosition.getY()-30)
                ) {
                    selectedLabel = label;
                    imageCorner = label.getPoint();
                    valid = true;
                    System.out.println("Curr Piece Index: " + labelIndex);
                    break;
                }
                else {
                    selectedLabel = null;
                    imageCorner = null;
                    valid = false;
                    labelIndex++;
                }
            }
        }

        
        public void mouseReleased(MouseEvent e) {
            if(valid) drawExactLocation(selectedLabel, prevPt);
            else /* Do Nothing */;
        }
        
    }

    private class DragListener extends MouseMotionAdapter {
        
        public void mouseDragged(MouseEvent e) {
            if(valid){
                currPt = e.getPoint();
                imageCorner.translate(
                    (int)(currPt.getX()-prevPt.getX()),
                    (int)(currPt.getY()-prevPt.getY())
                );
                prevPt = currPt;
                drawLabel(selectedLabel, currPt);
            }
            else {
                /* Do Nothing */
            }
        }
    }
}
