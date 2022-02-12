/*https://youtu.be/qolh7P13kd8*/
import java.util.Arrays;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Grid {
    private boolean[][] bombGrid;
    private int[][] countGrind;
    private int numRows;
    private int numColumns;
    private int numBombs;
    private int bombsInArea = 0;
    private Random random;

    public Grid() {
        numRows = 10;
        numColumns = 10;
        numBombs = 25;
        random = new Random();
        bombGrid = new boolean[numRows][numColumns];
        countGrind = new int[numRows][numColumns];
        createBombGrind();
        createCountGrid();
    }

    public Grid(int rows, int columns) {
       this.numRows = isValid(rows);
       this.numColumns = isValid(columns);
       this.bombGrid = new boolean[numRows][numColumns];
       this.countGrind = new int[numRows][numColumns];
       this.numBombs = 25;
       this.random = new Random();
       createBombGrind();
       createCountGrid();

    }

    public Grid(int rows, int columns, int numBombs) {
        this.numRows = isValid(rows);
        this.numColumns = isValid(columns);
        this.numBombs = isValid(numBombs);
        this.bombGrid = new boolean[numRows][numColumns];
        this.countGrind = new int[numRows][numColumns];
        this.random = new Random();
        createBombGrind();
        createCountGrid();
    }
    public boolean isValidHelper(int value) {
        return value >= 0;
    }
    public int isValid(int value){
        if(isValidHelper(value)){
            return value;
        } else return 0;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumColumns() {
        return this.numColumns;
    }

    public int getNumBombs() {
        return this.numBombs;
    }

    public boolean[][] getBombGrid() {
        boolean [][] copyOfBombGrid = Arrays.copyOf(bombGrid,bombGrid.length);

        return copyOfBombGrid;
    }

    public int[][] getCountGrid() {
        int [][] copyOfCountGrid = Arrays.copyOf(countGrind, countGrind.length);
        return copyOfCountGrid;
    }

    public boolean isBombAtLocation(int row, int column) {

        return bombGrid[row][column];
    }

    public int getCountAtLocation(int row, int column) {
        return countGrind[row][column];
    }


  private  void createBombGrind() {
        int remainingBombs = numBombs;
        while (remainingBombs> 0){
            for (int i = 0; i < bombGrid.length; i++) {
                if (remainingBombs == 0){
                    break;
                }else {
                    for (int j = 0; j < bombGrid[i].length; j++) {
                        if (remainingBombs == 0) {
                            break;
                        } else if (random.nextInt(4) == 0 && !isBombAtLocation(i, j)) {
                            bombGrid[i][j] = true;
                            --remainingBombs;
                        }
                    }
                }
            }
        }
    }


    private void createCountGrid() {
        for(int row = 0; row < bombGrid.length; row++){
            for(int column = 0; column < bombGrid[row].length; column++){
             hasBomb(row-1,column-1,column+1);
             hasBomb(row,column-1,column+1);
             hasBomb(row+1,column-1,column+1);
             countGrind[row][column] = bombsInArea;
             bombsInArea = 0;
            }
        }
    }
    private void  hasBomb (int row, int column,int target) {
        if((row >= 0 && row < bombGrid.length) && (column >= 0) && (column < bombGrid[row].length)) {
            if (bombGrid[row][column]){
                bombsInArea++;
            }

        }
       if (column != target){
            hasBomb(row, column + 1,target);
        }
    }
}
class MineSweeperJFrame extends JFrame {
    private JPanel jpMain;
    private MineSweeperBoard mineSweeperBoard;

    public MineSweeperJFrame() {
        jpMain = new JPanel();
        mineSweeperBoard = new MineSweeperBoard();
        add(mineSweeperBoard);
        setSize(500, 500);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private class MineSweeperBoard extends JPanel implements ActionListener {
        private JButton[][] board;
        private Grid newGrid;
        private ImageIcon[] imageIcons;
        private Icon unopenedIcon;
        private int unopenedCells;
        private Icon flags = new ImageIcon(new ImageIcon("src/main/resources/Minesweeper_flag.svg.png")
                .getImage()
                .getScaledInstance(50,50,Image.SCALE_FAST));

        public MineSweeperBoard() {
            newGrid = new Grid();
            board = new JButton[newGrid.getNumRows()][newGrid.getNumColumns()];
            Image unopened = new ImageIcon("src/main/resources/Minesweeper_unopened_square.svg.png").
                    getImage().getScaledInstance(50, 50, Image.SCALE_FAST);
            unopenedIcon = new ImageIcon(unopened);
            unopenedCells = newGrid.getNumRows() * newGrid.getNumColumns() - newGrid.getNumBombs();
            setLayout(new GridLayout(newGrid.getNumRows(), newGrid.getNumColumns()));
            setImageIcons();
            displayBoard();
        }

        public void setImageIcons() {
            imageIcons = new ImageIcon[]{new ImageIcon("src/main/resources/Minesweeper_0.svg.png"),
                    new ImageIcon("src/main/resources/Minesweeper_1.png"),
                    new ImageIcon("src/main/resources/Minesweeper_2.svg.png"),
                    new ImageIcon("src/main/resources/Minesweeper_3.svg.png"),
                    new ImageIcon("src/main/resources/Minesweeper_4.svg.png"),
                    new ImageIcon("src/main/resources/Minesweeper_5.svg.png"),
                    new ImageIcon("src/main/resources/-Minesweeper_6.svg.png"),
                    new ImageIcon("src/main/resources/Minesweeper_7.svg.png"),
                    new ImageIcon("src/main/resources/Minesweeper_8.svg.png")};
            for(int i = 0; i < imageIcons.length; i++){
                imageIcons[i] = new ImageIcon(imageIcons[i].getImage().getScaledInstance(50,50,Image.SCALE_FAST));
            }
        }

        public void displayBoard() {

            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[row].length; col++) {

                    board[row][col] = new JButton();

                    Font bigFont = new Font(Font.SANS_SERIF, Font.BOLD, 36);
                    board[row][col].setFont(bigFont);
                    board[row][col].addActionListener(this);
                    board[row][col].setIcon(unopenedIcon);
                    board[row][col].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            super.mouseClicked(e);
                            if (SwingUtilities.isRightMouseButton(e)) {
                                for (JButton[] jButtons : board) {
                                    for (JButton jButton : jButtons) {
                                        if (jButton == e.getSource() && jButton.getIcon() == unopenedIcon) {
                                            jButton.setIcon(flags);
                                        }
                                    }
                                }
                            }
                        }
                    });
                    board[row][col].setEnabled(true);
                    this.add(board[row][col]);

                }
            }
        }

        public void revealAllBombs(JButton e) {
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[row].length; col++) {
                    if (board[row][col] != e && newGrid.isBombAtLocation(row, col)) {
                        board[row][col].setIcon(new ImageIcon(new ImageIcon(
                                "src/main/resources/Minesweeper_normalbomb.svg.png").
                                getImage().getScaledInstance(50, 50, Image.SCALE_FAST)));
                    }
                }
            }
            createJPane("You Lose");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btnClicked = (JButton) e.getSource();

            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[row].length; col++) {
                    if (board[row][col] == btnClicked) {
                        if (newGrid.isBombAtLocation(row, col)) {
                            board[row][col].setIcon(new ImageIcon(new ImageIcon(
                                    "src/main/resources/Minesweeper_redbomb.svg.png")
                                    .getImage().getScaledInstance(50, 50, Image.SCALE_FAST)));
                            revealAllBombs(btnClicked);
                        } else if (!newGrid.isBombAtLocation(row, col) && board[row][col].getIcon() == unopenedIcon
                                || board[row][col].getIcon() == flags) {
                            unopenedCells--;
                            if (newGrid.getCountAtLocation(row, col) == 0) {
                                board[row][col].setIcon(imageIcons[newGrid.getCountAtLocation(row, col)]);
                                revealSurrounding(row-1,col-1,col + 1);
                                revealSurrounding(row,col-1,col + 1);
                                revealSurrounding(row+1,col-1,col + 1);
                            } else if (newGrid.getCountAtLocation(row, col) != 0)
                                board[row][col].setIcon(imageIcons[newGrid.getCountAtLocation(row, col)]);

                        }

                        break;
                    }

                }

            }
            if(unopenedCells == newGrid.getNumBombs()){
                createJPane("Congratulations You Won!!!");
            }
        }

        public void createJPane(String s) {
            int yesNo = JOptionPane.showConfirmDialog(null, "Play Again?", s, JOptionPane.YES_NO_OPTION);
            if (yesNo == JOptionPane.YES_OPTION) {
                unopenedCells = newGrid.getNumRows() * newGrid.getNumColumns() - newGrid.getNumBombs();
                clearBoard();
            } else
                System.exit(EXIT_ON_CLOSE);

        }
        public void clearBoard() {
            newGrid = new Grid();
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[row].length; col++) {

                    board[row][col].setIcon(unopenedIcon);
                }
            }
        }
        public void revealSurrounding (int row, int column, int target) {
            if((row >= 0 && row < board.length) && (column >= 0) && (column < board[row].length)) {
                if (!newGrid.isBombAtLocation(row,column)){
                    board[row][column].setIcon(imageIcons[newGrid.getCountAtLocation(row, column)]);
                    unopenedCells--;
                }
            }
            if (column != target && target < board.length){
                revealSurrounding(row, column + 1,target);
            }
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MineSweeperJFrame mineSweeperJFrame = new MineSweeperJFrame();
            }
        });
    }
}


