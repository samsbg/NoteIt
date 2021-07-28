package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.Date;

@ParseClassName("Substring")
public class Substring extends ParseObject {
    private static final String KEY_KEY = "key";
    private static final String KEY_VALUE = "value";

    public String getKey() { return getString(KEY_KEY); }

    public void setKey(String key) { put(KEY_KEY, key); }

    public JSONArray getValue() {
        return getJSONArray(KEY_VALUE);
    }

    public void setValue(JSONArray value) { put(KEY_VALUE, value); }
}
