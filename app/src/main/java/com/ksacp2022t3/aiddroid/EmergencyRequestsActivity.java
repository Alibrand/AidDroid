package com.ksacp2022t3.aiddroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aiddroid.adapters.RequestsListAdapter;
import com.ksacp2022t3.aiddroid.models.EmergencyRequest;

import java.util.List;

public class EmergencyRequestsActivity extends AppCompatActivity {

    ImageView btn_back;
    RecyclerView recycler_requests;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_requests);
        btn_back = findViewById(R.id.btn_back);
        recycler_requests = findViewById(R.id.recycler_requests);

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


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


        load_requests();





    }

    private void load_requests() {
        progressDialog.show();
        firestore.collection("emergency_requests")
                .whereEqualTo("center_id",firebaseAuth.getUid())
                .orderBy("status")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<EmergencyRequest>requests=queryDocumentSnapshots.toObjects(EmergencyRequest.class);
                        RequestsListAdapter adapter=new RequestsListAdapter(requests,EmergencyRequestsActivity.this);
                        recycler_requests.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EmergencyRequestsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_requests();
    }
}