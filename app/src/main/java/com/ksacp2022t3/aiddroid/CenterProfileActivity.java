package com.ksacp2022t3.aiddroid;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ksacp2022t3.aiddroid.models.CenterAccount;
import com.ksacp2022t3.aiddroid.models.EmergencyRequest;
import com.ksacp2022t3.aiddroid.services.SendSOS;

import org.json.JSONException;
import org.json.JSONObject;

public class CenterProfileActivity extends AppCompatActivity {
    TextView txt_full_name, sp_from, sp_to, txt_phone, txt_services;
    AppCompatButton btn_call, btn_request_ambulance, btn_instant_help;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_profile);
        txt_full_name = findViewById(R.id.txt_full_name);
        sp_from = findViewById(R.id.sp_from);
        sp_to = findViewById(R.id.sp_to);
        txt_phone = findViewById(R.id.txt_phone);
        txt_services = findViewById(R.id.txt_services);
        btn_call = findViewById(R.id.btn_call);
        btn_request_ambulance = findViewById(R.id.btn_request_ambulance);
        btn_instant_help = findViewById(R.id.btn_instant_help);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        String center_id = getIntent().getStringExtra("center_id");
        progressDialog.show();
        firestore.collection("accounts")
                .document(center_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        CenterAccount account = documentSnapshot.toObject(CenterAccount.class);
                        txt_full_name.setText(account.getFull_name());
                        txt_phone.setText(account.getPhone());
                        txt_services.setText(account.getServices());
                        sp_from.setText(String.valueOf(account.getWork_from()));
                        sp_to.setText(String.valueOf(account.getWork_to()));


                        btn_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + account.getPhone()));
                                startActivity(intent);
                            }
                        });

                        btn_instant_help.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                progressDialog.setTitle("Updating");
                                progressDialog.show();
                                firestore.collection("accounts")
                                        .document(firebaseAuth.getUid())
                                        .update("instant_help_center", center_id)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                makeText(CenterProfileActivity.this, "Center was set as a default instant help center", LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                progressDialog.dismiss();
                                                makeText(CenterProfileActivity.this, "Error :" + e.getMessage(), LENGTH_LONG).show();
                                            }
                                        });

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(CenterProfileActivity.this, "Error :" + e.getMessage(), LENGTH_LONG).show();
                    }
                });


        btn_request_ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(CenterProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CenterProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(CenterProfileActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},110);
                    return;
                }
                progressDialog.setMessage("Getting your location");

                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                SharedPreferences sharedPreferences=CenterProfileActivity.this.getSharedPreferences("aiddroid",MODE_PRIVATE);
                                String user_name=sharedPreferences.getString("full_name","");
                                EmergencyRequest request=new EmergencyRequest();
                                request.setCenter_id(center_id);
                                request.setLocation(new GeoPoint(location.getLatitude(),location.getLongitude()));
                                request.setPatient_id(FirebaseAuth.getInstance().getUid());
                                request.setPatient_name(user_name);
                                DocumentReference doc=FirebaseFirestore.getInstance()
                                        .collection("emergency_requests")
                                        .document();
                                request.setId(doc.getId());

                                doc.set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        JSONObject data=new JSONObject() ;
                                        try {
                                            data.put("to",center_id);
                                            data.put("patient_id",FirebaseAuth.getInstance().getUid());
                                            data.put("patient_name",user_name);
                                            data.put("lat",location.getLatitude());
                                            data.put("lng",location.getLongitude());
                                            data.put("request_id",request.getId());
                                            data.put("type","emergency_request");
                                            data.put("title","Emergency Request");
                                            data.put("body","I need quick help");


                                            SendSOS sendSOS=new SendSOS(CenterProfileActivity.this);
                                            sendSOS.send(data);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });


                                }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 makeText(CenterProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });



    }
}