package com.edu.chatapp.model;

public class Notify {
    private String user;
    private String icon;
    private String title;
    private String body;
    private String sented;

    public Notify() {}

    public Notify(String user, String icon, String title, String body, String sented) {
        this.user = user;
        this.icon = icon;
        this.title = title;
        this.body = body;
        this.sented = sented;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    @Override
    public String toString() {
        return "Notify{" +
                "user='" + user + '\'' +
                ", icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", sented='" + sented + '\'' +
                '}';
    }
}
