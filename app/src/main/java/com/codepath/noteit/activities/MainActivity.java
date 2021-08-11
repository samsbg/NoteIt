package com.codepath.noteit.activities;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.noteit.ActionButton;
import com.codepath.noteit.NoteItApp;
import com.codepath.noteit.R;
import com.codepath.noteit.GoogleCalendarClient;
import com.codepath.noteit.adapters.MainGoalAdapter;
import com.codepath.noteit.adapters.MainNoteAdapter;
import com.codepath.noteit.adapters.SearchNoteAdapter;
import com.codepath.noteit.adapters.SearchTagAdapter;
import com.codepath.noteit.adapters.TagAdapter;
import com.codepath.noteit.databinding.ActivityMainBinding;
import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Tag;
import com.codepath.noteit.models.User;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import static java.util.Collections.reverse;

public class MainActivity extends OAuthLoginActionBarActivity<GoogleCalendarClient> implements PopupMenu.OnMenuItemClickListener  {

    ActivityMainBinding binding;

    TagAdapter tagAdapter;
    MainNoteAdapter noteAdapter;
    MainGoalAdapter goalAdapter;
    SearchNoteAdapter searchNoteAdapter;
    SearchTagAdapter searchTagAdapter;

    List<Note> notes;
    List<Goal> goals;
    List<Tag> tags;
    List<Note> notesSearch;
    List<Tag> tagsSearch;

