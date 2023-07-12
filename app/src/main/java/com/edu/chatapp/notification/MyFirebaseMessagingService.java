package com.edu.chatapp.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.edu.chatapp.R;
import com.edu.chatapp.activity.MainActivity;
import com.edu.chatapp.activity.MessageActivity;
import com.edu.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = MyFirebaseMessagingService.class.getName();
    public static final String BUNDLE_KEY_STR_USER_ID = "BUNDLE_KEY_STR_USER_ID";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

/** send notify only message*/
/**
 RemoteMessage.Notification notification = message.getNotification();
 if (notification == null) {
 return;
 }
 String strTitle = notification.getTitle();
 String strBody = notification.getBody();
 */


/** send data object */
/**
 final Map<String, String> map = message.getData();
 String strTitle = map.get("title");
 String strBody = map.get("message");

 showNotification(strTitle, strBody);
 */


/** send dynamic */
        if (message == null) {
            System.out.println("7777777777777777777777777777777777777777777777777777777777777777777777");
            System.out.println("message nullllllllllllllllllllllllllllll");
        }
        if (message.getNotification() != null) {
            System.out.println("88888888888888888888888888888888888888888888888888888888888888888");
            System.out.println("qqq: " + message.getNotification());
            System.out.println("qqq t: " + message.getNotification().getTitle());
            System.out.println("qqq b: " + message.getNotification().getBody());
        }
        if (message.getData() == null) {
            System.out.println("999999999999999999999999999999999999999999999999999999999999999");
        }

        if (message.getData().size() == 0) {
            System.out.println("000000000000000000000000000000000000000000000000");
        }
        System.out.println("XXXXXXXXXX 22: " + message);

        String sented = message.getData().get("sented");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("11 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println("message.getSenderId(): " + message.getSenderId());
        System.out.println("message.getMessageId(): " + message.getMessageId());
        System.out.println("sented: " + sented);
        System.out.println("firebaseUser: " + firebaseUser.getUid());
        System.out.println("getData size: " + message.getData().size());
        System.out.println("getData user: " + message.getData().get("user"));
        System.out.println("getData noti: " + message.getData().get("notify"));
        System.out.println("getData data: " + message.getData().get("data"));
        System.out.println("getData notification: " + message.getData().get("notification"));
        System.out.println("22 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
            sendNotificationFirebase(message);
        }

    }

    private void sendNotificationFirebase(@NonNull RemoteMessage message) {
        String username = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");
        String sented = message.getData().get("sented");

        System.out.println("ss user: " + username);
        System.out.println("ss icon: " + icon);
        System.out.println("ss title: " + title);
        System.out.println("ss body: " + body);
        System.out.println("ss sented: " + sented);


        RemoteMessage.Notification notification = message.getNotification();
        int stack = Integer.parseInt(username.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putString(BUNDLE_KEY_STR_USER_ID, sented);
//        intent.putExtras(bundle);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.img_logo_shop)
                .setSound(defaultSound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int stackNew = 0;
        if (stack > 0) {
            stackNew = stack;
        }
        if (notificationManager != null) {
            notificationManager.notify(stackNew, builder.build());
        }

    }

    private void showNotification(String strTitle, String strBody) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANEL_ID)
                .setContentTitle(strTitle)
                .setContentText(strBody)
                .setSmallIcon(R.mipmap.img_logo_shop)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }

    private void updateToken(String token) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new FCM registration token
                    // Log and toast
                    System.out.println("tokenDevice updateToken: " + token);
                });


    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        updateToken(token);
    }
}
