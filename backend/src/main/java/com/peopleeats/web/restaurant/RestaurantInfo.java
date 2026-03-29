package com.peopleeats.web.restaurant;

public class RestaurantInfo {
    private String id;
    private String name;
    private String location;
    private boolean deliveryAvailable;
    private boolean pickupAvailable;
    private boolean dineInAvailable;

    public RestaurantInfo() {
    }

    public RestaurantInfo(String id, String name, String location,
                          boolean deliveryAvailable, boolean pickupAvailable, boolean dineInAvailable) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.deliveryAvailable = deliveryAvailable;
        this.pickupAvailable = pickupAvailable;
        this.dineInAvailable = dineInAvailable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isDeliveryAvailable() {
        return deliveryAvailable;
    }

    public void setDeliveryAvailable(boolean deliveryAvailable) {
        this.deliveryAvailable = deliveryAvailable;
    }

    public boolean isPickupAvailable() {
        return pickupAvailable;
    }

    public void setPickupAvailable(boolean pickupAvailable) {
        this.pickupAvailable = pickupAvailable;
    }

    public boolean isDineInAvailable() {
        return dineInAvailable;
    }

    public void setDineInAvailable(boolean dineInAvailable) {
        this.dineInAvailable = dineInAvailable;
    }
}
