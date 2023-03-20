package com.ksacp2022t3.aiddroid;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ksacp2022t3.aiddroid.models.CenterAccount;

public class CenterFormActivity extends AppCompatActivity {
    EditText txt_full_name,txt_phone,txt_services;
    Spinner sp_from,sp_to;
    AppCompatButton btn_set_location,btn_sign_up;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String [] hours=new String[]{"00","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23",};
    GeoPoint center_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_form);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_services = findViewById(R.id.txt_services);
        sp_from = findViewById(R.id.sp_from);
        sp_to = findViewById(R.id.sp_to);
        btn_set_location = findViewById(R.id.btn_set_location);
        btn_sign_up = findViewById(R.id.btn_sign_up);



        ArrayAdapter adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,hours);
        sp_from.setAdapter(adapter);
        sp_to.setAdapter(adapter);


        String email=getIntent().getStringExtra("email");
        String password=getIntent().getStringExtra("password");


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        btn_set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CenterFormActivity.this,MapsActivity. class);
                startActivityForResult(intent,100);
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_full_name =txt_full_name.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();
                String str_txt_services =txt_services.getText().toString();
                int from=Integer.parseInt(sp_from.getSelectedItem().toString());
                int to=Integer.parseInt(sp_to.getSelectedItem().toString());

                if(str_txt_full_name.isEmpty())
                {
                    txt_full_name.setError("Required Field");
                    return;
                }

                if(str_txt_phone.isEmpty()) {
                    txt_phone.setError("Required Field");
                    return;
                }
                if(str_txt_services.isEmpty())
                {
                    txt_services.setError("Required Field");
                    return;
                }
                if(center_location==null)
                {
                    makeText(CenterFormActivity.this,"You should set center location" , LENGTH_LONG).show();
                    return;
                }


                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String uid=authResult.getUser().getUid();
                                CenterAccount centerAccount=new CenterAccount();
                                centerAccount.setId(uid);
                                centerAccount.setFull_name(str_txt_full_name);
                                centerAccount.setPhone(str_txt_phone);
                                centerAccount.setServices(str_txt_services);
                                centerAccount.setLocation(center_location);
                                centerAccount.setWork_from(from);
                                centerAccount.setWork_to(to);
                                centerAccount.setAccount_type("Medical Center");

                                firestore.collection("accounts")
                                        .document(uid)
                                        .set(centerAccount)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                makeText(CenterFormActivity.this,"Center Created successfully" , LENGTH_LONG).show();
                                                SharedPreferences sharedPreferences=CenterFormActivity.this.getSharedPreferences("aiddroid",MODE_PRIVATE);
                                                SharedPreferences.Editor shareEditor=sharedPreferences.edit();
                                                shareEditor.putString("full_name",str_txt_full_name);
                                                shareEditor.putString("account_type", "Medical Center");
                                                shareEditor.apply();

                                                Intent intent = new Intent(CenterFormActivity.this,CenterHomeActivity.
                                                        class);
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
                                                makeText(CenterFormActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(CenterFormActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });




            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100)
            if(resultCode==RESULT_OK)
            {
                double latitude= (double) data.getSerializableExtra("lat");
                double longitude= (double) data.getSerializableExtra("long");
                makeText(CenterFormActivity.this,"Location set successfully", LENGTH_LONG).show();
                center_location=new GeoPoint(latitude,longitude);
            }
    }
}