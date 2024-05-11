package ui;
import order.Order;
import order.OrderType;
import restaurant.Restaurant;
import user.User;
import user.UserType;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class RestaurantForm {
    private JFrame frame;
    private JButton manageButton;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public RestaurantForm(Restaurant restaurant) {
        frame = new JFrame("Restaurant UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create a table model for orders
        tableModel = new DefaultTableModel(new Object[]{"Customer", "Items", "Type"}, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        manageButton = new JButton("Manage Orders");
        manageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Fetch orders when the "Manage Orders" button is clicked
                List<Order> orders = fetchOrdersFromDatabase(); // Example: Fetch orders from the database
                updateOrderTable(orders); // Update the order table with fetched orders
            }
        });
        panel.add(manageButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Method to update the order table
    public void updateOrderTable(List<Order> orders) {
        tableModel.setRowCount(0); // Clear existing rows
        for (Order order : orders) {
            // Add a row to the table for each order
            tableModel.addRow(new Object[]{order.getUser().getUsername(), order.getItems(), order.getType()});
        }
    }

    // Method to fetch orders from the database (example)
    private List<Order> fetchOrdersFromDatabase() {
        // Replace this with your actual database fetch logic
        // Example: Dummy data
        return List.of(
                new Order(new User("Customer1", "123456", UserType.CUSTOMER), new Restaurant("RestaurantA", "LocationA"), OrderType.DELIVERY),
                new Order(new User("Customer2", "123456", UserType.CUSTOMER), new Restaurant("RestaurantB", "LocationB"), OrderType.PICKUP)
        );
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RestaurantForm(new Restaurant("MyRestaurant", "MyLocation"));
        });
    }
}
