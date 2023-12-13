package com.example.mapsassignment;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.mapsassignment.CleaningEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mapsassignment.LocationHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationHandler locationHandler;
    private final int REQUEST_COARSE_ACCESS = 123;
    private boolean permissionGranted = false;
    private List<CleaningEvent> cleaningEvents = new ArrayList<>();
    private static final int ADD_EVENT_REQUEST_CODE = 1;

    private String eventDate = "2023-12-31";
    private String eventTime = "12:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Find the map fragment and get a reference to the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Set a click listener for the map after it's ready
        mapFragment.getMapAsync(this);

        // Location manager setup
        locationHandler = new LocationHandler(this, new MyLocationListener(this, mMap));

        locationHandler.startLocationUpdates();

        // Check and request location permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_COARSE_ACCESS);
        } else {
            // If permissions are granted, start location updates
            permissionGranted = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates when the activity is paused
        locationHandler.stopLocationUpdates();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EVENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            CleaningEvent updatedEvent = (CleaningEvent) data.getSerializableExtra("updatedEvent");

            if (updatedEvent != null) {
                // Update the map with the updated event
                updateMapWithCleaningEvents();

                Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("onActivityResult", "Updated event is null");
            }
        }
    }


    private void updateMapWithCleaningEvents() {
        EventDbHelper dbHelper = new EventDbHelper(this);
        List<CleaningEvent> events = CleaningEvent.getAllEventsFromDatabase(dbHelper);
        dbHelper.close();

        // Clear existing markers
        mMap.clear();

        // Add markers for the retrieved events
        for (CleaningEvent event : events) {
            LatLng eventLocation = event.getLocation();
            String eventName = event.getEventName();
            Marker marker = mMap.addMarker(new MarkerOptions().position(eventLocation).title(eventName));
            marker.setTag(event);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the default location to Belfast City Airport
        LatLng defaultLocation = new LatLng(54.617611, -5.8718491);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12.0f));
        EventDbHelper dbHelper = new EventDbHelper(this);
        cleaningEvents = CleaningEvent.getAllEventsFromDatabase(dbHelper);
        dbHelper.close();

        // Add existing markers
        for (CleaningEvent event : cleaningEvents) {
            LatLng eventLocation = event.getLocation();
            String eventName = event.getEventName();
            Marker marker = mMap.addMarker(new MarkerOptions().position(eventLocation).title(eventName));
            marker.setTag(event);
        }

        // Handle map click events
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                Log.d("MapClick", "onMapClick called");

                // Check if cleaningEvents is not null
                if (cleaningEvents == null) {
                    Log.e("MapClick", "cleaningEvents is null");
                    return;
                }

                Log.d("MapClick", "cleaningEvents size: " + cleaningEvents.size());
                CleaningEvent newEvent = new CleaningEvent();
                newEvent.setLocation(point);

                // Display the AddEventActivity for event details
                Intent intent = new Intent(MapsActivity.this, AddEventActivity.class);
                intent.putExtra("newEvent", newEvent);
                startActivityForResult(intent, ADD_EVENT_REQUEST_CODE);

                // Check if eventDate and eventTime are not null before combining
                if (eventDate != null && eventTime != null) {
                    newEvent.setDateTime(newEvent.combineDateTime(eventDate, eventTime));

                    // Log details for debugging
                    Log.d("MapClick", "Created new event: " + newEvent.getEventName() + " at " + newEvent.getLocation());

                    // Check if dateTime is not null before calling getFormattedDate()
                    if (newEvent.getDateTime() != null) {
                        String formattedDate = newEvent.getFormattedDate();
                        String formattedTime = newEvent.getFormattedTime();
                        // Proceed with using formattedDate and formattedTime
                        Log.d("MapClick", "Formatted Date: " + formattedDate + ", Formatted Time: " + formattedTime);
                    } else {
                        // Handle the case where dateTime is null
                        Log.e("MapClick", "DateTime is null");
                    }

                    // Add a marker for the clicked location
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(point)
                            .title("New Marker") // Use a default title or customize it as needed
                            .snippet("Event: " + newEvent.getEventName() +
                                    "\nDate: " + newEvent.getFormattedDate() +
                                    "\nTime: " + newEvent.getFormattedTime()));

                    // Set the CleaningEvent as the tag for the marker
                    marker.setTag(newEvent);

                    updateMapWithCleaningEvents();



                    Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geoCoder.getFromLocation(point.latitude, point.longitude, 1);
                        if (addresses.size() > 0) {
                            Address address = addresses.get(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("MapClick", "eventDate or eventTime is null");
                    // Handle the case where either eventDate or eventTime is null
                    return;
                }
            }
        });

        // Set marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Retrieve the CleaningEvent associated with the clicked marker
                CleaningEvent clickedEvent = (CleaningEvent) marker.getTag();

                // If you have an AddEventActivity, launch it and pass necessary data
                if (clickedEvent != null) {
                    Intent intent = new Intent(MapsActivity.this, AddEventActivity.class);
                    intent.putExtra("clickedEvent", clickedEvent);
                    startActivityForResult(intent, ADD_EVENT_REQUEST_CODE);
                }

                // Return true to consume the click event
                return true;
            }
        });
    }

}






