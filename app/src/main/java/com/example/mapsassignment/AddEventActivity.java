package com.example.mapsassignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapsassignment.CleaningEvent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    private CleaningEvent currentEvent; // Variable to hold the current event
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // ... (existing code)

        // Receive latitude and longitude
        double latitude = getIntent().getDoubleExtra("latitude", 0.0);
        double longitude = getIntent().getDoubleExtra("longitude", 0.0);

        // Set location coordinates for the new event
        LatLng location = new LatLng(latitude, longitude);

        // Check if there is an existing event to edit
        currentEvent = getIntent().getParcelableExtra("clickedEvent");
        if (currentEvent == null) {
            // If there is no existing event, create a new one
            currentEvent = new CleaningEvent();
            // Set location coordinates for the new event
            currentEvent.setLocation(location);
        } else {
            // If there is an existing event, populate the UI with its details
            populateUIWithEventDetails();
        }

        Button buttonSaveEvent = findViewById(R.id.buttonSaveEvent);
        buttonSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the event details from the UI
                updateEventDetailsFromUI();

                // Save the event to the database
                EventDbHelper dbHelper = new EventDbHelper(AddEventActivity.this);
                long newRowId = currentEvent.saveEventToDatabase(dbHelper);
                dbHelper.close();

                // Pass the updated event back to MapsActivity
                Intent resultIntent = new Intent();

                // Include the latitude and longitude of the event in the result
                resultIntent.putExtra("latitude", currentEvent.getLatitude());
                resultIntent.putExtra("longitude", currentEvent.getLongitude());

                // Pass the entire CleaningEvent to EventDetailsActivity
                resultIntent.putExtra("updatedEvent", currentEvent);

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        // ... (existing code)
    }

    private void populateUIWithEventDetails() {
        // Populate the UI components with details of the existing event
        EditText editTextEventName = findViewById(R.id.editTextEventName);
        EditText editTextEventDate = findViewById(R.id.Date_txt);
        EditText editTextEventTime = findViewById(R.id.Time_txt);
        EditText editTextEventLocation = findViewById(R.id.Location_txt);
        EditText editTextEventGoal = findViewById(R.id.Goal_txt);

        editTextEventName.setText(currentEvent.getEventName());
        editTextEventDate.setText(currentEvent.getEventDate());
        editTextEventTime.setText(currentEvent.getEventTime());
        editTextEventLocation.setText(currentEvent.getEventLocation());
        editTextEventGoal.setText(currentEvent.getEventGoal());
        // ... (populate other UI components as needed)
    }

    private void updateEventDetailsFromUI() {
        // Update the event details from the UI components
        EditText editTextEventName = findViewById(R.id.editTextEventName);
        EditText editTextEventDate = findViewById(R.id.Date_txt);
        EditText editTextEventTime = findViewById(R.id.Time_txt);
        EditText editTextEventLocation = findViewById(R.id.Location_txt);
        EditText editTextEventGoal = findViewById(R.id.Goal_txt);

        currentEvent.setEventName(editTextEventName.getText().toString());
        currentEvent.setEventDate(editTextEventDate.getText().toString());
        currentEvent.setEventTime(editTextEventTime.getText().toString());
        currentEvent.setEventLocation(editTextEventLocation.getText().toString());
        currentEvent.setEventGoal(editTextEventGoal.getText().toString());
        // ... (update other event details as needed)
    }
}





