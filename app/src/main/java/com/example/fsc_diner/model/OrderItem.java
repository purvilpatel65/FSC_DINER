package com.example.fsc_diner.model;

import java.util.HashMap;
import java.util.List;

public class OrderItem {

    private int orderID;
    private String customerName;
    private String customerEmail;
    private String itemName;
    private String restaurantName;
    private String restaurantKey;
    private int quantity;
    private String currentOrderItemKey;
    private String employeeOrderItemKey;
    private String image;
    private int status;
    private List<HashMap<String, List<String>>> ingredients;

    public OrderItem(){}

    public OrderItem(int orderID, String customerName, String customerEmail, String itemName, String restaurantName, String restaurantKey, int quantity, String currentOrderItemKey, String employeeOrderItemKey, int status, List<HashMap<String, List<String>>> ingredients) {
        this.orderID = orderID;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.itemName = itemName;
        this.restaurantName = restaurantName;
        this.restaurantKey = restaurantKey;
        this.quantity = quantity;
        this.currentOrderItemKey = currentOrderItemKey;
        this.employeeOrderItemKey = employeeOrderItemKey;
        this.status = status;
        this.ingredients = ingredients;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantKey() {
        return restaurantKey;
    }

    public void setRestaurantKey(String restaurantKey) {
        this.restaurantKey = restaurantKey;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCurrentOrderItemKey() {
        return currentOrderItemKey;
    }

    public void setCurrentOrderItemKey(String currentOrderItemKey) {
        this.currentOrderItemKey = currentOrderItemKey;
    }

    public String getEmployeeOrderItemKey() {
        return employeeOrderItemKey;
    }

    public void setEmployeeOrderItemKey(String employeeOrderItemKey) {
        this.employeeOrderItemKey = employeeOrderItemKey;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<HashMap<String, List<String>>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<HashMap<String, List<String>>> ingredients) {
        this.ingredients = ingredients;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
