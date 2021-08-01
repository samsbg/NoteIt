package com.codepath.noteit;

import android.content.Context;
import android.util.Log;

import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.api.BaseApi;
import com.google.api.services.calendar.CalendarScopes;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GoogleCalendarClient extends OAuthBaseClient {
    public static final BaseApi REST_API_INSTANCE = GoogleApi20.instance();
    public static final String REST_URL = "https://www.googleapis.com/calendar/v3";
    public static final String REST_CONSUMER_KEY = "367948355918-d04tb8gmfnk0l2jr6uujbep3kp9s5s6d.apps.googleusercontent.com";
    public static final String REST_CONSUMER_SECRET = "zZVctjSOVAT_dBIYogEbV6VA";

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "https://fakeredirectlink.com";

    public static final String OAUTH2_SCOPE = CalendarScopes.CALENDAR;

    private MediaType JSONObject = MediaType.parse("application/json; charset=utf-8");

    public GoogleCalendarClient(Context context) {
        super(context, REST_API_INSTANCE, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, OAUTH2_SCOPE,  // OAuth2 scope, null for OAuth1
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host), context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }

    public void createCalendar(String title, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("calendars");
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("summary", title);
        } catch (JSONException e) {
            Log.e("NoteItClient", "Create Calendar Json exception: " + e, e);
        }
        RequestBody body = RequestBody.create(JSONObject, jsonObject.toString());

        client.post(apiUrl, headers, params, body, handler);
    }

    public void getCalendar(String calendarId, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("calendars/calendarId");
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        params.put("calendarId", calendarId);
        client.get(apiUrl, params, handler);
    }

    public void createEvent(String calendarId, String title, Date date, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("calendars/calendarId/events");
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        params.put("calendarId", calendarId);

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonDate = new JSONObject();
        try {
            jsonDate.put("date", formatDate(date));

            jsonObject.put("summary", title);
            jsonObject.put("end", jsonDate);
            jsonObject.put("start", jsonDate);
        } catch (JSONException e) {
            Log.e("NoteItClient", "Create event json exception: " + e, e);
        }
        RequestBody body = RequestBody.create(JSONObject, jsonObject.toString());

        client.post(apiUrl, headers, params, body, handler);
    }

    // Can't delete or update Google Calendar events using this library

    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
}
