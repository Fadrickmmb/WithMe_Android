package com.example.withme_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String name;
    private String email;
    private String id;
    private Map<String, Post> posts;
    private int numberPosts;
    private int numberFollowing;
    private int numberFollowers;
    private String userPhotoUrl;

    public User(String name, String email, String id, int numberPosts, int numberYummys, int numberFollowers, String userPhotoUrl){
        this.name = name;
        this.email = email;
        this.id = id;
        this.numberPosts = numberPosts;
        this.numberFollowing = numberYummys;
        this.numberFollowers = numberFollowers;
        this.userPhotoUrl = userPhotoUrl;
        this.posts = new HashMap<>();
    }

    public User(String name, String email, String id){
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public User(){
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Post> getPosts() {
        return posts;
    }

    public void setPosts(Map<String, Post> posts) {
        this.posts = posts;
    }

    public int getNumberPosts() {
        return numberPosts;
    }

    public void setNumberPosts(int numberPosts) {
        this.numberPosts = numberPosts;
    }

    public int getNumberFollowing() {
        return numberFollowing;
    }

    public void setNumberFollowing(int numberComments) {
        this.numberFollowing = numberComments;
    }

    public int getNumberFollowers() {
        return numberFollowers;
    }

    public void setNumberFollowers(int numberFollowers) {
        this.numberFollowers = numberFollowers;
    }
}

