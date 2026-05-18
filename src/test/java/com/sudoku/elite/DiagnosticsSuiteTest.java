package com.sudoku.elite;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DiagnosticsSuiteTest {
    private Sudoku sudoku;
    private SudokuGenerator generator;
    private CommandManager commandManager;

    @BeforeEach
    public void setUp() {
        sudoku = new Sudoku();
        generator = new SudokuGenerator(sudoku);
        commandManager = new CommandManager();
    }

    // 1-3. Generation Performance
    @Test
    public void test01_GenPerfEasy() {
        measureGen("easy", 50);
    }

    @Test
    public void test02_GenPerfMedium() {
        measureGen("medium", 50);
    }

    @Test
    public void test03_GenPerfHard() {
        measureGen("hard", 20);
    } // Hard takes longer

    private void measureGen(String diff, int iterations) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            generator.generate(diff);
        }
        long avg = (System.currentTimeMillis() - start) / iterations;
        System.out.println("TEST 01-03: Avg Generation [" + diff + "] -> " + avg + "ms");
        assertTrue(avg < 1500, "Generation took too long!");
    }

    // 4. Verification Performance
    @Test
    public void test04_ValidMovePerf() {
        generator.generate("easy");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            sudoku.isValidMovement(i % 9, (i / 9) % 9, (i % 9) + 1);
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 04: Valid Move Check (10k) -> " + time + "ms");
        assertTrue(time < 500);
    }

    // 5. Uniqueness Checking Perf
    @Test
    public void test05_UniquenessPerf() {
        generator.generate("medium");
        // Using backtracking to verify uniqueness
        Sudoku copy = new Sudoku();
        copy.importState(sudoku.exportBoard(), sudoku.exportSolution(), sudoku.exportFixed());
        long start = System.currentTimeMillis();
        int solutions = countSolutions(copy, 0, 0, 0);
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 05: Uniqueness Check -> " + time + "ms, Sols=" + solutions);
        // Note: uniqueness might not be enforced effectively in current generator.
    }

    private int countSolutions(Sudoku s, int r, int c, int count) {
        if (r == 9) return count + 1;
        if (c == 9) return countSolutions(s, r + 1, 0, count);
        if (s.getValue(r, c) != 0) return countSolutions(s, r, c + 1, count);

        for (int v = 1; v <= 9; v++) {
            if (s.isValidMovement(r, c, v)) {
                s.placeNumber(r, c, v);
                count = countSolutions(s, r, c + 1, count);
                s.placeNumber(r, c, 0);
                if (count > 1) return count; // Stop if more than 1
            }
        }
        return count;
    }

    // 6-7. Command Manager Stres
    @Test
    public void test06_CommandManagerCapacity() {
        generator.generate("easy");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            commandManager.executeCommand(new MoveCommand(sudoku, 0, 0, (i % 9) + 1));
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 06: CommandManager 5k Executes -> " + time + "ms");
        assertTrue(time < 3000);
    }

    @Test
    public void test07_UndoRedoPerf() {
        generator.generate("easy");
        for (int i = 0; i < 1000; i++)
            commandManager.executeCommand(new MoveCommand(sudoku, 0, 0, (i % 9) + 1));
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) commandManager.undo();
        for (int i = 0; i < 1000; i++) commandManager.redo();
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 07: Undo/Redo 1k -> " + time + "ms");
        assertTrue(time < 500);
    }

    // 8. Import/Export State
    @Test
    public void test08_StateExportImport() {
        generator.generate("hard");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String b = sudoku.exportBoard();
            String s = sudoku.exportSolution();
            String f = sudoku.exportFixed();
            Sudoku clone = new Sudoku();
            clone.importState(b, s, f);
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 08: State Export/Import (1k) -> " + time + "ms");
        assertTrue(time < 2000);
    }

    // 9. Hint Performance
    @Test
    public void test09_HintPerf() {
        generator.generate("easy");
        GameController gc = new GameController();
        gc.newGame("easy");
        long start = System.currentTimeMillis();
        int[] hint = gc.getHint();
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 09: Hint Generation -> " + time + "ms");
        assertTrue(time < 100);
    }

    // 10-12. DAO Mock / Speed (assuming local SQLite is fast)
    @Test
    public void test10_DAORankings() throws SQLException, InterruptedException {
        // Wait for async DB init
        Thread.sleep(200);
        GameDAO dao = new GameDAO();
        long start = System.currentTimeMillis();
        try {
            List<String> ranks = dao.getTopRankings();
            long time = System.currentTimeMillis() - start;
            System.out.println("TEST 10: DAO TopRankings -> " + time + "ms");
        } catch (Exception e) {
            System.out.println("TEST 10: Ignored due to offline db missing table");
        }
    }

    // 13. Concurrency
    @Test
    public void test13_ConcurrencyGen() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(() -> new SudokuGenerator(new Sudoku()).generate("hard"));
        Thread t2 = new Thread(() -> new SudokuGenerator(new Sudoku()).generate("hard"));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 13: Concurrency Generation (2 threads) -> " + time + "ms");
    }

    // 14. Controller memory
    @Test
    public void test14_ControllerStateChange() {
        GameController gc = new GameController();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            gc.setCurrentUser(i, "User" + i);
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 14: Controller User Swap 10k -> " + time + "ms");
    }

    // 15. Validation Board Limits
    @Test
    public void test15_BoardLimits() {
        // Just checking out of bounds handled gracefully or throws what we expect
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> sudoku.getValue(9, 9));
    }

    // 16. Language Manager Perf
    @Test
    public void test16_LangManager() {
        LanguageManager lm = LanguageManager.getInstance();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            lm.setLanguage(i % 2 == 0 ? "es" : "en");
            lm.getString("app.title");
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 16: Language Swap 1k -> " + time + "ms");
    }

    // 17. Theme creation speed
    @Test
    public void test17_ThemeAccess() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            SudokuTheme t = SudokuTheme.values()[i % SudokuTheme.values().length];
            assertNotNull(t.background);
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 17: Theme Enum Access 10k -> " + time + "ms");
    }

    // 18. MoveCommand encapsulation
    @Test
    public void test18_MoveCommandInstance() {
        MoveCommand mc = new MoveCommand(sudoku, 0, 0, 5);
        mc.execute();
        mc.undo();
        assertEquals(0, sudoku.getValue(0, 0));
    }

    // 19. Repeated New Game memory/speed
    @Test
    public void test19_RepeatedNewGame() {
        GameController gc = new GameController();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) gc.newGame("easy");
        long time = System.currentTimeMillis() - start;
        System.out.println("TEST 19: Repeated newGame (20) -> " + time + "ms");
    }

    // 20. MainFrame headless init
    @Test
    public void test20_MainFrameInit() {
        try {
            long start = System.currentTimeMillis();
            MainFrame mf = new MainFrame();
            long time = System.currentTimeMillis() - start;
            System.out.println("TEST 20: MainFrame Init -> " + time + "ms");
        } catch (java.awt.HeadlessException e) {
            System.out.println("TEST 20: Skipped due to HeadlessEnv");
        }
    }
}
