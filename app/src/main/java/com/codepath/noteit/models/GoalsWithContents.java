package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("GoalsWithContents")
public class GoalsWithContents extends ParseObject {

    private static final String KEY_GOAL = "goal";
    private static final String KEY_NOTE = "note";
    private static final String KEY_TAG = "tag";
    private static final String KEY_CREATED_BY = "createdBy";

    public ParseObject getGoal() {
        return getParseObject(KEY_GOAL);
    }

    public void setGoal(ParseObject goal) {
        put(KEY_GOAL, goal);
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

    public void setCreatedBy(ParseUser user) {
        put(KEY_CREATED_BY, user);
    }
}
