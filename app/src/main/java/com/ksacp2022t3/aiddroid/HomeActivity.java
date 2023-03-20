package com.ksacp2022t3.aiddroid;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aiddroid.models.UserAccount;
import com.ksacp2022t3.aiddroid.services.SendSOS;

public class HomeActivity extends AppCompatActivity {
    AppCompatButton btn_first_aid,btn_logout,btn_nearby_centers,btn_instant_help,
    btn_health_form;
    FirebaseAuth  firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    TextView txt_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btn_logout = findViewById(R.id.btn_logout);
        txt_name = findViewById(R.id.txt_name);
        btn_nearby_centers = findViewById(R.id.btn_nearby_centers);
        btn_instant_help = findViewById(R.id.btn_instant_help);
        btn_health_form = findViewById(R.id.btn_health_form);
        btn_first_aid = findViewById(R.id.btn_first_aid);





        btn_first_aid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,FirstAidTopicsActivity. class);
                startActivity(intent);
            }
        });




        SharedPreferences sharedPreferences=this.getSharedPreferences("aiddroid",MODE_PRIVATE);
        String user_name=sharedPreferences.getString("full_name","");
        txt_name.setText(user_name);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        btn_nearby_centers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,NearbyCentersActivity. class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                Intent intent = new Intent(HomeActivity.this,MainActivity. class);
                startActivity(intent);
                finish();
            }
        });

        btn_health_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,MyHealthFormActivity. class);
                startActivity(intent);
            }
        });

        btn_instant_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                firestore.collection("accounts")
                        .document(firebaseAuth.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                progressDialog.dismiss();
                                UserAccount userAccount=documentSnapshot.toObject(UserAccount.class);
                                if(userAccount.getInstant_help_center()==null)
                                {
                                    makeText(HomeActivity.this,"No Medical center was set as an instant help" , LENGTH_LONG).show();
                                    makeText(HomeActivity.this,"Please tap on Nearby Centers and choose an instant center" , LENGTH_LONG).show();

                                }
                                else
                                {
                                    Intent intent = new Intent(HomeActivity.this,CenterProfileActivity. class);
                                    intent.putExtra("center_id",userAccount.getInstant_help_center());
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 makeText(HomeActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });

    }
}