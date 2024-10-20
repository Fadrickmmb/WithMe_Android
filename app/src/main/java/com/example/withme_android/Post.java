package com.example.withme_android;

import java.util.Map;

public class Post {
    private String content;
    private String userId;
    private String postImageUrl;
    private String postDate;
    private String name;
    private String location;
    private int yummys;
    private int commentsNumber;
    private String userPhotoUrl;
    private String postId;
    private Map<String, Comment> comments;

    public Post() {
    }

    public Post(String content, String userId, String postImageUrl, String postDate, String name,
                String location, int yummys, int commentsNumber, String userPhotoUrl,String postId) {
        this.content = content;
        this.userId = userId;
        this.postImageUrl = postImageUrl;
        this.postDate = postDate;
        this.name = name;
        this.location = location;
        this.yummys = yummys;
        this.commentsNumber = commentsNumber;
        this.userPhotoUrl = userPhotoUrl;
        this.postId = postId;
    }

    public Post(String content, String uid, String string, String string1, String name, String location, int i, String userPhotoUrl) {
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

    public int getCommentsNumber() {
        return commentsNumber;
    }

    public void setCommentsNumber(int commentsNumber) {
        this.commentsNumber = commentsNumber;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


}