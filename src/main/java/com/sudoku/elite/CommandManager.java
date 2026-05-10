package com.sudoku.elite;

import java.util.Stack;

/**
 * Command Pattern interface for board operations.
 */
interface SudokuCommand {
    void execute();
    void undo();
}

/**
 * Command specifically for placing a number in a cell.
 */
class MoveCommand implements SudokuCommand {
    private final Sudoku sudoku;
    private final int row, col, oldValue, newValue;

    public MoveCommand(Sudoku sudoku, int row, int col, int newValue) {
        this.sudoku = sudoku;
        this.row = row;
        this.col = col;
        this.newValue = newValue;
        this.oldValue = sudoku.getValue(row, col);
    }

    @Override
    public void execute() {
        // Direct board access to bypass "placeNumber" rules if necessary 
        // (but placeNumber is better to maintain consistency)
        sudoku.placeNumber(row, col, newValue);
    }

    @Override
    public void undo() {
        // Reset to old value
        sudoku.placeNumber(row, col, oldValue);
    }
}

/**
 * Manager to handle undo and redo stacks.
 */
public class CommandManager {
    private final Stack<SudokuCommand> undoStack = new Stack<>();
    private final Stack<SudokuCommand> redoStack = new Stack<>();
    private Runnable onUpdate;

    public CommandManager() {
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void executeCommand(SudokuCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo on new move
        if (onUpdate != null) onUpdate.run();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            SudokuCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            if (onUpdate != null) onUpdate.run();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            SudokuCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            if (onUpdate != null) onUpdate.run();
        }
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
