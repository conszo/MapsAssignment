package com.example.mapsassignment;

// EventDetailsActivity.java
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Find your TextViews in the layout
        TextView eventNameTextView = findViewById(R.id.textViewEventName);
        TextView eventDateTextView = findViewById(R.id.textViewEventDate);
        TextView eventTimeTextView = findViewById(R.id.textViewEventTime);
        TextView eventLocationTextView = findViewById(R.id.textViewEventLocation);
        TextView eventGoalTextView = findViewById(R.id.textViewEventGoal);

        // Retrieve the clicked event from the Intent
        Intent intent = getIntent();
        CleaningEvent clickedEvent = intent.getParcelableExtra("clickedEvent");

        // Now use 'clickedEvent' to update your UI elements
        eventNameTextView.setText("Event Name: " + clickedEvent.getEventName());
        eventDateTextView.setText("Event Date: " + clickedEvent.getEventDate());
        eventTimeTextView.setText("Event Time: " + clickedEvent.getEventTime());
        eventLocationTextView.setText("Event Location: " + clickedEvent.getEventLocation());
        eventGoalTextView.setText("Event Goal: " + clickedEvent.getEventGoal());
    }

    // Add the following method to save the event and send it back to MapsActivity
    private void saveEventAndFinish(CleaningEvent updatedEvent) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedEvent", updatedEvent);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}


