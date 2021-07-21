package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.List;


@ParseClassName("Tag")
public class Tag extends ParseObject {
    private static final String KEY_CREATED_BY = "createdBy";
    private static final String KEY_NOTES = "notes";

    public ParseUser getCreatedBy() {
        return getParseUser(KEY_CREATED_BY);
    }

    public JSONArray getNotes() {
        return getJSONArray(KEY_NOTES);
    }
}
