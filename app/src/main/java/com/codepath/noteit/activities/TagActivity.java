package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.codepath.noteit.R;
import com.codepath.noteit.adapters.MainNoteAdapter;
import com.codepath.noteit.databinding.ActivityTagBinding;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Tag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class TagActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    ActivityTagBinding binding;
    MainNoteAdapter noteAdapter;
    Tag tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        
        binding = ActivityTagBinding.inflate(getLayoutInflater());

        tag = getIntent().getParcelableExtra("TAG");
        
        binding.tvGoalName.setText(tag.getName());

        MainNoteAdapter.OnClickListener onClickListenerNote = new MainNoteAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position, Note note, View v) {
                Intent i = new Intent(TagActivity.this, NoteEditorActivity.class);
                i.putExtra("NOTE", note);
                startActivity(i);
            }
        };

        MainNoteAdapter.OnLongClickListener onLongClickListenerNote = new MainNoteAdapter.OnLongClickListener() {
            @Override
            public void onItemClicked(int position, Note note, View v) {
                PopupMenu popup = new PopupMenu(TagActivity.this, v);
                popup.setOnMenuItemClickListener(TagActivity.this);
                popup.inflate(R.menu.menu_note);
                popup.show();
            }
        };

        JSONArray notesArray = tag.getNotes();
        List<Note> notes = new ArrayList<>();
        Gson gson = new GsonBuilder().create();

        if (notesArray != null) {
            for (int i=0;i<notesArray.length();i++){
                try {
                    notes.add(gson.fromJson(String.valueOf(notesArray.getJSONObject(i)), Note.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        noteAdapter = new MainNoteAdapter(this, notes, onClickListenerNote, onLongClickListenerNote);
        binding.rvNotes.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvNotes.setAdapter(noteAdapter);
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