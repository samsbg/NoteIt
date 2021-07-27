package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("Tag")
public class Tag extends ParseObject {

    private static final String KEY_NAME = "name";
    private static final String KEY_CREATED_BY = "createdBy";
    private static final String KEY_NOTES = "notes";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public ParseUser getCreatedBy() {
        return getParseUser(KEY_CREATED_BY);
    }

    public void setCreatedBy(ParseUser user) {
        put(KEY_CREATED_BY, user);
    }

    public JSONArray getNotes() {
        return getJSONArray(KEY_NOTES);
    }

    public void setNotes(JSONArray notes) {
        put(KEY_NOTES, notes);
    }
}
