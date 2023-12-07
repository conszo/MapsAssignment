package com.example.mapsassignment;

import android.app.Activity;
import android.content.Intent;
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
        EditText editTextEventDate = findViewById(R.id.Date_txt);
        EditText editTextEventTime = findViewById(R.id.Time_txt);
        EditText editTextEventLocation = findViewById(R.id.Location_txt);
        EditText editTextEventGoal = findViewById(R.id.Goal_txt);

        Button buttonSaveEvent = findViewById(R.id.buttonSaveEvent);
        buttonSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = editTextEventName.getText().toString();
                String eventDate = editTextEventDate.getText().toString();
                String eventTime = editTextEventTime.getText().toString();
                String eventLocation = editTextEventLocation.getText().toString();
                String eventGoal = editTextEventGoal.getText().toString();

                if (cleaningEvents != null) {

                    CleaningEvent newEvent = new CleaningEvent();
                    newEvent.setEventName(eventName);
                    cleaningEvents.add(newEvent);

                    // Intent resultIntent = new Intent();
                    // resultIntent.putExtra("newEvent", newEvent);
                    // setResult(Activity.RESULT_OK, resultIntent);

                    finish();

                }
            }
        });

        //  CleaningEvent clickedEvent = (CleaningEvent) getIntent().getSerializableExtra("clickedEvent");
        //  if (clickedEvent != null) {
        // }


    }
}