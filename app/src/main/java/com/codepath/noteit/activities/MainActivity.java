package com.codepath.noteit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.codepath.noteit.models.Substring;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.reverse;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ActivityMainBinding binding;

    MainNoteAdapter noteAdapter;
    MainGoalAdapter goalAdapter;
    SearchAdapter searchAdapter;

    List<Note> notes;
    List<Note> notesSearch;
    List<Goal> goals;
    Map<String, List<Note>> map;

    GoogleSignInClient googleClient;

    final int RC_SIGN_IN = 23;

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

        MainNoteAdapter.OnLongClickListener onLongClickListenerNote = new MainNoteAdapter.OnLongClickListener() {
            @Override
            public void onItemClicked(int position, Note note, View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.inflate(R.menu.menu_note);
                popup.show();
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.your_web_app_client_id))
                .build();
        googleClient = GoogleSignIn.getClient(MainActivity.this, gso);

        notes = new ArrayList<>();
        noteAdapter = new MainNoteAdapter(this, notes, onLongClickListenerNote);
        binding.rvNotes.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvNotes.setAdapter(noteAdapter);
        queryNotes();

        goals = new ArrayList<>();
        //goalAdapter = new MainGoalAdapter(this, goals);
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
        //binding.rvGoals.setAdapter(goalAdapter);
        queryGoals();

        map = new HashMap<>();
        notesSearch = new ArrayList<>();
        searchAdapter = new SearchAdapter(this, notesSearch, onClickListenerSearch);
        binding.rvSearchMain.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearchMain.setAdapter(searchAdapter);
        querySubstrings();

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
                if(!text.equals("") && map.get(text) != null) {
                    notesSearch.addAll(map.get(text));
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
    }

    private void querySubstrings() {
        ParseQuery<Substring> query = ParseQuery.getQuery(Substring.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Substring>() {
            @Override
            public void done(List<Substring> substringList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("MainActivity", "Issue with getting substring", e);
                    return;
                }
                objectToMap(substringList.get(0).getMap());
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
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.d("OAuth login", "User signed in to google");
            Toast.makeText(getApplicationContext(), "Successful login", Toast.LENGTH_LONG).show();

        } catch (ApiException e) {
            Log.w("OAuth login", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "Error while signing in, code="+ e.getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }

    private void objectToMap(JSONObject object) {
        Iterator<String> keys = object.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            List<Note> listMap = new ArrayList<>();
            try {
                if (object.get(key) instanceof JSONArray) {
                    for (int i = 0; i < ((JSONArray) object.get(key)).length(); i++) {
                        Note noteObj = new Gson().fromJson(((JSONArray) object.get(key)).get(i).toString(), Note.class);
                        listMap.add(noteObj);
                    }
                    map.put(key, listMap);
                }
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
    }
}