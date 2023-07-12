package com.edu.chatapp.model;

import java.io.Serializable;

public class Upload implements Serializable {
    private String uploadId;
    private String uploadName;
    private String uploadUri;
    private String uploadUid;

    public Upload(String uploadId, String uploadName, String uploadUri, String uploadUid) {
        this.uploadId = uploadId;
        this.uploadName = uploadName;
        this.uploadUri = uploadUri;
        this.uploadUid = uploadUid;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    public String getUploadUri() {
        return uploadUri;
    }

    public void setUploadUri(String uploadUri) {
        this.uploadUri = uploadUri;
    }

    public String getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(String uploadUid) {
        this.uploadUid = uploadUid;
    }
}
