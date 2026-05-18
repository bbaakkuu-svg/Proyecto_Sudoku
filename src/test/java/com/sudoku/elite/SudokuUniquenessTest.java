package com.sudoku.elite;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SudokuUniquenessTest {

    @Test
    public void testGeneratedSudokuHasUniqueSolution() {
        Sudoku sudoku = new Sudoku();
        SudokuGenerator generator = new SudokuGenerator(sudoku);
        
        // Test for different difficulties
        String[] difficulties = {"easy", "medium", "hard"};
        
        for (String diff : difficulties) {
            generator.generate(diff);
            assertEquals(1, sudoku.countSolutions(), "Sudoku generated for " + diff + " should have exactly one solution.");
        }
    }
}
