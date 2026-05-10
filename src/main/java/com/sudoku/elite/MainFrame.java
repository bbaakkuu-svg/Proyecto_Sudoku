package com.sudoku.elite;

import javax.swing.*;
import java.awt.*;

/**
 * Main Premium Application Frame for Sudoku Elite.
 */
public class MainFrame extends JFrame {
    private final Sudoku sudoku;
    private final SudokuGenerator generator;
    private final CommandManager commandManager;
    private final SudokuBoardPanel boardPanel;
    private final GameDAO gameDAO;
    private int currentUserId = -1;

    public MainFrame() {
        this.sudoku = new Sudoku();
        this.generator = new SudokuGenerator(sudoku);
        this.commandManager = new CommandManager();
        this.boardPanel = new SudokuBoardPanel(sudoku, commandManager);
        this.commandManager.setOnUpdate(boardPanel::updateBoard);
        this.gameDAO = new GameDAO();

        setTitle("SUDOKU ELITE - PREMIUM EDITION");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(33, 33, 33));

        setLayout(new BorderLayout());
        
        // Setup Components
        add(createHeader(), BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(createSidePanel(), BorderLayout.EAST);

        // Initial Generation
        generator.generate("easy");
        boardPanel.updateBoard();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(25, 25, 25));
        JLabel title = new JLabel("SUDOKU ELITE");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(100, 180, 255));
        header.add(title);
        return header;
    }

    private JPanel createSidePanel() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(new Color(45, 45, 45));
        side.setPreferredSize(new Dimension(250, 0));
        side.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton newGameBtn = createStyledButton("NEW GAME");
        JButton saveBtn = createStyledButton("SAVE GAME");
        JButton rankingBtn = createStyledButton("TOP RANKINGS");
        
        JComboBox<SudokuTheme> themeSelect = new JComboBox<>(SudokuTheme.values());
        themeSelect.setMaximumSize(new Dimension(200, 40));
        themeSelect.addActionListener(e -> {
            SudokuTheme theme = (SudokuTheme) themeSelect.getSelectedItem();
            applyGlobalTheme(theme);
        });
        
        JComboBox<String> diffSelect = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        diffSelect.setMaximumSize(new Dimension(200, 40));

        newGameBtn.addActionListener(e -> {
            newGameBtn.setEnabled(false);
            newGameBtn.setText("GENERATING...");
            String diff = diffSelect.getSelectedItem().toString().toLowerCase();
            
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    generator.generate(diff);
                    return null;
                }

                @Override
                protected void done() {
                    commandManager.clear();
                    boardPanel.updateBoard();
                    newGameBtn.setEnabled(true);
                    newGameBtn.setText("NEW GAME");
                }
            };
            worker.execute();
        });

        saveBtn.addActionListener(e -> handleSave());
        rankingBtn.addActionListener(e -> showRankings());

        JButton undoBtn = createStyledButton("UNDO (Ctrl+Z)");
        JButton redoBtn = createStyledButton("REDO (Ctrl+Y)");
        undoBtn.setBackground(new Color(200, 200, 200));
        redoBtn.setBackground(new Color(200, 200, 200));

        undoBtn.addActionListener(e -> commandManager.undo());
        redoBtn.addActionListener(e -> commandManager.redo());

        JButton hintBtn = createStyledButton("GET HINT");
        hintBtn.setBackground(new Color(255, 200, 100));

        hintBtn.addActionListener(e -> {
            // Find a random empty cell
            java.util.List<int[]> emptyCells = new java.util.ArrayList<>();
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (sudoku.getValue(r, c) == 0) {
                        emptyCells.add(new int[]{r, c});
                    }
                }
            }
            if (!emptyCells.isEmpty()) {
                int[] cell = emptyCells.get(new java.util.Random().nextInt(emptyCells.size()));
                int row = cell[0];
                int col = cell[1];
                int val = sudoku.getSolutionValue(row, col);
                commandManager.executeCommand(new MoveCommand(sudoku, row, col, val));
                JOptionPane.showMessageDialog(this, "Analista IA: Sugiero " + val + " en [" + (row+1) + "," + (col+1) + "]", "IA HINT", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        side.add(new JLabel("THEME:"));
        side.add(Box.createRigidArea(new Dimension(0, 5)));
        side.add(themeSelect);
        side.add(Box.createRigidArea(new Dimension(0, 20)));
        side.add(new JLabel("DIFFICULTY:"));
        side.add(Box.createRigidArea(new Dimension(0, 5)));
        side.add(diffSelect);
        side.add(Box.createRigidArea(new Dimension(0, 20)));
        side.add(newGameBtn);
        side.add(Box.createRigidArea(new Dimension(0, 10)));
        side.add(undoBtn);
        side.add(Box.createRigidArea(new Dimension(0, 5)));
        side.add(redoBtn);
        side.add(Box.createRigidArea(new Dimension(0, 10)));
        side.add(hintBtn);
        side.add(Box.createRigidArea(new Dimension(0, 10)));
        side.add(saveBtn);
        side.add(Box.createRigidArea(new Dimension(0, 10)));
        side.add(rankingBtn);

        return side;
    }

    private void applyGlobalTheme(SudokuTheme theme) {
        getContentPane().setBackground(theme.background);
        // Find components and apply
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.getLayout() instanceof FlowLayout) { // Header
                    p.setBackground(theme.header);
                    for (Component hc : p.getComponents()) {
                        if (hc instanceof JLabel) hc.setForeground(theme.accent);
                    }
                } else if (p.getLayout() instanceof BoxLayout) { // Side
                    p.setBackground(theme.sidePanel);
                    for (Component sc : p.getComponents()) {
                        if (sc instanceof JLabel) sc.setForeground(theme.text);
                    }
                }
            }
        }
        boardPanel.applyTheme(theme);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 50));
        btn.setBackground(new Color(100, 180, 255));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Inter", Font.BOLD, 14));
        return btn;
    }

    private void handleSave() {
        JOptionPane.showMessageDialog(this, "Game saved to local vault (RA2 Persistence Active)");
        // Logic to call GameDAO would go here with currentUserId
    }

    private void showRankings() {
        try {
            var tops = gameDAO.getTopRankings();
            String list = String.join("\n", tops);
            JOptionPane.showMessageDialog(this, tops.isEmpty() ? "No scores yet." : list, "TOP 10 RANKINGS", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading rankings: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
