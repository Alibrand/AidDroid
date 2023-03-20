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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aiddroid.models.UserAccount;

import java.util.Arrays;
import java.util.Collections;

public class MyHealthFormActivity extends AppCompatActivity {

    EditText txt_full_name,txt_age,txt_diseases  ,
            txt_phone;
    RadioGroup group_gender;
    RadioButton rdbtn_male,rdbtn_female;
    Spinner sp_blood_group;
    AppCompatButton btn_save;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String[] blood_groups;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_health_form);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_age = findViewById(R.id.txt_age);
        txt_diseases = findViewById(R.id.txt_diseases);
         txt_phone = findViewById(R.id.txt_phone);
        group_gender = findViewById(R.id.group_gender);
        sp_blood_group = findViewById(R.id.sp_blood_group);
        btn_save = findViewById(R.id.btn_save);
        rdbtn_female = findViewById(R.id.rdbtn_female);
        rdbtn_male = findViewById(R.id.rdbtn_male);
        btn_back = findViewById(R.id.btn_back);





         blood_groups=new String[]{"A+","B+","AB+","O+","A-","B-","AB-","O-"};
        ArrayAdapter adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,blood_groups);
        sp_blood_group.setAdapter(adapter);

       btn_back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               finish();
           }
       });


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Updating Account");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        load_info();



        btn_save.setOnClickListener(new View.OnClickListener() {
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
                                String uid=firebaseAuth.getUid();



                                firestore.collection("accounts")
                                        .document(uid)
                                        .update(
                                                "full_name",str_txt_full_name,
                                                "phone",str_txt_phone,
                                                "blood_group",sp_blood_group.getSelectedItem().toString(),
                                                "chronic_diseases",str_txt_diseases,
                                                 "age",str_txt_age,
                                                "gender",gender

                                        )
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                makeText(MyHealthFormActivity.this,"Health form was updated successfully" , LENGTH_LONG).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                makeText(MyHealthFormActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                                            }
                                        });

            }
        });




    }

    private void load_info() {
        String uid= firebaseAuth.getUid();
        progressDialog.setTitle("Loading Info");
        progressDialog.show();
        firestore.collection("accounts")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        UserAccount userAccount=documentSnapshot.toObject(UserAccount.class);
                        txt_full_name.setText(userAccount.getFull_name());
                        txt_phone.setText(userAccount.getPhone());
                        txt_age.setText(String.valueOf(userAccount.getAge()));
                        txt_diseases.setText(userAccount.getChronic_diseases());
                        sp_blood_group.setSelection(Arrays.asList(blood_groups).indexOf(userAccount.getBlood_group()));
                         if(userAccount.getGender().equals("Male"))
                            rdbtn_male.setChecked(true);
                        else
                            rdbtn_female.setChecked(true);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         Toast.makeText(MyHealthFormActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();

                    }
                });
    }
}