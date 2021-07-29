package com.codepath.noteit;

import android.app.Application;

import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Reminder;
import com.codepath.noteit.models.Substring;
import com.codepath.noteit.models.Tag;
import com.codepath.noteit.models.User;
import com.parse.Parse;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("OMdiYWaXeIVjl120Qi6VNfw8eBmDOBwJQ304pzGX")
                .clientKey("dcoNCsjaoOtIS6hiNoun02hoVSQZ6necyx2EiIsA")
                .server("https://parseapi.back4app.com")
                .build()
        );

        ParseUser.registerSubclass(User.class);
        ParseUser.registerSubclass(Goal.class);
        ParseUser.registerSubclass(Note.class);
        ParseUser.registerSubclass(Reminder.class);
        ParseUser.registerSubclass(Tag.class);
        ParseUser.registerSubclass(Substring.class);
    }
}