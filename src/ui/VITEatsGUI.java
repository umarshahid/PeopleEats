package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VITEatsGUI {
    private JFrame frame;
    private JPanel panel;
    private JLabel titleLabel;
    private JButton loginButton;
    private JButton registerButton;
    private JComboBox<String> restaurantComboBox;
    private JButton orderButton;

    public VITEatsGUI() {
        // Create frame
        frame = new JFrame("VIT Eats");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Create panel
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        // Create title label
        titleLabel = new JLabel("Welcome to VIT Eats");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);

        // Create login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Add logic to handle login
                JOptionPane.showMessageDialog(frame, "Login button clicked");
            }
        });
        panel.add(loginButton);

        // Create restaurant selection dropdown
        String[] restaurants = {"restaurant.restaurant.Restaurant A", "restaurant.restaurant.Restaurant B", "restaurant.restaurant.Restaurant C"};
        restaurantComboBox = new JComboBox<>(restaurants);
        panel.add(restaurantComboBox);

        // Create order button
        orderButton = new JButton("Place order.Order");
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Add logic to handle order placement
                JOptionPane.showMessageDialog(frame, "order.Order button clicked");
            }
        });
        panel.add(orderButton);

        // Add panel to frame
        frame.add(panel);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Make the frame visible
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new VITEatsGUI();
            }
        });
    }
}

