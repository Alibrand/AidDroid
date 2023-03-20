package com.ksacp2022t3.aiddroid;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aiddroid.models.CenterAccount;

import org.w3c.dom.Text;

public class CenterHomeActivity extends AppCompatActivity {
    AppCompatButton btn_logout,btn_requests,btn_account;
    FirebaseAuth firebaseAuth;
    TextView txt_name,txt_message;
    LinearLayoutCompat notification_alert;
    ImageButton btn_close;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_home);
        btn_logout = findViewById(R.id.btn_logout);
        txt_name = findViewById(R.id.txt_name);
        btn_requests = findViewById(R.id.btn_requests);
        btn_account = findViewById(R.id.btn_account);
        btn_close = findViewById(R.id.btn_close);
        txt_message = findViewById(R.id.txt_message);
        notification_alert = findViewById(R.id.notification_alert);





        SharedPreferences sharedPreferences=this.getSharedPreferences("aiddroid",MODE_PRIVATE);
        String user_name=sharedPreferences.getString("full_name","");
        txt_name.setText(user_name);

        firebaseAuth=FirebaseAuth.getInstance();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                Intent intent = new Intent(CenterHomeActivity.this,MainActivity. class);
                startActivity(intent);
                finish();
            }
        });
        btn_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CenterHomeActivity.this,EmergencyRequestsActivity. class);
                startActivity(intent);
            }
        });

        btn_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CenterHomeActivity.this,MyCenterFormActivity. class);
                startActivity(intent);
            }
        });

        notification_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CenterHomeActivity.this,EmergencyRequestsActivity. class);
                startActivity(intent);
            }
        });





    }


    void check_requests(){
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();

        firestore.collection("emergency_requests")
                .whereEqualTo("center_id",firebaseAuth.getUid())
                .whereEqualTo("status",null)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()>0)
                        {
                            notification_alert.setVisibility(View.VISIBLE);
                            txt_message.setText("You have "+queryDocumentSnapshots.size()+" requests without a reply");
                            if(!isNotificationVisible()) {
                                make_notification(queryDocumentSnapshots.size());
                           }
                            btn_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    notification_alert.setVisibility(View.GONE);
                                }
                            });



                        }
                        else
                        { notification_alert.setVisibility(View.GONE);
                            NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.cancel(4);
                        }
                    }
                }) ;

    }

    @Override
    protected void onResume() {
        super.onResume();
        check_requests();
    }

    void make_notification(int count){
        String CHANNEL_ID = "channel_02";
        Intent intent=new Intent(this,EmergencyRequestsActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Caution")
                .setContentText("There are "+count+" requests need a reply")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(activityPendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("There are "+count+" requests need a reply")
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

        Notification notification=builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
// notificationId is a unique int for each notification that you must define
        notificationManager.notify(4, builder.build());
    }

    private boolean isNotificationVisible () {
        NotificationManager mNotificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        StatusBarNotification[] notifications =
                mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == 4) {
                 return true;
            }
        }
        return  false;
    }
}