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
import java.io.Serializable;
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
            CleaningEvent updatedEvent = data.getParcelableExtra("updatedEvent");

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
            if (eventLocation != null) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(eventLocation)
                        .title(event.getEventName());

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(event);
            }
        }

        // Set custom info window adapter
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;  // Use the default info window layout
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                TextView titleTextView = view.findViewById(R.id.infoWindowTitle);
                TextView snippetTextView = view.findViewById(R.id.infoWindowSnippet);

                // Retrieve the CleaningEvent associated with the marker
                CleaningEvent event = (CleaningEvent) marker.getTag();

                // Debugging: Log the event details
                Log.d("InfoContents", "Event: " + event);
                Log.d("InfoContents", "EventName: " + event.getEventName());
                Log.d("InfoContents", "EventDate: " + event.getEventDate());
                Log.d("InfoContents", "EventTime: " + event.getEventTime());

                // Set custom information in the info window
                if (event != null) {
                    titleTextView.setText(event.getEventName());
                    snippetTextView.setText("Date: " + event.getEventDate() + "\nTime: " + event.getEventTime());
                }

                return view;
            }

        });
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
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null; // Use default info window frame
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the custom info window layout
                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                // Retrieve the CleaningEvent associated with the clicked marker
                CleaningEvent clickedEvent = (CleaningEvent) marker.getTag();

                // Populate the custom info window with event details
                if (clickedEvent != null) {
                    TextView titleTextView = view.findViewById(R.id.infoWindowTitle);
                    TextView snippetTextView = view.findViewById(R.id.infoWindowSnippet);

                    titleTextView.setText(clickedEvent.getEventName());
                    snippetTextView.setText(clickedEvent.getEventDate() + " " + clickedEvent.getEventTime());
                }

                return view;
            }
        });
        // Handle map click events
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
                CleaningEvent newEvent = new CleaningEvent();
                newEvent.setLocation(point);

                // Check if the location of the new event is not null
                if (newEvent.getLocation() != null) {
                    // Add marker for the new event
                    Marker marker = mMap.addMarker(new MarkerOptions().position(newEvent.getLocation()).title("New Event"));
                    marker.setTag(newEvent);

                    // Display the AddEventActivity for event details
                    Intent intent = new Intent(MapsActivity.this, AddEventActivity.class);
                    intent.putExtra("latitude", point.latitude);
                    intent.putExtra("longitude", point.longitude);
                    intent.putExtra("newEvent", newEvent);

                    // Check if eventDate and eventTime are not null before combining
                    if (eventDate != null && eventTime != null) {
                        newEvent.setDateTime(newEvent.combineDateTime(eventDate, eventTime)); // Assuming combineDateTime is in CleaningEvent

                        // ... (rest of your code)

                    } else {
                        Log.e("MapClick", "eventDate or eventTime is null");
                        // Handle the case where either eventDate or eventTime is null
                    }

                    startActivityForResult(intent, ADD_EVENT_REQUEST_CODE);
                } else {
                    Log.e("MapClick", "New event location is null");
                }
            }



        });

        // Set marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            // MapsActivity.java
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Retrieve the CleaningEvent associated with the clicked marker
                CleaningEvent clickedEvent = (CleaningEvent) marker.getTag();

                // If you have an EventDetailsActivity, launch it and pass necessary data
                if (clickedEvent != null) {
                    Intent intent = new Intent(MapsActivity.this, EventDetailsActivity.class);
                    intent.putExtra("clickedEvent", clickedEvent);
                    startActivity(intent);
                }

                // Return true to consume the click event
                return true;
            }

        });
    }


}






