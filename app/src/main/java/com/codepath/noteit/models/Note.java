package com.codepath.noteit.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("Note")
public class Note extends ParseObject implements Parcelable {

    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_IMAGES = "images";
    private static final String KEY_CREATED_BY = "createdBy";
    private static final String KEY_COLOR = "color";
    private static final String KEY_TAGS = "tags";

    public String getTitle() {
        try {
            return fetchIfNeeded().getString(KEY_TITLE);
        } catch (ParseException e) {
            Log.e("Goal", "Something has gone terribly wrong with Parse", e);
            return "";
        }
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

    public int getColor() {
        return getInt(KEY_COLOR);
    }

    public void setColor(int color) {
        put(KEY_COLOR, color);
    }

    public JSONArray getTags() {
        return getJSONArray(KEY_TAGS);
    }

    public void setTags(JSONArray tags) {
        put(KEY_TAGS, tags);
    }

}
