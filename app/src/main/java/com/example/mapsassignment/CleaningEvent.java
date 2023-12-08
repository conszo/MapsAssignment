package com.example.mapsassignment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CleaningEvent implements Parcelable {
    //fields
    private int attendees;
    private String eventName;
    private transient LatLng location;
    private Date dateTime;
    private String eventDate;
    private String eventTime;
    private String eventLocation;
    private String eventGoal;
    private double latitude;
    private double longitude;

    private boolean isNewlyAdded;

    // Constructors
    public CleaningEvent() {
        // Default constructor
    }

    public CleaningEvent(String eventName, String eventDate, String eventTime, String eventLocation, String eventGoal, double latitude, double longitude) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventGoal = eventGoal;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude, longitude);
        this.isNewlyAdded =true;
        // this.dateTime = combineDateTime(eventDate, eventTime);
    }

    // Parcelable implementation
    protected CleaningEvent(Parcel in) {
        eventName = in.readString();
        eventDate = in.readString();
        eventTime = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        location = in.readParcelable(LatLng.class.getClassLoader());
        eventLocation = in.readString();
        eventGoal = in.readString();
        attendees = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(eventDate);
        dest.writeString(eventTime);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeParcelable(location, flags);
        dest.writeString(eventLocation);
        dest.writeString(eventGoal);
        dest.writeInt(attendees);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CleaningEvent> CREATOR = new Parcelable.Creator<CleaningEvent>() {
        @Override
        public CleaningEvent createFromParcel(Parcel in) {
            return new CleaningEvent(in);
        }

        @Override
        public CleaningEvent[] newArray(int size) {
            return new CleaningEvent[size];
        }
    };

    // Getters and Setters
    // ... (rest of your existing code) ...






    // Getters and Setters
    public boolean isNewlyAdded() {
        return isNewlyAdded;
    }

    public void setNewlyAdded(boolean newlyAdded) {
        isNewlyAdded = newlyAdded;
    }

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

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventGoal() {
        return eventGoal;
    }

    public void setEventGoal(String eventGoal) {
        this.eventGoal = eventGoal;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public int getAttendees() {
        return attendees;
    }

    public void setAttendees(int attendees) {
        this.attendees = attendees;
    }
}


