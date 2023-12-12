package com.example.mapsassignment;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHandler implements LocationListener {

    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationHandler(Context context, LocationListener locationListener) {
        this.context = context;
        this.locationListener = locationListener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startLocationUpdates() {
        // Check and request location permissions
        // ... (your existing permission check logic)

        // If permissions are granted, start location updates
        if (locationManager != null) {
            try {
                // Request location updates with your desired parameters
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0, // minTime in milliseconds
                        0, // minDistance in meters
                        locationListener
                );
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopLocationUpdates() {
        // Stop location updates when they are no longer needed
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Handle location changes
        locationListener.onLocationChanged(location);
    }

    // ... (other LocationListener methods)
}

