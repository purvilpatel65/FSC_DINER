package com.example.fsc_diner.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItem {

    private String itemName;
    private String restaurantName;
    private String restaurantKey;
    private double totalPrice;
    private int quantity;
    private String cartItemKey;
    private String image;
    private List<HashMap<String, List<String>>> ingredients;

    public  CartItem(){}

    public CartItem(String itemName, String restaurantName, String resKey, int quantity, String cartItemKey, String img, List<HashMap<String, List<String>>> ingredients) {
        this.itemName = itemName;
        this.restaurantName = restaurantName;
        this.restaurantKey = resKey;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.cartItemKey = cartItemKey;
        this.image = img;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCartItemKey() {
        return cartItemKey;
    }

    public void setCartItemKey(String cartItemKey) {
        this.cartItemKey = cartItemKey;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<HashMap<String, List<String>>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<HashMap<String, List<String>>> ingredients) {
        this.ingredients = ingredients;
    }

    public String getRestaurantKey() {
        return restaurantKey;
    }

    public void setRestaurantKey(String restaurantKey) {
        this.restaurantKey = restaurantKey;
    }
}