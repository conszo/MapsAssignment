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

        // Retrieve the CleaningEvent from the intent
        CleaningEvent clickedEvent = getIntent().getParcelableExtra("updatedEvent");

        // Populate UI components with event details
        if (clickedEvent != null) {
            TextView textViewEventName = findViewById(R.id.textViewEventName);
            TextView textViewEventDate = findViewById(R.id.textViewEventDate);
            TextView textViewEventTime = findViewById(R.id.textViewEventTime);
            TextView textViewEventLocation = findViewById(R.id.textViewEventLocation);
            TextView textViewEventGoal = findViewById(R.id.textViewEventGoal);

            textViewEventName.setText("Event Name: " + clickedEvent.getEventName());
            textViewEventDate.setText("Event Date: " + clickedEvent.getFormattedDate());
            textViewEventTime.setText("Event Time: " + clickedEvent.getFormattedTime());
            textViewEventLocation.setText("Event Location: " + clickedEvent.getEventLocation());
            textViewEventGoal.setText("Event Goal: " + clickedEvent.getEventGoal());
        }
    }


    // Add the following method to save the event and send it back to MapsActivity
    private void saveEventAndFinish(CleaningEvent updatedEvent) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedEvent", updatedEvent);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


    // This method can be called when saving the event in your activity
    // You can call this method passing the updated event
    // Example: saveEventAndFinish(updatedEvent);
}

