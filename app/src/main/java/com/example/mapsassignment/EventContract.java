package com.example.mapsassignment;


import android.provider.BaseColumns;

public class EventContract {
    private EventContract() {
    }

    public static class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_EVENT_NAME = "event_name";
        public static final String COLUMN_NAME_EVENT_DATE = "event_date"; // Add this line for event date
        // Add more columns as needed

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_EVENT_NAME + " TEXT," +
                        COLUMN_NAME_EVENT_DATE + " TEXT," +  // Add this line for event date
                        // Add more column definitions
                        ");";
    }
}

