package com.example.withme_android;

public class Follower {
    private String id;
    private String name;
    private String userPhotoUrl;

    public Follower() {
    }

    public Follower(String id) {
        this.id = id;
    }

    public Follower(String id, String name, String userPhotoUrl) {
        this.id = id;
        this.name = name;
        this.userPhotoUrl = userPhotoUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getId() {
        return id;
    }
}
