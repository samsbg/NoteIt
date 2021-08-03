package com.codepath.noteit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.noteit.GoogleCalendarClient;
import com.codepath.noteit.NoteItApp;
import com.codepath.noteit.R;
import com.codepath.noteit.models.User;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import okhttp3.Headers;

public class RedirectActivity extends OAuthLoginActionBarActivity<GoogleCalendarClient> {

    private GoogleCalendarClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);
        getClient().connect();
    }

    @Override
    public void onLoginSuccess() {
        Log.d("MainActivity", "Google login successful");
        createCalendar();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void createCalendar() {
        if (((User) ParseUser.getCurrentUser()).getCalendarId().equals("-")) {
            client = NoteItApp.getRestClient(RedirectActivity.this);

            client.createCalendar("NoteIt", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d("MainActivity", "Success in creating calendar");
                    try {
                        ((User) ParseUser.getCurrentUser()).setCalendarId(json.jsonObject.getString("id"));
                        ((User) ParseUser.getCurrentUser()).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e("LoginActivity", "Issue with saving calendar to user", e);
                                    return;
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d("MainActivity", "Error in creating calendar " + statusCode + response);
                }
            });
        }
    }

    @Override
    public void onLoginFailure(Exception e) {
        Toast.makeText(getApplicationContext(), "There was a problem connecting, try again later", Toast.LENGTH_SHORT).show();
        Log.e("MainActivity", "Google login error " + e);
    }
}