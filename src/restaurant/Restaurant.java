package restaurant;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private String name;
    private String location;
    private List<String> menuItems;
    private boolean deliveryAvailable;
    private boolean pickupAvailable;
    private boolean dineInAvailable;

    public Restaurant(String name, String location) {
        this.name = name;
        this.location = location;
        this.menuItems = new ArrayList<>();
        this.deliveryAvailable = false;
        this.pickupAvailable = false;
        this.dineInAvailable = false;
    }

    // Method to display menu
    public void displayMenu() {
        // Display menu items
    }
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
