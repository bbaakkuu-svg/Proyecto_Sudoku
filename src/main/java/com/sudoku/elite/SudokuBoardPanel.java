package com.sudoku.elite;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Premium Sudoku Board UI component.
 */
public class SudokuBoardPanel extends JPanel {
    private final Sudoku sudoku;
    private final JTextField[][] cells;
    private static final Color BG_DARK = new Color(33, 33, 33);
    private static final Color CELL_BG = new Color(45, 45, 45);
    private static final Color SELECTED_BG = new Color(60, 60, 60);
    private static final Color CLUE_COLOR = new Color(100, 180, 255);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color ERROR_COLOR = new Color(255, 100, 100);

    public SudokuBoardPanel(Sudoku sudoku) {
        this.sudoku = sudoku;
        this.cells = new JTextField[Sudoku.SIZE][Sudoku.SIZE];
        setLayout(new GridLayout(Sudoku.SIZE, Sudoku.SIZE));
        setBackground(BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initializeCells();
    }

    private void initializeCells() {
        for (int row = 0; row < Sudoku.SIZE; row++) {
            for (int col = 0; col < Sudoku.SIZE; col++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(new Font("Inter", Font.BOLD, 24));
                cell.setBackground(CELL_BG);
                cell.setForeground(TEXT_COLOR);
                cell.setCaretColor(TEXT_COLOR);
                
                // Borders to simulate 3x3 blocks
                int top = (row % 3 == 0) ? 2 : 1;
                int left = (col % 3 == 0) ? 2 : 1;
                int bottom = (row == 8) ? 2 : 0;
                int right = (col == 8) ? 2 : 0;
                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.GRAY));

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

    private void handleInput(JTextField cell, int row, int col) {
        String text = cell.getText();
        if (text.length() > 1) {
            cell.setText(text.substring(0, 1));
            return;
        }

        if (text.isEmpty()) {
            sudoku.placeNumber(row, col, 0);
            return;
        }

        try {
            int val = Integer.parseInt(text);
            if (val < 1 || val > 9 || !sudoku.placeNumber(row, col, val)) {
                cell.setForeground(ERROR_COLOR);
                if (val >= 1 && val <= 9) {
                    sudoku.placeNumber(row, col, val); // Force for visual error
                }
            } else {
                cell.setForeground(TEXT_COLOR);
            }
        } catch (NumberFormatException e) {
            cell.setText("");
        }
    }

    public void updateBoard() {
        for (int i = 0; i < Sudoku.SIZE; i++) {
            for (int j = 0; j < Sudoku.SIZE; j++) {
                int val = sudoku.getValue(i, j);
                cells[i][j].setText(val == 0 ? "" : String.valueOf(val));
                cells[i][j].setEditable(!sudoku.isFixed(i, j));
                cells[i][j].setForeground(sudoku.isFixed(i, j) ? CLUE_COLOR : TEXT_COLOR);
            }
        }
    }

    private void highlightRelated(int row, int col) {
        for (int i = 0; i < Sudoku.SIZE; i++) {
            for (int j = 0; j < Sudoku.SIZE; j++) {
                if (i == row || j == col) {
                    cells[i][j].setBackground(SELECTED_BG);
                } else {
                    cells[i][j].setBackground(CELL_BG);
                }
            }
        }
        cells[row][col].setBackground(new Color(80, 80, 80));
    }
}
