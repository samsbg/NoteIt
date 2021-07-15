package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Tag")
public class Tag extends ParseObject {
    private static final String KEY_CREATED_BY = "createdBy";

    public ParseUser getCreatedBy() {
        return getParseUser(KEY_CREATED_BY);
    }
}
