package com.ksacp2022t3.aiddroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {
    AppCompatButton btn_logout,btn_users,btn_medical_centers,btn_topics;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        btn_logout = findViewById(R.id.btn_logout);
        btn_users = findViewById(R.id.btn_users);
        btn_medical_centers = findViewById(R.id.btn_medical_centers);
        btn_topics = findViewById(R.id.btn_topics);



        firebaseAuth= FirebaseAuth.getInstance();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                Intent intent = new Intent(AdminHomeActivity.this,MainActivity. class);
                startActivity(intent);
                finish();
            }
        });

        btn_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminAccountsActivity. class);
                intent.putExtra("type","Normal User");
                startActivity(intent);
            }
        });

        btn_medical_centers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminAccountsActivity. class);
                intent.putExtra("type","Medical Center");
                startActivity(intent);
            }
        });

        btn_topics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminFirstAidTopicsActivity. class);
                startActivity(intent);
            }
        });
    }
}