package com.example.fsc_diner.model;

// Class contains necessary member variables for storing User Information into the Database
public class UserInformation {
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String userType;
    private String RestaurantKey;

    public UserInformation(){}

    public UserInformation(String email, String firstName, String lastName, String userType){
        this(email, firstName, lastName, userType, "");
    }

    public UserInformation(String email, String firstName, String lastName, String userType, String resKey){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.RestaurantKey = resKey;
    }

    // Constructor used for facebook login users
    public UserInformation(String email, String fullName){
        this.email = email;
        this.fullName = fullName;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getEmail(){
        return email;
    }

    public String getFullName(){
        return getFirstName() + " " + getLastName();
    }

    public String getUserType(){
        return userType;
    }

    public String getRestaurantKey() {
        return RestaurantKey;
    }

    public void setRestaurantKey(String restaurantKey) {
        RestaurantKey = restaurantKey;
    }
}
