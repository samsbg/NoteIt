package com.codepath.noteit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.databinding.ItemGoalBinding;
import com.codepath.noteit.models.Goal;

import java.util.List;

public class MainGoalAdapter extends  RecyclerView.Adapter<MainGoalAdapter.ViewHolder>{

    MainGoalAdapter.OnClickListener onClickListener;
    Context context;
    List<Goal> goals;

    public MainGoalAdapter(Context context, List<Goal> goals, OnClickListener onClickListener) {
        super();
        this.context = context;
        this.goals = goals;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainGoalAdapter.ViewHolder(ItemGoalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainGoalAdapter.ViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.bind(goal);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public interface OnClickListener {
        void onItemClicked(int position, Goal goal, View v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemGoalBinding binding;

        public ViewHolder(ItemGoalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Goal goal) {
            binding.tvGoal.setText(goal.getName());
            binding.progressBar.setMax(goal.getTotalReviews());
            binding.progressBar.setProgress(goal.getReviewed());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClicked(getAdapterPosition(), goal, v);
                }
            });
        }
    }
}
