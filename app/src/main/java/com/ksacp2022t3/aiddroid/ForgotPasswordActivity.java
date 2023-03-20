package com.ksacp2022t3.aiddroid;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText txt_email;
    AppCompatButton btn_send;
    ImageView btn_back;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        txt_email = findViewById(R.id.txt_email);
        btn_send = findViewById(R.id.btn_send);
        btn_back = findViewById(R.id.btn_back);


        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Sending");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);


         btn_back.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 finish();
             }
         });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_email =txt_email.getText().toString();

                if(str_txt_email.isEmpty())
                {
                     txt_email.setError("Required Field");
                     return;
                }

                progressDialog.show();

                firebaseAuth.sendPasswordResetEmail(str_txt_email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                              progressDialog.dismiss();
                                makeText(ForgotPasswordActivity.this,"A rest password link was sent...check your inbox" , LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 makeText(ForgotPasswordActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });

            }
        });
    }
}