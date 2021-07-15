package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("NotesWithTags")
public class NotesWithTags extends ParseObject {

    private static final String KEY_NOTE = "note";
    private static final String KEY_TAG = "tag";
    private static final String KEY_CREATED_BY = "createdBy";

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
