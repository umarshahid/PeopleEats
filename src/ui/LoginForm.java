package ui;

import restaurant.Restaurant;
import user.Driver;
import user.User;
import user.UserType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null); // Center the frame on the screen

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                // Call method to authenticate user
                boolean authenticated = authenticateUser(username, password);
                if (authenticated) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Login successful!");
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Invalid username or password. Please try again.");
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open sign up form
                openSignUpForm();
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signupButton);

        add(panel);
        setVisible(true);
    }

    private void signupUser(String username, String password, String role) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, role);
                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Sign up successful! You can now login.");
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Sign up failed. Please try again.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(LoginForm.this, "Error signing up user!");
        }
    }


    private boolean authenticateUser(String username, String password) {
        // Implement authentication logic here by querying the database
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String role = resultSet.getString("role");
                        switch (role) {
                            case "customer":
                                openCustomerForm();
                                break;
                            case "restaurant":
                                openRestaurantForm();
                                break;
                            case "rider":
                                openRiderForm();
                                break;
                            default:
                                JOptionPane.showMessageDialog(LoginForm.this, "Invalid user role.");
                                break;
                        }
                        return true; // Authentication successful
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(LoginForm.this, "Error authenticating user!");
        }
        return false; // Authentication failed
    }

    private void openCustomerForm() {
        // Implement opening the customer form
        // For demonstration purposes, show a simple message dialog
        JOptionPane.showMessageDialog(this, "Opening customer form...");
        User customer = new User("Customer", "123456", UserType.CUSTOMER);
        new CustomerForm(customer);
    }

    private void openRestaurantForm() {
        // Implement opening the restaurant form
        // For demonstration purposes, show a simple message dialog
        JOptionPane.showMessageDialog(this, "Opening restaurant form...");
        Restaurant restaurant = new Restaurant("Restaurant", "Location");
        new RestaurantForm(restaurant);
    }

    private void openRiderForm() {
        // Implement opening the rider form
        // For demonstration purposes, show a simple message dialog
        JOptionPane.showMessageDialog(this, "Opening rider form...");
        user.Driver rider = new Driver("Rider");
        new RiderForm(rider);
    }


    private void openMainWindow() {
        // Implement opening the main application window
        // For demonstration purposes, show a simple message dialog
        JOptionPane.showMessageDialog(this, "Opening main window...");
    }

    private void openSignUpForm() {
        JFrame signUpFrame = new JFrame("Sign Up");
        signUpFrame.setSize(300, 200);
        signUpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        signUpFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JLabel roleLabel = new JLabel("Role:");
        String[] roles = {"customer", "restaurant", "rider"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        JButton signupButton = new JButton("Sign Up");

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String role = (String) roleComboBox.getSelectedItem();
                signupUser(username, password, role);
                signUpFrame.dispose();
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(new JLabel());
        panel.add(signupButton);

        signUpFrame.add(panel);
        signUpFrame.setVisible(true);
    }

}

