package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codepath.noteit.R;
import com.codepath.noteit.adapters.NoteImagesAdapter;
import com.codepath.noteit.databinding.ActivityNoteEditorBinding;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Tag;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.reverse;

public class NoteEditorActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int PICK_IMAGE = 103;

    ActivityNoteEditorBinding binding;
    NoteImagesAdapter adapter;
    List<Bitmap> images;
    File photoFile;
    Note note;
    Tag tag;

    String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteEditorBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        note = new Note();

        images = new ArrayList<>();
        adapter = new NoteImagesAdapter(this, images);

        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvImages.setAdapter(adapter);

        binding.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        binding.ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(NoteEditorActivity.this, v);
                popup.setOnMenuItemClickListener(NoteEditorActivity.this);
                popup.inflate(R.menu.menu_add_note);
                popup.show();
            }
        });

        binding.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.deleteInBackground();
                returnMain();
            }
        });

        binding.ibAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });
    }

    private void saveNote() {
        note.setTitle(binding.tvTitle.getText().toString());
        note.setContent(binding.tvContent.getText().toString());
        note.setImages(new JSONArray(images));
        note.setCreatedBy(ParseUser.getCurrentUser());

        note.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("MainActivity", "Error while saving", e);
                    Toast.makeText(NoteEditorActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(NoteEditorActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
                Log.d("NoteEditor", "Note saved");
            }
        });
    }

    private void addTag() {
        String tagString = binding.etTag.getText().toString();
        if (tagString == null) {
            Toast.makeText(NoteEditorActivity.this, "Needs tag name", Toast.LENGTH_SHORT).show();
            return;
        }
        tag = new Tag();
        ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
        query.include(Tag.KEY_USER);
        query.whereEqualTo("name", tagString);
        query.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> tagsList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("NoteEditorActivity", "Issue with getting notes", e);
                    return;
                }
                if (!tagsList.isEmpty()) {
                    tag = tagsList.get(0);
                }
            }
        });

        //Issue to solve

        JSONArray notes = tag.getNotes();

        if(notes == null) {
            notes = new JSONArray();
        }

        saveNote();
        notes.put(note);
        tag.setNotes(notes);

        Log.d("NoteEditor", notes.toString());
    }

    private void returnMain() {
        finish();
        Intent i = new Intent(NoteEditorActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iChooseImage:
                openGallery();
                return true;
            case R.id.iTakePhoto:
                launchCamera();
                return true;
            default:
                return false;
        }
    }

    private void openGallery() {
        Intent gallery =
                new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void launchCamera() {
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(NoteEditorActivity.this, "com.codepath.fileprovider", photoFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                images.add(takenImage);
                adapter.notifyDataSetChanged();
                binding.rvImages.smoothScrollToPosition(0);
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                images.add(takenImage);
                adapter.notifyDataSetChanged();
                binding.rvImages.smoothScrollToPosition(0);
            } else {
                Toast.makeText(this, "Picture wasn't chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MainActivity");
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.e("GetPhotoFileUri", "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }
}