package com.example.mapsassignment;

// EventDetailsActivity.java
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    private CleaningEvent clickedEvent;
    private TextView attendanceCountTextView;

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
        attendanceCountTextView = findViewById(R.id.textViewAttendanceCount);

        // Retrieve the clicked event from the Intent
        Intent intent = getIntent();
        clickedEvent = intent.getParcelableExtra("clickedEvent");

        // Now use 'clickedEvent' to update your UI elements
        eventNameTextView.setText("Event Name: " + clickedEvent.getEventName());
        eventDateTextView.setText("Event Date: " + clickedEvent.getEventDate());
        eventTimeTextView.setText("Event Time: " + clickedEvent.getEventTime());
        eventLocationTextView.setText("Event Location: " + clickedEvent.getEventLocation());
        eventGoalTextView.setText("Event Goal: " + clickedEvent.getEventGoal());
        attendanceCountTextView.setText("Attendance Count: " + clickedEvent.getAttendees());

        // Set up the Edit Event button click listener
        Button editEventButton = findViewById(R.id.buttonEditEvent);
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the Edit Event button click (you can open a new activity for editing)
                // Example: openEditEventActivity();
            }
        });

        // Set up the Attend Event button click listener
        Button attendEventButton = findViewById(R.id.buttonAttendEvent);
        attendEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the Attend Event button click (increment the attendees count, update UI)
                clickedEvent.setAttendees(clickedEvent.getAttendees() + 1);
                // Update UI or send the updated event back to MapsActivity
                saveEventAndFinish(clickedEvent);
            }
        });
    }

    // Add the following method to save the event and send it back to MapsActivity
    private void saveEventAndFinish(CleaningEvent updatedEvent) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedEvent", updatedEvent);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}




