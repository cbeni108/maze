import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class MyMaze extends JFrame implements KeyListener {

    private static final String[] FILE = { "maze1.txt", "maze2.txt" };
    private static final int CELL_WIDTH = 50;
    private static final int CELL_HEIGHT = 50;
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private static final int UP = -1;
    private static final int DOWN = 1;

    // Instance variables
    private int[][] maze;
    private JLabel[][] mazeLbls;
    private int row;
    private int col;
    private int entryX = -1;
    private int entryY = -1;
    private int exitX = -1;
    private int exitY = -1;
    private int currX = -1;
    private int currY = -1;
    private ImageIcon playerImg;
    private boolean hasWon;
    private int score;

    /**
     * Constructor
     */
    public MyMaze() {
        super("Maze");

        // Init maze from file
        initMaze();

        if (this.maze.length > 0) {
            // Find entry and exit
            findEntryExit();

            if ((this.entryX != -1) && (this.entryY != -1) && (this.exitX != -1) && (this.exitY != -1)) {
                // Draw maze
                drawMaze();

                // Set current position
                this.currX = this.entryX;
                this.currY = this.entryY;

                // Set player
                setPlayer();

                // Set score
                this.score = 0;
            } else
                System.out.println("No Entry/Exit point(s) found.");
        } else {
            System.out.println("No maze found.");
        }

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set key listener
        addKeyListener(this);
        requestFocus();
        this.hasWon = false;
    }

    /**
     * Initializes the maze from the input file
     */
    private void initMaze() {
        // Select a random file
        int n = (int) (Math.random() * 10) % 2;

        // Scanner to read from the file
        Scanner file = null;

        try {
            file = new Scanner(new File(FILE[n]));
            System.out.println("Reading file: " + FILE[n]);

            // Read file into string array
            String[] lines = new String[0];
            while (file.hasNextLine()) {
                int len = lines.length;
                lines = Arrays.copyOf(lines, len + 1);
                lines[len] = file.nextLine().replaceAll("\\s+", "");
            }

            // Init array size
            if (lines.length > 0) {
                this.row = lines.length;
                this.col = lines[0].length();
                this.maze = new int[row][col];

                // Fill maze array
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++)
                        this.maze[i][j] = (lines[i].charAt(j) == '0') ? 0 : 1;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + FILE[n]);
            System.exit(0);
        } finally {
            // Close scanner
            if (file != null)
                file.close();
        }
    }

    /**
     * Draws maze from the array
     */
    private void drawMaze() {

        // Set layout
        setLayout(null);

        // Set size
        getContentPane().setPreferredSize(new Dimension((col * CELL_WIDTH), (row * CELL_HEIGHT)));
        pack();

        // Draw grid
        ImageIcon tree = new ImageIcon("tree.png");
        // Resize image to grid size
        tree = new ImageIcon(tree.getImage().getScaledInstance(CELL_WIDTH, CELL_HEIGHT, Image.SCALE_DEFAULT));

        // Create items to collect images
        ImageIcon[] items = new ImageIcon[3];
        items[0] = new ImageIcon("pizza.png");
        items[1] = new ImageIcon("burger.png");
        items[2] = new ImageIcon("drink.png");

        // Resize image to grid size
        for (int i = 0; i < 3; i++)
            items[i] = new ImageIcon(items[i].getImage().getScaledInstance(CELL_WIDTH, CELL_HEIGHT, Image.SCALE_DEFAULT));

        // Init maze labels
        this.mazeLbls = new JLabel[row][col];

        int y = 0;
        for (int i = 0; i < row; i++) {
            int x = 0;

            for (int j = 0; j < col; j++) {
                this.mazeLbls[i][j] = new JLabel();
                this.mazeLbls[i][j].setBounds(x, y, CELL_WIDTH, CELL_HEIGHT);
                this.mazeLbls[i][j].setOpaque(true);

                if (this.maze[i][j] == 1)
                    this.mazeLbls[i][j].setIcon(tree);
                else {
                    // Get random number to decide whether to place an item
                    if (((int) (Math.random() * 100) % 2) == 0) {
                        // Decide which item to place
                        int index = (int) (Math.random() * 10) % 3;
                        this.mazeLbls[i][j].setIcon(items[index]);
                    }
                    this.mazeLbls[i][j].setBackground(Color.WHITE);
                }

                // Add label to the frame
                add(this.mazeLbls[i][j]);

                x += CELL_WIDTH;
            }
            y += CELL_HEIGHT;
        }
    }

    /**
     * Finds the entry and exit points
     *
     * @return
     */
    private void findEntryExit() {

        // Check 0th column
        for (int i = 0; i < row; i++) {
            if (this.maze[i][0] == 0) {
                this.entryX = i;
                this.entryY = 0;
                break;
            }
        }

        // Check 0th row
        for (int i = 0; i < col; i++) {
            if (this.maze[0][i] == 0) {
                if ((this.entryX == -1) && (this.entryY == -1)) {
                    this.entryX = 0;
                    this.entryY = i;
                    break;
                } else if ((this.exitX != -1) && (this.exitY != -1)) {
                    this.exitX = 0;
                    this.exitY = i;
                    break;
                }
            }
        }

        if (((this.entryX == -1) && (this.entryY == -1)) || ((this.exitX == -1) && (this.exitY == -1))) {
            // Check last column
            for (int i = 0; i < row; i++) {
                if (this.maze[i][col - 1] == 0)
                    if ((this.entryX == -1) && (this.entryY == -1)) {
                        this.entryX = i;
                        this.entryY = col - 1;
                        break;
                    } else if ((this.exitX == -1) && (this.exitY == -1)) {
                        this.exitX = i;
                        this.exitY = col - 1;
                        break;
                    }
            }

            // Check last row
            for (int i = 0; i < col; i++) {
                if (this.maze[row - 1][i] == 0) {
                    if ((this.entryX == -1) && (this.entryY == -1)) {
                        this.entryX = row - 1;
                        this.entryY = i;
                        break;
                    } else if ((this.exitX == -1) && (this.exitY == -1)) {
                        this.exitX = row - 1;
                        this.exitY = i;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Sets the Player icon on the grid
     */
    private void setPlayer() {
        playerImg = new ImageIcon("car.png");
        // Resize image to grid size
        playerImg = new ImageIcon(playerImg.getImage().getScaledInstance(CELL_WIDTH, CELL_HEIGHT, Image.SCALE_DEFAULT));
        this.mazeLbls[currX][currY].setIcon(playerImg);
    }

    /**
     * Sets the new current location
     */
    private void setNewLocation(int newX, int newY) {
        this.mazeLbls[currX][currY].setIcon(null);
        this.mazeLbls[currX][currY].setBackground(Color.WHITE);
        currX = newX;
        currY = newY;

        // Check if the newX, newY has a fruit
        if (this.mazeLbls[currX][currY].getIcon() != null)
            score += 1;

        this.mazeLbls[currX][currY].setIcon(playerImg);
    }

    /**
     * Check if a horizontal move is valid
     *
     * @param dir
     *            - (-1) for left, 1 for right
     */
    private void checkHorizontal(int dir) {
        if (dir == LEFT) {
            // Check if curr position is not the first column and there is no
            // block
            if ((currY > 0) && (this.maze[currX][currY - 1] == 0)) {
                setNewLocation(currX, currY - 1);
            }

        } else if (dir == RIGHT) {
            // Check if curr position is not the last column and there is no
            // block
            if ((currY < (col - 1)) && (this.maze[currX][currY + 1] == 0)) {
                setNewLocation(currX, currY + 1);
            }
        }
    }

    /**
     * Check if a vertical move is valid
     *
     * @param dir
     *            - (-1) for down, 1 for up
     */
    private void checkVertical(int dir) {
        if (dir == UP) {
            // Check if curr position is not the first row and there is no block
            if ((currX > 0) && (this.maze[currX - 1][currY] == 0)) {
                setNewLocation(currX - 1, currY);
            }

        } else if (dir == DOWN) {
            // Check if curr position is not the last row and there is no block
            if ((currX < (row - 1)) && (this.maze[currX + 1][currY] == 0)) {
                setNewLocation(currX + 1, currY);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (!hasWon) {
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    checkHorizontal(LEFT);
                    break;

                case KeyEvent.VK_UP:
                    checkVertical(UP);
                    break;

                case KeyEvent.VK_RIGHT:
                    checkHorizontal(RIGHT);
                    break;

                case KeyEvent.VK_DOWN:
                    checkVertical(DOWN);
            }

            // Check if the player exits the maze
            if ((currX == exitX) && (currY == exitY)) {
                this.hasWon = true;
                JOptionPane.showMessageDialog(this, "Score: " + this.score + "\nYou won!!");
            }
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // Do Nothing
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // Do Nothing
    }

    public static void main(String[] args) {
        MyMaze maze = new MyMaze();
        maze.setVisible(true);
    }
}



