package com.ksacp2022t3.aiddroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aiddroid.adapters.AccountListAdapter;
import com.ksacp2022t3.aiddroid.models.Account;

import java.util.ArrayList;
import java.util.List;

public class AdminAccountsActivity extends AppCompatActivity {

    ImageView btn_back;
    TextView txt_title;
    RecyclerView recycler_accounts;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_accounts);
        btn_back = findViewById(R.id.btn_back);
        txt_title = findViewById(R.id.txt_title);
        recycler_accounts = findViewById(R.id.recycler_accounts);

        type=getIntent().getStringExtra("type");
        txt_title.setText(type);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);



        load_accounts();

    }

    private void load_accounts(){
       progressDialog.show();
       firestore.collection("accounts")
               .whereEqualTo("account_type",type)
               .get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       progressDialog.dismiss();

                       List<Account> accountList=new ArrayList<>();

                       for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()

                            ) {
                           Account account=doc.toObject(Account.class);
                           accountList.add(account);

                       }
                       AccountListAdapter adapter=new AccountListAdapter(accountList,AdminAccountsActivity.this);
                       recycler_accounts.setAdapter(adapter);

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       progressDialog.dismiss();
                        Toast.makeText(AdminAccountsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                   }
               });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_accounts();
    }
}