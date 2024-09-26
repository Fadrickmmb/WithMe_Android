package com.example.withme_android;

import java.util.Date;

class Comment {
    private String userName;
    private String text;
    private Date date;

    public Comment(String userName, String text, Date date) {
        this.userName = userName;
        this.text = text;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }
}
