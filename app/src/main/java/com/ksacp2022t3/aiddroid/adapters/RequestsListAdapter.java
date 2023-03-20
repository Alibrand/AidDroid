package com.ksacp2022t3.aiddroid.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aiddroid.R;
import com.ksacp2022t3.aiddroid.SOSActivity;
import com.ksacp2022t3.aiddroid.ShowLocationActivity;
import com.ksacp2022t3.aiddroid.models.Account;
import com.ksacp2022t3.aiddroid.models.EmergencyRequest;

import java.text.SimpleDateFormat;
import java.util.List;

public class RequestsListAdapter extends RecyclerView.Adapter<RequestItem> {
    List<EmergencyRequest> requestList;
    Context context;
    FirebaseFirestore firestore;


    public RequestsListAdapter(List<EmergencyRequest> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public RequestItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request,parent,false);

        return new RequestItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestItem holder, int position) {
            EmergencyRequest request=requestList.get(position);
            holder.txt_name.setText(request.getPatient_name());

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.txt_date.setText(simpleDateFormat.format(request.getCreated_at()));


            firestore.collection("accounts")
                            .document(request.getPatient_id())
                                    .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Account account=documentSnapshot.toObject(Account.class);
                                                    holder.btn_call.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent=new Intent(Intent.ACTION_DIAL);
                                                            intent.setData(Uri.parse("tel:"+account.getPhone()));
                                                            context.startActivity(intent);
                                                        }
                                                    });
                                                }
                                            });
            if(request.getStatus()==null)
            {
                holder.txt_status.setText( "Not Responded");
                holder.txt_status.setTextColor(Color.RED);
            }
            else
            {
                holder.txt_status.setText("Responded");
                holder.txt_status.setTextColor(context.getColor(R.color.teal_700));

            }





            holder.btn_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ShowLocationActivity. class);
                    intent.putExtra("lat",String.valueOf(request.getLocation().getLatitude()));
                    intent.putExtra("lng",String.valueOf(request.getLocation().getLongitude()));

                    context.startActivity(intent);
                }
            });

            holder.btn_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(context, SOSActivity.class);
                    intent.putExtra("patient_id",request.getPatient_id());
                    intent.putExtra("patient_name",request.getPatient_name());
                    intent.putExtra("lat",String.valueOf(request.getLocation().getLatitude()));
                    intent.putExtra("lng",String.valueOf(request.getLocation().getLongitude()));
                    intent.putExtra("request_id",request.getId());
                    context.startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}

class RequestItem extends RecyclerView.ViewHolder{

    TextView txt_name,txt_status,txt_date;
    AppCompatButton btn_call,btn_location,btn_view;

    public RequestItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        btn_location=itemView.findViewById(R.id.btn_location);
        btn_call=itemView.findViewById(R.id.btn_call);
        btn_view=itemView.findViewById(R.id.btn_view);
        txt_status=itemView.findViewById(R.id.txt_status);
        txt_date=itemView.findViewById(R.id.txt_date);
    }
}
