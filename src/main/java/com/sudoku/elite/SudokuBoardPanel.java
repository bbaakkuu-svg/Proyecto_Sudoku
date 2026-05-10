package com.sudoku.elite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Premium Sudoku Board UI component with Theme Support.
 */
public class SudokuBoardPanel extends JPanel {
    private final Sudoku sudoku;
    private final CommandManager commandManager;
    private final JTextField[][] cells;
    
    private SudokuTheme currentTheme = SudokuTheme.DARK;
    private static final Color ERROR_COLOR = new Color(255, 100, 100);

    public SudokuBoardPanel(Sudoku sudoku, CommandManager commandManager) {
        this.sudoku = sudoku;
        this.commandManager = commandManager;
        this.cells = new JTextField[Sudoku.SIZE][Sudoku.SIZE];
        setLayout(new GridLayout(Sudoku.SIZE, Sudoku.SIZE));
        
        initializeCells();
        applyTheme(SudokuTheme.DARK);
    }

    private void initializeCells() {
        for (int row = 0; row < Sudoku.SIZE; row++) {
            for (int col = 0; col < Sudoku.SIZE; col++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(new Font("Inter", Font.BOLD, 24));
                cell.setFocusTraversalKeysEnabled(true);
                
                final int r = row;
                final int c = col;

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        highlightRelated(r, c);
                    }
                });

                cell.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        handleInput(cell, r, c);
                    }
                });

                cells[row][col] = cell;
                add(cell);
            }
        }
    }

    public void applyTheme(SudokuTheme theme) {
        this.currentTheme = theme;
        setBackground(theme.background);
        setBorder(BorderFactory.createLineBorder(theme.header, 2));
        
        for (int r = 0; r < Sudoku.SIZE; r++) {
            for (int c = 0; c < Sudoku.SIZE; c++) {
                JTextField cell = cells[r][c];
                cell.setBackground(theme.sidePanel);
                cell.setCaretColor(theme.text);
                
                // Borders for subgrids
                int top = (r % 3 == 0) ? 2 : 1;
                int left = (c % 3 == 0) ? 2 : 1;
                int bottom = (r == 8) ? 2 : 1;
                int right = (c == 8) ? 2 : 1;
                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, theme.header));
                
                updateCellVisuals(r, c);
            }
        }
    }

    private void updateCellVisuals(int r, int c) {
        JTextField cell = cells[r][c];
        if (sudoku.isFixed(r, c)) {
            cell.setForeground(currentTheme.accent);
            cell.setFont(new Font("Inter", Font.BOLD, 24));
        } else {
            cell.setForeground(currentTheme.text);
            cell.setFont(new Font("Inter", Font.PLAIN, 24));
        }
    }

    public void updateBoard() {
        for (int i = 0; i < Sudoku.SIZE; i++) {
            for (int j = 0; j < Sudoku.SIZE; j++) {
                int val = sudoku.getValue(i, j);
                cells[i][j].setText(val == 0 ? "" : String.valueOf(val));
                cells[i][j].setEditable(!sudoku.isFixed(i, j));
                updateCellVisuals(i, j);
            }
        }
    }

    private void handleInput(JTextField cell, int row, int col) {
        String text = cell.getText();
        if (text.length() > 1) {
            cell.setText(text.substring(0, 1));
            return;
        }

        if (text.isEmpty()) {
            if (sudoku.getValue(row, col) != 0) {
                commandManager.executeCommand(new MoveCommand(sudoku, row, col, 0));
            }
            return;
        }

        try {
            int val = Integer.parseInt(text);
            if (val >= 1 && val <= 9) {
                if (val != sudoku.getValue(row, col)) {
                    commandManager.executeCommand(new MoveCommand(sudoku, row, col, val));
                }
                if (!sudoku.isValidMovement(row, col, val)) {
                    cell.setForeground(ERROR_COLOR);
                } else {
                    updateCellVisuals(row, col);
                }
            }
        } catch (NumberFormatException e) {
            if (sudoku.getValue(row, col) != 0) {
                commandManager.executeCommand(new MoveCommand(sudoku, row, col, 0));
            }
            cell.setText("");
        }
    }

    private void highlightRelated(int row, int col) {
        for (int i = 0; i < Sudoku.SIZE; i++) {
            for (int j = 0; j < Sudoku.SIZE; j++) {
                if (i == row || j == col) {
                    cells[i][j].setBackground(currentTheme.background);
                } else {
                    cells[i][j].setBackground(currentTheme.sidePanel);
                }
            }
        }
        cells[row][col].setBackground(currentTheme.accent.darker());
    }
}
