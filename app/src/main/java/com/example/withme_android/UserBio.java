package com.example.withme_android;

public class UserBio {
    private String userId;
    private String bio;

    // Constructor
    public UserBio(String userId, String bio) {
        this.userId = userId;
        this.bio = bio;
    }

    // Default Constructor
    public UserBio() {
    }

    // Getter for userId
    public String getUserId() {
        return userId;
    }

    // Setter for userId
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter for bio
    public String getBio() {
        return bio;
    }

    // Setter for bio
    public void setBio(String bio) {
        this.bio = bio;
    }
}
