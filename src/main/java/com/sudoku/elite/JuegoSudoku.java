package com.sudoku.elite;

import java.util.Scanner;

/** Console-based game controller for Sudoku. */
public class JuegoSudoku {
    private final Sudoku sudoku;
    private final SudokuGenerator generator;
    private final Scanner scanner;

    public JuegoSudoku(Scanner scanner) {
        this.sudoku = new Sudoku();
        this.generator = new SudokuGenerator(sudoku);
        this.scanner = scanner;
    }

    public JuegoSudoku() {
        this(new Scanner(System.in));
    }

    public void start() {
        System.out.println("--- WELCOME TO SUDOKU ELITE ---");
        System.out.print("Select difficulty (easy, medium, hard): ");
        if (!scanner.hasNextLine()) return;
        String diff = scanner.nextLine();

        generator.generate(diff);

        while (!sudoku.isResolved()) {
            displayBoard();
            System.out.println("Enter your move (row col value) or 'quit': ");
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("quit")) break;

            String[] parts = input.split(" ");
            if (parts.length == 3) {
                try {
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    int v = Integer.parseInt(parts[2]);

                    if (!sudoku.placeNumber(r, c, v)) {
                        System.out.println("[ERROR] Invalid move or fixed cell!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[ERROR] Please enter numbers.");
                }
            }
        }

        if (sudoku.isResolved()) {
            displayBoard();
            System.out.println("CONGRATULATIONS! You solved the Sudoku.");
        }
    }

    private void displayBoard() {
        System.out.println("    0 1 2   3 4 5   6 7 8");
        System.out.println("  +-------+-------+-------+");
        for (int i = 0; i < Sudoku.SIZE; i++) {
            System.out.print(i + " | ");
            for (int j = 0; j < Sudoku.SIZE; j++) {
                int val = sudoku.getValue(i, j);
                System.out.print((val == 0 ? "." : val) + " ");
                if ((j + 1) % 3 == 0) System.out.print("| ");
            }
            System.out.println();
            if ((i + 1) % 3 == 0) System.out.println("  +-------+-------+-------+");
        }
    }

    public static void main(String[] args) {
        new JuegoSudoku().start();
    }
}
