//package ui;
//
//import order.Order;
//import order.OrderState;
//import order.OrderType;
//import restaurant.MenuItems;
//import restaurant.Restaurant;
//import user.User;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//// Customer UI form
//public class CustomerForm {
//    private JFrame frame;
//    private JRadioButton coffeeButton;
//    private JRadioButton juiceButton;
//    private JRadioButton burgerButton;
//    private JButton placeOrderButton;
//
//    public CustomerForm(User customer) {
//        frame = new JFrame("Customer UI");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 200);
//
//        JPanel panel = new JPanel();
//        panel.setLayout(new GridLayout(4, 1));
//
//        coffeeButton = new JRadioButton("Coffee");
//        juiceButton = new JRadioButton("Juice");
//        burgerButton = new JRadioButton("Burger");
//
//        ButtonGroup foodGroup = new ButtonGroup();
//        foodGroup.add(coffeeButton);
//        foodGroup.add(juiceButton);
//        foodGroup.add(burgerButton);
//
//        panel.add(coffeeButton);
//        panel.add(juiceButton);
//        panel.add(burgerButton);
//
//        placeOrderButton = new JButton("Place Order");
//        placeOrderButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                String selectedFood = "";
//                if (coffeeButton.isSelected()) {
//                    selectedFood = "Coffee";
//                } else if (juiceButton.isSelected()) {
//                    selectedFood = "Juice";
//                } else if (burgerButton.isSelected()) {
//                    selectedFood = "Burger";
//                }
//
//                if (!selectedFood.isEmpty()) {
//                    // Example: Place order for customer
//                    Restaurant restaurant = new Restaurant("Restaurant A", "Location A");
//                    Order order = new Order(customer, restaurant, OrderType.DELIVERY);
//                    order.setState(OrderState.PREPARING);
//                    order.addItem(new MenuItems(selectedFood, 10.0)); // Price is a placeholder
//                    // Add more order handling logic as needed
//
//                    // Example: Save order to database
//                    saveOrderToDatabase(order);
//
//                    JOptionPane.showMessageDialog(frame, "Order placed successfully!");
//                } else {
//                    JOptionPane.showMessageDialog(frame, "Please select a food item!");
//                }
//            }
//        });
//        panel.add(placeOrderButton);
//
//        frame.add(panel);
//        frame.setVisible(true);
//    }
//
//    // Method to save order to database
//    private void saveOrderToDatabase(Order order) {
//
//        try {
//            // Connect to the SQLite database
////            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/jhona/IdeaProjects/VIT Eats/src/orders.db");
//            Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db");
//
//            // Create a PreparedStatement to insert the order into the database
//            String query = "INSERT INTO orders (customer, item, price, order_type, state) VALUES (?, ?, ?, ?, ?)";
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setString(1, order.getUser().getUsername());
//            preparedStatement.setString(2, order.getItems().get(0).getName()); // Assuming only one item for simplicity
//            preparedStatement.setDouble(3, order.getItems().get(0).getPrice()); // Assuming only one item for simplicity
//            String deliveryType = (order.getType() == OrderType.DELIVERY) ? "DELIVERY" : "NOT DELIVERY";
//            preparedStatement.setString(4, deliveryType); // Assuming only one item for simplicity
////            String orderState = (order.getState() == OrderState.PREPARING) ? "PREPARING" : "READY";
//            String orderState = "";
//            if (order.getState() == OrderState.PREPARING) {
//                orderState ="PREPARING";
//            }
//            else if (order.getState() == OrderState.READY) {
//                orderState = "READY";
//            }
//            else if (order.getState() == OrderState.ON_THE_WAY) {
//                orderState = "ON_THE_WAY";
//            }
//            else if (order.getState() == OrderState.DELIVERED) {
//                orderState = "DELIVERED";
//            }
//            preparedStatement.setString(5, orderState); // Assuming only one item for simplicity
//
//            // Execute the PreparedStatement
//            preparedStatement.executeUpdate();
//
//            // Close the connection
//            connection.close();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(frame, "Error saving order to database!");
//        }
//    }
//}


package ui;

import order.Order;
import order.OrderState;
import order.OrderType;
import restaurant.MenuItems;
import restaurant.Restaurant;
import user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Random;

// Customer UI form
public class CustomerForm {
    private JFrame frame;
    private JRadioButton[] foodButtons;
    private JButton placeOrderButton;
    private User customer;

