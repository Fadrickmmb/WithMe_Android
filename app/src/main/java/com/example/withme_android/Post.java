package com.example.withme_android;

import java.util.List;

public class Post {
    private String content;
    private String userId;
    private String postImageUrl;
    private String postDate;
    private String name;
    private String location;
    private int yummys;
    private int commentNumbers;
    private String userPhotoUrl;
    private List<Comment> comments;

    public Post() {
    }

    public Post(String content, String userId, String postImageUrl, String postDate, String name,
                String location, int yummys, int commentNumbers, String userPhotoUrl) {
        this.content = content;
        this.userId = userId;
        this.postImageUrl = postImageUrl;
        this.postDate = postDate;
        this.name = name;
        this.location = location;
        this.yummys = yummys;
        this.commentNumbers = commentNumbers;
        this.userPhotoUrl = userPhotoUrl;
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

    public int getCommentNumbers() {
        return commentNumbers;
    }

    public void setCommentNumbers(int commentNumbers) {
        this.commentNumbers = commentNumbers;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    //public List<Comment> getComments() {
    //    return comments;
   // }

    //public void setComments(List<Comment> comments) {
    //    this.comments = comments;
    //}
}