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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022t3.aiddroid.models.Topic;
import com.ksacp2022t3.aiddroid.services.GlideApp;

import java.util.UUID;

public class AdminAddTopicActivity extends AppCompatActivity {
    ImageView topic_image,btn_back;
    EditText txt_title,txt_content;
    AppCompatButton btn_save;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    Uri selected_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_topic);
        topic_image = findViewById(R.id.topic_image);
        btn_back = findViewById(R.id.btn_back);
        txt_title = findViewById(R.id.txt_title);
        txt_content = findViewById(R.id.txt_content);
        btn_save = findViewById(R.id.btn_save);


        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Saving");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        topic_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),110);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_title =txt_title.getText().toString();
                String str_txt_content =txt_content.getText().toString();


                if(str_txt_title.isEmpty())
                {
                     txt_title.setError("Required Field");
                     return;
                }
                 if(str_txt_content.isEmpty())
                 {
                      txt_content.setError("Required Field");
                      return;
                 }

                 if(selected_image==null)
                 {
                     makeText(AdminAddTopicActivity.this,"You should set image for the topic" , LENGTH_LONG).show();
                 return;
                 }

                 progressDialog.show();

                 String image_name= UUID.randomUUID().toString()+".png";

                StorageReference reference=storage.getReference();
                StorageReference image_path=reference.child("topic_images/"+image_name);

                image_path.putFile(selected_image)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Topic topic=new Topic();
                                topic.setTitle(str_txt_title);
                                topic.setContent(str_txt_content);
                                topic.setImage(image_name);
                                DocumentReference topic_doc=firestore.collection("topics")
                                        .document();
                                topic.setId(topic_doc.getId());

                                topic_doc.set(topic)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                makeText(AdminAddTopicActivity.this,"Topic was saved successfully" , LENGTH_LONG).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                makeText(AdminAddTopicActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(AdminAddTopicActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });




            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            selected_image = data.getData();

            GlideApp.with(AdminAddTopicActivity.this)
                    .load(selected_image)
                    .centerCrop()
                    .into(topic_image);

        }
    }
}