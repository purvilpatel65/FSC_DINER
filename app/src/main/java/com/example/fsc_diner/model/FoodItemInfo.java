package com.example.fsc_diner.model;

import java.util.List;

public class FoodItemInfo {

    private String ItemName;
    private String ItemImage;
    private double ItemPrice;
    private int ItemCalories;
    private float ItemRating;
    private String ItemKey;
    private String RestaurantKey;
    private List<List<String>> ItemIngredients;

    public FoodItemInfo(){}

    public FoodItemInfo(String itemName, String itemImage, double itemPrice, String itemKey, String resKey, int itemCalories) {
        this(itemName, itemImage, itemPrice, itemKey, resKey, itemCalories, (float)0.0);
    }

    public FoodItemInfo(String itemName, String itemImage, double itemPrice, String itemKey, String resKey, int itemCalories, float itemRating) {
        this(itemName, itemImage, itemPrice, itemCalories, itemKey, resKey, itemRating, null);
    }

    public FoodItemInfo(String itemName, String itemImage, double itemPrice, int itemCalories,  String itemKey, String resKey, float itemRating, List<List<String>> intemIngredients) {
        ItemName = itemName;
        ItemImage = itemImage;
        ItemPrice = itemPrice;
        ItemCalories = itemCalories;
        ItemRating = itemRating;
        ItemIngredients = intemIngredients;
        ItemKey = itemKey;
        RestaurantKey = resKey;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemImage() {
        return ItemImage;
    }

    public void setItemImage(String itemImage) {
        ItemImage = itemImage;
    }

    public double getItemPrice() {
        return ItemPrice;
    }

    public void setItemPrice(double itemPrice) {
        ItemPrice = itemPrice;
    }

    public int getItemCalories() {
        return ItemCalories;
    }

    public void setItemCalories(int itemCalories) {
        ItemCalories = itemCalories;
    }

    public float getItemRating() {
        return ItemRating;
    }

    public void setItemRating(float itemRating) {
        ItemRating = itemRating;
    }

    public List<List<String>> getIntemIngredients() {
        return ItemIngredients;
    }

    public void setIntemIngredients(List<List<String>> intemIngredients) {
        ItemIngredients = intemIngredients;
    }

    public String getItemKey() {
        return ItemKey;
    }

    public void setItemKey(String itemKey) {
        ItemKey = itemKey;
    }

    public String getRestaurantKey() {
        return RestaurantKey;
    }

    public void setRestaurantKey(String restaurantKey) {
        RestaurantKey = restaurantKey;
    }
}
