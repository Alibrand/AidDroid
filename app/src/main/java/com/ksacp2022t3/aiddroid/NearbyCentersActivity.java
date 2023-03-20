package com.ksacp2022t3.aiddroid;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.aiddroid.databinding.ActivityNearbyCentersBinding;
import com.ksacp2022t3.aiddroid.models.CenterAccount;

public class NearbyCentersActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityNearbyCentersBinding binding;
    private FusedLocationProviderClient fusedLocationProverClient;
    private Location user_location;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationProverClient = LocationServices.getFusedLocationProviderClient(this);
        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading locations");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
        firestore=FirebaseFirestore.getInstance();
        binding = ActivityNearbyCentersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NearbyCentersActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION  }, 110);

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        fusedLocationProverClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                 CameraUpdate update;
                if(getIntent().getDoubleExtra("lat",0.0)==0.0)
                    update = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(),location.getLongitude()), 15);
                else
                {
                    double lat=getIntent().getDoubleExtra("lat",location.getLatitude());
                    double lng=getIntent().getDoubleExtra("lng",location.getLongitude());
                    update = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lat,lng), 15);



                }
                mMap.animateCamera( update);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 Toast.makeText(NearbyCentersActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();

            }
        });

        load_centers_on_maps();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                String center_id=marker.getTag().toString();
                Intent intent = new Intent(NearbyCentersActivity.this,CenterProfileActivity. class);
                intent.putExtra("center_id",center_id);
                startActivity(intent);

            }
        });
    }

    private void load_centers_on_maps(){
            progressDialog.show();
            firestore.collection("accounts")
                    .whereEqualTo("account_type","Medical Center")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            progressDialog.dismiss();
                            for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                                 ) {
                            CenterAccount centerAccount=doc.toObject(CenterAccount.class);
                            LatLng latLng=new LatLng(centerAccount.getLocation().getLatitude(),centerAccount.getLocation().getLongitude());

                            Marker marker=mMap.addMarker(new MarkerOptions().position(latLng).title(centerAccount.getFull_name()));
                            marker.setTag(centerAccount.getId());

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                             Toast.makeText(NearbyCentersActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        }
                    });
    }
}