package com.example.mapsassignment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    private List<CleaningEvent> cleaningEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Retrieve cleaningEvents from the intent
        cleaningEvents = (List<CleaningEvent>) getIntent().getSerializableExtra("cleaningEvents");

        // Initialize UI components
        EditText editTextEventName = findViewById(R.id.editTextEventName);

        Button buttonSaveEvent = findViewById(R.id.buttonSaveEvent);
        buttonSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = editTextEventName.getText().toString();

                // Make sure the cleaningEvents list is initialized
                if (cleaningEvents != null) {
                    // Create a new CleaningEvent
                    CleaningEvent newEvent = new CleaningEvent();
                    newEvent.setEventName(eventName);

                    // Add the new event to the list
                    cleaningEvents.add(newEvent);

                    // You can also save the event to a database or perform other actions
                    // ...

                    // Finish the activity
                    finish();
                }
            }
        });


        CleaningEvent clickedEvent = (CleaningEvent) getIntent().getSerializableExtra("clickedEvent");
        if (clickedEvent != null) {
            // Handle the clicked event as needed
        }


    }
}