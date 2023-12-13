package com.example.mapsassignment;

// EventDetailsActivity.java
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Retrieve the CleaningEvent from the intent
        CleaningEvent clickedEvent = getIntent().getParcelableExtra("clickedEvent");

        // Populate UI components with event details
        if (clickedEvent != null) {
            TextView textViewEventName = findViewById(R.id.textViewEventName);
            TextView textViewEventDate = findViewById(R.id.textViewEventDate);

            textViewEventName.setText("Event Name: " + clickedEvent.getEventName());
            textViewEventDate.setText("Event Date: " + clickedEvent.getFormattedDate());

            // Add more TextViews and populate them with other event details as needed
        }
    }
}
