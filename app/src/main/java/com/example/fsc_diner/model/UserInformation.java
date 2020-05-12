package com.example.fsc_diner.model;

// Class contains necessary member variables for storing User Information into the Database
public class UserInformation {
    public String email;
    public String firstName;
    public String lastName;
    public String fullName;
    public String userType;
    public String joinDate;
    private String restaurantKey;

    // Constructor used for email and password login users
    public UserInformation(String email, String firstName, String lastName, String userType){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
    }

    public UserInformation(String email, String firstName, String lastName, String userType, String joinDate){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.joinDate = joinDate;
    }

    public UserInformation(String email, String firstName, String lastName, String userType, String restaurantKey, String joinDate){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.restaurantKey = restaurantKey;
        this.joinDate = joinDate;
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
        return fullName;
    }

    public String getUserType(){
        return userType;
    }

    public String getJoinDate(){return joinDate;}

    public String getRestaurantKey() {
        return restaurantKey;
    }

    public void setRestaurantKey(String restaurantKey) {
        restaurantKey = restaurantKey;
    }
}
