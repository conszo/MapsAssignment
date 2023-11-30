package com.example.mapsassignment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

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

        //binding = ActivityMapsBinding.inflate(getLayoutInflater());
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
        }



        {
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
        if(mMap !=null)
    {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Retrieve the associated CleaningEvent from the marker's tag
                CleaningEvent clickedEvent = (CleaningEvent) marker.getTag();

                // Check if the tag is not null (to avoid potential issues)
                if (clickedEvent != null) {
                    // Launch AddEventActivity with the clicked event
                    Intent intent = new Intent(MapsActivity.this, AddEventActivity.class);
                    intent.putExtra("clickedEvent", clickedEvent);
                    startActivity(intent);
                }

                // Return true to consume the event and prevent the default behavior
                return true;
            }
        });

        // Set the map click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(point.latitude, point.longitude, 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);

                        String placeName = "Titanic Belfast";

                        CleaningEvent newEvent = new CleaningEvent();
                        newEvent.setEventName(placeName);
                        newEvent.setLocation(point);
                        cleaningEvents.add(newEvent);

                        // Add a marker for the clicked location
                        LatLng eventLocation = new LatLng(address.getLatitude(), address.getLongitude());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(eventLocation).title(placeName));

                        // Set the CleaningEvent as the tag for the marker
                        marker.setTag(newEvent);

                        // Comment out the next line to allow free movement on the map
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 12.0f));
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
            LocationListener.super.onProviderEnabled(provider);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            LocationListener.super.onProviderDisabled(provider);
        }
    }
}
