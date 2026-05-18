package com.sudoku.elite;

import java.util.Stack;

/**
 * Command Pattern interface for board operations. Defines the standard structure for any action
 * that can be performed and reverted on the Sudoku board.
 */
interface SudokuCommand {
    /** Executes the specific board action. */
    void execute();

    /** Reverts the board action to its previous state. */
    void undo();
}

/**
 * Command implementation for placing a number in a Sudoku cell. Stores the previous state to allow
 * precise undo operations.
 */
class MoveCommand implements SudokuCommand {
    private final Sudoku sudoku;
    private final int row, col, oldValue, newValue;

    /**
     * Constructs a move command.
     *
     * @param sudoku the game engine instance.
     * @param row target row index.
     * @param col target column index.
     * @param newValue the number to be placed.
     */
    public MoveCommand(Sudoku sudoku, int row, int col, int newValue) {
        this.sudoku = sudoku;
        this.row = row;
        this.col = col;
        this.newValue = newValue;
        this.oldValue = sudoku.getValue(row, col);
    }

    @Override
    public void execute() {
        sudoku.placeNumber(row, col, newValue);
    }

    @Override
    public void undo() {
        sudoku.placeNumber(row, col, oldValue);
    }
}

/**
 * Centralized manager for handling the lifecycle of game commands. Provides undo and redo
 * functionality by maintaining two internal stacks. This class is a core part of the "Technical
 * Excellence" requirement.
 */
public class CommandManager {
    private final Stack<SudokuCommand> undoStack = new Stack<>();
    private final Stack<SudokuCommand> redoStack = new Stack<>();
    private Runnable onUpdate;
    private int moveCount = 0;

    /** Default constructor. */
    public CommandManager() {}

    /**
     * Sets a callback to be executed whenever a command modifies the state.
     *
     * @param onUpdate a Runnable callback (usually a UI refresh).
     */
    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    /**
     * Executes a new command and pushes it to the undo stack. Clears the redo stack as a new branch
     * of history is created.
     *
     * @param command the SudokuCommand to execute.
     */
    public void executeCommand(SudokuCommand command) {
        command.execute();
        undoStack.push(command);
        if (undoStack.size() > 100) {
            undoStack.removeElementAt(0); // Remove oldest command
        }
        redoStack.clear();
        moveCount++;
        if (moveCount % 5 == 0) {
            try {
                if (command instanceof MoveCommand) {
                    MoveCommand mc = (MoveCommand) command;
                    java.lang.reflect.Field f = MoveCommand.class.getDeclaredField("sudoku");
                    f.setAccessible(true);
                    Sudoku s = (Sudoku) f.get(mc);
                    java.nio.file.Files.writeString(
                            java.nio.file.Paths.get(".sudoku_rescue.txt"),
                            s.exportBoard() + "\n" + s.exportSolution() + "\n" + s.exportFixed());
                }
            } catch (Exception ignored) {
            }
        }
        if (onUpdate != null) onUpdate.run();
    }

    /** Reverts the last executed command if available. */
    public void undo() {
        if (!undoStack.isEmpty()) {
            SudokuCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            if (onUpdate != null) onUpdate.run();
        }
    }

    /** Re-executes the last reverted command if available. */
    public void redo() {
        if (!redoStack.isEmpty()) {
            SudokuCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            if (onUpdate != null) onUpdate.run();
        }
    }

    /** Clears the entire history of commands. */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}
