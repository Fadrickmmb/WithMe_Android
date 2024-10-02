package com.example.withme_android;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String email;
    private String id;
    private Map<String, Post> posts;
    private Integer numberPosts;
    private String numberFollowers;
    private String numberYummys;
    private String userPhotoUrl;
    private String userBio;

    public User(String name, String email, String id, Integer numberPosts, String numberYummys, String numberFollowers, String userPhotoUrl, String userBio){
        this.name = name;
        this.email = email;
        this.id = id;
        this.numberPosts = numberPosts;
        this.numberFollowers = numberYummys;
        this.numberYummys = numberFollowers;
        this.userPhotoUrl = userPhotoUrl;
        this.userBio = userBio;
        this.posts = new HashMap<>();
    }

    public User(String name, String email, String id){
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public User(){

    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
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

    public Integer getNumberPosts() {
        return numberPosts;
    }

    public void setNumberPosts(Integer numberPosts) {
        this.numberPosts = numberPosts;
    }

    public String getNumberFollowers() {
        return numberFollowers;
    }

    public void setNumberFollowers(String numberComments) {
        this.numberFollowers = numberComments;
    }

    public String getNumberYummys() {
        return numberYummys;
    }

    public void setNumberYummys(String numberYummys) {
        this.numberYummys = numberYummys;
    }


}

