package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.noteit.GridItemDecoration;
import com.codepath.noteit.adapters.NoteImagesAdapter;
import com.codepath.noteit.databinding.ActivityNoteEditorBinding;
import com.codepath.noteit.models.Note;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

public class NoteEditorActivity extends AppCompatActivity {

    ActivityNoteEditorBinding binding;
    JSONArray images;
    NoteImagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteEditorBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        images = new JSONArray();
        adapter = new NoteImagesAdapter(this, images);

        GridLayoutManager gridLayout = new GridLayoutManager(this, 3);
        binding.rvImages.setLayoutManager(gridLayout);
        binding.rvImages.setHasFixedSize(true);
        binding.rvImages.addItemDecoration(new GridItemDecoration());
        binding.rvImages.setAdapter(adapter);

        binding.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(binding.tvTitle.getText().toString(), binding.tvContent.getText().toString(), images, ParseUser.getCurrentUser());
            }
        });
    }

    private void saveNote(String title, String content, JSONArray images, ParseUser currentUser) {
        Note note = new Note();

        note.setTitle(title);
        note.setContent(content);
        note.setImages(images);
        note.setCreatedBy(currentUser);

        note.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("MainActivity", "Error while saving", e);
                    Toast.makeText(NoteEditorActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}