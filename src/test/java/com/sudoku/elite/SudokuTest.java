package com.sudoku.elite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Sudoku logic engine.
 */
public class SudokuTest {
    private Sudoku sudoku;

    @BeforeEach
    public void setup() {
        sudoku = new Sudoku();
    }

    @Test
    public void testValidMovement() {
        // Empty board, any placement 1-9 is valid
        assertTrue(sudoku.isValidMovement(0, 0, 5));
        
        // Place a 5 at (0,0)
        sudoku.placeNumber(0, 0, 5);
        
        // Same row: invalid
        assertFalse(sudoku.isValidMovement(0, 5, 5));
        
        // Same column: invalid
        assertFalse(sudoku.isValidMovement(5, 0, 5));
        
        // Same 3x3 block: invalid
        assertFalse(sudoku.isValidMovement(1, 1, 5));
        
        // Different block/row/col: valid
        assertTrue(sudoku.isValidMovement(5, 5, 5));
    }

    @Test
    public void testPlaceFixedCell() {
        sudoku.placeNumber(0, 0, 1);
        sudoku.setFixed(0, 0, true);
        
        // Try to overwrite fixed cell
        assertFalse(sudoku.placeNumber(0, 0, 2));
        assertEquals(1, sudoku.getValue(0, 0));
    }

    @Test
    public void testIsResolved() {
        assertFalse(sudoku.isResolved(), "Empty board should not be resolved");
        
        // Fill a 3x3 block incorrectly
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sudoku.placeNumber(i, j, 1); // This will fail validation after first placement
            }
        }
        assertFalse(sudoku.isResolved());
    }

    @Test
    public void testClear() {
        sudoku.placeNumber(0, 0, 5);
        sudoku.clear();
        assertEquals(0, sudoku.getValue(0, 0));
    }

    @Test
    public void testInvalidNumbers() {
        assertFalse(sudoku.isValidMovement(0, 0, 0));
        assertFalse(sudoku.isValidMovement(0, 0, 10));
    }
}
