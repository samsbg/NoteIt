package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.Toast;

import com.codepath.noteit.adapters.MainGoalAdapter;
import com.codepath.noteit.adapters.MainNoteAdapter;
import com.codepath.noteit.adapters.ReminderAdapter;
import com.codepath.noteit.adapters.SearchAdapter;
import com.codepath.noteit.databinding.ActivityGoalEditorBinding;
import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Reminder;
import com.codepath.noteit.models.Tag;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.reverse;

public class GoalEditorActivity extends AppCompatActivity {

    ActivityGoalEditorBinding binding;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    Date date;

    SearchAdapter searchAdapter;
    ReminderAdapter reminderAdapter;

    Goal goal;

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

        SearchAdapter.OnClickListener onClickListenerSearch = new SearchAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                goal.setNote(notes.get(position));
                binding.etSearch.setText(notes.get(position).getTitle());
                notes.clear();
                searchAdapter.notifyDataSetChanged();
            }
        };

        reminders = new ArrayList<>();
        notes = new ArrayList<>();
        tags = new ArrayList<>();

        mapNotes = new HashMap<>();

        reminderAdapter = new ReminderAdapter(this, reminders, onClickListenerReminder);
        binding.rvReminders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReminders.setAdapter(reminderAdapter);

        searchAdapter = new SearchAdapter(this, notes, onClickListenerSearch);
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearch.setAdapter(searchAdapter);

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
                save();
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
                searchAdapter.notifyDataSetChanged();
            }
        });
    }

    private void save() {
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

        JSONArray remindersJSON = new JSONArray();

        for (Reminder rem : reminders) {
            rem.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e("GoalEditor", "Reminder not saved", e);
                    }
                }
            });
            remindersJSON.put(rem);
        }

        goal.setReminders(remindersJSON);

        goal.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("GoalEditor", "Issue with saving goal", e);
                }
            }
        });
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

        String name = binding.tvName.getText().toString();
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
            c.setTime(date);
            c.add(Calendar.DATE, daysNum.get(i));
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