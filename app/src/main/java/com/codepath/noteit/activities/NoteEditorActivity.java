package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codepath.noteit.R;
import com.codepath.noteit.adapters.ColorAdapter;
import com.codepath.noteit.adapters.MainNoteAdapter;
import com.codepath.noteit.adapters.NoteImagesAdapter;
import com.codepath.noteit.adapters.SearchTagAdapter;
import com.codepath.noteit.adapters.TagAdapter;
import com.codepath.noteit.databinding.ActivityNoteEditorBinding;
import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Tag;
import com.codepath.noteit.models.User;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.plattysoft.leonids.ParticleSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.reverse;


public class NoteEditorActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int PICK_IMAGE = 103;

    ActivityNoteEditorBinding binding;
    NoteImagesAdapter adapter;
    ColorAdapter colorAdapterNote;
    ColorAdapter colorAdapterTag;
    TagAdapter tagAdapter;

    List<Bitmap> images;
    List<Tag> tagsTop;
    File photoFile;
    Note note;
    Tag tagSave;

    String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteEditorBinding.inflate(getLayoutInflater());

        int[] colors = {getColor(R.color.color_1_green),
                getColor(R.color.color_2_fuchsia),
                getColor(R.color.color_3_blue),
                getColor(R.color.color_4_yellow),
                getColor(R.color.color_5_orange),
                getColor(R.color.color_6_pink)};

        View view = binding.getRoot();
        setContentView(view);

        note = new Note();
        images = new ArrayList<>();

        note.setColor(colors[0]);

        ColorAdapter.OnClickListener onClickListenerColorNote = new ColorAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int color) {
                note.setColor(color);
                ImageViewCompat.setImageTintList(binding.colorCircle3, ColorStateList.valueOf(color));
                binding.rvColors.setVisibility(View.GONE);
            }
        };

        ColorAdapter.OnClickListener onClickListenerColorTag = new ColorAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int color) {
                tagSave.setColor(color);
                ImageViewCompat.setImageTintList(binding.colorCircle2, ColorStateList.valueOf(color));
                binding.rvColors2.setVisibility(View.GONE);
            }
        };

        TagAdapter.OnClickListener onClickListenerTag = new TagAdapter.OnClickListener() {
            @Override
            public void onItemClicked(Tag tag) {
                Intent i = new Intent(NoteEditorActivity.this, TagActivity.class);
                i.putExtra("TAG", tag);
                startActivity(i);
            }
        };

        if (getIntent().getParcelableExtra("NOTE") != null) {
            note = (Note) getIntent().getParcelableExtra("NOTE");
            binding.tvTitle.setText(note.getTitle());
            binding.tvContent.setText(note.getContent());
            if(note.getImages() != null) {
                for (int i = 0; i < note.getImages().length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) note.getImages().getJSONObject(i);
                        byte[] byteArray =  Base64.decode(jsonObject.getString("url"), Base64.DEFAULT) ;
                        images.add(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                    } catch (JSONException e) {
                        Log.e("NoteEditor", "Issue making bitmap " + e);
                    }
                }
            }
        }

        if (getIntent().getParcelableExtra("GOAL") != null) {
            Goal goal = (Goal) getIntent().getParcelableExtra("GOAL");
            note = (Note) getIntent().getParcelableExtra("NOTE_GOAL");
            binding.tvTitle.setText(note.getTitle());
            binding.tvContent.setText(note.getContent());
            /*
            if(note.getImages() != null) {
                images = (List<Bitmap>) note.getImages();
            }
             */
            binding.btnReview.setVisibility(View.VISIBLE);

            binding.btnReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ParticleSystem(NoteEditorActivity.this, 80, R.drawable.confeti2, (long) 1000)
                            .setSpeedRange(0.2f, 0.5f)
                            .oneShot(binding.btnReview, 40);
                    if (goal.getReviewed() < goal.getTotalReviews()) {
                        goal.setReviewed(goal.getReviewed() + 1);
                        User user = (User) ParseUser.getCurrentUser();
                        user.setNotesReviewed(user.getNotesReviewed() + 1);
                        if (goal.getReviewed() == goal.getTotalReviews()) {
                            goal.setCompletedBy(Calendar.getInstance().getTime());
                        }
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e("NoteEditor", "Error while saving user " + e);
                                }
                            }
                        });
                        goal.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e("NoteEditor", "Error while saving user " + e);
                                }
                            }
                        });
                    }
                    Intent i = new Intent(NoteEditorActivity.this, MainActivity.class);
                    startActivity(i);
                }
            });
        }

        tagsTop = new ArrayList<>();
        JSONArray tagsTopArr = note.getTags();
        List<String> tagsId = new ArrayList<>();

        if (tagsTopArr != null) {
            binding.tvTags2.setVisibility(View.VISIBLE);
            for (int i=0;i<tagsTopArr.length();i++) {
                try {
                    JSONObject jObj = tagsTopArr.getJSONObject(i);
                    tagsId.add(jObj.getString("objectId"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
            query.whereContainedIn("objectId", tagsId);
            query.findInBackground(new FindCallback<Tag>() {
                @Override
                public void done(List<Tag> notesList, com.parse.ParseException e) {
                    if (e != null) {
                        Log.e("TagActivity", "Issue with getting notes", e);
                        return;
                    }
                    tagsTop.addAll(notesList);

                    tagAdapter = new TagAdapter(NoteEditorActivity.this, tagsTop, onClickListenerTag);
                    binding.rvTags.setLayoutManager(new LinearLayoutManager(NoteEditorActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    binding.rvTags.setAdapter(tagAdapter);
                }
            });
        } else {
            tagAdapter = new TagAdapter(NoteEditorActivity.this, tagsTop, onClickListenerTag);
            binding.rvTags.setLayoutManager(new LinearLayoutManager(NoteEditorActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvTags.setAdapter(tagAdapter);
        }

        adapter = new NoteImagesAdapter(this, images);
        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvImages.setAdapter(adapter);

        colorAdapterNote = new ColorAdapter(this, colors, onClickListenerColorNote);
        binding.rvColors.setLayoutManager(new LinearLayoutManager(this));
        binding.rvColors.setAdapter(colorAdapterNote);

        colorAdapterTag = new ColorAdapter(this, colors, onClickListenerColorTag);
        binding.rvColors2.setLayoutManager(new LinearLayoutManager(this));
        binding.rvColors2.setAdapter(colorAdapterTag);

        binding.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                finish();
                returnMain();
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

        binding.btndropdown2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rvColors.setVisibility(View.VISIBLE);
            }
        });

        binding.btndropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rvColors2.setVisibility(View.VISIBLE);
            }
        });

        binding.btnSaveTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });
    }

    private void saveNote() {
        note.setTitle(binding.tvTitle.getText().toString());
        note.setContent(binding.tvContent.getText().toString());
        note.setCreatedBy(ParseUser.getCurrentUser());

        JSONArray jArray = new JSONArray();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        for (int i = 0; i < images.size(); i++) {
            images.get(i).compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] scaledData = bos.toByteArray();
            jArray.put(new ParseFile("image_to_be_saved.jpg", scaledData));
        }

        note.setImages(jArray);

        Log.d("NoteEditor", jArray.toString());

        note.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("NoteEditor", "Error while saving note", e);
                    Toast.makeText(NoteEditorActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(NoteEditorActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
            }
        });
        addNoteToMap();
    }

    public void addNoteToMap() {
        int stringLength = note.getTitle().length();
        String substr;

        for (int i = 0; i < stringLength; i++) {
            for (int j = i + 1; j <= stringLength; j++) {
                substr = note.getTitle().substring(i,j).toLowerCase();
                if(MainActivity.substringsNotes.containsKey(substr)) {
                    if (!MainActivity.substringsNotes.get(substr).contains(note)) {
                        MainActivity.substringsNotes.get(substr).add(note);
                    }
                } else {
                    MainActivity.substringsNotes.put(substr, new ArrayList<>());
                    MainActivity.substringsNotes.get(substr).add(note);
                }
            }

        }
    }

    private void addTag() {
        String tagString = binding.etTagName.getText().toString();
        saveNote();

        if (tagString.equals("")) {
            Toast.makeText(NoteEditorActivity.this, "Needs tag name", Toast.LENGTH_SHORT).show();
            return;
        }

        tagSave.setName(tagString);
        tagSave.setCreatedBy(ParseUser.getCurrentUser());
        tagSave.remove("notes");

        ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
        query.whereEqualTo("name", tagString);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> tagsList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("NoteEditorActivity", "Issue with getting notes", e);
                    return;
                }

                if (!tagsList.isEmpty()) {
                    tagSave = tagsList.get(0);
                    Log.d("NoteEditor", tagSave.toString());
                }

                Log.d("NoteEditor", "Name: " + tagSave.getName() + " Created by " + tagSave.getCreatedBy().getUsername());
                JSONArray notes = tagSave.getNotes();

                if(notes == null) {
                    binding.tvTags2.setVisibility(View.VISIBLE);
                    notes = new JSONArray();
                }

                notes.put(note);
                tagSave.setNotes(notes);

                Log.d("NoteEditor", notes.toString());

                tagSave.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Log.e("NoteEditor", "Issue while saving tag", e);
                            return;
                        }
                        Log.d("NoteEditor", "Tag was saved");
                        binding.etTagName.setText("");
                        binding.tagLayout.setVisibility(View.GONE);

                        JSONArray tags = note.getTags();

                        if(tags == null) {
                            tags = new JSONArray();
                        }

                        tagsTop.add(tagSave);
                        tags.put(tagSave);
                        note.setTags(tags);
                        tagAdapter.notifyDataSetChanged();

                        saveNote();
                    }
                });
            }
        });
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
            case R.id.iAddTag:
                binding.tagLayout.setVisibility(View.VISIBLE);
                tagSave = new Tag();
                tagSave.setColor(getColor(R.color.color_1_green));
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