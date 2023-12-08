package com.example.mapsassignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    private List<CleaningEvent> cleaningEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Retrieve cleaningEvents from the intent
        cleaningEvents = (List<CleaningEvent>) getIntent().getSerializableExtra("cleaningEvents");

        // Initialize cleaningEvents if null
        if (cleaningEvents == null) {
            cleaningEvents = new ArrayList<>();
        }

        // Initialize UI components
        EditText editTextEventName = findViewById(R.id.editTextEventName);
        EditText editTextEventDate = findViewById(R.id.Date_txt);
        EditText editTextEventTime = findViewById(R.id.Time_txt);
        EditText editTextEventLocation = findViewById(R.id.Location_txt);
        EditText editTextEventGoal = findViewById(R.id.Goal_txt);

        Button buttonSaveEvent = findViewById(R.id.buttonSaveEvent);
        buttonSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String eventName = editTextEventName.getText().toString();
                    String eventDate = editTextEventDate.getText().toString();
                    String eventTime = editTextEventTime.getText().toString();
                    String eventLocation = editTextEventLocation.getText().toString();
                    String eventGoal = editTextEventGoal.getText().toString();

                    Log.d("AddEventActivity", "Clicked Save Event");
                    Log.d("AddEventActivity", "Event Name: " + eventName);
                    Log.d("AddEventActivity", "Event Date: " + eventDate);
                    Log.d("AddEventActivity", "Event Time: " + eventTime);
                    Log.d("AddEventActivity", "Event Location: " + eventLocation);
                    Log.d("AddEventActivity", "Event Goal: " + eventGoal);

                    if (cleaningEvents != null) {
                        CleaningEvent newEvent = new CleaningEvent(
                                eventName,
                                eventDate,
                                eventTime,
                                eventLocation,
                                eventGoal,
                                getIntent().getDoubleExtra("latitude", 0.0),
                                getIntent().getDoubleExtra("longitude", 0.0));

                        cleaningEvents.add(newEvent);

                        Log.d("AddEventActivity", "New event added to cleaningEvents");

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("newEvent", newEvent);
                        resultIntent.putExtra("cleaningEvents", new ArrayList<>(cleaningEvents));
                        setResult(Activity.RESULT_OK, resultIntent);

                        finish();
                    } else {
                        Log.e("AddEventActivity", "CleaningEvents is null");
                    }
                } catch (Exception e) {
                    Log.e("AddEventActivity", "Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        // ... (rest of your code)
    }
}

