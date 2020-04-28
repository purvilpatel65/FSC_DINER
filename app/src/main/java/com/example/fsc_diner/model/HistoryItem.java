package com.example.fsc_diner.model;

import java.util.HashMap;
import java.util.List;

public class HistoryItem {

    private String itemName;
    private String restaurantName;
    private String restaurantKey;
    private String orderDate;
    private String historyItemkey;
    private List<HashMap<String, List<String>>> ingredients;

    public HistoryItem(){}

    public HistoryItem(String itemName, String restaurantName, String restaurantKey, String orderDate, String historyItemkey, List<HashMap<String, List<String>>> ingredients) {
        this.itemName = itemName;
        this.restaurantName = restaurantName;
        this.restaurantKey = restaurantKey;
        this.orderDate = orderDate;
        this.historyItemkey = historyItemkey;
        this.ingredients = ingredients;
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getHistoryItemkey() {
        return historyItemkey;
    }

    public void setHistoryItemkey(String historyItemkey) {
        this.historyItemkey = historyItemkey;
    }

    public List<HashMap<String, List<String>>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<HashMap<String, List<String>>> ingredients) {
        this.ingredients = ingredients;
    }
}
