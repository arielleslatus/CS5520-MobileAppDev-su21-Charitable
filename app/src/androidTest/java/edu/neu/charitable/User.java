package edu.neu.charitable;

public class User {

    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void resetUsername(String newUsername) {
        this.username = newUsername;
    }

    public void resetPassword(String newPassword) {
        this.password = newPassword;
    }
}