package com.codepath.noteit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.codepath.noteit.R;
import com.codepath.noteit.adapters.MainGoalAdapter;
import com.codepath.noteit.adapters.MainNoteAdapter;
import com.codepath.noteit.adapters.SearchNoteAdapter;
import com.codepath.noteit.adapters.SearchTagAdapter;
import com.codepath.noteit.databinding.ActivityMainBinding;
import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Tag;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.reverse;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener  {

    ActivityMainBinding binding;

    MainNoteAdapter noteAdapter;
    MainGoalAdapter goalAdapter;
    SearchNoteAdapter searchNoteAdapter;
    SearchTagAdapter searchTagAdapter;

    List<Note> notes;
    List<Goal> goals;
    List<Note> notesSearch;
    List<Tag> tagsSearch;

    final int RC_SIGN_IN = 23;

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
                                if (!substringsNotes.get(substring).contains(n)) {
                                    substringsNotes.get(substring).add(n);
                                }
                            } else {
                                substringsNotes.put(substring, new ArrayList<>());
                                substringsNotes.get(substring).add(n);
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
                                if (!substringsTag.get(substring).contains(t)) {
                                    substringsTag.get(substring).add(t);
                                }
                            } else {
                                substringsTag.put(substring, new ArrayList<>());
                                substringsTag.get(substring).add(t);
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

        SearchNoteAdapter.OnClickListener onClickListenerSearchNote = new SearchNoteAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position, Note note) {
                Intent i = new Intent(MainActivity.this, NoteEditorActivity.class);
                i.putExtra("NOTE", note);
                startActivity(i);
            }
        };

        SearchTagAdapter.OnClickListener onClickListenerSearchTag = new SearchTagAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position, Tag tag) {
                Intent i = new Intent(MainActivity.this, TagActivity.class);
                i.putExtra("TAG", tag);
                startActivity(i);
            }
        };

        MainNoteAdapter.OnClickListener onClickListenerNote = new MainNoteAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position, Note note, View v) {
                Intent i = new Intent(MainActivity.this, NoteEditorActivity.class);
                i.putExtra("NOTE", note);
                startActivity(i);
            }
        };

        MainNoteAdapter.OnLongClickListener onLongClickListenerNote = new MainNoteAdapter.OnLongClickListener() {
            @Override
            public void onItemClicked(int position, Note note, View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.inflate(R.menu.menu_note);
                popup.show();
            }
        };

        MainGoalAdapter.OnClickListener onClickListenerGoal = new MainGoalAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position, Goal goal, View v) {
                if(goal.getNote() != null) {
                    Intent i = new Intent(MainActivity.this, NoteEditorActivity.class);
                    i.putExtra("GOAL", goal);
                    i.putExtra("NOTE_GOAL", goal.getNote());
                    startActivity(i);
                }
            }
        };

        notes = new ArrayList<>();
        noteAdapter = new MainNoteAdapter(this, notes, onClickListenerNote, onLongClickListenerNote);
        binding.rvNotes.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvNotes.setAdapter(noteAdapter);
        queryNotes();

        goals = new ArrayList<>();
        goalAdapter = new MainGoalAdapter(this, goals, onClickListenerGoal);
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGoals.setAdapter(goalAdapter);
        queryGoals();

        notesSearch = new ArrayList<>();
        searchNoteAdapter = new SearchNoteAdapter(this, notesSearch, onClickListenerSearchNote);
        binding.rvSearchMainNote.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearchMainNote.setAdapter(searchNoteAdapter);

        tagsSearch = new ArrayList<>();
        searchTagAdapter = new SearchTagAdapter(this, tagsSearch, onClickListenerSearchTag);
        binding.rvSearchMainTag.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearchMainTag.setAdapter(searchTagAdapter);

        binding.ibUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.inflate(R.menu.menu_user_icon_main);
                popup.show();
            }
        });

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
                    notesSearch.addAll(substringsNotes.get(text));
                }
                searchNoteAdapter.notifyDataSetChanged();
                if(notesSearch.isEmpty()) {
                    binding.tvNotesSep.setVisibility(View.GONE);
                }

                binding.tvTagsSep.setVisibility(View.VISIBLE);
                binding.rvSearchMainTag.setVisibility(View.VISIBLE);
                tagsSearch.clear();
                if(!text.equals("") && substringsTag.containsKey(text)) {
                    tagsSearch.addAll(substringsTag.get(text));
                }
                searchTagAdapter.notifyDataSetChanged();
                if(tagsSearch.isEmpty()) {
                    binding.tvTagsSep.setVisibility(View.GONE);
                }

            }
        });

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                    default: return true;
                }
            }
        });
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
                reverse(notesList);
                notes.addAll(notesList);
                Log.d("MainActivity", "Size of list " + notes.size());
                noteAdapter.notifyDataSetChanged();
            }
        });
    }

    private void queryGoals() {
        ParseQuery<Goal> query = ParseQuery.getQuery(Goal.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.whereEqualTo("completedBy", null);
        query.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> goalsList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("MainActivity", "Issue with getting notes", e);
                    return;
                }
                reverse(goalsList);
                goals.addAll(goalsList);
                Log.d("MainActivity", "Size of list goal " + goals.size());
                goalAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iLogOut:
                ParseUser.logOut();
                Intent i  = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.iNote:
                Intent j = new Intent(this, NoteEditorActivity.class);
                startActivity(j);
                return true;
            case R.id.iGoal:
                Intent k = new Intent(this, GoalEditorActivity.class);
                startActivity(k);
                return true;
            case R.id.iConnectGoogle:
                Intent l = new Intent(this, RedirectActivity.class);
                startActivity(l);
                return true;
            default:
                return false;
        }
    }
}