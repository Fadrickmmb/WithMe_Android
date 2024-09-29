package com.example.withme_android;

public class Post {
    private String postId;
    private String imgUrl;
    private Number likes;
    private Number comments;
    private String location;
    private String userId;
    private String description;

    // Constructor
    public Post(String postId, String imgUrl, Number likes,
                Number comments, String location,
                String userId, String description) {
        this.postId = postId;
        this.imgUrl = imgUrl;
        this.likes = likes;
        this.comments = comments;
        this.location = location;
        this.userId = userId;
        this.description = description;
    }

    // Getter and Setter for postId
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // Getter and Setter for imgUrl
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    // Getter and Setter for likes
    public Number getLikes() {
        return likes;
    }

    public void setLikes(Number likes) {
        this.likes = likes;
    }

    // Getter and Setter for comments
    public Number getComments() {
        return comments;
    }

    public void setComments(Number comments) {
        this.comments = comments;
    }

    // Getter and Setter for location
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Getter and Setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and Setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
