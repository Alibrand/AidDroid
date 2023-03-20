package com.ksacp2022t3.aiddroid.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ksacp2022t3.aiddroid.R;
import com.ksacp2022t3.aiddroid.SOSActivity;
import com.ksacp2022t3.aiddroid.models.EmergencyRequest;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    String TAG="fcm";
    private static final String CHANNEL_ID = "channel_02";

    FirebaseAuth firebaseAuth;


    public FCMService() {


    }



    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + message.getFrom());
        Log.d(TAG, "Type: " + message.getMessageType());
        Log.d(TAG, "Data: " + message.getData().toString());
        Map<String,String > data=message.getData();
        firebaseAuth=FirebaseAuth.getInstance();
        Intent intent=new Intent();
        //check if message is to this user
        if(!firebaseAuth.getUid().equals(data.get("to")))
            return;
        if(data.get("type").equals("emergency_request"))
        {
            intent= new Intent(this, SOSActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("patient_id",data.get("patient_id"));
            intent.putExtra("patient_name",data.get("patient_name"));
            intent.putExtra("lat",data.get("lat"));
            intent.putExtra("lng",data.get("lng"));
            intent.putExtra("request_id",data.get("request_id"));
            startActivity(intent);


        }



        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
               intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(message.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(activityPendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(message.getNotification().getBody())
                .setWhen(System.currentTimeMillis())
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true);

        NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SOS";
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{500,1000,500,1000});

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel);
        }


// notificationId is a unique int for each notification that you must define
        notificationManager.notify(4, builder.build());

        // Check if message contains a data payload.
        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());



        }

        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());
        }

    }
}