package com.sudoku.elite;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;

/**
 * Controller managing the business logic for Sudoku Elite.
 */
public class GameController {
    private final Sudoku sudoku;
    private final SudokuGenerator generator;
    private final CommandManager commandManager;
    private final GameDAO gameDAO;
    
    private int currentUserId = -1;
    private String currentUsername = null;

    public GameController() {
        this.sudoku = new Sudoku();
        this.generator = new SudokuGenerator(sudoku);
        this.commandManager = new CommandManager();
        this.gameDAO = new GameDAO();
    }

    public Sudoku getSudoku() { return sudoku; }
    public CommandManager getCommandManager() { return commandManager; }
    
    public int getCurrentUserId() { return currentUserId; }
    public String getCurrentUsername() { return currentUsername; }
    
    public void setCurrentUser(int userId, String username) {
        this.currentUserId = userId;
        this.currentUsername = username;
    }

    public void newGame(String difficulty) {
        generator.generate(difficulty);
        commandManager.clear();
    }

    public void saveGame(String difficulty) throws SQLException {
        if (currentUserId == -1) {
            throw new IllegalStateException("User not logged in");
        }
        gameDAO.saveGame(
            currentUserId, 
            sudoku.exportBoard(), 
            sudoku.exportSolution(), 
            sudoku.exportFixed(), 
            difficulty
        );
    }

    public String loadGame() throws SQLException {
        if (currentUserId == -1) {
            throw new IllegalStateException("User not logged in");
        }
        Map<String, String> data = gameDAO.getSavedGame(currentUserId);
        if (data != null) {
            sudoku.clear();
            sudoku.importState(data.get("board"), data.get("solution"), data.get("fixed"));
            commandManager.clear();
            return data.get("difficulty");
        }
        return null; // Indicates no saved game found
    }

    public List<String> getTopRankings() throws SQLException {
        return gameDAO.getTopRankings();
    }

    public void undo() {
        commandManager.undo();
    }

    public void redo() {
        commandManager.redo();
    }

    public int[] getHint() {
        // Smart Hint: Find a "Naked Single"
        List<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (sudoku.getValue(r, c) == 0) {
                    emptyCells.add(new int[]{r, c});
                    int possibleCount = 0;
                    int lastPossible = 0;
                    for (int n = 1; n <= 9; n++) {
                        if (sudoku.isValidMovement(r, c, n)) {
                            possibleCount++;
                            lastPossible = n;
                        }
                    }
                    if (possibleCount == 1) {
                        commandManager.executeCommand(new MoveCommand(sudoku, r, c, lastPossible));
                        return new int[]{r, c, lastPossible};
                    }
                }
            }
        }
        
        // Fallback: Random empty cell
        if (!emptyCells.isEmpty()) {
            int[] cell = emptyCells.get(new Random().nextInt(emptyCells.size()));
            int row = cell[0];
            int col = cell[1];
            int val = sudoku.getSolutionValue(row, col);
            commandManager.executeCommand(new MoveCommand(sudoku, row, col, val));
            return new int[]{row, col, val};
        }
        return null;
    }
}
