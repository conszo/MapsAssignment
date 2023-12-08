package com.example.mapsassignment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
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
    private LocationManager lm;
    private LocationListener locationListener;
    private final int REQUEST_COARSE_ACCESS = 123;
    private boolean permissionGranted = false;
    private List<CleaningEvent> cleaningEvents = new ArrayList<>();

    private static final int ADD_EVENT_REQUEST_CODE = 1;

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
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        // Check and request location permissions
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_COARSE_ACCESS);
        } else {
            // If permissions are granted, start location updates
            permissionGranted = true;
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // Check if the intent contains the cleaningEvents
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("cleaningEvents")) {
            ArrayList<CleaningEvent> cleaningEvents = intent.getParcelableArrayListExtra("cleaningEvents");

            // You can now use the cleaningEvents in your MapsActivity
            for (CleaningEvent event : cleaningEvents) {
                // Access event properties, e.g., event.getEventName(), event.getLocation(), etc.
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EVENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                // Handle the result, e.g., update the map
                List<CleaningEvent> updatedCleaningEvents =
                        (List<CleaningEvent>) data.getSerializableExtra("cleaningEvents");

                if (updatedCleaningEvents != null) {
                    cleaningEvents = updatedCleaningEvents;

                    // Update the map with the new events
                    updateMapWithCleaningEvents();
                } else {
                    Log.e("onActivityResult", "Updated cleaningEvents is null");
                }
            } catch (Exception e) {
                Log.e("onActivityResult", "Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateMapWithCleaningEvents() {
        // Clear existing markers
        mMap.clear();

        // Add markers for the updated cleaningEvents
        for (CleaningEvent event : cleaningEvents) {
            LatLng eventLocation = event.getLocation();
            String eventName = event.getEventName();
            Marker marker = mMap.addMarker(new MarkerOptions().position(eventLocation).title(eventName));
            marker.setTag(event);

            // Show the info window for the newly added marker
            if (event.isNewlyAdded()) {
                marker.showInfoWindow();
                event.setNewlyAdded(false); // Reset the flag
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the default location to Belfast City Airport
        LatLng defaultLocation = new LatLng(54.617611, -5.8718491);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12.0f));

        // Add existing markers
        for (CleaningEvent event : cleaningEvents) {
            LatLng eventLocation = event.getLocation();
            String eventName = event.getEventName();
            Marker marker = mMap.addMarker(new MarkerOptions().position(eventLocation).title(eventName));
            marker.setTag(event);
        }

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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Handle marker click events
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Retrieve the CleaningEvent associated with the clicked marker
                CleaningEvent clickedEvent = (CleaningEvent) marker.getTag();

                // If you have an AddEventActivity, launch it and pass necessary data
                if (clickedEvent != null) {
                    Intent intent = new Intent(MapsActivity.this, AddEventActivity.class);
                    intent.putExtra("clickedEvent", clickedEvent);
                    intent.putExtra("latitude", clickedEvent.getLocation().latitude);
                    intent.putExtra("longitude", clickedEvent.getLocation().longitude);
                    startActivityForResult(intent, ADD_EVENT_REQUEST_CODE);
                }

                // Return true to consume the click event
                return true;
            }
        });

        // Inside onMapReady method
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // Use a custom layout for the info window
                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                // Retrieve the CleaningEvent associated with the marker
                CleaningEvent clickedEvent = (CleaningEvent) marker.getTag();

                if (clickedEvent != null) {
                    // Update the UI elements in the custom info window layout
                    TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
                    TextView eventDateTimeTextView = view.findViewById(R.id.eventDateTimeTextView);
                    TextView eventLocationTextView = view.findViewById(R.id.eventLocationTextView);
                    TextView eventGoalTextView = view.findViewById(R.id.eventGoalTextView);
                    Button joinButton = view.findViewById(R.id.joinButton);

                    eventNameTextView.setText(clickedEvent.getEventName());
                    eventDateTimeTextView.setText(clickedEvent.getEventDate() + " " + clickedEvent.getEventTime());
                    eventLocationTextView.setText(clickedEvent.getEventLocation());
                    eventGoalTextView.setText(clickedEvent.getEventGoal());

                    // Set a tag on the joinButton to identify the associated event
                    joinButton.setTag(clickedEvent);

                    // Set a click listener for the joinButton
                    joinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the join button click
                            // You can access the clicked event using v.getTag()
                            CleaningEvent clickedEvent = (CleaningEvent) v.getTag();
                            if (clickedEvent != null) {
                                // Perform actions related to joining the event
                                // For example, update the number of attendees
                                clickedEvent.setAttendees(clickedEvent.getAttendees() + 1);
                                // Update the info window
                                marker.showInfoWindow();
                            }
                        }
                    });
                }

                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
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






