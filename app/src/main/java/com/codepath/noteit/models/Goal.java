package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;

@ParseClassName("Goal")
public class Goal extends ParseObject{

    private static final String KEY_REVIEWED = "reviewed";
    private static final String KEY_TOTAL_REVIEWS = "totalReviews";
    private static final String KEY_DUE_DATE = "dueDate";
    private static final String KEY_NOTE = "note";
    private static final String KEY_TAG = "tag";
    private static final String KEY_CREATED_BY = "createdBy";
    private static final String KEY_COMPLETED_BY = "completedBy";
    private static final String KEY_REMINDERS = "reminders";

    public int getTotalReviews() {
        return getInt(KEY_TOTAL_REVIEWS);
    }

    public void setTotalReviews(int totalReviews) {
        put(KEY_TOTAL_REVIEWS, totalReviews);
    }

    public int getReviewed() {
        return getInt(KEY_REVIEWED);
    }

    public void setReviewed(int reviewed) {
        put(KEY_REVIEWED, reviewed);
    }

    public Date getDueDate() {
        return getDate(KEY_DUE_DATE);
    }

    public void setDueDate(Date dueDate) {
        put(KEY_DUE_DATE, dueDate);
    }

    public ParseObject getNote() {
        return getParseObject(KEY_NOTE);
    }

    public void setNote(ParseObject note) {
        put(KEY_NOTE, note);
    }

    public ParseObject getTag() {
        return getParseObject(KEY_TAG);
    }

    public void setTag(ParseObject tag) {
        put(KEY_TAG, tag);
    }

    public ParseUser getCreatedBy() {
        return getParseUser(KEY_CREATED_BY);
    }

    public Date getCompletedBy() {
        return getDate(KEY_COMPLETED_BY);
    }

    public void setCompletedBy(Date date) {
        put(KEY_COMPLETED_BY, date);
    }

    public JSONArray getReminders() {
        return getJSONArray(KEY_REMINDERS);
    }
}
