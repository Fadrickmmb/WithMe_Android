package com.example.withme_android;

import java.util.Date;

class Comment {
    private String name;
    private String text;
    private String date;
    private String userAvatar;

    public Comment() {

    }

    public Comment(String name, String text, String date, String userAvatar) {
        this.name = name;
        this.text = text;
        this.date = date;
        this.userAvatar = userAvatar;
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

    public String getUserAvatar() {
        return userAvatar;
    }
}
