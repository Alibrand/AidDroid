package com.ksacp2022t3.aiddroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    EditText txt_email,txt_password;
    TextView txt_sign_up,txt_forgot_password;
    AppCompatButton btn_sign_in;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_sign_up = findViewById(R.id.txt_sign_up);
        txt_forgot_password = findViewById(R.id.txt_forgot_password);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_back = findViewById(R.id.btn_back);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loging In");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity. class);
                startActivity(intent);
                finish();
            }
        });

        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,ForgotPasswordActivity. class);
                startActivity(intent);
            }
        });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_email=txt_email.getText().toString();
                String str_txt_password=txt_password.getText().toString();

                if(str_txt_email.isEmpty())
                {
                     txt_email.setError("Required Field");
                     return;
                }

                if(str_txt_password.isEmpty())
                {
                     txt_password.setError("Required Field");
                     return;
                }

                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(str_txt_email,str_txt_password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String uid=authResult.getUser().getUid();
                                if(authResult.getUser().getEmail().equals("admin@aiddroid.com"))
                                {
                                    Intent intent = new Intent(SignInActivity.this,AdminHomeActivity. class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("EXIT", true);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                               firestore.collection("accounts")
                                       .document(uid)
                                       .get()
                                       .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                           @Override
                                           public void onSuccess(DocumentSnapshot documentSnapshot) {
                                               progressDialog.dismiss();
                                               String account_type=documentSnapshot.getData().get("account_type").toString();
                                               String str_txt_full_name=documentSnapshot.getData().get("full_name").toString();
                                               String status=documentSnapshot.getData().get("status").toString();
                                               if(!status.equals("Active"))
                                               {
                                                   firebaseAuth.signOut();

                                                   Intent intent = new Intent(SignInActivity.this,SorryActivity.
                                                   class);
                                                   startActivity(intent);

                                                   return;

                                               }



                                               SharedPreferences sharedPreferences=SignInActivity.this.getSharedPreferences("aiddroid",MODE_PRIVATE);
                                               SharedPreferences.Editor shareEditor=sharedPreferences.edit();
                                               shareEditor.putString("full_name",str_txt_full_name);
                                               shareEditor.putString("account_type", account_type);
                                               shareEditor.apply();
                                               Intent intent;
                                               if(account_type.equals("Normal User"))
                                               {
                                                   intent = new Intent(SignInActivity.this,HomeActivity.
                                                           class);
                                               }
                                               else {
                                                   intent = new Intent(SignInActivity.this,CenterHomeActivity.
                                                           class);
                                               }
                                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                               intent.putExtra("EXIT", true);
                                               startActivity(intent);
                                               finish();
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               progressDialog.dismiss();
                                                Toast.makeText(SignInActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                           }
                                       });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 Toast.makeText(SignInActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();

                            }
                        });


            }
        });





    }
}