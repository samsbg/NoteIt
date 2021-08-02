package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.noteit.GoogleCalendarClient;
import com.codepath.noteit.NoteItApp;
import com.codepath.noteit.adapters.ReminderAdapter;
import com.codepath.noteit.adapters.SearchNoteAdapter;
import com.codepath.noteit.databinding.ActivityGoalEditorBinding;
import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Reminder;
import com.codepath.noteit.models.Tag;
import com.codepath.noteit.models.User;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;

public class GoalEditorActivity extends AppCompatActivity {

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_EVENTS};
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    ActivityGoalEditorBinding binding;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    private GoogleCalendarClient client;

    SearchNoteAdapter searchNoteAdapter;
    ReminderAdapter reminderAdapter;

    Goal goal;
    Date date;

    List<Reminder> reminders;
    List<Integer> daysNum;
    List<Note> notes;
    List<Tag> tags;
    Map<String, List<Note>> mapNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalEditorBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        goal = new Goal();

        ReminderAdapter.OnClickListener onClickListenerReminder = new ReminderAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position, Date newDate) {
                reminders.get(position).setDate(newDate);
            }
        };

        SearchNoteAdapter.OnClickListener onClickListenerSearch = new SearchNoteAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position, Note note) {
                goal.setNote(notes.get(position));
                binding.etSearch.setText(notes.get(position).getTitle());
                notes.clear();
                searchNoteAdapter.notifyDataSetChanged();
            }
        };

        reminders = new ArrayList<>();
        notes = new ArrayList<>();
        tags = new ArrayList<>();

        mapNotes = new HashMap<>();

        reminderAdapter = new ReminderAdapter(this, reminders, onClickListenerReminder);
        binding.rvReminders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReminders.setAdapter(reminderAdapter);

        searchNoteAdapter = new SearchNoteAdapter(this, notes, onClickListenerSearch);
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearch.setAdapter(searchNoteAdapter);

        queryNotes();
        queryTags();

        binding.tvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        GoalEditorActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String dateString = month + "/" + dayOfMonth + "/" + year;
                binding.tvDatePicker.setText(dateString);
                date = new Date(year-1900, month-1, dayOfMonth);
            }
        };

        binding.btnSaveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });

        binding.etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                generateReminders();
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                binding.rvSearch.setVisibility(View.VISIBLE);
                notes.clear();
                String text = s.toString().toLowerCase();
                if(!text.equals("")) {
                    notes.addAll(mapNotes.get(text));
                }
                searchNoteAdapter.notifyDataSetChanged();
            }
        });
    }

    private void save(View view) {
        client = NoteItApp.getRestClient(GoalEditorActivity.this);

        String name = binding.tvName.getText().toString();
        String amount = binding.etNumber.getText().toString();

        if (name.equals("")) {
            Toast.makeText(GoalEditorActivity.this, "Name must be filled", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount.equals("")) {
            Toast.makeText(GoalEditorActivity.this, "Number of reminders must be filled", Toast.LENGTH_SHORT).show();
            return;
        }
        if (date == null) {
            Toast.makeText(GoalEditorActivity.this, "Date due must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        goal.setDueDate(date);
        goal.setName(name);
        goal.setTotalReviews(Integer.parseInt(amount));
        goal.setReviewed(0);
        goal.setCreatedBy(ParseUser.getCurrentUser());

        goal.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("GoalEditor", "Issue with saving goal", e);
                }

                for (Reminder rem : reminders) {
                    rem.setGoal(goal);
                    rem.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e("GoalEditor", "Reminder not saved", e);
                            }
                        }
                    });

                    if (client != null) {
                        try {
                            client.createEvent(((User) ParseUser.getCurrentUser()).getCalendarId(), goal.getName(), rem.getDate(), new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Headers headers, JSON json) {

                                }

                                @Override
                                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                                }
                            });
                        } catch (Exception e2) {
                            Log.e("GoalEditor", "Event not created ", e2);
                        }
                    }


                    client.createEvent(((User) ParseUser.getCurrentUser()).getCalendarId(), goal.getName(), rem.getDate(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d("MainActivity", "Success in creating event");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d("MainActivity", "Error in creating event " + statusCode + response);
                        }
                    });
                }

            }
        });

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void separateReminders(int beginning, int remindersM, int days) {
        if (remindersM >= days) {
            for (int i = 0; i < remindersM/days; i++) {
                for (int j = 0; j < days; j++) { daysNum.add(beginning + j); }
            }
            remindersM = remindersM % days;
        }

        if (remindersM == 0) { return; }

        if(remindersM == 1) {
            daysNum.add(beginning + days/2);
            return;
        }

        int size = days/remindersM;
        if (size == 1) {
            separateReminders(beginning, remindersM/2, days/2);
            separateReminders(beginning + days/2, remindersM - remindersM/2, days - days/2);
            return;
        }

        int j = beginning;
        for (int i = 0; i < remindersM; i++) {
            separateReminders(j, 1, size);
            j += size;
        }
    }

    public void generateReminders() {

        reminders.clear();

        String amount = binding.etNumber.getText().toString();

        if (amount.equals("")) {
            Toast.makeText(GoalEditorActivity.this, "Number of reminders must be filled", Toast.LENGTH_SHORT).show();
            return;
        }
        if (date == null) {
            Toast.makeText(GoalEditorActivity.this, "Date due must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        Date currentDate = Calendar.getInstance().getTime();

        long diff = date.getTime() - currentDate.getTime();
        int diffDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        daysNum = new ArrayList<>();
        separateReminders(0, Integer.parseInt(binding.etNumber.getText().toString()), diffDays);

        for (int i = 0; i < daysNum.size(); i++) {
            Reminder rem = new Reminder();
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.DATE, daysNum.get(i));
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);
            rem.setDate(c.getTime());
            rem.setCreatedBy(ParseUser.getCurrentUser());
            reminders.add(rem);
            reminderAdapter.notifyDataSetChanged();
        }
    }

    private void queryNotes() {
        ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Note>() {
            @Override
            public void done(List<Note> notesList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("MainActivity", "Issue with getting notes", e);
                    return;
                }
                notes.addAll(notesList);

                for (Note note : notesList) {
                    addNoteToMap(note);
                }

                //From map to JSONobject
                JSONObject objMap = new JSONObject();
                JSONArray arr2;

                for (String key : mapNotes.keySet()) {
                    arr2 = new JSONArray();

                    for (Note noteIt : mapNotes.get(key)) {
                        arr2.put(noteIt);
                    }

                    try {
                        objMap.put(key, arr2);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }

            }
        });

    }

    private void queryTags() {
        ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> tagsList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("MainActivity", "Issue with getting tags", e);
                    return;
                }
                tags.addAll(tagsList);
            }
        });
    }

    public void addNoteToMap(Note n) {
        int stringLength = n.getTitle().length();
        String substring;

        for (int i = 0; i < stringLength; i++) {
            for (int j = i + 1; j <= stringLength; j++) {
                substring = n.getTitle().substring(i,j).toLowerCase();
                if(mapNotes.containsKey(substring)) {
                    if (!mapNotes.get(substring).contains(n)) {
                        mapNotes.get(substring).add(n);
                    }
                } else {
                    mapNotes.put(substring, new ArrayList<>());
                    mapNotes.get(substring).add(n);
                }
            }

        }
    }

}
