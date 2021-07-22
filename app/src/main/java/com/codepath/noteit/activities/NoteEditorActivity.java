package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.codepath.noteit.GridItemDecoration;
import com.codepath.noteit.adapters.NoteImagesAdapter;
import com.codepath.noteit.databinding.ActivityNoteEditorBinding;

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
    }
}