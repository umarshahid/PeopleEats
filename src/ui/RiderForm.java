package ui;

import user.Driver;
import order.Order;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RiderForm {
    private JFrame frame;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton pursueDeliveryButton;
    private JTable onTheWayOrderTable;
    private DefaultTableModel onTheWayOrderTableModel;
    private JButton deliveredButton;

    public RiderForm(Driver rider) {
        frame = new JFrame("Rider UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

//        JPanel panel = new JPanel();
//        panel.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(2, 2)); // GridLayout with 2 rows and 2 columns

        // Create a table model for orders
        tableModel = new DefaultTableModel(new Object[]{"Order No.", "Customer", "Items", "Type", "State"}, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane);
//        panel.add(scrollPane, BorderLayout.WEST);

        // Create a table model for on-the-way orders
        onTheWayOrderTableModel = new DefaultTableModel(new Object[]{"Order No.", "Customer", "Items", "Type", "State"}, 0);
        onTheWayOrderTable = new JTable(onTheWayOrderTableModel);
        JScrollPane onTheWayScrollPane = new JScrollPane(onTheWayOrderTable);
        panel.add(onTheWayScrollPane);

// Create a button to pursue delivery
        pursueDeliveryButton = new JButton("Pursue Delivery");
        pursueDeliveryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected row
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Get the order number from the selected row
                    int orderNo = (int) tableModel.getValueAt(selectedRow, 0);
                    // Update the order status to "ON_THE_WAY" in the database
                    updateOrderState(orderNo, "ON_THE_WAY");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an order to pursue delivery.");
                }
            }
        });
        panel.add(pursueDeliveryButton);

        // Create button to mark as delivered
        deliveredButton = new JButton("Delivered");

        deliveredButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected row
                int selectedRow = onTheWayOrderTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Get the order number from the selected row
                    int orderNo = (int) onTheWayOrderTable.getValueAt(selectedRow, 0);
                    // Update the order status to "ON_THE_WAY" in the database
                    updateOrderState(orderNo, "DELIVERED");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an order to pursue delivery.");
                }
            }
        });

        panel.add(deliveredButton);



        frame.add(panel);
        frame.setVisible(true);

        // Fetch READY orders from the database when the form is initialized
        fetchReadyOrdersFromDatabase(tableModel, "READY");
    }

    // Method to fetch READY orders from the database
    private void fetchReadyOrdersFromDatabase() {
        // Clear existing rows in the table
        tableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
            String query = "SELECT * FROM orders WHERE state = 'READY'";
            try (Statement statement = connection.createStatement();

                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    int orderNo = resultSet.getInt("order_no");
                    String username = resultSet.getString("customer");
                    String itemName = resultSet.getString("item");
                    String orderType = resultSet.getString("order_type");
                    String orderState = resultSet.getString("state");

                    // Add a row to the table for each READY order
                    tableModel.addRow(new Object[]{orderNo, username, itemName, orderType, orderState});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching READY orders from database!");
        }
    }

    private void fetchReadyOrdersFromDatabase(DefaultTableModel tableModel, String stateToFetch) {
        // Clear existing rows in the table
        tableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
            String query = "SELECT * FROM orders WHERE state = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, stateToFetch);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int orderNo = resultSet.getInt("order_no");
                        String username = resultSet.getString("customer");
                        String itemName = resultSet.getString("item");
                        String orderType = resultSet.getString("order_type");
                        String orderState = resultSet.getString("state");

                        // Add a row to the table for each order with the specified state
                        tableModel.addRow(new Object[]{orderNo, username, itemName, orderType, orderState});
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching orders from database!");
        }
    }


    // Method to update the order status in the database
    private void updateOrderState(int orderNo, String newState) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
            // Construct the SQL update statement
            String updateQuery = "UPDATE orders SET state = ? WHERE order_no = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                // Set the parameters for the update statement
                preparedStatement.setString(1, newState); // Update the state
                preparedStatement.setInt(2, orderNo); // Set the order number

                // Execute the update statement
                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Order state updated successfully.");
                    // Refresh the table to reflect the updated status
                    fetchReadyOrdersFromDatabase(tableModel, "READY");
                    fetchReadyOrdersFromDatabase(onTheWayOrderTableModel, "ON_THE_WAY");
                } else {
                    System.out.println("Failed to update order state.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating order state in database!");
        }
    }

    // Method to update the order status to DELIVERED
    private void updateOrderStateToDelivered(int orderNo) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
            // Construct the SQL update statement
            String updateQuery = "UPDATE orders SET state = 'DELIVERED' WHERE order_no = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                // Set the parameter for the update statement
                preparedStatement.setInt(1, orderNo);

                // Execute the update statement
                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Order state updated to DELIVERED successfully.");
                    // Remove the order from the ON_THE_WAY order table
                    onTheWayOrderTableModel.removeRow(onTheWayOrderTable.getSelectedRow());
                } else {
                    System.out.println("Failed to update order state to DELIVERED.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating order state to DELIVERED in database!");
        }
    }
}
