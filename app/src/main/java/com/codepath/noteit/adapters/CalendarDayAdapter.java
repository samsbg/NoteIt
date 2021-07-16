package com.codepath.noteit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.databinding.CalendarDayLayoutBinding;
import com.kizitonwose.calendarview.model.CalendarDay;

public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.ViewHolder>{

    Context context;

    public CalendarDayAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("CalendarDayAdapter", "1");
        return new ViewHolder(CalendarDayLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarDayAdapter.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CalendarDayLayoutBinding binding;

        public ViewHolder(CalendarDayLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CalendarDay day) {
            binding.tvCalendarDay.setText(day.toString());
        }

    }


}
