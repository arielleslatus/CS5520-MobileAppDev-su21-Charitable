package edu.neu.charitable.models;

public class User {

    public String fullName, city, email, username;

    public User(){
    }

    public User(String fullName, String city, String email){
        this.fullName = fullName;
        this.city = city;
        this.email = email;
    }

    public User(String fullName, String city, String email, String username){
        this.fullName = fullName;
        this.city = city;
        this.email = email;
        this.username = username;
    }

}
