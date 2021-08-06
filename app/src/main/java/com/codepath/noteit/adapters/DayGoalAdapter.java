package com.codepath.noteit.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.databinding.ItemGoalBinding;
import com.codepath.noteit.databinding.ItemGoalDayBinding;
import com.codepath.noteit.databinding.ItemReminderGoalBinding;
import com.codepath.noteit.models.Goal;
import com.parse.ParseException;

import java.util.List;

public class DayGoalAdapter extends RecyclerView.Adapter<DayGoalAdapter.ViewHolder>{
    Context context;
    List<Goal> goals;

    public DayGoalAdapter(Context context, List<Goal> goals) {
        this.context = context;
        this.goals = goals;
    }

    @NonNull
    @Override
    public DayGoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DayGoalAdapter.ViewHolder(ItemGoalDayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DayGoalAdapter.ViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.bind(goal);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemGoalDayBinding binding;

        public ViewHolder(ItemGoalDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Goal goal) {
            binding.tvDayGoal.setText(goal.getName());
            Drawable background = binding.tvDayGoal.getBackground();
            int color = goal.getColor();
            DrawableCompat.setTint(background, color);
        }
    }
}
