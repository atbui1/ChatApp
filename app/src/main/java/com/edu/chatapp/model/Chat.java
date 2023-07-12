package com.edu.chatapp.model;

public class Chat {
    private int id;
    private String username;
    private String lastMessage;
    private String time;
    private String avatar;

    public Chat(int id, String username, String lastMessage, String time, String avatar) {
        this.id = id;
        this.username = username;
        this.lastMessage = lastMessage;
        this.time = time;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
