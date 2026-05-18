package com.sudoku.elite;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Dialog for User Login and Registration.
 */
public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final UserDAO userDAO;
    private int authenticatedUserId = -1;
    private String authenticatedUsername = null;

    public LoginDialog(Frame parent) {
        super(parent, "Sudoku Elite - Access", true);
        this.userDAO = new UserDAO();
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());

        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Optional<Integer> userId = userDAO.login(username, password);
            if (userId.isPresent()) {
                authenticatedUserId = userId.get();
                authenticatedUsername = username;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int userId = userDAO.createUser(username, password);
            if (userId != -1) {
                JOptionPane.showMessageDialog(this, "Account created successfully! Please login.");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists or error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getAuthenticatedUserId() {
        return authenticatedUserId;
    }

    public String getAuthenticatedUsername() {
        return authenticatedUsername;
    }
}
