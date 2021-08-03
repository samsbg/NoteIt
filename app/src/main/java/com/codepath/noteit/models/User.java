package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

// UserParse.getCurrentUsername can replace this class if it is only used for the user in the app
// Check at the end if it is not needed

@ParseClassName("_User")
public class User extends ParseUser{

    private static final String KEY_ID = "objectId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_REVIEWED = "reviewed";
    private static final String KEY_CALENDAR_ID = "calendarId";

    public String getId() {
        return getString(KEY_ID);
    }

    public String getUsername() {
        try {
            return fetchIfNeeded().getString(KEY_USERNAME);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setUsername(String username) {
        put(KEY_USERNAME, username);
    }

    public int getCompleted() {
        return getInt(KEY_COMPLETED);
    }

    public void setCompleted(int completed) {
        put(KEY_COMPLETED, completed);
    }

    public int getReviewed() {
        return getInt(KEY_REVIEWED);
    }

    public void setReviewed(int reviewed) {
        put(KEY_REVIEWED, reviewed);
    }

    public String getCalendarId() {
        return getString(KEY_CALENDAR_ID);
    }

    public void setCalendarId(String calendar) {
        put(KEY_CALENDAR_ID, calendar);
    }
}
