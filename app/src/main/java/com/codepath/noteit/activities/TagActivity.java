package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.codepath.noteit.R;
import com.codepath.noteit.adapters.MainNoteAdapter;
import com.codepath.noteit.databinding.ActivityTagBinding;
import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Tag;
import com.codepath.noteit.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.plattysoft.leonids.ParticleSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Collections.reverse;

public class TagActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    ActivityTagBinding binding;
    MainNoteAdapter noteAdapter;
    Tag tag;
    Goal goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityTagBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        tag = getIntent().getParcelableExtra("TAG");

        if (getIntent().getParcelableExtra("GOAL") != null) {
            goal = getIntent().getParcelableExtra("GOAL");
            binding.btnReviewTag.setVisibility(View.VISIBLE);
            binding.btnReviewTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ParticleSystem(TagActivity.this, 80, R.drawable.confeti2, (long) 1000)
                            .setSpeedRange(0.2f, 0.5f)
                            .oneShot(binding.btnReviewTag, 40);
                    if (goal.getReviewed() < goal.getTotalReviews()) {
                        goal.setReviewed(goal.getReviewed() + 1);
                        User user = (User) ParseUser.getCurrentUser();
                        user.setNotesReviewed(user.getNotesReviewed() + 1);
                        if (goal.getReviewed() == goal.getTotalReviews()) {
                            goal.setCompletedBy(Calendar.getInstance().getTime());
                        }
                    }
                    goal.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e("TagActivity", "Issue with saving goal", e);
                                return;
                            }
                            Intent i = new Intent(TagActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    });
                }
            });
        }
        
        binding.tvTagName.setText(tag.getName());
        DrawableCompat.setTint(binding.tvTagName.getBackground(), tag.getColor());

        MainNoteAdapter.OnClickListener onClickListenerNote = new MainNoteAdapter.OnClickListener() {
            @Override
            public void onItemClicked(Note note) {
                Intent i = new Intent(TagActivity.this, NoteEditorActivity.class);
                i.putExtra("NOTE", note);
                startActivity(i);
            }
        };

        MainNoteAdapter.OnLongClickListener onLongClickListenerNote = new MainNoteAdapter.OnLongClickListener() {
            @Override
            public void onItemClicked(Note note, View v) {
                PopupMenu popup = new PopupMenu(TagActivity.this, v);
                popup.setOnMenuItemClickListener(TagActivity.this);
                popup.inflate(R.menu.menu_note);
                popup.show();
            }
        };

        JSONArray notesArray = tag.getNotes();
        List<Note> notes = new ArrayList<>();
        List<String> notesId = new ArrayList<>();

        for (int i=0;i<notesArray.length();i++) {
            try {
                JSONObject jObj = notesArray.getJSONObject(i);
                notesId.add(jObj.getString("objectId"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.whereContainedIn("objectId", notesId);
        query.findInBackground(new FindCallback<Note>() {
            @Override
            public void done(List<Note> notesList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("TagActivity", "Issue with getting notes", e);
                    return;
                }
                notes.addAll(notesList);

                noteAdapter = new MainNoteAdapter(TagActivity.this, notes, onClickListenerNote, onLongClickListenerNote);
                binding.rvNotes.setLayoutManager(new GridLayoutManager(TagActivity.this, 2));
                binding.rvNotes.setAdapter(noteAdapter);
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iDelete:
                // Delete
                return true;
            default:
                return false;
        }
    }
}