package com.codepath.noteit.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.databinding.ItemReminderBinding;
import com.codepath.noteit.models.Reminder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder>{

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
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReminderAdapter.ViewHolder(ItemReminderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderAdapter.ViewHolder holder, int position) {
        Reminder rem = reminders.get(position);
        holder.bind(rem);
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemReminderBinding binding;

        public ViewHolder(ItemReminderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Reminder rem) {
            String dateStr = ( (int) rem.getDate().getMonth()+1) + "/" + rem.getDate().getDate()  + "/" + (rem.getDate().getYear()+1900);
            binding.tvDate.setText(dateStr);

            binding.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Date newDate = new Date(year-1900, month, dayOfMonth);
                            clickListener.onItemClicked(getAdapterPosition(), newDate);
                            notifyDataSetChanged();
                        }
                    };

                    DatePickerDialog dialog = new DatePickerDialog(
                            v.getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,
                            rem.getDate().getYear()+1900, rem.getDate().getMonth(), rem.getDate().getDate());
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });
        }
    }
}
