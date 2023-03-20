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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aiddroid.models.UserAccount;

import java.util.Locale;

public class HealthFormActivity extends AppCompatActivity {
    EditText txt_full_name,txt_age,txt_diseases ,
    txt_phone;
    RadioGroup group_gender;
    Spinner sp_blood_group;
    AppCompatButton btn_sign_up;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_form);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_age = findViewById(R.id.txt_age);
        txt_diseases = findViewById(R.id.txt_diseases);
          txt_phone = findViewById(R.id.txt_phone);
        group_gender = findViewById(R.id.group_gender);
        sp_blood_group = findViewById(R.id.sp_blood_group);
        btn_sign_up = findViewById(R.id.btn_sign_up);

        String[] blood_groups=new String[]{"A+","B+","AB+","O+","A-","B-","AB-","O-"};
        ArrayAdapter adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,blood_groups);
        sp_blood_group.setAdapter(adapter);

        String email=getIntent().getStringExtra("email");
        String password=getIntent().getStringExtra("password");


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Creating Account");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);



          btn_sign_up.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  String str_txt_full_name =txt_full_name.getText().toString();
                  String str_txt_age =txt_age.getText().toString();
                  String str_txt_diseases =txt_diseases.getText().toString();

                  String str_txt_phone =txt_phone.getText().toString();
                  RadioButton selected_gender=findViewById(group_gender.getCheckedRadioButtonId());
                  String gender=selected_gender.getText().toString();
                  String blood_group=sp_blood_group.getSelectedItem().toString();


                  if(str_txt_full_name.isEmpty())
                  {
                       txt_full_name.setError("Required Field");
                       return;
                  }
                  if(str_txt_age.isEmpty())
                  {
                       txt_age.setError("Required Field");
                       return;
                  }
                  if(str_txt_phone.isEmpty())
                  {
                       txt_phone.setError("Required Field");
                       return;
                  }


                  progressDialog.show();
                  firebaseAuth.createUserWithEmailAndPassword(email,password)
                          .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                              @Override
                              public void onSuccess(AuthResult authResult) {
                                  String uid=authResult.getUser().getUid();

                                  UserAccount userAccount=new UserAccount();
                                  userAccount.setId(uid);
                                  userAccount.setGender(gender);
                                  userAccount.setFull_name(str_txt_full_name);
                                  userAccount.setAge(str_txt_age);
                                  userAccount.setBlood_group(blood_group);
                                  userAccount.setChronic_diseases(str_txt_diseases);
                                   userAccount.setPhone(str_txt_phone);
                                  userAccount.setAccount_type("Normal User");

                                  firestore.collection("accounts")
                                          .document(uid)
                                          .set(userAccount)
                                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void unused) {
                                                  progressDialog.dismiss();
                                                  makeText(HealthFormActivity.this,"User Created successfully" , LENGTH_LONG).show();
                                                  SharedPreferences sharedPreferences=HealthFormActivity.this.getSharedPreferences("aiddroid",MODE_PRIVATE);
                                                  SharedPreferences.Editor shareEditor=sharedPreferences.edit();
                                                  shareEditor.putString("full_name",str_txt_full_name);
                                                  shareEditor.putString("account_type", "Normal User");
                                                  shareEditor.apply();

                                                  Intent intent = new Intent(HealthFormActivity.this,HomeActivity.
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
                                                  makeText(HealthFormActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                                              }
                                          });

                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  progressDialog.dismiss();
                                   makeText(HealthFormActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                              }
                          });
              }
          });




    }
}