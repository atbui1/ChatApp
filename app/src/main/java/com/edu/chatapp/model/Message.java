package com.edu.chatapp.model;

import java.io.Serializable;

public class Message {
    private String senderId;
    private String receiveId;
    private String message;
    private boolean seenStatus;


    public Message() {
    }

    public Message(String message) {
        this.message = message;
    }

    public Message(String senderId, String receiveId, String message, boolean seenStatus) {
        this.senderId = senderId;
        this.receiveId = receiveId;
        this.message = message;
        this.seenStatus = seenStatus;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeenStatus() {
        return seenStatus;
    }

    public void setSeenStatus(boolean seenStatus) {
        this.seenStatus = seenStatus;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderId='" + senderId + '\'' +
                ", receiveId='" + receiveId + '\'' +
                ", message='" + message + '\'' +
                ", seenStatus=" + seenStatus +
                '}';
    }
}
