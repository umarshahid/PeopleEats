package ui;

import order.Order;
import order.OrderType;
import restaurant.MenuItems;
import restaurant.Restaurant;
import user.User;
import user.UserType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Restaurant UI form
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
                // Fetch orders from the database when the "Manage Orders" button is clicked
                List<Order> orders = fetchOrdersFromDatabase();
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

    // Method to fetch orders from the database
    private List<Order> fetchOrdersFromDatabase() {
        List<Order> orders = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
            String query = "SELECT * FROM orders";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    // Extract data from the ResultSet and create Order objects
                    String username = resultSet.getString("customer");
                    String itemName = resultSet.getString("item");
                    double price = resultSet.getDouble("price");
                    UserType userType = UserType.CUSTOMER; // Assuming all orders are for customers
                    OrderType orderType = OrderType.valueOf(resultSet.getString("order_type")); // Assuming order_type column in database

                    User user = new User(username, "dummyPassword", userType);
                    Restaurant restaurant = new Restaurant("DummyRestaurant", "DummyLocation"); // You can fetch restaurant details if needed
                    MenuItems menuItem = new MenuItems(itemName, price); // Create MenuItem object

                    // Create Order object and add it to the list
                    Order order = new Order(user, restaurant, orderType);
                    order.addItem(menuItem);
                    orders.add(order);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching orders from database!");
        }

        return orders;
    }
}
