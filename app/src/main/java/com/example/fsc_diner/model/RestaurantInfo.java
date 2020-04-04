package com.example.fsc_diner.model;

public class RestaurantInfo {

    private String Restaurantname;
    private String RestaurantImage;
    private String RestaurantKey;

    public RestaurantInfo(){}

    public RestaurantInfo(String restaurantname, String restaurantImage, String restaurantKey) {
        Restaurantname = restaurantname;
        RestaurantImage = restaurantImage;
        RestaurantKey = restaurantKey;
    }

    public String getRestaurantname() {
        return Restaurantname;
    }

    public void setRestaurantname(String restaurantname) {
        Restaurantname = restaurantname;
    }

    public String getRestaurantImage() {
        return RestaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        RestaurantImage = restaurantImage;
    }

    public String getRestaurantKey() {
        return RestaurantKey;
    }

    public void setRestaurantKey(String restaurantKey) {
        RestaurantKey = restaurantKey;
    }
}
