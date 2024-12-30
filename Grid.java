import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class Grid {
//    private final int[][] rows;
    private final int[][] bombProximity;
    private final Set<Integer> bombMap;
    int bombs;

    public Grid(int rows, double mode) {
//        this.rows = new int[rows][rows];
        this.bombProximity = new int[rows][rows];
        this.bombMap = new HashSet<>();
        /*
        calculate the percentage of bombs to be displayed
        Ex: 9 * 9 board is 81 cells on a mode of easy will yield 8 mines

         */
        this.bombs = (int) (Math.pow(rows, 2) * (mode / 100));
        createGrid();
    }

    public boolean isValidHelper(int value) {
        return value >= 0;
    }

    public int isValid(int value) {
        if (isValidHelper(value)) {
            return value;
        } else return 0;
    }

    public int getNumRows() {
        return this.bombProximity.length;
    }

    public int getNumColumns() {
        return this.bombProximity.length;
    }

    public int getNumBombs() {
        return this.bombs;
    }

    public int getGridItemProximityCount(int row, int col) {
        return this.bombProximity[row][col];
    }

    public int[][] getRows() {
        return Arrays.copyOf(bombProximity, bombProximity.length);
    }

    public int[][] getCountGrid() {
        return Arrays.copyOf(bombProximity, bombProximity.length);
    }

    public boolean isBombAtLocation(int row, int column) {

        return bombProximity[row][column] == -1;
    }

    public int getCountAtLocation(int row, int column) {
        return bombProximity[row][column];
    }

    private void createGrid() {
        Random random = new Random();


        // Randomly place mines
        while (bombMap.size() < bombs) {
            int position = random.nextInt(this.bombProximity.length * this.bombProximity.length);
            this.bombMap.add(position);

        }

        // Place the mines on the board
        for (int position : bombMap) {
            int r = position / this.bombProximity.length;
            int c = position % this.bombProximity.length;
            this.bombProximity[r][c] = -1; // Place a mine
        }


        for (int r = 0; r < this.bombProximity.length; r++) {
            for (int c = 0; c < bombProximity[r].length; c++) {
                if (bombProximity[r][c] != -1) calculateNearestBomb(r, c);
            }
        }

        for (int r = 0; r < this.bombProximity.length; r++) {
            for (int c = 0; c < this.bombProximity[r].length; c++) {
                System.out.println(this.bombProximity[r][c]);
            }
        }
    }

    private void calculateNearestBomb(int row, int column) {
        for (int r = row - 1; r < row + 1; r++) {
            if (r < 0 || r > this.bombProximity.length) continue;

            for (int c = column - 1; c < column + 1; c++) {
                if (c < 0 || c > this.bombProximity[r].length) continue;
                if (bombProximity[r][c] == -1) {
                    ++bombProximity[row][column];
                }
            }

        }
    }


    static class MineSweeperJFrame extends JFrame {
        MineSweeperBoard currentMineSweeperBoard;
        public MineSweeperJFrame() {
            currentMineSweeperBoard = new MineSweeperBoard();
            add(currentMineSweeperBoard);

            setSize(500, 500);
            setVisible(true);
            setResizable(false);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

        }

        public void remove() {
            this.remove(currentMineSweeperBoard);
        }

        public static void main(String[] args) {
            javax.swing.SwingUtilities.invokeLater(MineSweeperJFrame::new);
        }

        private static class MineSweeperBoard extends JPanel implements ActionListener {
            private final JButton[][] board;
            private final Icon unopenedIcon;
            private final Map<String, ImageIcon> iconMap;
            private final Icon flags = new ImageIcon(new ImageIcon("src/main/resources/Minesweeper_flag.svg.png")
                    .getImage()
                    .getScaledInstance(50, 50, Image.SCALE_FAST));
            private Grid grid;
            private ImageIcon[] imageIcons;
            private int unopenedCells;

            public MineSweeperBoard() {
                grid = new Grid(8, 10);
                iconMap = new HashMap<>();
                board = new JButton[grid.getNumRows()][grid.getNumColumns()];
                Image unopened = new ImageIcon("src/main/resources/Minesweeper_unopened_square.svg.png").
                        getImage().getScaledInstance(50, 50, Image.SCALE_FAST);
                unopenedIcon = new ImageIcon(unopened);
                unopenedCells = grid.getNumRows() * grid.getNumColumns() - grid.getNumBombs();
                setLayout(new GridLayout(grid.getNumRows(), grid.getNumColumns()));
                try {
                    setImageIcons();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                displayBoard();
            }

            public void setImageIcons() throws IOException {
                SpriteReader bombProximityCount = new SpriteReader("bomb-proximity-count.png", 16, 16);
                List<ImageIcon> bombProximityList = bombProximityCount.getSpriteSheetIcons();
//                BufferedImage bombProximityCount = ImageIO.read(new File("bomb-proximity-count.png"));
                BufferedImage bombAndFlags = ImageIO.read(new File("flags-bombs.png"));
                BufferedImage uncheckedTiles = ImageIO.read(new File("unchecked-tiles.png"));


                int x = 0;
                int y = 0;
                int tileWidth = 16;
                int tileHeight = 16;

                for (int i = bombProximityList.size() - 1; i >= 0 ; i--) {
                    System.out.println(16 - i);
                    ImageIcon tileIcon = bombProximityList.get(i);
//                    ImageIcon tileIcon = new ImageIcon(tile);
                    iconMap.put(Integer.toString(i), tileIcon);
                }

                BufferedImage explodedBomb = bombAndFlags.getSubimage(x, 2, tileWidth, tileHeight);
                ImageIcon tileIcon = new ImageIcon(explodedBomb);
                iconMap.put("explodedBomb", tileIcon);

                BufferedImage unexploredBomb = bombAndFlags.getSubimage(x, 1, tileWidth, tileHeight);
               tileIcon = new ImageIcon(unexploredBomb);
                iconMap.put("unexploredBomb", tileIcon);


                BufferedImage uncheckedTile = uncheckedTiles.getSubimage(x, 1, tileWidth, tileHeight);
                tileIcon = new ImageIcon(uncheckedTile);
                iconMap.put("unchecked", tileIcon);
            }

            public void displayBoard() {
                for (int row = 0; row < board.length; row++) {
                    for (int col = 0; col < board[row].length; col++) {
                        board[row][col] = new JButton();
                        board[row][col].setActionCommand(row + "," + col); // Store row and col
                        Font bigFont = new Font(Font.SANS_SERIF, Font.BOLD, 36);
                        board[row][col].setFont(bigFont);
                        board[row][col].addActionListener(this);
                        board[row][col].setIcon(iconMap.get("unchecked"));

//                        board[row][col].addMouseListener(new MouseAdapter() {
//                            @Override
//                            public void mouseClicked(MouseEvent e) {
//                                super.mouseClicked(e);
//                                if (SwingUtilities.isRightMouseButton(e)) {
//                                    for (JButton[] jButtons : board) {
//                                        for (JButton jButton : jButtons) {
//                                            if (jButton == e.getSource() && jButton.getIcon() == unopenedIcon) {
//                                                jButton.setIcon(flags);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        });
                        board[row][col].setEnabled(true);
                        this.add(board[row][col]);

                    }
                }
            }

            private void setButtonFlag(JButton button) {
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (SwingUtilities.isRightMouseButton(e) && button.getIcon() == unopenedIcon) {
                            button.setIcon(flags);
                        }
                    }
                });
            }

            public void revealAllBombs(JButton e) {
                for (int position: grid.bombMap) {
                    int row = position / grid.bombProximity.length;
                    int col = position % grid.bombProximity.length;
                    board[row][col].setIcon(new ImageIcon(new ImageIcon(
                            "src/main/resources/Minesweeper_normalbomb.svg.png").
                            getImage().getScaledInstance(50, 50, Image.SCALE_FAST)));
                }
                createJPane("You Lose");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                JButton btnClicked = (JButton) e.getSource();
                String[] delimiter = e.getActionCommand().split(",");
                int row = Integer.parseInt(delimiter[0]);
                int col = Integer.parseInt(delimiter[1]);


                if (grid.isBombAtLocation(row, col)) {
                    revealAllBombs(btnClicked);
                    board[row][col].setIcon(new ImageIcon(new ImageIcon(
                            "src/main/resources/Minesweeper_redbomb.svg.png")
                            .getImage().getScaledInstance(50, 50, Image.SCALE_FAST)));
                    return;
                }

                int currentTileBombCount = this.grid.bombProximity[row][col];
//
//                if (currentTileBombCount == 0) {
//                    btnClicked.setIcon(iconMap.get);
//                }

                btnClicked.setIcon(iconMap.get(Integer.toString(currentTileBombCount)));


                // Iterate through the board to find which button was clicked
//                for (int row = 0; row < board.length; row++) {
//                    for (int col = 0; col < board[row].length; col++) {
//                        if (board[row][col] == btnClicked) {
//                            // Check if the clicked cell is a bomb
//                            if (grid.isBombAtLocation(row, col)) {
//                                // Set bomb image on the button
//                                board[row][col].setIcon(new ImageIcon(new ImageIcon(
//                                        "src/main/resources/Minesweeper_redbomb.svg.png")
//                                        .getImage().getScaledInstance(50, 50, Image.SCALE_FAST)));
//
//                                // Reveal all bombs on the board
//                                revealAllBombs(btnClicked);
//                                break;  // No need to check further once a bomb is clicked
//
//                            } else if (!grid.isBombAtLocation(row, col) &&
//                                    (board[row][col].getIcon() == unopenedIcon ||
//                                            board[row][col].getIcon() == flags)) {
//                                // Decrease the count of unopened cells
//                                unopenedCells--;
//
//                                // If the cell is not a bomb and has 0 surrounding bombs, reveal surrounding cells
//                                int count = grid.getCountAtLocation(row, col);
//                                if (count == 0) {
//                                    // Set the cell's icon based on the count (in this case, 0)
//                                    board[row][col].setIcon(imageIcons[count]);
//
//                                    // Reveal the surrounding cells
//                                    revealSurrounding(row, col);
//                                } else {
//                                    // Set the cell's icon to show the number of surrounding bombs
//                                    board[row][col].setIcon(imageIcons[count]);
//                                }
//                            }
//                            break;  // Stop iterating after handling the clicked button
//                        }
//                    }
//                }

//                // Check if the player has won the game
//                if (unopenedCells == grid.getNumBombs()) {
//                    createJPane("Congratulations! You Won!!!");
//                }
            }

            // Method to reveal surrounding cells
            private void revealSurrounding(int row, int col) {
                // Check all 8 surrounding cells
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        // Skip the current cell itself
                        if (i == 0 && j == 0) continue;

                        int newRow = row + i;
                        int newCol = col + j;

                        // Ensure the new position is within bounds
                        if (isInBounds(newRow, newCol)) {
                            // Check if the surrounding cell is unopened and needs to be revealed
                            if (board[newRow][newCol].getIcon() == unopenedIcon) {
                                int surroundingCount = grid.getCountAtLocation(newRow, newCol);
                                board[newRow][newCol].setIcon(imageIcons[surroundingCount]);

                                // If the surrounding cell has no bombs around it, recursively reveal its neighbors
                                if (surroundingCount == 0) {
                                    revealSurrounding(newRow, newCol);
                                }
                            }
                        }
                    }
                }
            }

            // Helper method to check if a position is within bounds
            private boolean isInBounds(int row, int col) {
                // Check if the row and column are within the valid range of the board
                return row >= 0 && row < board.length && col >= 0 && col < board[0].length;
            }


            public void createJPane(String s) {
                int yesNo = JOptionPane.showConfirmDialog(null, "Play Again?", s, JOptionPane.YES_NO_OPTION);
                if (yesNo == JOptionPane.YES_OPTION) {
                    unopenedCells = grid.getNumRows() * grid.getNumColumns() - grid.getNumBombs();
                    clearBoard();

                } else
                    System.exit(EXIT_ON_CLOSE);

            }

            public void clearBoard() {

                grid = new Grid(9, 10);
                for (JButton[] jButtons : board) {
                    for (JButton jButton : jButtons) {

                        jButton.setIcon(unopenedIcon);
                    }
                }
            }

            public void revealSurrounding(int row, int column, int target) {
                if ((row >= 0 && row < board.length) && (column >= 0) && (column < board[row].length)) {
                    if (!grid.isBombAtLocation(row, column)) {
                        board[row][column].setIcon(imageIcons[grid.getCountAtLocation(row, column)]);
                        unopenedCells--;
                    }
                }
                if (column != target && target < board.length) {
                    revealSurrounding(row, column + 1, target);
                }
            }
        }
    }
}


