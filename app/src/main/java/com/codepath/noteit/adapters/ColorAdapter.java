package com.codepath.noteit.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.R;
import com.codepath.noteit.activities.GoalEditorActivity;
import com.codepath.noteit.databinding.ItemColorBinding;
import com.codepath.noteit.models.Goal;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder>{

    public interface OnClickListener {
        void onItemClicked(int color);
    }

    ColorAdapter.OnClickListener onClickListener;
    Context context;
    int[] items;

    public ColorAdapter(Context context, int[] items, ColorAdapter.OnClickListener onClickListener) {
        super();
        this.context = context;
        this.items = items;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColorAdapter.ViewHolder(ItemColorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorAdapter.ViewHolder holder, int position) {
        int color = items[position];
        holder.bind(color);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemColorBinding binding;

        public ViewHolder(ItemColorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final int color) {
            ImageViewCompat.setImageTintList(binding.ivColor, ColorStateList.valueOf(color));
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClicked(color);
                }
            });
        }
    }
}
