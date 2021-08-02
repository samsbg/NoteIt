package com.codepath.noteit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Reminder")
public class Reminder extends ParseObject {
    private static final String KEY_DATE = "date";
    private static final String KEY_CREATED_BY = "createdBy";
    private static final String KEY_GOAL = "goal";

    public Date getDate() {
        return getDate(KEY_DATE);
    }

    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    public void setCreatedBy(ParseUser user) {
        put(KEY_CREATED_BY, user);
    }

    public ParseObject getGoal() {
        return getParseObject(KEY_GOAL);
    }

    public void setGoal(ParseObject goal) {
        put(KEY_GOAL, goal);
    }
}
