package com.ksacp2022t3.aiddroid.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendSOS {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAs1-p-ss:APA91bGtDvWPvq3eErX5d_dwPafrtBh3EQDpB_gROse3oslfbCyW5-zsfS2XJA17fzyKFLWLLza_nDUpc1TbwVwdv779Z6eGRuljTzZERzhP-V_n8SrWZM3pKiJpg6mkwLRUCtG3m0g8";
    final private String contentType = "application/json";
    final String TAG = "fcm";
    Context context;

    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;


    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC="/topics/all";

    public SendSOS(Context context)
    {
       firebaseAuth=FirebaseAuth.getInstance();
        this.context=context;
        progressDialog=new ProgressDialog(context);
    }


    public void send(JSONObject data) throws JSONException {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending");
        progressDialog.show();


        NOTIFICATION_TITLE= data.getString("title");
        NOTIFICATION_MESSAGE= data.getString("body");



        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        //JSONObject data = new JSONObject();

        Log.i(TAG, "sending " );
        try {
          //  data.put("center_id",center_id);
          //  data.put("user_id",uid);
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("body", NOTIFICATION_MESSAGE);
            notification.put("data", data);
            notification.put("to", TOPIC);
            notification.put("notification", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
            Toast.makeText(context,  e.getMessage() ,Toast.LENGTH_LONG).show();
progressDialog.dismiss();
        }
        sendNotification(notification);

    }

    private void sendNotification(JSONObject notification) {
        Log.i(TAG, "onResponse: " + notification.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        Toast.makeText(context,"Request has been sent successfully" ,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        ((Activity)context).finish();

                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
                        Toast.makeText(context,error.getMessage() ,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
