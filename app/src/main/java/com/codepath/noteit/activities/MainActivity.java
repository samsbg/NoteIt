package com.codepath.noteit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codepath.noteit.R;
import com.codepath.noteit.adapters.MainGoalAdapter;
import com.codepath.noteit.adapters.MainNoteAdapter;
import com.codepath.noteit.adapters.SearchAdapter;
import com.codepath.noteit.databinding.ActivityMainBinding;
import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Note;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.reverse;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ActivityMainBinding binding;

    MainNoteAdapter noteAdapter;
    MainGoalAdapter goalAdapter;
    SearchAdapter searchAdapter;

    List<Note> notes;
    List<Note> notesSearch;
    List<Goal> goals;

    GoogleSignInClient googleClient;
    static GoogleSignInAccount account;

    final int RC_SIGN_IN = 23;

    public static HashMap<String, List<Note>> substrings;
    static {
        substrings = new HashMap<>();
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
                            if(substrings.containsKey(substring)) {
                                if (!substrings.get(substring).contains(n)) {
                                    substrings.get(substring).add(n);
                                }
                            } else {
                                substrings.put(substring, new ArrayList<>());
                                substrings.get(substring).add(n);
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

        SearchAdapter.OnClickListener onClickListenerSearch = new SearchAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                binding.etSearchMain.setText(notesSearch.get(position).getTitle());
                notesSearch.clear();
                searchAdapter.notifyDataSetChanged();
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.your_web_app_client_id))
                .build();
        googleClient = GoogleSignIn.getClient(MainActivity.this, gso);

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
        searchAdapter = new SearchAdapter(this, notesSearch, onClickListenerSearch);
        binding.rvSearchMain.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearchMain.setAdapter(searchAdapter);

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
                binding.rvSearchMain.setVisibility(View.VISIBLE);
                notesSearch.clear();
                String text = s.toString().toLowerCase();
                if(!text.equals("") && substrings.get(text) != null) {
                    notesSearch.addAll(substrings.get(text));
                }
                searchAdapter.notifyDataSetChanged();
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
                Intent l = googleClient.getSignInIntent();
                startActivityForResult(l, RC_SIGN_IN);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            Log.d("OAuth login", "User signed in to google");
            Toast.makeText(getApplicationContext(), "Successful login", Toast.LENGTH_LONG).show();

        } catch (ApiException e) {
            Log.w("OAuth login", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "Error while signing in, code="+ e.getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }
}