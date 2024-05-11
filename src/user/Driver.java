package user;

import order.Order;
import order.OrderType;

public class Driver {
    private String name;
    private String location;
    private boolean available;
    private double earnings;

    public Driver(String name) {
        this.name = name;
        this.location = "";
        this.available = true;
        this.earnings = 0.0;
    }

    public void acceptTask(Order order) {
        System.out.println("User.Driver " + name + " has accepted the task.");
        // Assuming you need to update driver's location to the restaurant's location for delivery
        if (order.getType() == OrderType.DELIVERY) {
            updateLocation(order.getRestaurant().getLocation());
        }
    }

    public void updateLocation(String newLocation) {
        System.out.println("User.Driver " + name + " is now at " + newLocation);
        this.location = newLocation;
    }

    // Other methods...
}
