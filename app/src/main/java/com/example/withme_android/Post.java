package com.example.withme_android;

import java.util.Map;

public class Post {
    private String postId;
    private String content;
    private String userId;
    private String postImageUrl;
    private String postDate;
    private String name;
    private String location;
    private int yummys;
    private String userPhotoUrl;
    private Map<String, Comment> comments;

    public Post() {
    }

    public Post(String postId, String content, String userId, String postImageUrl, String postDate,
                String name, String location, int yummys,
                String userPhotoUrl, Map<String, Comment> comments) {
        this.postId = postId;
        this.content = content;
        this.userId = userId;
        this.postImageUrl = postImageUrl;
        this.postDate = postDate;
        this.name = name;
        this.location = location;
        this.yummys = yummys;
        this.userPhotoUrl = userPhotoUrl;
        this.comments = comments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getYummys() {
        return yummys;
    }

    public void setYummys(int yummys) {
        this.yummys = yummys;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }
}