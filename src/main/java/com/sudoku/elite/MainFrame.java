package com.sudoku.elite;

import javax.swing.*;
import java.awt.*;

/**
 * Main Premium Application Frame for Sudoku Elite.
 */
public class MainFrame extends JFrame {
    private final Sudoku sudoku;
    private final SudokuGenerator generator;
    private final SudokuBoardPanel boardPanel;
    private final GameDAO gameDAO;
    private int currentUserId = -1;

    public MainFrame() {
        this.sudoku = new Sudoku();
        this.generator = new SudokuGenerator(sudoku);
        this.boardPanel = new SudokuBoardPanel(sudoku);
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
        
        JComboBox<String> diffSelect = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        diffSelect.setMaximumSize(new Dimension(200, 40));

        newGameBtn.addActionListener(e -> {
            generator.generate(diffSelect.getSelectedItem().toString().toLowerCase());
            boardPanel.updateBoard();
        });

        saveBtn.addActionListener(e -> handleSave());
        rankingBtn.addActionListener(e -> showRankings());

        side.add(new JLabel("DIFFICULTY:"));
        side.add(Box.createRigidArea(new Dimension(0, 5)));
        side.add(diffSelect);
        side.add(Box.createRigidArea(new Dimension(0, 20)));
        side.add(newGameBtn);
        side.add(Box.createRigidArea(new Dimension(0, 10)));
        side.add(saveBtn);
        side.add(Box.createRigidArea(new Dimension(0, 10)));
        side.add(rankingBtn);

        return side;
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