    public static HashMap<String, List<Note>> substringsNotes;
    static {
        substringsNotes = new HashMap<>();
        ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Note>() {
            @Override
            public void done(List<Note> notesList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("MainActivity", "Issue with getting notes", e);
                    return;
                }

                for(Note n : notesList) {
                    int stringLength = n.getTitle().length();
                    String substring;

                    for (int i = 0; i < stringLength; i++) {
                        for (int j = i + 1; j <= stringLength; j++) {
                            substring = n.getTitle().substring(i,j).toLowerCase();
                            if(substringsNotes.containsKey(substring)) {
                                if (!Objects.requireNonNull(substringsNotes.get(substring)).contains(n)) {
                                    Objects.requireNonNull(substringsNotes.get(substring)).add(n);
                                }
                            } else {
                                substringsNotes.put(substring, new ArrayList<>());
                                Objects.requireNonNull(substringsNotes.get(substring)).add(n);
                            }
                        }

                    }
                }
            }
        });
    }

    public static HashMap<String, List<Tag>> substringsTag;
    static {
        substringsTag = new HashMap<>();
        ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> tagsList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("MainActivity", "Issue with getting notes", e);
                    return;
                }

                for(Tag t : tagsList) {
                    int stringLength = t.getName().length();
                    String substring;

                    for (int i = 0; i < stringLength; i++) {
                        for (int j = i + 1; j <= stringLength; j++) {
                            substring = t.getName().substring(i,j).toLowerCase();
                            if(substringsTag.containsKey(substring)) {
                                if (!Objects.requireNonNull(substringsTag.get(substring)).contains(t)) {
                                    Objects.requireNonNull(substringsTag.get(substring)).add(t);
                                }
                            } else {
                                substringsTag.put(substring, new ArrayList<>());
                                Objects.requireNonNull(substringsTag.get(substring)).add(t);
                            }
                        }

                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        tagAdapterSetup();
        noteAdapterSetup();
        goalAdapterSetup();

        searchSetup();
        bottomNavigationSetup();
        fabSetup();

        binding.ibUserIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.setOnMenuItemClickListener(MainActivity.this);
            popup.inflate(R.menu.menu_user_icon_main);
            popup.show();
        });
    }

    private void tagAdapterSetup() {
        TagAdapter.OnClickListener onClickListenerTag = tag -> {
            Intent i = new Intent(MainActivity.this, TagActivity.class);
            i.putExtra("TAG", tag);
            startActivity(i);
        };

        tags = new ArrayList<>();
        tagAdapter = new TagAdapter(this, tags, onClickListenerTag);
        binding.rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvTags.setAdapter(tagAdapter);
        queryTags();
    }

    private void noteAdapterSetup() {
        MainNoteAdapter.OnClickListener onClickListenerNote = note -> {
            Intent i = new Intent(MainActivity.this, NoteEditorActivity.class);
            i.putExtra("NOTE", note);
            startActivity(i);
        };
        MainNoteAdapter.OnLongClickListener onLongClickListenerNote = (note, v) -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.setOnMenuItemClickListener(MainActivity.this);
            popup.inflate(R.menu.menu_note);
            popup.show();
        };

        notes = new ArrayList<>();
        noteAdapter = new MainNoteAdapter(this, notes, onClickListenerNote, onLongClickListenerNote);
        binding.rvNotes.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvNotes.setAdapter(noteAdapter);
        queryNotes();
    }

    private void goalAdapterSetup() {
        MainGoalAdapter.OnClickListener onClickListenerGoal = goal -> {
            Intent i;
            if(goal.getNote() != null) {
                i = new Intent(MainActivity.this, NoteEditorActivity.class);
                i.putExtra("NOTE_GOAL", goal.getNote());
            } else {
                i = new Intent(MainActivity.this, TagActivity.class);
                i.putExtra("TAG", goal.getTag());
            }
            i.putExtra("GOAL", goal);
            startActivity(i);
        };

        goals = new ArrayList<>();
        goalAdapter = new MainGoalAdapter(this, goals, onClickListenerGoal);
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGoals.setAdapter(goalAdapter);
        queryGoals();
    }

    private void searchSetup() {
        SearchNoteAdapter.OnClickListener onClickListenerSearchNote = note -> {
            Intent i = new Intent(MainActivity.this, NoteEditorActivity.class);
            i.putExtra("NOTE", note);
            startActivity(i);
        };

        SearchTagAdapter.OnClickListener onClickListenerSearchTag = tag -> {
            Intent i = new Intent(MainActivity.this, TagActivity.class);
            i.putExtra("TAG", tag);
            startActivity(i);
        };

        notesSearch = new ArrayList<>();
        searchNoteAdapter = new SearchNoteAdapter(this, notesSearch, onClickListenerSearchNote);
        binding.rvSearchMainNote.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearchMainNote.setAdapter(searchNoteAdapter);

        tagsSearch = new ArrayList<>();
        searchTagAdapter = new SearchTagAdapter(this, tagsSearch, onClickListenerSearchTag);
        binding.rvSearchMainTag.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearchMainTag.setAdapter(searchTagAdapter);

        binding.etSearchMain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().toLowerCase();

                binding.tvNotesSep.setVisibility(View.VISIBLE);
                binding.rvSearchMainNote.setVisibility(View.VISIBLE);
                notesSearch.clear();
                if(!text.equals("") && substringsNotes.containsKey(text)) {
                    notesSearch.addAll(Objects.requireNonNull(substringsNotes.get(text)));
                }
                searchNoteAdapter.notifyDataSetChanged();
                if(notesSearch.isEmpty()) {
                    binding.tvNotesSep.setVisibility(View.GONE);
                }

                binding.tvTagsSep.setVisibility(View.VISIBLE);
                binding.rvSearchMainTag.setVisibility(View.VISIBLE);
                tagsSearch.clear();
                if(!text.equals("") && substringsTag.containsKey(text)) {
                    tagsSearch.addAll(Objects.requireNonNull(substringsTag.get(text)));
                }
                searchTagAdapter.notifyDataSetChanged();
                if(tagsSearch.isEmpty()) {
                    binding.tvTagsSep.setVisibility(View.GONE);
                }

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void bottomNavigationSetup() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.iAdd:
                    PopupMenu popup = new PopupMenu(MainActivity.this, findViewById(R.id.iAdd));
                    popup.setOnMenuItemClickListener(MainActivity.this);
                    popup.inflate(R.menu.menu_add_main);
                    popup.show();
                    return true;
                case R.id.iCalendar:
                    Intent i = new Intent(MainActivity.this, CalendarActivity.class);
                    startActivity(i);
                    return true;
                case R.id.iStats:
                    Intent j = new Intent(MainActivity.this, StatisticsActivity.class);
                    startActivity(j);
                    return true;
                default:
                    return true;
            }
        });
    }

    private void fabSetup() {
        MaterialSheetFab<ActionButton> materialSheetFab = new MaterialSheetFab<>(binding.fab, binding.fabSheet, binding.overlay, getColor(R.color.white), getColor(R.color.color_2_fuchsia));

        findViewById(R.id.fab_note).setOnClickListener(v -> {
            materialSheetFab.hideSheet();
            Intent j = new Intent(MainActivity.this, NoteEditorActivity.class);
            startActivity(j);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });
        findViewById(R.id.fab_goal).setOnClickListener(v -> {
            materialSheetFab.hideSheet();
            Intent k = new Intent(MainActivity.this, GoalEditorActivity.class);
            startActivity(k);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });
    }

    private void queryNotes() {
        ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground((notesList, e) -> {
            if (e != null) {
                Log.e("MainActivity", "Issue with getting notes", e);
                return;
            }
            reverse(notesList);
            notes.addAll(notesList);
            noteAdapter.notifyDataSetChanged();
        });
    }

    private void queryGoals() {
        ParseQuery<Goal> query = ParseQuery.getQuery(Goal.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.whereEqualTo("completedBy", null);
        query.findInBackground((goalsList, e) -> {
            if (e != null) {
                Log.e("MainActivity", "Issue with getting notes", e);
                return;
            }
            reverse(goalsList);
            goals.addAll(goalsList);
            goalAdapter.notifyDataSetChanged();
        });
    }

    private void queryTags() {
        ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground((tagsList, e) -> {
            if (e != null) {
                Log.e("MainActivity", "Issue with getting tags", e);
                return;
            }
            tags.addAll(tagsList);
            tagAdapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iLogOut:
                ParseUser.logOut();
                Intent i  = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.iConnectGoogle:
                getClient().connect();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onLoginSuccess() {
        Log.d("MainActivity", "Google login successful");
        createCalendar();
    }

    private void createCalendar() {
        if (((User) ParseUser.getCurrentUser()).getCalendarId().equals("-")) {
            GoogleCalendarClient client = NoteItApp.getRestClient(MainActivity.this);

            client.createCalendar("NoteIt", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d("MainActivity", "Success in creating calendar");
                    try {
                        ((User) ParseUser.getCurrentUser()).setCalendarId(json.jsonObject.getString("id"));
                        ((User) ParseUser.getCurrentUser()).saveInBackground(e -> {
                            if (e != null) {
                                Log.e("LoginActivity", "Issue with saving calendar to user", e);
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