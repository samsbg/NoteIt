package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.codepath.noteit.adapters.ReminderAdapter;
import com.codepath.noteit.databinding.ActivityGoalEditorBinding;
import com.codepath.noteit.models.Reminder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GoalEditorActivity extends AppCompatActivity {

    ActivityGoalEditorBinding binding;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    Date date;
    List<Reminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalEditorBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        binding.tvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        GoalEditorActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String dateString = dayOfMonth + "/" + month + "/" + year;
                binding.tvDatePicker.setText(dateString);
                date = new Date(year, month, dayOfMonth);
            }
        };

        binding.btnSaveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        binding.etNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                for (int i = 0; i < Integer.parseInt(binding.etNumber.getText().toString()); i++) {
                    Date currentDate = Calendar.getInstance().getTime();
                }
            }
        });

        ReminderAdapter.OnClickListener onClickListener = new ReminderAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position, Date newDate) {
                reminders.get(position).setDate(newDate);
            }
        };
    }

    private void save() {
        String name = binding.tvName.getText().toString();
        String reminders = binding.etNumber.getText().toString();

        if (name == null) {
            Toast.makeText(GoalEditorActivity.this, "Name must be filled", Toast.LENGTH_SHORT);
            return;
        } else if (reminders == null) {
            Toast.makeText(GoalEditorActivity.this, "Number of reminders must be filled", Toast.LENGTH_SHORT);
            return;
        } else if (date == null) {
            Toast.makeText(GoalEditorActivity.this, "Date due must be filled", Toast.LENGTH_SHORT);
            return;
        }


    }


}