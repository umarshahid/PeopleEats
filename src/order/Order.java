package order;

//import restaurant.MenuItem;

import restaurant.MenuItems;
import restaurant.Restaurant;
import user.User;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private User user;
    private Restaurant restaurant;
    private List<MenuItems> items;
    private OrderType type;
    private String paymentMethod;

    public Order(User user, Restaurant restaurant, OrderType type) {
        this.user = user;
        this.restaurant = restaurant;
        this.type = type;
        this.items = new ArrayList<>();
    }

    // Method to add item to order
    public void addItem(MenuItems item) {
        items.add(item);
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public User getUser() {
        return user;
    }

    public List<MenuItems> getItems() {
        return items;
    }

    public OrderType getType() {
        return type;
    }

    public double calculateTotal() {
        double total = 0.0;
        for (MenuItems item : items) {
            total += item.getPrice();
        }
        return total;
    }
}
