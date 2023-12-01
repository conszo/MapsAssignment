package com.example.mapsassignment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
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

    private ActivityMapsBinding binding;
    final private int REQUEST_COARSE_ACCESS = 123;
    boolean permissionGranted = false;
    LocationManager lm;
    LocationListener locationListener;

    List<com.example.mapsassignment.CleaningEvent> cleaningEvents = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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

        // Request location updates after checking permissions

        if (mMap != null) {
            mMap.clear(); // Clear existing markers

            for (CleaningEvent event : cleaningEvents) {
                LatLng eventLocation = event.getLocation();
                String eventName = event.getEventName();
                Marker marker = mMap.addMarker(new MarkerOptions().position(eventLocation).title(eventName));
                marker.setTag(event); // Set the CleaningEvent as the tag for the marker
            }

            // Set the map click listener
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

                    // ... rest of your code

                    Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geoCoder.getFromLocation(point.latitude, point.longitude, 1);
                        if (addresses.size() > 0) {
                            Address address = addresses.get(0);

                            // Additional logic if needed

                            // Comment out the next line to allow free movement on the map
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 12.0f));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });



        }
    }



    private class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                Toast.makeText(getBaseContext(),
                        "Current Location : Lat: " + location.getLatitude() +
                                "Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                LatLng p = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(p).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 12.0f));
            }
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_COARSE_ACCESS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform your location-related operations
                // e.g., start location updates
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            } else {
                // Permission denied, handle accordingly
                // You might want to inform the user or provide an alternative way to use the app

                // Inform the user why the permission is necessary
                Toast.makeText(MapsActivity.this, "Location permission is required to use this feature.", Toast.LENGTH_SHORT).show();

                // Provide an alternative way or direct the user to the app settings
                // for them to grant the necessary permission manually
                // For example, you can show a dialog with a button to open app settings
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied")
                .setMessage("Location permission is required to use this feature. Please grant the permission in the app settings.")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppSettings();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}



