package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("Note")
public class Note extends ParseObject {

    public static final String KEY_USER = "user";

    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_IMAGES = "images";
    private static final String KEY_CREATED_BY = "createdBy";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String content) {
        put(KEY_TITLE, content);
    }

    public String getContent() {
        return getString(KEY_CONTENT);
    }

    public void setContent(String content) {
        put(KEY_CONTENT, content);
    }

    public JSONArray getImages() {
        return getJSONArray(KEY_IMAGES);
    }

    public void setImages(JSONArray content) {
        put(KEY_IMAGES, content);
    }

    public void setCreatedBy(ParseUser user) {
        put(KEY_CREATED_BY, user);
    }

    public ParseUser getCreatedBy() {
        return getParseUser(KEY_CREATED_BY);
    }

}
