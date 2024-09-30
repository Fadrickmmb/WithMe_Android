package com.example.withme_android;

import java.util.Date;

class Comment {
    private String name;
    private String text;
    private Date date;

    public Comment(String name, String text, Date date) {
        this.name = name;
        this.text = text;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }
}
