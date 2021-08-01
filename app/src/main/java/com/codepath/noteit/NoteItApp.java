package com.codepath.noteit;

import android.app.Application;
import android.content.Context;

public class NoteItApp extends Application {
    public static GoogleCalendarClient getRestClient(Context context) {
        return (GoogleCalendarClient) GoogleCalendarClient.getInstance(GoogleCalendarClient.class, context);
    }
}
