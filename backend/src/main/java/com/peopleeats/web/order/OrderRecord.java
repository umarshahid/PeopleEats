package com.peopleeats.web.order;

public class OrderRecord {
    private Integer orderNo;
    private String customer;
    private String item;
    private double price;
    private String orderType;
    private String state;
    private String riderName;

    public OrderRecord() {
    }

    public OrderRecord(Integer orderNo, String customer, String item, double price, String orderType, String state, String riderName) {
        this.orderNo = orderNo;
        this.customer = customer;
        this.item = item;
        this.price = price;
        this.orderType = orderType;
        this.state = state;
        this.riderName = riderName;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }
}
