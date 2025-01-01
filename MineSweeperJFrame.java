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
import java.util.Queue;
import java.util.*;

public class MineSweeperJFrame extends JFrame {
    MineSweeperBoard currentMineSweeperBoard;

    public MineSweeperJFrame() {
        currentMineSweeperBoard = new MineSweeperBoard();
        add(currentMineSweeperBoard);
        setSize(375, 375);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(MineSweeperJFrame::new);
    }

    public void remove() {
        this.remove(currentMineSweeperBoard);
    }

    private static class MineSweeperBoard extends JPanel implements ActionListener {
        private final JButton[][] board;
        private final Map<String, ImageIcon> iconMap;
        private boolean firstClick = true;
        private int remainingCellCount;
        private Grid grid;

        public MineSweeperBoard() {
            grid = new Grid(8, 10);
            iconMap = new HashMap<>();
            board = new JButton[grid.getNumRows()][grid.getNumColumns()];
            remainingCellCount = grid.getNumRows() * grid.getNumBombs();

            setLayout(new GridLayout(grid.getNumRows(), grid.getNumColumns()));
            try {
                setImageIcons();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            displayBoard();
        }

        private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }


        public void setImageIcons() throws IOException {
            BufferedImage bombProximityCount = ImageIO.read(new File("bomb-proximity-count.png"));
            BufferedImage bombAndFlags = ImageIO.read(new File("flags-bombs.png"));
            BufferedImage uncheckedTiles = ImageIO.read(new File("unchecked-tiles.png"));


            int x = 0;
            int y = 0;
            int tileWidth = 16;
            int tileHeight = 16;

            for (int i = 0; i < bombProximityCount.getHeight() / 16; i++) {
                int currentY = bombProximityCount.getHeight() - tileHeight - 16 * i;

                BufferedImage tile = bombProximityCount.getSubimage(0, currentY, tileWidth, tileHeight);

                ImageIcon tileIcon = new ImageIcon(tile);
                iconMap.put(Integer.toString(i), scaleIcon(new ImageIcon(tile), 40, 40));
            }
            BufferedImage flag = bombAndFlags.getSubimage(0, 0, tileWidth, tileHeight);
            iconMap.put("flag", scaleIcon(new ImageIcon(flag), 40, 40));

            BufferedImage explodedBomb = bombAndFlags.getSubimage(x, tileHeight, tileWidth, tileHeight);
            iconMap.put("explodedBomb", scaleIcon(new ImageIcon(explodedBomb), 40, 40));

            BufferedImage unexploredBomb = bombAndFlags.getSubimage(x, 31, tileWidth, tileHeight);
            iconMap.put("unexploredBomb", scaleIcon(new ImageIcon(unexploredBomb), 40, 40));


            BufferedImage uncheckedTile = uncheckedTiles.getSubimage(x, 1, tileWidth, tileHeight);
            iconMap.put("unchecked", scaleIcon(new ImageIcon(uncheckedTile), 40, 40)); // Example size

        }

        private void updateButtonIcon(JButton button, String iconKey) {
            Icon scaledIcon = scaleIcon(iconMap.get(iconKey), button.getWidth(), button.getHeight());
            button.setIcon(scaledIcon);
        }

        public void displayBoard() {
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[row].length; col++) {
                    board[row][col] = new JButton();
                    JButton currentButton = board[row][col];
                    currentButton.setActionCommand(row + "," + col); // Store row and col
                    currentButton.addActionListener(this);
                    currentButton.setIcon(iconMap.get("unchecked"));
                    currentButton.setEnabled(true);

                    setButtonFlag(currentButton);
                    this.add(currentButton);


                }
            }
        }

        private void setButtonFlag(JButton button) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (SwingUtilities.isRightMouseButton(e)
                            && button.getIcon() == iconMap.get("unchecked")
                    ) {
                        System.out.println("Right clicked");
                        button.setIcon(iconMap.get("flag"));
                    }
                }
            });
        }

        public void revealAllBombs(JButton e) {
            String[] delimiter = e.getActionCommand().split(",");
            int clickedRow = Integer.parseInt(delimiter[0]);
            int clickedCol = Integer.parseInt(delimiter[1]);

            for (int position : grid.bombMap) {

                int row = position / grid.bombProximity.length;
                int col = position % grid.bombProximity.length;
                if (!(row == clickedRow && col == clickedCol))
                    board[row][col].setIcon(iconMap.get("unexploredBomb"));

            }
            createJPane("You Lose");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btnClicked = (JButton) e.getSource();
            String[] delimiter = e.getActionCommand().split(",");
            int row = Integer.parseInt(delimiter[0]);
            int col = Integer.parseInt(delimiter[1]);

            JButton currentButton = board[row][col];

//            if (firstClick) {
//                grid.setSafeZone(row, col);
//                firstClick = false;
//            }
            if (grid.isBombAtLocation(row, col)) {
                updateButtonIcon(currentButton, "explodedBomb");

                revealAllBombs(btnClicked);
                return;
            }

            int currentTileBombCount = this.grid.bombProximity[row][col];
            btnClicked.setIcon(iconMap.get(Integer.toString(currentTileBombCount)));
            revealSurrounding(row, col);

        }

        // Method to reveal surrounding cells DFS
        private void revealSurrounding(int row, int col) {

            Set<JButton> unique = new HashSet<>();

            Queue<JButton> bfsQueue = new LinkedList<>();

            bfsQueue.add(board[row][col]);

            while (!bfsQueue.isEmpty()) {
                JButton currentButton = bfsQueue.poll();

                String[] delimiter = currentButton.getActionCommand().split(",");
                row = Integer.parseInt(delimiter[0]);
                col = Integer.parseInt(delimiter[1]);

                for (int r = row - 1; r <= row + 1; r++) {
                    if (r < 0 || r >= grid.bombProximity.length) continue;
                    for (int c = col - 1; c <= col + 1; c++) {
                        if (c < 0 || c >= grid.bombProximity[r].length) continue;
                        if (grid.bombProximity[r][c] == 0) {
                            JButton current = board[r][c];
                            if (unique.add(current)) {
                                bfsQueue.add(current);
                                remainingCellCount--;
                                String currentCount = Integer.toString(grid.bombProximity[r][c]);
                                current.setIcon(iconMap.get(currentCount));
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
            int dialog = JOptionPane.showConfirmDialog(null, "Play Again?", s, JOptionPane.YES_NO_OPTION);
            if (dialog == JOptionPane.YES_OPTION) {
                clearBoard();

            } else
                System.exit(EXIT_ON_CLOSE);

        }

        public void clearBoard() {

            grid = new Grid(9, 10);
            displayBoard();
        }


    }
}
