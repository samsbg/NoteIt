package com.codepath.noteit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.databinding.ItemSearchBinding;
import com.codepath.noteit.models.Tag;

import java.util.List;

public class SearchTagAdapter extends RecyclerView.Adapter<SearchTagAdapter.ViewHolder>{

    public interface OnClickListener {
        void onItemClicked(Tag tag);
    }

    Context context;
    SearchTagAdapter.OnClickListener clickListener;
    List<Tag> items;

    public SearchTagAdapter(Context context, List<Tag> items, SearchTagAdapter.OnClickListener clickListener) {
        super();
        this.context = context;
        this.items = items;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SearchTagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchTagAdapter.ViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTagAdapter.ViewHolder holder, int position) {
        Tag tag = items.get(position);
        holder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemSearchBinding binding;

        public ViewHolder(ItemSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Tag tag) {
            binding.tvItem.setText(tag.getName());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(tag);
                }
            });
        }
    }
}
