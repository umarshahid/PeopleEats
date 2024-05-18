//package ui;
//
//import order.Order;
//import order.OrderState;
//import order.OrderType;
//import restaurant.MenuItems;
//import restaurant.Restaurant;
//import user.User;
//import user.UserType;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//// Restaurant UI form
//public class RestaurantForm {
//    private JFrame frame;
//    private JButton manageButton;
//    private JButton orderReadyButton;
//    private JTable orderTable;
//    private DefaultTableModel tableModel;
//
//    public RestaurantForm(Restaurant restaurant) {
//        frame = new JFrame("Restaurant UI");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 300);
//
//        JPanel panel = new JPanel();
//        panel.setLayout(new BorderLayout());
//
//        // Create a table model for orders
//        tableModel = new DefaultTableModel(new Object[]{"Customer", "Items", "Price", "Type", "State", "Order No."}, 0);
//        orderTable = new JTable(tableModel);
//        JScrollPane scrollPane = new JScrollPane(orderTable);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        manageButton = new JButton("Manage Orders");
//        manageButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                // Fetch orders from the database when the "Manage Orders" button is clicked
//                List<Order> orders = fetchOrdersFromDatabase();
//                updateOrderTable(orders); // Update the order table with fetched orders
//            }
//        });
//        panel.add(manageButton, BorderLayout.SOUTH);
//
//        orderReadyButton = new JButton("Order Ready");
//        orderReadyButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // Get the selected row(s) from the table
//                int[] selectedRows = orderTable.getSelectedRows();
//
//                // Update the state of the selected order(s) in the database
//                updateOrderState(selectedRows);
//            }
//        });
//        panel.add(orderReadyButton, BorderLayout.NORTH);
//
//        frame.add(panel);
//        frame.setVisible(true);
//    }
//
//    // Method to update the order table
//    public void updateOrderTable(List<Order> orders) {
//        tableModel.setRowCount(0); // Clear existing rows
//        for (Order order : orders) {
//            // Add a row to the table for each order
//            tableModel.addRow(new Object[]{
//                    order.getUser().getUsername(),
//                    order.getItems().get(0).getName(),
//                    order.getItems().get(0).getPrice(),
//                    order.getType(),
//                    order.getState(),
//                    order.getOrderNo()
//            });
//        }
//    }
//
//    private void updateOrderState(int[] selectedRows) {
//        // Iterate over the selected rows and update the state of the corresponding orders in the database
//        for (int row : selectedRows) {
//            Integer order_no = (Integer) tableModel.getValueAt(row, 5); // Assuming the username is in the first column
//
//            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
//                // Construct the SQL update statement
//                String updateQuery = "UPDATE orders SET state = ? WHERE order_no = ?";
//                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
//                    // Set the parameters for the update statement
//                    preparedStatement.setString(1, "READY"); // Update the state to "Ready"
//                    preparedStatement.setInt(2, order_no);
//
//
//                    // Execute the update statement
//                    int rowsUpdated = preparedStatement.executeUpdate();
//                    if (rowsUpdated > 0) {
//                        System.out.println("Order state updated successfully.");
//                        // re-fetch table and populate
//                        List<Order> orders = fetchOrdersFromDatabase();
//                        updateOrderTable(orders);
//                    } else {
//                        System.out.println("Failed to update order state.");
//                    }
//                }
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(frame, "Error updating order state in database!");
//            }
//        }
//    }
//
//    // Method to fetch orders from the database
//    private List<Order> fetchOrdersFromDatabase() {
//        List<Order> orders = new ArrayList<>();
//
//        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
//            String query = "SELECT * FROM orders";
//            try (Statement statement = connection.createStatement();
//                 ResultSet resultSet = statement.executeQuery(query)) {
//                while (resultSet.next()) {
//                    // Extract data from the ResultSet and create Order objects
//                    String username = resultSet.getString("customer");
//                    String itemName = resultSet.getString("item");
//                    double price = resultSet.getDouble("price");
//                    String orderState = resultSet.getString("state");
//                    int orderNo = resultSet.getInt("order_no");
//                    UserType userType = UserType.CUSTOMER; // Assuming all orders are for customers
//                    OrderType orderType = OrderType.valueOf(resultSet.getString("order_type")); // Assuming order_type column in database
//
//                    User user = new User(username, "dummyPassword", userType);
//                    Restaurant restaurant = new Restaurant("DummyRestaurant", "DummyLocation"); // You can fetch restaurant details if needed
//                    MenuItems menuItem = new MenuItems(itemName, price); // Create MenuItem object
//
//                    // Create Order object and add it to the list
//                    Order order = new Order(user, restaurant, orderType);
//                    OrderState state = null;
//                    if (orderState.equals("PREPARING")) {
//                        state =OrderState.PREPARING;
//                    }
//                    else if (orderState.equals("READY")) {
//                        state = OrderState.READY;
//                    }
//                    else if (orderState.equals("ON_THE_WAY")) {
//                        state = OrderState.ON_THE_WAY;
//                    }
//                    else if (orderState.equals("DELIVERED")) {
//                        state = OrderState.DELIVERED;
//                    }
//                    order.setState(state);
//                    order.setOrderNo(orderNo);
//                    order.addItem(menuItem);
//                    orders.add(order);
//                }
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(frame, "Error fetching orders from database!");
//        }
//
//        return orders;
//    }
//}

