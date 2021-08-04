package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.codepath.noteit.databinding.ActivityStatisticsBinding;
import com.codepath.noteit.models.User;
import com.parse.ParseUser;

public class StatisticsActivity extends AppCompatActivity {

    ActivityStatisticsBinding binding;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        user = (User) ParseUser.getCurrentUser();

        //binding.tvResult1.setText(Integer.toString(user.getCompleted()));
        //binding.tvResult2.setText(Integer.toString(user.getReviewed()));
        binding.tvResult3.setText("Undetermined");
    }
}