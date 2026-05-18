package com.sudoku.elite;

/**
 * Core Sudoku engine representing the board and its validation rules. This class handles the 9x9
 * matrix, stores the solution for hints, and identifies fixed cells generated at the start of a
 * puzzle.
 */
public class Sudoku {
    /** The dimension of the Sudoku board (9x9). */
    public static final int SIZE = 9;

    /** The dimension of each subgrid (3x3). */
    public static final int SUBGRID_SIZE = 3;

    private int[][] board;
    private int[][] solutionBoard;
    private boolean[][] fixedCells;

    /** Initializes an empty Sudoku board with zero values and no fixed cells. */
    public Sudoku() {
        this.board = new int[SIZE][SIZE];
        this.solutionBoard = new int[SIZE][SIZE];
        this.fixedCells = new boolean[SIZE][SIZE];
    }

    /**
     * Checks if placing a value in a specific cell is valid according to Sudoku rules. Rules
     * checked: unique in row, unique in column, and unique in 3x3 subgrid.
     *
     * @param row row index (0-8)
     * @param col column index (0-8)
     * @param value value to place (1-9)
     * @return true if the movement follows Sudoku rules.
     */
    public boolean isValidMovement(int row, int col, int value) {
        if (value < 1 || value > 9) return false;

        // Row check
        for (int i = 0; i < SIZE; i++) {
            if (i != col && board[row][i] == value) return false;
        }

        // Column check
        for (int i = 0; i < SIZE; i++) {
            if (i != row && board[i][col] == value) return false;
        }

        // Subgrid (3x3) check
        int startRow = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int startCol = (col / SUBGRID_SIZE) * SUBGRID_SIZE;

        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                int r = startRow + i;
                int c = startCol + j;
                if ((r != row || c != col) && board[r][c] == value) return false;
            }
        }

        return true;
    }

    /**
     * Attempts to place a number on the board.
     *
     * @param row target row.
     * @param col target column.
     * @param value number to place.
     * @return false if the cell is fixed, true otherwise (even if the move is rule-invalid, to
     *     allow UI feedback).
     */
    public boolean placeNumber(int row, int col, int value) {
        if (fixedCells[row][col]) return false;
        board[row][col] = value;
        return true;
    }

    /**
     * Gets the current value at a cell.
     *
     * @param row row index.
     * @param col col index.
     * @return value (0 for empty).
     */
    public int getValue(int row, int col) {
        return board[row][col];
    }

    /**
     * Gets the solution value for a cell (used for hints).
     *
     * @param row row index.
     * @param col col index.
     * @return the correct solution value.
     */
    public int getSolutionValue(int row, int col) {
        return solutionBoard[row][col];
    }

    /**
     * Internal use: sets the solution value for a cell during generation.
     *
     * @param row row.
     * @param col col.
     * @param value correct value.
     */
    public void setSolutionValue(int row, int col, int value) {
        solutionBoard[row][col] = value;
    }

    /**
     * Overwrites the entire board state.
     *
     * @param board 2D array of integers.
     */
    public void setBoard(int[][] board) {
        this.board = board;
    }

    /**
     * Marks a cell as fixed (initial clues).
     *
     * @param row row.
     * @param col col.
     * @param fixed true to lock the cell.
     */
    public void setFixed(int row, int col, boolean fixed) {
        fixedCells[row][col] = fixed;
    }

    /**
     * Checks if a cell is fixed.
     *
     * @param row row index.
     * @param col col index.
     * @return true if locked.
     */
    public boolean isFixed(int row, int col) {
        return fixedCells[row][col];
    }

    /**
     * Verifies if the board is completely filled and valid.
     *
     * @return true if the puzzle is correctly solved.
     */
    public boolean isResolved() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int val = board[i][j];
                if (val == 0 || !isValidMovement(i, j, val)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Counts the number of solutions for the current board state. Used to ensure uniqueness during
     * generation.
     *
     * @return the number of solutions found (capped at 2 for performance).
     */
    public int countSolutions() {
        return solveAndCount(0);
    }

    private int solveAndCount(int index) {
        if (index == SIZE * SIZE) return 1;

        int row = index / SIZE;
        int col = index % SIZE;

        if (board[row][col] != 0) {
            return solveAndCount(index + 1);
        }

        int count = 0;
        for (int num = 1; num <= 9; num++) {
            if (isValidMovement(row, col, num)) {
                board[row][col] = num;
                count += solveAndCount(index + 1);
                board[row][col] = 0;
                if (count > 1) return count; // Optimization: stop if not unique
            }
        }
        return count;
    }

    public String exportBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) sb.append(board[i][j]);
        }
        return sb.toString();
    }

    public String exportSolution() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) sb.append(solutionBoard[i][j]);
        }
        return sb.toString();
    }

    public String exportFixed() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) sb.append(fixedCells[i][j] ? "1" : "0");
        }
        return sb.toString();
    }

    public void importState(String boardStr, String solutionStr, String fixedStr) {
        for (int i = 0; i < SIZE * SIZE; i++) {
            int r = i / SIZE;
            int c = i % SIZE;
            board[r][c] = Character.getNumericValue(boardStr.charAt(i));
            solutionBoard[r][c] = Character.getNumericValue(solutionStr.charAt(i));
            fixedCells[r][c] = fixedStr.charAt(i) == '1';
        }
    }

    /** Clears all cells, solution, and fixed status. */
    public void clear() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
                solutionBoard[i][j] = 0;
                fixedCells[i][j] = false;
            }
        }
    }
}
