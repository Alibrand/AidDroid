package com.ksacp2022t3.aiddroid;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.lights.LightState;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aiddroid.adapters.TopicsListAdapter;
import com.ksacp2022t3.aiddroid.models.Topic;

import java.util.List;

public class AdminFirstAidTopicsActivity extends AppCompatActivity {

    ImageView btn_back;
    RecyclerView recycler_topics;
    FloatingActionButton btn_add;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_first_aid_topics);
        btn_back = findViewById(R.id.btn_back);
        recycler_topics = findViewById(R.id.recycler_topics);
        btn_add = findViewById(R.id.btn_add);

        firestore=FirebaseFirestore.getInstance();

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

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminFirstAidTopicsActivity.this,AdminAddTopicActivity. class);
                startActivity(intent);
            }
        });


    }

    private void load_topics() {
        progressDialog.show();
        firestore.collection("topics")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Topic> topicList=queryDocumentSnapshots.toObjects(Topic.class);
                        if(topicList.size()==0)
                        {
                            makeText(AdminFirstAidTopicsActivity.this,"No topics were found" , LENGTH_LONG).show();
                        }

                        TopicsListAdapter adapter = new TopicsListAdapter(topicList, AdminFirstAidTopicsActivity.this);
                        recycler_topics.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(AdminFirstAidTopicsActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_topics();
    }
}