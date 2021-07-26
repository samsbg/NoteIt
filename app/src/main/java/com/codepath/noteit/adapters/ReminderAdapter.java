package com.codepath.noteit.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.DatePicker;

import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.activities.GoalEditorActivity;
import com.codepath.noteit.databinding.ItemNoteBinding;
import com.codepath.noteit.databinding.ItemReminderBinding;
import com.codepath.noteit.models.Note;
import com.codepath.noteit.models.Reminder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderAdapter {

    public interface OnClickListener {
        void onItemClicked(int position, Date date);
    }

    DatePickerDialog.OnDateSetListener mDateSetListener;
    OnClickListener clickListener;
    Context context;
    List<Reminder> reminders;

    public ReminderAdapter(Context context, List<Reminder> reminders, OnClickListener clickListener) {
        this.context = context;
        this.reminders = reminders;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemReminderBinding binding;

        public ViewHolder(ItemReminderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Date date) {
            binding.tvDate.setText(date.getDay() + "/" + date.getMonth() + "/" + date.getYear());

            binding.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            context.getApplicationContext(),
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
                    binding.tvDate.setText(dayOfMonth + "/" + month + "/" + year);
                    Date newDate = new Date(year, month, dayOfMonth);
                    clickListener.onItemClicked(getAdapterPosition(), newDate);
                }
            };
        }

    }
}
