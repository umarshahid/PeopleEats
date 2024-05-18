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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Customer UI form
public class CustomerForm {
    private JFrame frame;
    private JRadioButton coffeeButton;
    private JRadioButton juiceButton;
    private JRadioButton burgerButton;
    private JButton placeOrderButton;

    public CustomerForm(User customer) {
        frame = new JFrame("Customer UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        coffeeButton = new JRadioButton("Coffee");
        juiceButton = new JRadioButton("Juice");
        burgerButton = new JRadioButton("Burger");

        ButtonGroup foodGroup = new ButtonGroup();
        foodGroup.add(coffeeButton);
        foodGroup.add(juiceButton);
        foodGroup.add(burgerButton);

        panel.add(coffeeButton);
        panel.add(juiceButton);
        panel.add(burgerButton);

        placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedFood = "";
                if (coffeeButton.isSelected()) {
                    selectedFood = "Coffee";
                } else if (juiceButton.isSelected()) {
                    selectedFood = "Juice";
                } else if (burgerButton.isSelected()) {
                    selectedFood = "Burger";
                }

                if (!selectedFood.isEmpty()) {
                    // Example: Place order for customer
                    Restaurant restaurant = new Restaurant("Restaurant A", "Location A");
                    Order order = new Order(customer, restaurant, OrderType.DELIVERY);
                    order.setState(OrderState.PREPARING);
                    order.addItem(new MenuItems(selectedFood, 10.0)); // Price is a placeholder
                    // Add more order handling logic as needed

                    // Example: Save order to database
                    saveOrderToDatabase(order);

                    JOptionPane.showMessageDialog(frame, "Order placed successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a food item!");
                }
            }
        });
        panel.add(placeOrderButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Method to save order to database
    private void saveOrderToDatabase(Order order) {

        try {
            // Connect to the SQLite database
//            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/jhona/IdeaProjects/VIT Eats/src/orders.db");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:orders_customer.db");

            // Create a PreparedStatement to insert the order into the database
            String query = "INSERT INTO orders (customer, item, price, order_type, state) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, order.getUser().getUsername());
            preparedStatement.setString(2, order.getItems().get(0).getName()); // Assuming only one item for simplicity
            preparedStatement.setDouble(3, order.getItems().get(0).getPrice()); // Assuming only one item for simplicity
            String deliveryType = (order.getType() == OrderType.DELIVERY) ? "DELIVERY" : "NOT DELIVERY";
            preparedStatement.setString(4, deliveryType); // Assuming only one item for simplicity
//            String orderState = (order.getState() == OrderState.PREPARING) ? "PREPARING" : "READY";
            String orderState = "";
            if (order.getState() == OrderState.PREPARING) {
                orderState ="PREPARING";
            }
            else if (order.getState() == OrderState.READY) {
                orderState = "READY";
            }
            else if (order.getState() == OrderState.ON_THE_WAY) {
                orderState = "ON_THE_WAY";
            }
            else if (order.getState() == OrderState.DELIVERED) {
                orderState = "DELIVERED";
            }
            preparedStatement.setString(5, orderState); // Assuming only one item for simplicity

            // Execute the PreparedStatement
            preparedStatement.executeUpdate();

            // Close the connection
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving order to database!");
        }
    }
}
