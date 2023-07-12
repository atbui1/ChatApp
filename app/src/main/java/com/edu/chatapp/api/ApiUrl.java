package com.edu.chatapp.api;

public class ApiUrl {
    public static final String BASE_URL_APP = "https://fcm.googleapis.com/";
    public static final String BASE_URL_FCM = "https://fcm.googleapis.com/";
    public static final String BASE_URL_REAL_TIME = "https://chatapp-aadeb-default-rtdb.firebaseio.com/";

    public interface UlrDetail {
        String SEND_NOTIFICATION = "fcm/send";
        String URL_TBL_USERS = "users";
    }
}
