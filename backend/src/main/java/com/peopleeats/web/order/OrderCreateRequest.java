package com.peopleeats.web.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OrderCreateRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String customer;
    @NotBlank
    @Size(min = 1, max = 100)
    private String item;
    @Min(1)
    private double price;
    @NotBlank
    private String orderType;

    public OrderCreateRequest() {
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
