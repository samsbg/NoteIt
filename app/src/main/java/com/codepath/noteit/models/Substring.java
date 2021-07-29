package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

@ParseClassName("Substring")
public class Substring extends ParseObject {
    private static final String KEY_MAP = "map";
    private static final String KEY_CREATED_BY = "createdBy";

    public JSONObject getMap() {
        return getJSONObject(KEY_MAP);
    }

    public void setMap(JSONObject map) { put(KEY_MAP, map); }

    public void setCreatedBy(ParseUser user) {
        put(KEY_CREATED_BY, user);
    }

    public ParseUser getCreatedBy() {
        return getParseUser(KEY_CREATED_BY);
    }
}
