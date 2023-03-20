package com.ksacp2022t3.aiddroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aiddroid.models.Account;
import com.ksacp2022t3.aiddroid.services.SendSOS;

import org.json.JSONException;
import org.json.JSONObject;

public class SOSActivity extends AppCompatActivity {

    TextView txt_name;
    AppCompatButton btn_call,btn_show_location,
    btn_on_the_way,btn_sorry;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosactivity);
        txt_name = findViewById(R.id.txt_name);
        btn_call = findViewById(R.id.btn_call);
        btn_show_location = findViewById(R.id.btn_show_location);
        btn_on_the_way = findViewById(R.id.btn_on_the_way);
        btn_sorry = findViewById(R.id.btn_sorry);

        String patient_name=getIntent().getStringExtra("patient_name");
        String patient_id=getIntent().getStringExtra("patient_id");
        String lat=getIntent().getStringExtra("lat");
        String lng=getIntent().getStringExtra("lng");
        String request_id=getIntent().getStringExtra("request_id");


        firestore=FirebaseFirestore.getInstance();

        btn_call.setEnabled(false);
        firestore.collection("accounts")
                        .document(patient_id)
                                .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Account account=documentSnapshot.toObject(Account.class);
                                                btn_call.setEnabled(true);
                                                btn_call.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent=new Intent(Intent.ACTION_DIAL);
                                                        intent.setData(Uri.parse("tel:"+account.getPhone()));
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                         Toast.makeText(SOSActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });

        txt_name.setText(patient_name);


        btn_show_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SOSActivity.this,ShowLocationActivity. class);
               intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                startActivity(intent);
            }
        });


        btn_on_the_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences=SOSActivity.this.getSharedPreferences("aiddroid",MODE_PRIVATE);
                String user_name=sharedPreferences.getString("full_name","");
                JSONObject data=new JSONObject() ;
                try {
                    data.put("to",patient_id);
                    data.put("center_id",FirebaseAuth.getInstance().getUid());
                    data.put("center_name",user_name);
                    data.put("type","response");
                    data.put("title",user_name+" Response");
                    data.put("body","We are on the way");

                    SendSOS sendSOS=new SendSOS(SOSActivity.this);
                    sendSOS.send(data);

                    firestore.collection("emergency_requests")
                            .document(request_id)
                            .update(
                                    "status","We are on the way"
                            );



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_sorry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences=SOSActivity.this.getSharedPreferences("aiddroid",MODE_PRIVATE);
                String user_name=sharedPreferences.getString("full_name","");
                JSONObject data=new JSONObject() ;
                try {
                    data.put("to",patient_id);
                    data.put("center_id",FirebaseAuth.getInstance().getUid());
                    data.put("center_name",user_name);
                    data.put("type","response");
                    data.put("title",user_name+" Response");
                    data.put("body","Sorry ... we are busy");


                    SendSOS sendSOS=new SendSOS(SOSActivity.this);
                    sendSOS.send(data);

                    firestore.collection("emergency_requests")
                            .document(request_id)
                            .update(
                                    "status","Sorry ... we are busy"
                            );



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });






    }
}