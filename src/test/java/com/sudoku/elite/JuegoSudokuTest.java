package com.sudoku.elite;

import org.junit.jupiter.api.Test;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JuegoSudoku CLI.
 */
public class JuegoSudokuTest {

    @Test
    public void testGameInitializationAndQuit() {
        // Simulate "easy" then "quit"
        String input = "easy\nquit\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        JuegoSudoku game = new JuegoSudoku(scanner);
        assertDoesNotThrow(game::start);
    }

    @Test
    public void testGameInvalidMoveInput() {
        // Simulate "easy", then invalid input, then "quit"
        String input = "easy\ninvalid\nquit\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        JuegoSudoku game = new JuegoSudoku(scanner);
        assertDoesNotThrow(game::start);
    }
}
