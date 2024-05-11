package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginRegisterUI {
    private JFrame frame;
    private JPanel panel;
    private JButton loginButton;
    private JButton registerButton;

    public LoginRegisterUI() {
        // Create frame
        frame = new JFrame("Login/Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // Create panel
        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        // Create login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Login button clicked");
            }
        });
        panel.add(loginButton);

        // Create register button
        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Show dialog to enter ID and password
                showRegisterDialog();
            }
        });
        panel.add(registerButton);

        // Add panel to frame
        frame.add(panel);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Make the frame visible
        frame.setVisible(true);
    }

    private void showRegisterDialog() {
        JTextField idField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);

        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridLayout(2, 2));
        registerPanel.add(new JLabel("ID:"));
        registerPanel.add(idField);
        registerPanel.add(new JLabel("Password:"));
        registerPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(frame, registerPanel, "Register",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String password = new String(passwordField.getPassword());

            // Handle registration logic here
            System.out.println("ID: " + id);
            System.out.println("Password: " + password);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginRegisterUI();
            }
        });
    }
}

