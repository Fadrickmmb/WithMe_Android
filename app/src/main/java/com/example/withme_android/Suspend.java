package com.example.withme_android;

public class Suspend {
    private String suspendId;
    private String userId;
    private String postId;
    private String commentId;
    private String postOwnerId;
    private String commentOwnerId;
    private String userSuspendId;

    public Suspend(){

    }

    //report user
    public Suspend(String suspendId, String userId, String userSuspendId) {
        this.suspendId = suspendId;
        this.userId = userId;
        this.userSuspendId= userSuspendId;
    }

    public String getSuspendId() {
        return suspendId;
    }

    public void setSuspendId(String suspendId) {
        this.suspendId = suspendId;
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

    public String getPostOwnerId() {
        return postOwnerId;
    }

    public void setPostOwnerId(String postOwnerId) {
        this.postOwnerId = postOwnerId;
    }

    public String getCommentOwnerId() {
        return commentOwnerId;
    }

    public void setCommentOwnerId(String commentOwnerId) {
        this.commentOwnerId = commentOwnerId;
    }

    public String getUserSuspendId() {
        return userSuspendId;
    }

    public void setUserSuspendId(String userSuspendId) {
        this.userSuspendId = userSuspendId;
    }
}
