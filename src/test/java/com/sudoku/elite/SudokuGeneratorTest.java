package com.sudoku.elite;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the SudokuGenerator.
 */
public class SudokuGeneratorTest {

    @Test
    public void testGenerationEasy() {
        Sudoku sudoku = new Sudoku();
        SudokuGenerator generator = new SudokuGenerator(sudoku);
        
        generator.generate("easy");
        
        int filledCount = 0;
        for (int i = 0; i < Sudoku.SIZE; i++) {
            for (int j = 0; j < Sudoku.SIZE; j++) {
                if (sudoku.getValue(i, j) != 0) {
                    filledCount++;
                    assertTrue(sudoku.isFixed(i, j));
                }
            }
        }
        
        // Easy is roughly 45 clues (81 - 36)
        assertTrue(filledCount >= 40 && filledCount <= 50, "Clue count for easy should be around 45");
    }

    @Test
    public void testGenerationMedium() {
        Sudoku sudoku = new Sudoku();
        SudokuGenerator generator = new SudokuGenerator(sudoku);
        generator.generate("medium");
        
        int filledCount = 0;
        for (int i = 0; i < Sudoku.SIZE; i++) {
            for (int j = 0; j < Sudoku.SIZE; j++) {
                if (sudoku.getValue(i, j) != 0) filledCount++;
            }
        }
        assertTrue(filledCount >= 30 && filledCount <= 40);
    }

    @Test
    public void testGenerationDefault() {
        Sudoku sudoku = new Sudoku();
        SudokuGenerator generator = new SudokuGenerator(sudoku);
        generator.generate("unknown"); // Should hit default case
        
        int filledCount = 0;
        for (int i = 0; i < Sudoku.SIZE; i++) {
            for (int j = 0; j < Sudoku.SIZE; j++) {
                if (sudoku.getValue(i, j) != 0) filledCount++;
            }
        }
        assertTrue(filledCount > 0);
    }
}
