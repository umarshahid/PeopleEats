import restaurant.Restaurant;
import ui.CustomerForm;
import ui.RestaurantForm;
import ui.RiderForm;
import user.Driver;
import user.User;
import user.UserType;

import javax.swing.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
//public class Main {
//    public static void main(String[] args) {
        // Create some sample users
//        User.User customer = new User.User("customer1", "password1", User.UserType.CUSTOMER);
//        User.User driver = new User.User("driver1", "password1", User.UserType.DRIVER);
//
//        // Create some sample restaurants
//        restaurant.Restaurant restaurant1 = new restaurant.Restaurant("restaurant.Restaurant A", "Location A");
//        restaurant.Restaurant restaurant2 = new restaurant.Restaurant("restaurant.Restaurant B", "Location B");
//
//        // Display available restaurants
//        System.out.println("Available Restaurants:");
//        System.out.println("1. " + restaurant1.getName());
//        System.out.println("2. " + restaurant2.getName());
//
//        // Sample order creation
//        restaurant.MenuItem item1 = new restaurant.MenuItem("Item 1", 10.99);
//        restaurant.MenuItem item2 = new restaurant.MenuItem("Item 2", 8.49);
//
//        order.Order order = new order.Order(customer, restaurant1, order.OrderType.DELIVERY);
//        order.addItem(item1);
//        order.addItem(item2);
//
//        // Sample driver accepting task
//        Users.User.Driver assignedDriver = new Users.User.Driver("Assigned Users.User.Driver");
//        assignedDriver.acceptTask(order);
//
//        // Display order details
//        System.out.println("\nOrder Details:");
//        System.out.println("Customer: " + order.getUser().getUsername());
//        System.out.println("restaurant.Restaurant: " + order.getRestaurant().getName());
//        System.out.println("Items: " + order.getItems());
//        System.out.println("order.Order Type: " + order.getType());
//
//        // Sample payment processing
//        payment.Payment payment = new payment.Payment();
//        payment.processCreditCardPayment(customer, order.calculateTotal(), "1234-5678-9012-3456");
//
//        // Sample feedback submission
//        feedback.Feedback feedback = new feedback.Feedback();
//        feedback.submitFeedback(customer, "Great experience!", 5);
//
//        // Sample driver updating location
//        assignedDriver.updateLocation("New Location");
//
//        // Sample administrator actions
//        administrator.Administrator administrator = new administrator.Administrator();
//        administrator.manageDrivers();
//        administrator.manageAccounts();
//        administrator.monitorFeedback();

        // Register users (customers, drivers) and set up administration mode
//


//}

public class Main {
    public static void main(String[] args) {
        // Example: Creating instances of customer, restaurant, and rider
        User customer = new User("Customer", "123456", UserType.CUSTOMER);
        Restaurant restaurant = new Restaurant("Restaurant", "Location");
        Driver rider = new Driver("Rider");

        // Example: Launching UI forms for customer, restaurant, and rider
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CustomerForm(customer);
                new RestaurantForm(restaurant);
                new RiderForm(rider);
            }
        });
    }
}
