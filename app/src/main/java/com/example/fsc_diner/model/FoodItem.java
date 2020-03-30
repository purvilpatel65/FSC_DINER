package com.example.fsc_diner.model;

import java.util.ArrayList;
import java.util.List;

public class FoodItem {

    private String name;
    private String calories;
    private String price;
    private int image; //make string later
    private float rating;
    private List<String> ingredients;

    public FoodItem(){};

    public FoodItem(String name, String calories, String price, int image, float rating, List<String> ingredients) {
        this.name = name;
        this.calories = calories;
        this.price = price;
        this.image = image;
        this.rating = rating;
        this.ingredients = ingredients;
    }

    /*
    public FoodItem(String name, String calories, String price, String image, float rating, List<String> ingredients) {
        this.name = name;
        this.calories = calories;
        this.price = price;
        this.image = image;
        this.rating = rating;
        this.ingredients = ingredients;
    }

    public FoodItem(String name, String calories, String price, String image, float rating) {
        this(name, calories, price, image, rating, null);
    } */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
