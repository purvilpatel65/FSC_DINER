package com.example.fsc_diner;

// Class contains necessary member variables for storing User Information into the Database
public class UserInformation {
    public String email;
    public String firstName;
    public String lastName;
    public String fullName;

    // Constructor used for email and password login users
    public UserInformation(String email, String firstName, String lastName){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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
        return fullName; }

}
