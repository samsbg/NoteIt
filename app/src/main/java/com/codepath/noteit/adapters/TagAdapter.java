package com.codepath.noteit.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.R;
import com.codepath.noteit.databinding.ItemSearchBinding;
import com.codepath.noteit.databinding.ItemTagBinding;
import com.codepath.noteit.models.Tag;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

    public interface OnClickListener {
        void onItemClicked(Tag tag);
    }

    Context context;
    TagAdapter.OnClickListener clickListener;
    List<Tag> items;

    public TagAdapter(Context context, List<Tag> items, TagAdapter.OnClickListener clickListener) {
        super();
        this.context = context;
        this.items = items;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TagAdapter.ViewHolder(ItemTagBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, int position) {
        Tag tag = items.get(position);
        holder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemTagBinding binding;

        public ViewHolder(ItemTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Tag tag) {
            binding.tvTag.setText(tag.getName());
            DrawableCompat.setTint(binding.tvTag.getBackground(), tag.getColor());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(tag);
                }
            });
        }
    }
}
