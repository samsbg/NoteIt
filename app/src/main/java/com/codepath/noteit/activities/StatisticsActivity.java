package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.noteit.databinding.ActivityStatisticsBinding;
import com.codepath.noteit.models.Goal;
import com.codepath.noteit.models.Tag;
import com.codepath.noteit.models.User;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    ActivityStatisticsBinding binding;

    int goals = 0;
    int incomplete = 0;
    int complete = 0;
    int overdue = 0;

    int reviews = 0;
    int pending = 0;
    int reviewed = 0;
    int tags = 0;
    int notes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        ParseQuery<Goal> query = ParseQuery.getQuery(Goal.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> goalsList, com.parse.ParseException e) {
                if (e != null) {
                    Log.e("MainActivity", "Issue with getting tags", e);
                    return;
                }
                for (Goal goal : goalsList) {
                    if(goal.getDueDate().compareTo(Calendar.getInstance().getTime()) < 0) {
                        if(goal.getCompletedBy() == null) {
                            overdue++;
                            if(goal.getNote() != null) {
                                tags += goal.getReviewed();
                            } else {
                                notes += goal.getReviewed();
                            }
                        } else {
                            complete++;
                        }
                    } else {
                        if(goal.getCompletedBy() == null) {
                            incomplete++;
                            pending += (goal.getTotalReviews() - goal.getReviewed());
                            if(goal.getNote() != null) {
                                tags += goal.getReviewed();
                            } else {
                                notes += goal.getReviewed();
                            }
                            reviewed += goal.getReviewed();
                        } else {
                            complete++;
                            reviewed += goal.getTotalReviews();
                            if(goal.getNote() != null) {
                                tags += goal.getTotalReviews();
                            } else {
                                notes += goal.getTotalReviews();
                            }
                        }
                    }
                    goals++;
                    reviews += goal.getTotalReviews();
                }

                binding.tvGoalsTot.setText((Integer.toString(goals)));
                binding.tvCompleted.setText((Integer.toString(complete)));
                binding.tvIncompleted.setText((Integer.toString(incomplete)));
                binding.tvOverdue.setText((Integer.toString(overdue)));

                binding.tvReviewsTot.setText((Integer.toString(reviews)));
                binding.tvPending.setText((Integer.toString(pending)));
                binding.tvReviewed.setText((Integer.toString(reviewed)));
                binding.tvNotesReviewed.setText((Integer.toString(notes)));
                binding.tvTagsReviewed.setText((Integer.toString(tags)));
            }
        });
    }
}