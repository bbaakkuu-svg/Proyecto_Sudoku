package com.sudoku.elite;

import java.util.Random;

/**
 * Handles the generation of Sudoku boards using backtracking.
 */
public class SudokuGenerator {
    private final Sudoku sudoku;
    private final Random random;

    public SudokuGenerator(Sudoku sudoku) {
        this.sudoku = sudoku;
        this.random = new Random();
    }

    /**
     * Generates a new valid board according to difficulty.
     * @param difficulty "easy", "medium", or "hard"
     */
    public void generate(String difficulty) {
        sudoku.clear();
        fillBoard();
        
        // Save the solved board as the reference solution
        for (int i = 0; i < Sudoku.SIZE; i++) {
            for (int j = 0; j < Sudoku.SIZE; j++) {
                sudoku.setSolutionValue(i, j, sudoku.getValue(i, j));
            }
        }
        
        removeNumbers(difficulty);
    }

    private boolean fillBoard() {
        for (int row = 0; row < Sudoku.SIZE; row++) {
            for (int col = 0; col < Sudoku.SIZE; col++) {
                if (sudoku.getValue(row, col) == 0) {
                    int[] numbers = getShuffledNumbers();
                    for (int num : numbers) {
                        if (sudoku.isValidMovement(row, col, num)) {
                            sudoku.placeNumber(row, col, num);
                            if (fillBoard()) return true;
                            sudoku.placeNumber(row, col, 0);
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private int[] getShuffledNumbers() {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = nums.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
        return nums;
    }

    private void removeNumbers(String difficulty) {
        int targetEmptyCells = switch (difficulty.toLowerCase()) {
            case "easy" -> 36;   // ~45 clues left
            case "medium" -> 46; // ~35 clues left
            case "hard" -> 56;   // ~25 clues left
            default -> 40;
        };

        int removed = 0;
        while (removed < targetEmptyCells) {
            int r = random.nextInt(Sudoku.SIZE);
            int c = random.nextInt(Sudoku.SIZE);
            if (sudoku.getValue(r, c) != 0) {
                sudoku.placeNumber(r, c, 0);
                removed++;
            }
        }

        // Set remaining as fixed
        for (int i = 0; i < Sudoku.SIZE; i++) {
            for (int j = 0; j < Sudoku.SIZE; j++) {
                if (sudoku.getValue(i, j) != 0) {
                    sudoku.setFixed(i, j, true);
                }
            }
        }
    }
}
