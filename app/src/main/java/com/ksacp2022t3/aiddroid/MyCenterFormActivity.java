package com.ksacp2022t3.aiddroid;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ksacp2022t3.aiddroid.models.CenterAccount;

import java.util.Arrays;

public class MyCenterFormActivity extends AppCompatActivity {
    EditText txt_full_name,txt_phone,txt_services;
    Spinner sp_from,sp_to;
    AppCompatButton btn_set_location,btn_save;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String [] hours=new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23",};
    GeoPoint center_location;
    ImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_center_form);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_services = findViewById(R.id.txt_services);
        sp_from = findViewById(R.id.sp_from);
        sp_to = findViewById(R.id.sp_to);
        btn_set_location = findViewById(R.id.btn_set_location);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        ArrayAdapter adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,hours);
        sp_from.setAdapter(adapter);
        sp_to.setAdapter(adapter);

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading Account");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        progressDialog.show();
        firestore.collection("accounts")
                        .document(firebaseAuth.getUid())
                                .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                progressDialog.dismiss();
                                                CenterAccount account=documentSnapshot.toObject(CenterAccount.class);

                                                txt_full_name.setText(account.getFull_name());
                                                txt_phone.setText(account.getPhone());
                                                txt_services.setText(account.getServices());
                                                sp_from.setSelection(Arrays.asList(hours).indexOf(String.valueOf(account.getWork_from())));
                                                sp_to.setSelection(Arrays.asList(hours).indexOf(String.valueOf(account.getWork_to())));

                                                center_location=account.getLocation();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                   progressDialog.dismiss();
                   makeText(MyCenterFormActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                   finish();
                    }
                });

        btn_set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyCenterFormActivity.this,MapsActivity. class);
                startActivityForResult(intent,100);
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
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

                progressDialog.setTitle("Updating Account");
                progressDialog.show();
                firestore.collection("accounts")
                        .document(firebaseAuth.getUid())
                        .update(
                                "full_name",str_txt_full_name,
                                "phone",str_txt_phone,
                                "services",str_txt_services,
                                "location",center_location,
                                "work_from",from,
                                "work_to",to
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(MyCenterFormActivity.this,"Account updated successfully" , LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(MyCenterFormActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
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
                makeText(MyCenterFormActivity.this,"Location set successfully", LENGTH_LONG).show();
                center_location=new GeoPoint(latitude,longitude);
            }
    }
}