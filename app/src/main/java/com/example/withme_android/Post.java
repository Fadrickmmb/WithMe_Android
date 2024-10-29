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
    private Map<String, Boolean> yummys;
    private String userPhotoUrl;
    private Map<String, Comment> comments;
    private Double latitude;
    private Double longitude;

    public Post() {
    }

    public Post(String postId, String content, String userId, String postImageUrl, String postDate,
                String name, String location, Map<String, Boolean> yummys,
                String userPhotoUrl, Map<String, Comment> comments,
                Double latitude, Double longitude) {
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
        this.latitude = latitude;
        this.longitude = longitude;
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

    public Map<String, Boolean> getYummys() {
        return yummys;
    }

    public void setYummys(Map<String, Boolean> yummys) {
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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double val) {
        this.latitude = val;
    }

    public void setLongitude(Double val) {
        this.longitude = val;
    }
}