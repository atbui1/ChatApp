package com.edu.chatapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Sender implements Serializable {
    @SerializedName("data")
    private Notify notify;
    private String to;

    public Sender(Notify notify, String to) {
        this.notify = notify;
        this.to = to;
    }

    public Notify getNotify() {
        return notify;
    }

    public void setNotify(Notify notify) {
        this.notify = notify;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Sender{" +
                "notify=" + notify +
                ", to='" + to + '\'' +
                '}';
    }
}
