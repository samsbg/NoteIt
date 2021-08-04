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
    private static final String KEY_CALENDAR_ID = "calendarId";
    private static final String KEY_TAGS_REVIEWED = "tagsReviewed";
    private static final String KEY_NOTES_REVIEWED = "notesReviewed";

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

    public String getCalendarId() {
        return getString(KEY_CALENDAR_ID);
    }

    public void setCalendarId(String calendar) {
        put(KEY_CALENDAR_ID, calendar);
    }

    public int getTagsReviewed() {
        return getInt(KEY_TAGS_REVIEWED);
    }

    public void setTagsReviewed(int tagsReviewed) {
        put(KEY_TAGS_REVIEWED, tagsReviewed);
    }

    public int getNotesReviewed() {
        return getInt(KEY_NOTES_REVIEWED);
    }

    public void setNotesReviewed(int notesReviewed) {
        put(KEY_NOTES_REVIEWED, notesReviewed);
    }
}
