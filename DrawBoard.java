import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    ArrayList<LabelCoord> labels = new ArrayList<>();
    int labelIndex;
    private int WIDTH = 640;
    private int HEIGHT = 640;
    private Board board;
    private boolean valid;
    JPanel panel;
    JLabel selectedLabel;
    Point[][] gridPositions;
    Point prevPt = null;
    Point currPt = null;
    Point imageCorner = null;
    Point lastPosition = null;
    // private DrawPanel panel = null;

    public DrawBoard(Board board) {

        this.pack();
        Insets insets = this.getInsets();
        int addedWidth = insets.left + insets.right;
        int addedHeight = insets.top + insets.bottom;

        this.board = board;
        this.setResizable(false);
        this.setSize(new Dimension(WIDTH + addedWidth, HEIGHT + addedHeight));
        this.setLayout(null);
        this.add(panel());
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
        
        for(int i = 0; i < gridPositions.length; i++) {
            for(int j = 0; j < gridPositions[0].length; j++) {
                gridPositions[i][j] = new Point(i*length + length/2, j*length + length/2);
            }
        }

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

    public void drawLabel(JLabel label, Point p) {
        label.setLocation((int)p.getX(), (int)p.getY());
    }

    /**
     * Draw the exact location of the piece after it is released
     * @param label
     * @param p
     */
    public void drawExactLocation(JLabel label, Point p) {
        int length = 20000;
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

        // Check if the piece being dragged collides with a nother piece when a move is made.
        for(LabelCoord tempLabel : labels) {
            if( // If the label 
                tempLabel.getPoint().getX() + 30 == curr.getX() &&
                tempLabel.getPoint().getY() + 30 == curr.getY()
            ) {
                if(tempLabel.getPiece().getColor().compareTo("Dark") == 0){
                    tempLabel.getLabel().setVisible(false);
                    // labels.remove(tempLabel); ------------- CONTINUE HERE -------------
                    System.out.println("Here!");
                    break;
                }
            }
        }
        label.setLocation((int)curr.getX() - 30, (int)curr.getY() - 30);
        if(!valid) {/* Do nothing */}
        else labels.get(labelIndex).setPoint(new Point((int)curr.getX() - 30, (int)curr.getY() - 30));
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
                    selectedLabel = label.getLabel();
                    imageCorner = label.getPoint();
                    valid = true;
                    break;
                }
                else {
                    selectedLabel = null;
                    imageCorner = null;
                    valid = false;
                    System.out.println("You have not picked a valid piece!");
                }
                labelIndex++;
            }
        }

        
        public void mouseReleased(MouseEvent e) {
            drawExactLocation(selectedLabel, prevPt);
        }
        
    }

    private class DragListener extends MouseMotionAdapter {
        
        public void mouseDragged(MouseEvent e) {
            currPt = e.getPoint();
            imageCorner.translate(
                (int)(currPt.getX()-prevPt.getX()),
                (int)(currPt.getY()-prevPt.getY())
            );
            prevPt = currPt;
            drawLabel(selectedLabel, currPt);
        }
        
    }
}
