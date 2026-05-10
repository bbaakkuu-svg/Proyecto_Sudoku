package com.sudoku.elite;

/**
 * Core Sudoku engine representing the board and its validation rules.
 * Adheres to technical English standards for naming and documentation.
 */
public class Sudoku {
    public static final int SIZE = 9;
    public static final int SUBGRID_SIZE = 3;
    
    private int[][] board;
    private boolean[][] fixedCells;

    public Sudoku() {
        this.board = new int[SIZE][SIZE];
        this.fixedCells = new boolean[SIZE][SIZE];
    }

    /**
     * Checks if placing a value in a specific cell is valid according to Sudoku rules.
     * @param row row index (0-8)
     * @param col column index (0-8)
     * @param value value to place (1-9)
     * @return true if the movement is valid.
     */
    public boolean isValidMovement(int row, int col, int value) {
        if (value < 1 || value > 9) return false;
        
        // Row check
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == value) return false;
        }
        
        // Column check
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == value) return false;
        }
        
        // Subgrid (3x3) check
        int startRow = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int startCol = (col / SUBGRID_SIZE) * SUBGRID_SIZE;
        
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (board[startRow + i][startCol + j] == value) return false;
            }
        }
        
        return true;
    }

    public boolean placeNumber(int row, int col, int value) {
        if (fixedCells[row][col]) return false;
        if (value != 0 && !isValidMovement(row, col, value)) return false;
        
        board[row][col] = value;
        return true;
    }

    public int getValue(int row, int col) {
        return board[row][col];
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public void setFixed(int row, int col, boolean fixed) {
        fixedCells[row][col] = fixed;
    }

    public boolean isFixed(int row, int col) {
        return fixedCells[row][col];
    }

    /**
     * Verifies if the board is completely filled and valid.
     * @return true if resolved.
     */
    public boolean isResolved() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int val = board[i][j];
                if (val == 0) return false;
                
                // Temporarily clear to check validation
                board[i][j] = 0;
                if (!isValidMovement(i, j, val)) {
                    board[i][j] = val;
                    return false;
                }
                board[i][j] = val;
            }
        }
        return true;
    }

    public void clear() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
                fixedCells[i][j] = false;
            }
        }
    }
}
