import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Grid {
    //    private final int[][] rows;
    public final int[][] bombProximity;
    public final Set<Integer> bombMap;
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
        System.out.println(this.bombs);
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

    }

    public void setSafeZone(int row, int col) {
        int safeZoneSize = 1; // Defines a 3x3 grid (1 cell radius)
        int rows = getNumRows();
        int cols = getNumColumns();

        // Mark cells within the safe zone as "safe"
        for (int r = Math.max(0, row - safeZoneSize); r <= Math.min(rows - 1, row + safeZoneSize); r++) {
            for (int c = Math.max(0, col - safeZoneSize); c <= Math.min(cols - 1, col + safeZoneSize); c++) {
                if (isBombAtLocation(r, c)) {
                    relocateBomb(r, c);
                }
            }
        }

        // Recalculate bomb proximity counts
        recalculateBombProximity();
    }

    private void relocateBomb(int bombRow, int bombCol) {
        int rows = getNumRows();
        int cols = getNumColumns();

        while (true) {
            int newRow = (int) (Math.random() * rows);
            int newCol = (int) (Math.random() * cols);

            // Ensure the new location is not within the safe zone or already a bomb
            if (!isBombAtLocation(newRow, newCol) && !isInSafeZone(newRow, newCol, bombRow, bombCol)) {
                bombProximity[newRow][newCol] = -1;
                bombProximity[bombRow][bombCol] = 0;
                break;
            }
        }
    }

    private boolean isInSafeZone(int row, int col, int centerRow, int centerCol) {
        int safeZoneSize = 1;
        return Math.abs(row - centerRow) <= safeZoneSize && Math.abs(col - centerCol) <= safeZoneSize;
    }

    public void recalculateBombProximity() {
        // Reset bomb proximity counts
        for (int r = 0; r < bombProximity.length; r++) {
            for (int c = 0; c < bombProximity[0].length; c++) {
                calculateNearestBomb(r, c);
            }
        }
    }

    private void calculateNearestBomb(int row, int column) {
        for (int r = row - 1; r <= row + 1; r++) {
            if (r < 0 || r >= this.bombProximity.length) continue;

            for (int c = column - 1; c <= column + 1; c++) {
                if (c < 0 || c >= this.bombProximity[r].length) continue;
                if (r == row && c == column) continue; // Skip the center cell

                if (bombProximity[r][c] == -1) {
                    ++bombProximity[row][column];
                }
            }
        }
    }

}


