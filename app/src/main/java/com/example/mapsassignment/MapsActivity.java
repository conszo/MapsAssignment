package com.example.mapsassignment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import android.app.AlertDialog;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import com.example.mapsassignment.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    final private int REQUEST_COARSE_ACCESS = 123;
    boolean permissionGranted = false;
    LocationManager lm;
    LocationListener locationListener;

    List<com.example.mapsassignment.CleaningEvent> cleaningEvents = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the default location to Belfast City Airport
        LatLng defaultLocation = new LatLng(54.617611, -5.8718491);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12.0f));

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_COARSE_ACCESS);
            return;
        } else {
            permissionGranted = true;
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        if (mMap != null) {
            mMap.clear(); // Clear existing markers

            for (CleaningEvent event : cleaningEvents) {
                LatLng eventLocation = event.getLocation();
                String eventName = event.getEventName();
                Marker marker = mMap.addMarker(new MarkerOptions().position(eventLocation).title(eventName));
                marker.setTag(event); // Set the CleaningEvent as the tag for the marker
            }


            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
                    Log.d("MapClick", "onMapClick called");

                    // Check if cleaningEvents is not null
                    if (cleaningEvents == null) {
                        Log.e("MapClick", "cleaningEvents is null");
                        return;
                    }

                    Log.d("MapClick", "cleaningEvents size: " + cleaningEvents.size());

                    // Creating new event
                    CleaningEvent newEvent = new CleaningEvent();
                    String placeName = "New Marker";
                    newEvent.setEventName(placeName);
                    newEvent.setLocation(point);
                    cleaningEvents.add(newEvent);

                    // Log details for debugging
                    Log.d("MapClick", "Created new event: " + newEvent.getEventName() + " at " + newEvent.getLocation());

                    // Add a marker for the clicked location
                    Marker marker = mMap.addMarker(new MarkerOptions().position(point).title(placeName));

                    // Set the CleaningEvent as the tag for the marker
                    marker.setTag(newEvent);



                    Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geoCoder.getFromLocation(point.latitude, point.longitude, 1);
                        if (addresses.size() > 0) {
                            Address address = addresses.get(0);




                           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 12.0f));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // Retrieve the CleaningEvent associated with the clicked marker
                    CleaningEvent clickedEvent = (CleaningEvent) marker.getTag();

                    // If you have an EventActivity, launch it and pass necessary data
                    if (clickedEvent != null) {
                        Intent intent = new Intent(MapsActivity.this, AddEventActivity.class);
                        intent.putExtra("eventName", clickedEvent.getEventName());
                        // Add more data as needed
                        startActivity(intent);
                    }

                    // Return true to consume the click event
                    return true;
                }
            });
        }
    }







    private class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(@NonNull Location location) {

                LatLng p = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(p).title("Current Location"));

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setMessage("Location provider is disabled. Please enable it to use this feature.")
                    .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }


}



