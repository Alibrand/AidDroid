package com.ksacp2022t3.aiddroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.aiddroid.models.Topic;
import com.ksacp2022t3.aiddroid.services.GlideApp;

public class ViewTopicActivity extends AppCompatActivity {
    TextView txt_title,txt_content;
    ImageView topic_image,btn_back;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_topic);
        txt_title = findViewById(R.id.txt_title);
        txt_content = findViewById(R.id.txt_content);
        btn_back = findViewById(R.id.btn_back);
        topic_image = findViewById(R.id.topic_image);

        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String topic_id=getIntent().getStringExtra("topic_id");


        firestore.collection("topics")
                .document(topic_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Topic topic=documentSnapshot.toObject(Topic.class);
                        txt_title.setText(topic.getTitle());
                        txt_content.setText(topic.getContent());

                        StorageReference reference=storage.getReference();
                        StorageReference image=reference.child("topic_images/"+topic.getImage());

                        GlideApp.with(ViewTopicActivity.this)
                                .load(image)
                                .centerCrop()
                                .into(topic_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ViewTopicActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });




    }
}