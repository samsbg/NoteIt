package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Note")
public class Note extends ParseObject {

    private static final String KEY_CONTENT = "content";
    private static final String KEY_CREATED_BY = "createdBy";

    public ParseFile getContent() {
        return getParseFile(KEY_CONTENT);
    }

    public void setContent(ParseFile content) {
        put(KEY_CONTENT, content);
    }

    public ParseUser getCreatedBy() {
        return getParseUser(KEY_CREATED_BY);
    }

}
