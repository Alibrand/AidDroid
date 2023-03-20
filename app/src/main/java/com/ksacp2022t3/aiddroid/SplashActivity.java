package com.ksacp2022t3.aiddroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        firebaseAuth=FirebaseAuth.getInstance();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                FirebaseMessaging fcm=FirebaseMessaging.getInstance();

                fcm.subscribeToTopic("all");

                if(firebaseAuth.getCurrentUser()!=null)
                {
                    if(firebaseAuth.getCurrentUser().getEmail().equals("admin@aiddroid.com"))
                    {
                        Intent intent = new Intent(SplashActivity.this,AdminHomeActivity. class);
                        startActivity(intent);
                    }
                    else {
                        SharedPreferences sharedPreferences = SplashActivity.this.getSharedPreferences("aiddroid", MODE_PRIVATE);
                        String account_type = sharedPreferences.getString("account_type", "");
                        if (account_type.equals("Normal User")) {
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SplashActivity.this, CenterHomeActivity.class);
                            startActivity(intent);
                        }
                    }
                }
                else{
                    Intent intent = new Intent(SplashActivity.this,MainActivity. class);
                    startActivity(intent);
                }
                finish();


            }
        },3000);
    }
}