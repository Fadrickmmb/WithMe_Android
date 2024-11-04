package com.example.withme_android;

import java.util.List;
import java.util.Map;

public class User {
    private String name;
    private String email;
    private String id;
    private Long numberPosts;
    private String userPhotoUrl;
    private String userBio;
    private Map<String, Post> posts;
    private Map<String, Boolean> following;
    private Map<String, Boolean> followers;

    public User() {
    }

    public User(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public User(String name, String email, String id, long numberPosts,String userPhotoUrl, String userBio) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.numberPosts = numberPosts;
        this.userPhotoUrl = userPhotoUrl;
        this.userBio = userBio;
    }

    public User(String name, String id, long numberPosts, String userPhotoUrl, String userBio) {
        this.name = name;
        this.id = id;
        this.numberPosts = numberPosts;
        this.userPhotoUrl = userPhotoUrl;
        this.userBio = userBio;
    }

    public Map<String, Post> getPosts() {
        return posts;
    }

    public void setPosts(Map<String, Post> posts) {
        this.posts = posts;
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

    public Long getNumberPosts() {
        return numberPosts;
    }

    public void setNumberPosts(Long numberPosts) {
        this.numberPosts = numberPosts;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public Map<String, Boolean> getFollowing() {
        return following;
    }

    public void setFollowing(Map<String, Boolean> following) {
        this.following = following;
    }

    public Map<String, Boolean> getFollowers() {
        return followers;
    }

    public void setFollowers(Map<String, Boolean> followers) {
        this.followers = followers;
    }
}