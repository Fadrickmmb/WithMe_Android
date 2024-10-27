package com.example.withme_android;

import java.util.Date;

class Comment {
    private String name;
    private String text;
    private String date;
    private String userId; //id from user who commented
    private String postId;
    private String commentId;

    public Comment(){

    }

    public Comment(String name, String text, String date, String userId, String postId, String commentId) {
        this.name = name;
        this.text = text;
        this.date = date;
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
    }

    public Comment(String name, String text, String date, String commentId) {
        this.name = name;
        this.text = text;
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

}
