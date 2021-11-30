package com.hastellc.workoutapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hastellc.workoutapp.databinding.ActivityGymMapsBinding;

public class GymMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityGymMapsBinding binding;


    public static final String LATITUDE = "";
    public static final String LONGITUDE = "l";

    private double latitude;
    private double longitude;
    private String TAG = "GymMaps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGymMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra(LATITUDE,0);
        longitude = intent.getDoubleExtra(LONGITUDE,0);
        Log.d(TAG, "lat is " + String.valueOf(latitude) + " and long is " + String.valueOf(longitude));
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
        UiSettings setting = mMap.getUiSettings();
        setting.setZoomControlsEnabled(true);


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);

        LatLng O2 = new LatLng(34.21911,-77.90468);
        LatLng O2Racinedrive = new LatLng(34.24163,-77.86558);

        LatLng planetFitness = new LatLng(34.24096,-77.89055);

        LatLng anytimeFitness5060 = new LatLng(34.23784,-77.87740);
        LatLng anytimeFitness3715 = new LatLng(34.17459,-77.89300);

        LatLng atpFitness = new LatLng(34.25444,-77.85708);
        LatLng tfFitness = new LatLng(34.24546,-77.88809);
        LatLng goGirl = new LatLng(34.22099,-77.88823);
        LatLng outSidetheBoxFitness = new LatLng(34.23212,-77.90376);
        LatLng riseFitnessStudio = new LatLng(34.22574,-77.92376);
        LatLng orangetheaoryfitness = new LatLng(34.23517,-77.83188);
        LatLng unleashedFitness = new LatLng(34.17961,-77.92363);
        LatLng ripxFit = new LatLng(34.21272,-77.88716);

        mMap.addMarker(new MarkerOptions().position(ripxFit).title("RipX Fit"));
        mMap.addMarker(new MarkerOptions().position(unleashedFitness).title("Unleashed Fitness"));
        mMap.addMarker(new MarkerOptions().position(orangetheaoryfitness).title("Orange Theory Fitness"));
        mMap.addMarker(new MarkerOptions().position(riseFitnessStudio).title("Rise Fitness"));
        mMap.addMarker(new MarkerOptions().position(outSidetheBoxFitness).title("Out Side the Box Fitness"));
        mMap.addMarker(new MarkerOptions().position(goGirl).title("Go Girl Fitness"));
        mMap.addMarker(new MarkerOptions().position(tfFitness).title("TF Fitness"));
        mMap.addMarker(new MarkerOptions().position(atpFitness).title("ATP Fitness"));
        mMap.addMarker(new MarkerOptions().position(anytimeFitness3715).title("Anytime Fitness"));
        mMap.addMarker(new MarkerOptions().position(anytimeFitness5060).title("Anytime Fitness"));
        mMap.addMarker(new MarkerOptions().position(planetFitness).title("Planet Fitness"));
        mMap.addMarker(new MarkerOptions().position(O2).title("O2 Fitness Independence Blvd"));

        mMap.addMarker(new MarkerOptions().position(sydney).title("YOUR APRROXIMATE LOCATION"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 5));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);



    }
}