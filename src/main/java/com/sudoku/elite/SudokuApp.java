package com.sudoku.elite;

/** Main application class for Sudoku Elite. */
public class SudokuApp {
    public static void main(String[] args) {
        System.out.println("SUDOKU ELITE - Starting UI Engine...");
        javax.swing.SwingUtilities.invokeLater(
                () -> {
                    new MainFrame().setVisible(true);
                });
    }

    /**
     * Professional status check for the engine.
     *
     * @return true if operational.
     */
    public boolean isOperational() {
        return true;
    }
}
