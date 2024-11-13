package com.example.libraryofalexandria.Models;

public class AuthRequest {

    private String username;
    private String password;

    // Getters and setters
    public String getUsername() {
        username = "admin";
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        password = "admin123";
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
