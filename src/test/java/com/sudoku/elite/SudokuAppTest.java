package com.sudoku.elite;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SudokuApp.
 */
public class SudokuAppTest {

    @Test
    public void testIsOperational() {
        SudokuApp app = new SudokuApp();
        assertTrue(app.isOperational(), "The app should be operational upon initialization");
    }

    @Test
    public void testMain() {
        // Testing main method to ensure no exceptions and full coverage
        assertDoesNotThrow(() -> SudokuApp.main(new String[]{}));
    }
}
