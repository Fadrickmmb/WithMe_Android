package com.example.withme_android;

public class Report {
    private String reportId;
    private String userId;
    private String postId;
    private String commentId;
    private String postOwnerId;
    private String commentOwnerId;
    private String userReportingId;

    public Report(){

    }

    //report user
    public Report(String reportId, String userId, String userReportingId) {
        this.reportId = reportId;
        this.userId = userId;
        this.userReportingId = userReportingId;
    }

    //report post
    public Report(String reportId, String postId, String postOwnerId, String userReportingId) {
        this.reportId = reportId;
        this.postId = postId;
        this.postOwnerId = postOwnerId;
        this.userReportingId = userReportingId;
    }

    //report comment
    public Report(String reportId, String postId, String commentId, String postOwnerId, String commentOwnerId, String userReportingId) {
        this.reportId = reportId;
        this.postId = postId;
        this.commentId = commentId;
        this.postOwnerId = postOwnerId;
        this.commentOwnerId = commentOwnerId;
        this.userReportingId = userReportingId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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

    public String getUserReportingId() {
        return userReportingId;
    }

    public void setUserReportingId(String userReportingId) {
        this.userReportingId = userReportingId;
    }
}
