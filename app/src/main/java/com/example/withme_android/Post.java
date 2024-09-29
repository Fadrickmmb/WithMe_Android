package com.example.withme_android;

public class Post {
    private String title;
    private String content;
    private String userId;
    private String postImageUrl;
    private String postDate;
    private String name;
    private String location;
    private int yummys;
    private String userPhotoUrl;

    public Post() {

    }

    public Post(String title, String content, String userId, String postImageUrl, String postDate, String name, String location, int yummys, String userPhotoUrl) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.postImageUrl = postImageUrl;
        this.postDate = postDate;
        this.name = name;
        this.location = location;
        this.yummys = yummys;
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }


    public int getYummys() {
        return yummys;
    }

    public void setYummys(int yummys) {
        this.yummys = yummys;
    }

    public String getTitle() {
        return title;
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

    public void setTitle(String title) {
        this.title = title;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String imageUrl) {
        this.postImageUrl = imageUrl;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
}
