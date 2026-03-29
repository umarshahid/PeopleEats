package com.peopleeats.web.restaurant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public List<RestaurantInfo> listRestaurants() {
        return restaurantService.getRestaurants();
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemInfo>> getMenu(@PathVariable String id) {
        return restaurantService.getRestaurant(id)
                .map(restaurant -> ResponseEntity.ok(restaurantService.getMenu(id)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
