package com.sudoku.elite;

import javax.swing.*;
import java.awt.*;

/**
 * Main Premium Application Frame for Sudoku Elite.
 */
public class MainFrame extends JFrame {
    private final GameController controller;
    private final SudokuBoardPanel boardPanel;

    // UI Components that need localized text updates
    private JLabel headerTitleLabel;
    private JLabel themeLabel;
    private JLabel difficultyLabel;
    private JLabel languageLabel;
    private JButton newGameBtn;
    private JButton saveBtn;
    private JButton loadBtn;
    private JButton rankingBtn;
    private JButton undoBtn;
    private JButton redoBtn;
    private JButton hintBtn;
    private JComboBox<String> diffSelect;
    private JComboBox<String> langSelect;
    private JComboBox<SudokuTheme> themeSelect;
    private JLabel timerLabel;
    private Timer gameTimer;
    private int secondsElapsed;

    public MainFrame() {
        this.controller = new GameController();
        this.boardPanel = new SudokuBoardPanel(controller.getSudoku(), controller.getCommandManager());
        this.controller.getCommandManager().setOnUpdate(boardPanel::updateBoard);

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
        
        // KeyBindings
        setupKeyBindings();

        // Initial Generation
        startNewGame("easy");
        boardPanel.updateBoard();

        // Prompt for login after UI is shown if not in offline mode
        SwingUtilities.invokeLater(() -> {
            if (!DatabaseConnection.isOfflineMode()) {
                showLogin();
            } else {
                saveBtn.setEnabled(false);
                loadBtn.setEnabled(false);
                rankingBtn.setEnabled(false);
            }
        });
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(25, 25, 25));
        headerTitleLabel = new JLabel(LanguageManager.getInstance().getString("header.title"));
        headerTitleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        headerTitleLabel.setForeground(new Color(100, 180, 255));
        header.add(headerTitleLabel);
        
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Inter", Font.BOLD, 24));
        timerLabel.setForeground(Color.WHITE);
        header.add(Box.createHorizontalStrut(50));
        header.add(timerLabel);
        
        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            int m = secondsElapsed / 60;
            int s = secondsElapsed % 60;
            timerLabel.setText(String.format("%02d:%02d", m, s));
        });
        
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
        loadBtn = createStyledButton(LanguageManager.getInstance().getString("btn.load_game") == null ? "Load Game" : LanguageManager.getInstance().getString("btn.load_game"));
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
            LanguageManager.getInstance().getString("diff.hard"),
            "Custom..."
        });
        diffSelect.setMaximumSize(new Dimension(200, 40));

        newGameBtn.addActionListener(e -> {
            newGameBtn.setEnabled(false);
            newGameBtn.setText(LanguageManager.getInstance().getString("btn.generating"));
            
            String diff;
            int idx = diffSelect.getSelectedIndex();
            if (idx == 1) diff = "medium";
            else if (idx == 2) diff = "hard";
            else if (idx == 3) {
                String input = JOptionPane.showInputDialog(MainFrame.this, "Cantidad de pistas iniciales (17-64):", "Custom", JOptionPane.QUESTION_MESSAGE);
                if (input == null || input.trim().isEmpty()) { 
                    newGameBtn.setEnabled(true); 
                    newGameBtn.setText(LanguageManager.getInstance().getString("btn.new_game")); 
                    return; 
                }
                diff = "custom:" + input;
            }
            else diff = "easy";
            
            startNewGame(diff);
        });

        saveBtn.addActionListener(e -> handleSave());
        loadBtn.addActionListener(e -> handleLoad());
        rankingBtn.addActionListener(e -> showRankings());

        undoBtn = createStyledButton(LanguageManager.getInstance().getString("btn.undo"));
        redoBtn = createStyledButton(LanguageManager.getInstance().getString("btn.redo"));
        undoBtn.setBackground(new Color(200, 200, 200));
        redoBtn.setBackground(new Color(200, 200, 200));

        undoBtn.addActionListener(e -> { if (undoBtn.isEnabled()) controller.undo(); });
        redoBtn.addActionListener(e -> { if (redoBtn.isEnabled()) controller.redo(); });

        this.controller.getCommandManager().setOnUpdate(() -> {
            boardPanel.updateBoard();
            updateButtonStates();
        });
        updateButtonStates();

        hintBtn = createStyledButton(LanguageManager.getInstance().getString("btn.hint"));
        hintBtn.setBackground(new Color(255, 200, 100));

        hintBtn.addActionListener(e -> {
            int[] hint = controller.getHint();
            if (hint != null) {
                int row = hint[0];
                int col = hint[1];
                int val = hint[2];
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
        side.add(Box.createRigidArea(new Dimension(0, 5)));
        side.add(loadBtn);
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
                        if (sc instanceof JLabel) {
                            JLabel l = (JLabel) sc;
                            if (l.getText().startsWith("User: ")) {
                                l.setForeground(theme.accent);
                            } else {
                                l.setForeground(theme.text);
                            }
                        }
                    }
                }
            }
        }
        boardPanel.applyTheme(theme);
    }
    
    private void startNewGame(final String difficulty) {
        newGameBtn.setEnabled(false);
        newGameBtn.setText(LanguageManager.getInstance().getString("btn.generating"));
        if (gameTimer != null) gameTimer.stop();
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                controller.newGame(difficulty);
                return null;
            }
            @Override
            protected void done() {
                boardPanel.animateGeneration();
                newGameBtn.setEnabled(true);
                newGameBtn.setText(LanguageManager.getInstance().getString("btn.new_game"));
                secondsElapsed = 0;
                if (timerLabel != null) timerLabel.setText("00:00");
                if (gameTimer != null) gameTimer.restart();
                updateButtonStates();
            }
        };
        worker.execute();
    }

    private void updateButtonStates() {
        if (undoBtn != null) {
            undoBtn.setEnabled(controller.getCommandManager().canUndo());
            undoBtn.setBackground(undoBtn.isEnabled() ? new Color(200, 200, 200) : new Color(100, 100, 100));
        }
        if (redoBtn != null) {
            redoBtn.setEnabled(controller.getCommandManager().canRedo());
            redoBtn.setBackground(redoBtn.isEnabled() ? new Color(200, 200, 200) : new Color(100, 100, 100));
        }
    }
    
    private void setupKeyBindings() {
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK), "Undo");
        am.put("Undo", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { if (undoBtn.isEnabled()) controller.undo(); }
        });
        
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK), "Redo");
        am.put("Redo", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { if (redoBtn.isEnabled()) controller.redo(); }
        });
        
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK), "New");
        am.put("New", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { if (newGameBtn.isEnabled()) newGameBtn.doClick(); }
        });
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

    private void showLogin() {
        LoginDialog dialog = new LoginDialog(this);
        dialog.setVisible(true);
        if (dialog.getAuthenticatedUserId() != -1) {
            controller.setCurrentUser(dialog.getAuthenticatedUserId(), dialog.getAuthenticatedUsername());
            headerTitleLabel.setText("Sudoku Elite - " + controller.getCurrentUsername());
        }
    }

    private void handleSave() {
        if (controller.getCurrentUserId() == -1) {
            JOptionPane.showMessageDialog(this, "Please login to save your progress.", "Session Required", JOptionPane.WARNING_MESSAGE);
            showLogin();
            return;
        }
        try {
            int idx = diffSelect.getSelectedIndex();
            String diff = switch (idx) {
                case 1 -> "medium";
                case 2 -> "hard";
                default -> "easy";
            };
            controller.saveGame(diff);
            JOptionPane.showMessageDialog(this, LanguageManager.getInstance().getString("msg.game_saved"));
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLoad() {
        if (controller.getCurrentUserId() == -1) {
            JOptionPane.showMessageDialog(this, "Please login to load your progress.", "Session Required", JOptionPane.WARNING_MESSAGE);
            showLogin();
            return;
        }
        try {
            String loadedDiff = controller.loadGame();
            if (loadedDiff != null) {
                diffSelect.setSelectedIndex(loadedDiff.equals("hard") ? 2 : (loadedDiff.equals("medium") ? 1 : 0));
                boardPanel.updateBoard();
                JOptionPane.showMessageDialog(this, "Game loaded successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No saved game found for this user.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRankings() {
        try {
            var tops = controller.getTopRankings();
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
        diffSelect.addItem("Custom...");
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
        SwingUtilities.invokeLater(() -> {
            JFrame loading = new JFrame("Sudoku Elite");
            loading.setSize(400, 100);
            loading.setLocationRelativeTo(null);
            loading.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel loadingLabel = new JLabel("Connecting to database... Please wait.", SwingConstants.CENTER);
            loadingLabel.setFont(new Font("Inter", Font.BOLD, 14));
            loading.add(loadingLabel);
            loading.setVisible(true);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    try {
                        // Initialize database pool in background thread
                        Class.forName("com.sudoku.elite.DatabaseConnection");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    loading.dispose();
                    MainFrame frame = new MainFrame();
                    
                    if (DatabaseConnection.isOfflineMode()) {
                        frame.setTitle(frame.getTitle() + " [OFFLINE MODE]");
                        frame.headerTitleLabel.setText(frame.headerTitleLabel.getText() + " (Offline)");
                        frame.headerTitleLabel.setForeground(new Color(255, 100, 100)); // Red for offline
                        JOptionPane.showMessageDialog(frame, 
                            "Remote database unavailable.\nRunning in Local Offline Mode.", 
                            "Offline Mode", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                    
                    frame.setVisible(true);
                }
            }.execute();
        });
    }
}
