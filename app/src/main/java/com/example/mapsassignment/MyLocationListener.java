package com.example.mapsassignment;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyLocationListener implements LocationListener {

    private FragmentActivity activity;
    private GoogleMap mMap;

    public MyLocationListener(FragmentActivity activity, GoogleMap mMap) {
        this.activity = activity;
        this.mMap = mMap;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng p = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(p).title("Current Location"));
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Location provider is disabled. Please enable it to use this feature.")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