    public CustomerForm(User customer) {
        this.customer = customer;

        frame = new JFrame("Customer UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 250);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        String[] foodItems = {"Coffee", "Juice", "Hot Chocolate", "Burger", "Pizza", "Salad", "Sandwich", "Omelette", "Oreo Shake"};
        Random random = new Random();
        foodButtons = new JRadioButton[9];

        ButtonGroup foodGroup = new ButtonGroup();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        for (int i = 0; i < 9; i++) {
            String foodItem = foodItems[i];
            double price = 5 + (10 - 5) * random.nextDouble(); // Random price between 5 and 10
            foodButtons[i] = new JRadioButton(foodItem + " - $" + String.format("%.2f", price));
            foodButtons[i].setActionCommand(foodItem + ":" + price);
            foodGroup.add(foodButtons[i]);

            gbc.gridx = i % 3;
            gbc.gridy = i / 3;
            panel.add(foodButtons[i], gbc);
        }

        placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (JRadioButton button : foodButtons) {
                    if (button.isSelected()) {
                        String[] selectedFood = button.getActionCommand().split(":");
                        String itemName = selectedFood[0];
                        double price = Double.parseDouble(selectedFood[1]);

                        // Example: Place order for customer
                        Restaurant restaurant = new Restaurant("Restaurant A", "Location A");
                        Order order = new Order(customer, restaurant, OrderType.DELIVERY);
                        order.setState(OrderState.PREPARING);
                        order.addItem(new MenuItems(itemName, price)); // Price is a placeholder

                        // Save order to database and get the generated order number
                        int generatedOrderNo = saveOrderToDatabase(order);

                        if (generatedOrderNo != -1) {
                            JOptionPane.showMessageDialog(frame, "Order placed successfully!");

                            // Start observer thread to monitor order status and pass the generated order number
                            new OrderStatusObserver(generatedOrderNo).start();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Failed to place order!");
                        }

                        return;
                    }
                }
                JOptionPane.showMessageDialog(frame, "Please select a food item!");
            }
        });


        // Set placeOrderButton to span the entire last row
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(placeOrderButton, gbc);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Method to save order to database
    private int saveOrderToDatabase(Order order) {
        int generatedOrderNo = -1;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db")) {
            // Create a PreparedStatement to insert the order into the database
            String query = "INSERT INTO orders (customer, item, price, order_type, state) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, order.getUser().getUsername());
            preparedStatement.setString(2, order.getItems().get(0).getName()); // Assuming only one item for simplicity
            preparedStatement.setDouble(3, order.getItems().get(0).getPrice()); // Assuming only one item for simplicity
            String deliveryType = (order.getType() == OrderType.DELIVERY) ? "DELIVERY" : "NOT DELIVERY";
            preparedStatement.setString(4, deliveryType); // Assuming only one item for simplicity
            preparedStatement.setString(5, order.getState().toString());

            // Execute the PreparedStatement
            // Execute the PreparedStatement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Retrieve the generated order number
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedOrderNo = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving order to database!");
        }
        return generatedOrderNo;
    }

    // Observer class to monitor order status
    private class OrderStatusObserver extends Thread {
        private int orderNo;

        public OrderStatusObserver(int orderNo) {
            this.orderNo = orderNo;
        }

        public void run() {
            while (true) {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db");
                    String query = "SELECT state FROM orders WHERE order_no = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, orderNo);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String state = resultSet.getString("state");
                        if (OrderState.DELIVERED.toString().equals(state)) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(frame, "Your order has been delivered!");
                                showRatingDialog();
                            });
                            break;
                        }
                    }

                    // Close the database resources
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();

                    Thread.sleep(5000); // Check every 5 seconds
                } catch (SQLException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }



    // Show rating dialog
    private void showRatingDialog() {
        JDialog ratingDialog = new JDialog(frame, "Rate the Rider", true);
        ratingDialog.setSize(300, 200);
        ratingDialog.setLayout(new BorderLayout());

        JPanel ratingPanel = new JPanel();
        ratingPanel.setLayout(new GridLayout(6, 1));
        JLabel ratingLabel = new JLabel("Rate the rider (1-5):");
        ratingPanel.add(ratingLabel);

        ButtonGroup ratingGroup = new ButtonGroup();
        JRadioButton[] ratingButtons = new JRadioButton[5];
        for (int i = 0; i < 5; i++) {
            ratingButtons[i] = new JRadioButton(String.valueOf(i + 1));
            ratingGroup.add(ratingButtons[i]);
            ratingPanel.add(ratingButtons[i]);
        }

        JButton submitRatingButton = new JButton("Submit Rating");
        submitRatingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (JRadioButton button : ratingButtons) {
                    if (button.isSelected()) {
                        int rating = Integer.parseInt(button.getText());
                        JOptionPane.showMessageDialog(frame, "Thank you for rating the rider " + rating + " stars!");
                        ratingDialog.dispose();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(ratingDialog, "Please select a rating!");
            }
        });

        ratingDialog.add(ratingPanel, BorderLayout.CENTER);
        ratingDialog.add(submitRatingButton, BorderLayout.SOUTH);
        ratingDialog.setVisible(true);
    }
}
