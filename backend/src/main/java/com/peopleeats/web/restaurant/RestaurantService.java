package com.peopleeats.web.restaurant;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RestaurantService {
    private final Map<String, RestaurantInfo> restaurants = new LinkedHashMap<>();
    private final Map<String, List<MenuItemInfo>> menus = new LinkedHashMap<>();

    public RestaurantService() {
        RestaurantInfo cafe = new RestaurantInfo("cafe-01", "PeopleEats Cafe", "Main Street", true, true, true);
        RestaurantInfo burger = new RestaurantInfo("burger-01", "Burger Bay", "River Road", true, true, false);
        RestaurantInfo juice = new RestaurantInfo("juice-01", "Juice Junction", "Market Lane", false, true, true);

        restaurants.put(cafe.getId(), cafe);
        restaurants.put(burger.getId(), burger);
        restaurants.put(juice.getId(), juice);

        menus.put(cafe.getId(), List.of(
                new MenuItemInfo("Coffee", 4.50),
                new MenuItemInfo("Tea", 3.00),
                new MenuItemInfo("Sandwich", 6.75)
        ));
        menus.put(burger.getId(), List.of(
                new MenuItemInfo("Burger", 9.25),
                new MenuItemInfo("Fries", 3.50),
                new MenuItemInfo("Soda", 2.50)
        ));
        menus.put(juice.getId(), List.of(
                new MenuItemInfo("Orange Juice", 5.00),
                new MenuItemInfo("Mango Smoothie", 6.00),
                new MenuItemInfo("Detox Blend", 6.50)
        ));
    }

    public List<RestaurantInfo> getRestaurants() {
        return new ArrayList<>(restaurants.values());
    }

    public Optional<RestaurantInfo> getRestaurant(String id) {
        return Optional.ofNullable(restaurants.get(id));
    }

    public List<MenuItemInfo> getMenu(String id) {
        return menus.getOrDefault(id, List.of());
    }
}
