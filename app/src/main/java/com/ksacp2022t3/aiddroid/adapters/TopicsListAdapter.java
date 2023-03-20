package com.ksacp2022t3.aiddroid.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.aiddroid.AdminEditTopicActivity;
import com.ksacp2022t3.aiddroid.R;
import com.ksacp2022t3.aiddroid.ViewTopicActivity;
import com.ksacp2022t3.aiddroid.models.Topic;
import com.ksacp2022t3.aiddroid.services.GlideApp;

import java.util.List;

public class TopicsListAdapter extends RecyclerView.Adapter<TopicItem>{
    List<Topic> topicList;
    Context context;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    FirebaseStorage storage;

    public TopicsListAdapter(List<Topic> topicList, Context context) {
        this.topicList = topicList;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Deleting Topic");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @NonNull
    @Override
    public TopicItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic,parent,false);
        return new TopicItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicItem holder, int position) {
            Topic topic=topicList.get(position);

            holder.txt_title.setText(topic.getTitle());


        StorageReference reference=storage.getReference();
        StorageReference image=reference.child("topic_images/"+topic.getImage());


        GlideApp.with(context)
                .load(image)
                .transform(new CenterCrop(), new RoundedCorners(16))
                .into(holder.topic_image);

            if(!firebaseAuth.getCurrentUser().getEmail().equals("admin@aiddroid.com"))
            {
                holder.btn_delete.setVisibility(View.GONE);
            }

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    firestore.collection("topics")
                            .document(topic.getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    topicList.remove(topic);
                                    TopicsListAdapter.this.notifyDataSetChanged();
                                    image.delete();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                }
                            });
                }
            });

            holder.topic_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(firebaseAuth.getCurrentUser().getEmail().equals("admin@aiddroid.com"))
                    {
                        Intent intent=new Intent(context, AdminEditTopicActivity.class);
                        intent.putExtra("topic_id",topic.getId());
                        context.startActivity(intent);
                    }else
                    {
                        Intent intent=new Intent(context, ViewTopicActivity.class);
                        intent.putExtra("topic_id",topic.getId());
                        context.startActivity(intent);
                    }
                }
            });





    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }
}

class TopicItem extends RecyclerView.ViewHolder
{
    LinearLayoutCompat topic_card;
    TextView txt_title;
    ImageView topic_image,btn_delete;

    public TopicItem(@NonNull View itemView) {
        super(itemView);
        topic_card=itemView.findViewById(R.id.topic_card);
        txt_title=itemView.findViewById(R.id.txt_title);
        btn_delete=itemView.findViewById(R.id.btn_delete);
        topic_image=itemView.findViewById(R.id.topic_image);
    }
}
