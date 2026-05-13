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

    // UI Components that need localized text updates
    private JLabel headerTitleLabel;
    private JLabel themeLabel;
    private JLabel difficultyLabel;
    private JLabel languageLabel;
    private JButton newGameBtn;
    private JButton saveBtn;
    private JButton rankingBtn;
    private JButton undoBtn;
    private JButton redoBtn;
    private JButton hintBtn;
    private JComboBox<String> diffSelect;
    private JComboBox<String> langSelect;
    private JComboBox<SudokuTheme> themeSelect;

    public MainFrame() {
        this.sudoku = new Sudoku();
        this.generator = new SudokuGenerator(sudoku);
        this.commandManager = new CommandManager();
        this.boardPanel = new SudokuBoardPanel(sudoku, commandManager);
        this.commandManager.setOnUpdate(boardPanel::updateBoard);
        this.gameDAO = new GameDAO();

        setTitle(LanguageManager.getInstance().getString("app.title"));
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
        headerTitleLabel = new JLabel(LanguageManager.getInstance().getString("header.title"));
        headerTitleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        headerTitleLabel.setForeground(new Color(100, 180, 255));
        header.add(headerTitleLabel);
        return header;
    }

    private JPanel createSidePanel() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(new Color(45, 45, 45));
        side.setPreferredSize(new Dimension(250, 0));
        side.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        newGameBtn = createStyledButton(LanguageManager.getInstance().getString("btn.new_game"));
        saveBtn = createStyledButton(LanguageManager.getInstance().getString("btn.save_game"));
        rankingBtn = createStyledButton(LanguageManager.getInstance().getString("btn.rankings"));
        
        themeSelect = new JComboBox<>(SudokuTheme.values());
        themeSelect.setMaximumSize(new Dimension(200, 40));
        themeSelect.addActionListener(e -> {
            SudokuTheme theme = (SudokuTheme) themeSelect.getSelectedItem();
            applyGlobalTheme(theme);
        });
        
        diffSelect = new JComboBox<>(new String[]{
            LanguageManager.getInstance().getString("diff.easy"),
            LanguageManager.getInstance().getString("diff.medium"),
            LanguageManager.getInstance().getString("diff.hard")
        });
        diffSelect.setMaximumSize(new Dimension(200, 40));

        newGameBtn.addActionListener(e -> {
            newGameBtn.setEnabled(false);
            newGameBtn.setText(LanguageManager.getInstance().getString("btn.generating"));
            
            final String diff;
            int idx = diffSelect.getSelectedIndex();
            if (idx == 1) diff = "medium";
            else if (idx == 2) diff = "hard";
            else diff = "easy";
            
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
                    newGameBtn.setText(LanguageManager.getInstance().getString("btn.new_game"));
                }
            };
            worker.execute();
        });

        saveBtn.addActionListener(e -> handleSave());
        rankingBtn.addActionListener(e -> showRankings());

        undoBtn = createStyledButton(LanguageManager.getInstance().getString("btn.undo"));
        redoBtn = createStyledButton(LanguageManager.getInstance().getString("btn.redo"));
        undoBtn.setBackground(new Color(200, 200, 200));
        redoBtn.setBackground(new Color(200, 200, 200));

        undoBtn.addActionListener(e -> commandManager.undo());
        redoBtn.addActionListener(e -> commandManager.redo());

        hintBtn = createStyledButton(LanguageManager.getInstance().getString("btn.hint"));
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
                JOptionPane.showMessageDialog(this, 
                    LanguageManager.getInstance().getString("msg.hint_text", val, (row+1), (col+1)), 
                    LanguageManager.getInstance().getString("msg.hint_title"), 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        langSelect = new JComboBox<>(new String[]{"Español", "English"});
        langSelect.setMaximumSize(new Dimension(200, 40));
        langSelect.addActionListener(e -> {
            String code = langSelect.getSelectedIndex() == 0 ? "es" : "en";
            LanguageManager.getInstance().setLanguage(code);
            updateTexts();
        });

        themeLabel = new JLabel(LanguageManager.getInstance().getString("side.theme"));
        side.add(themeLabel);
        side.add(Box.createRigidArea(new Dimension(0, 5)));
        side.add(themeSelect);
        side.add(Box.createRigidArea(new Dimension(0, 20)));
        
        difficultyLabel = new JLabel(LanguageManager.getInstance().getString("side.difficulty"));
        side.add(difficultyLabel);
        side.add(Box.createRigidArea(new Dimension(0, 5)));
        side.add(diffSelect);
        side.add(Box.createRigidArea(new Dimension(0, 20)));

        languageLabel = new JLabel(LanguageManager.getInstance().getString("side.language"));
        side.add(languageLabel);
        side.add(Box.createRigidArea(new Dimension(0, 5)));
        side.add(langSelect);
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
        JOptionPane.showMessageDialog(this, LanguageManager.getInstance().getString("msg.game_saved"));
        // Logic to call GameDAO would go here with currentUserId
    }

    private void showRankings() {
        try {
            var tops = gameDAO.getTopRankings();
            String list = String.join("\n", tops);
            JOptionPane.showMessageDialog(this, 
                tops.isEmpty() ? LanguageManager.getInstance().getString("msg.no_scores") : list, 
                LanguageManager.getInstance().getString("msg.rankings_title"), 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, LanguageManager.getInstance().getString("msg.rank_error", e.getMessage()));
        }
    }

    private void updateTexts() {
        LanguageManager lm = LanguageManager.getInstance();
        setTitle(lm.getString("app.title"));
        headerTitleLabel.setText(lm.getString("header.title"));
        themeLabel.setText(lm.getString("side.theme"));
        difficultyLabel.setText(lm.getString("side.difficulty"));
        languageLabel.setText(lm.getString("side.language"));
        newGameBtn.setText(lm.getString("btn.new_game"));
        saveBtn.setText(lm.getString("btn.save_game"));
        rankingBtn.setText(lm.getString("btn.rankings"));
        undoBtn.setText(lm.getString("btn.undo"));
        redoBtn.setText(lm.getString("btn.redo"));
        hintBtn.setText(lm.getString("btn.hint"));
        
        // Update Difficulty ComboBox items
        int selectedDiff = diffSelect.getSelectedIndex();
        diffSelect.removeAllItems();
        diffSelect.addItem(lm.getString("diff.easy"));
        diffSelect.addItem(lm.getString("diff.medium"));
        diffSelect.addItem(lm.getString("diff.hard"));
        diffSelect.setSelectedIndex(selectedDiff);

        // Update Theme ComboBox items
        int selectedTheme = themeSelect.getSelectedIndex();
        themeSelect.removeAllItems();
        for (SudokuTheme t : SudokuTheme.values()) {
            themeSelect.addItem(t);
        }
        themeSelect.setSelectedIndex(selectedTheme);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
