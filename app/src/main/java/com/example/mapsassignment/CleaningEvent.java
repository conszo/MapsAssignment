package com.example.mapsassignment;



import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CleaningEvent implements Serializable {
    private String eventName;
    private LatLng location;
    private Date dateTime;

    // Constructors
    public CleaningEvent() {
        // Default constructor
    }

    public CleaningEvent(String eventName, LatLng location, Date dateTime) {
        this.eventName = eventName;
        this.location = location;
        this.dateTime = dateTime;
    }

    // Getters and Setters
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    // Formatted Date and Time
    public String getFormattedDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(dateTime);
    }

    public String getFormattedTime() {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(dateTime);
    }

    // Full formatted date and time
    public String getFullFormattedDateTime() {

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return dateTimeFormat.format(dateTime);
    }
}

