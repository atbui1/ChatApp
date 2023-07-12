package com.edu.chatapp.model;

import java.io.Serializable;

public class Post implements Serializable {
    private String pId;
    private String pUsername;
    private String pEmail;
    private String pTime;
    private String pAvatar;
    private String pImage;
    private String pTitle;
    private String pUid;
    private String pLike;
    private String pComment;

    public Post() {
    }

    /** cts 9 element */
    public Post(String pUsername, String pEmail, String pTime, String pAvatar, String pImage, String pTitle, String pUid, String pLike, String pComment) {
        this.pUsername = pUsername;
        this.pEmail = pEmail;
        this.pTime = pTime;
        this.pAvatar = pAvatar;
        this.pImage = pImage;
        this.pTitle = pTitle;
        this.pUid = pUid;
        this.pLike = pLike;
        this.pComment = pComment;
    }

    /** cts 10 element */
    public Post(String pId, String pUsername, String pEmail, String pTime, String pAvatar, String pImage, String pTitle, String pUid, String pLike, String pComment) {
        this.pId = pId;
        this.pUsername = pUsername;
        this.pEmail = pEmail;
        this.pTime = pTime;
        this.pAvatar = pAvatar;
        this.pImage = pImage;
        this.pTitle = pTitle;
        this.pUid = pUid;
        this.pLike = pLike;
        this.pComment = pComment;
    }

    /** cts post create -  */
    public Post(String pId, String pUsername, String pEmail, String pTime, String pAvatar, String pImage, String pTitle, String pUid) {
        this.pId = pId;
        this.pUsername = pUsername;
        this.pEmail = pEmail;
        this.pTime = pTime;
        this.pAvatar = pAvatar;
        this.pImage = pImage;
        this.pTitle = pTitle;
        this.pUid = pUid;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpUsername() {
        return pUsername;
    }

    public void setpUsername(String pUsername) {
        this.pUsername = pUsername;
    }

    public String getpEmail() {
        return pEmail;
    }

    public void setpEmail(String pEmail) {
        this.pEmail = pEmail;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getpAvatar() {
        return pAvatar;
    }

    public void setpAvatar(String pAvatar) {
        this.pAvatar = pAvatar;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpUid() {
        return pUid;
    }

    public void setpUid(String pUid) {
        this.pUid = pUid;
    }

    public String getpLike() {
        return pLike;
    }

    public void setpLike(String pLike) {
        this.pLike = pLike;
    }

    public String getpComment() {
        return pComment;
    }

    public void setpComment(String pComment) {
        this.pComment = pComment;
    }


    @Override
    public String toString() {
        return "Post{" +
                "pId='" + pId + '\'' +
                ", pUsername='" + pUsername + '\'' +
                ", pEmail='" + pEmail + '\'' +
                ", pTime='" + pTime + '\'' +
                ", pAvatar='" + pAvatar + '\'' +
                ", pImage='" + pImage + '\'' +
                ", pTitle='" + pTitle + '\'' +
                ", pUid='" + pUid + '\'' +
                ", pLike='" + pLike + '\'' +
                ", pComment='" + pComment + '\'' +
                '}';
    }
}
