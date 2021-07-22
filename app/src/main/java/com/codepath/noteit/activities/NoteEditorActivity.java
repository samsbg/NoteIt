package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.codepath.noteit.databinding.ActivityNoteEditorBinding;

public class NoteEditorActivity extends AppCompatActivity {

    ActivityNoteEditorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteEditorBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);
    }
}