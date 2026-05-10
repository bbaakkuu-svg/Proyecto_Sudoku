package com.sudoku.elite;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Audit Stress Test: 20 cycles of game generation and validation.
 */
public class AuditExecutionTest {

    @Test
    public void testStressGeneration() {
        Sudoku sudoku = new Sudoku();
        SudokuGenerator generator = new SudokuGenerator(sudoku);
        String[] difficulties = {"easy", "medium", "hard"};

        System.out.println("Starting Audit Stress Test (20 cycles)...");
        for (int i = 1; i <= 20; i++) {
            String diff = difficulties[i % 3];
            long start = System.currentTimeMillis();
            generator.generate(diff);
            long end = System.currentTimeMillis();
            
            assertTrue(isValidBoard(sudoku), "Cycle " + i + " generated an invalid board!");
            System.out.println("Cycle " + i + " [" + diff + "] - Time: " + (end - start) + "ms - SUCCESS");
        }
    }

    private boolean isValidBoard(Sudoku sudoku) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int val = sudoku.getValue(r, c);
                if (val != 0 && !sudoku.isValidMovement(r, c, val)) {
                    return false;
                }
            }
        }
        return true;
    }
}
