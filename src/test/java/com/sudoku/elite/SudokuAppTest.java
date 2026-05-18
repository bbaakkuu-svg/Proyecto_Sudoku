package com.sudoku.elite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Unit tests for SudokuApp. */
public class SudokuAppTest {

    @Test
    public void testIsOperational() {
        SudokuApp app = new SudokuApp();
        assertTrue(app.isOperational(), "The app should be operational upon initialization");
    }

    @Test
    public void testMain() {
        // Testing main method - Handling HeadlessException for CI/CD environments
        try {
            SudokuApp.main(new String[] {});
        } catch (java.awt.HeadlessException e) {
            System.out.println("Headless environment detected, UI execution skipped in test.");
        }
    }
}
