package com.example.withme_android;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    private String userName;
    private String userPhotoUrl;
    private String postImageUrl;
    private String location;
    private Date postDate;
    private int yummys;
    private List<Comment> comments;

    public Post(String userName, String userPhotoUrl, String postImageUrl, String location, Date postDate) {
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
        this.postImageUrl = postImageUrl;
        this.location = location;
        this.postDate = postDate;
        this.yummys = 0;
        this.comments = new ArrayList<>();
    }

    public void addYummy() {
        this.yummys++;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public int getYummys() {
        return yummys;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