package ui;

import order.Order;
import order.OrderState;
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
import java.util.Timer;
import java.util.TimerTask;

// Restaurant UI form
public class RestaurantForm {
    private JFrame frame;
    private JButton orderReadyButton;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private Timer timer;

    public RestaurantForm(Restaurant restaurant) {
        frame = new JFrame("Restaurant UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create a table model for orders
        tableModel = new DefaultTableModel(new Object[]{"Customer", "Items", "Price", "Type", "State", "Order No."}, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        orderReadyButton = new JButton("Order Ready");
        orderReadyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected row(s) from the table
                int[] selectedRows = orderTable.getSelectedRows();

                // Update the state of the selected order(s) in the database
                updateOrderState(selectedRows);
            }
        });
        panel.add(orderReadyButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        // Start the periodic check for new orders
        startOrderPolling();
    }

    private void startOrderPolling() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Fetch orders from the database periodically
                List<Order> orders = fetchOrdersFromDatabase();
                SwingUtilities.invokeLater(() -> updateOrderTable(orders));
            }
        }, 0, 5000); // Poll every 5 seconds (5000 milliseconds)
    }

    // Method to update the order table
    public void updateOrderTable(List<Order> orders) {
        tableModel.setRowCount(0); // Clear existing rows
        for (Order order : orders) {
            // Add a row to the table for each order
            tableModel.addRow(new Object[]{
                    order.getUser().getUsername(),
                    order.getItems().get(0).getName(),
                    order.getItems().get(0).getPrice(),
                    order.getType(),
                    order.getState(),
                    order.getOrderNo()
            });
        }
    }

    private void updateOrderState(int[] selectedRows) {
        // Iterate over the selected rows and update the state of the corresponding orders in the database
        for (int row : selectedRows) {
            Integer orderNo = (Integer) tableModel.getValueAt(row, 5); // Assuming the order number is in the 6th column
            OrderState currentState = (OrderState) tableModel.getValueAt(row, 4); // Assuming the state is in the 5th column

            if (OrderState.PREPARING == currentState) {
                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
                    // Construct the SQL update statement
                    String updateQuery = "UPDATE orders SET state = ? WHERE order_no = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                        // Set the parameters for the update statement
                        preparedStatement.setString(1, "READY"); // Update the state to "Ready"
                        preparedStatement.setInt(2, orderNo);

                        // Execute the update statement
                        int rowsUpdated = preparedStatement.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Order state updated successfully.");
                        } else {
                            System.out.println("Failed to update order state.");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error updating order state in database!");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "You can only change the state of orders that are 'PREPARING'.", "Invalid Operation", JOptionPane.WARNING_MESSAGE);
            }
        }
        // Re-fetch table and populate
        List<Order> orders = fetchOrdersFromDatabase();
        updateOrderTable(orders);
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
                    String orderState = resultSet.getString("state");
                    int orderNo = resultSet.getInt("order_no");
                    UserType userType = UserType.CUSTOMER; // Assuming all orders are for customers
                    OrderType orderType = OrderType.valueOf(resultSet.getString("order_type")); // Assuming order_type column in database

                    User user = new User(username, "dummyPassword", userType);
                    Restaurant restaurant = new Restaurant("DummyRestaurant", "DummyLocation"); // You can fetch restaurant details if needed
                    MenuItems menuItem = new MenuItems(itemName, price); // Create MenuItem object

                    // Create Order object and add it to the list
                    Order order = new Order(user, restaurant, orderType);
                    OrderState state = null;
                    if (orderState.equals("PREPARING")) {
                        state = OrderState.PREPARING;
                    } else if (orderState.equals("READY")) {
                        state = OrderState.READY;
                    } else if (orderState.equals("ON_THE_WAY")) {
                        state = OrderState.ON_THE_WAY;
                    } else if (orderState.equals("DELIVERED")) {
                        state = OrderState.DELIVERED;
                    }
                    order.setState(state);
                    order.setOrderNo(orderNo);
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

    public static void main(String[] args) {
        // Sample usage
        new RestaurantForm(new Restaurant("SampleRestaurant", "SampleLocation"));
    }
}
