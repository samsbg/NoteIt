package com.codepath.noteit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.databinding.ItemSearchBinding;
import com.codepath.noteit.models.Note;

import java.util.List;

public class SearchNoteAdapter extends RecyclerView.Adapter<SearchNoteAdapter.ViewHolder>{

    public interface OnClickListener {
        void onItemClicked(int position, Note note);
    }

    Context context;
    OnClickListener clickListener;
    List<Note> items;

    public SearchNoteAdapter(Context context, List<Note> items, OnClickListener clickListener) {
        super();
        this.context = context;
        this.items = items;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchNoteAdapter.ViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchNoteAdapter.ViewHolder holder, int position) {
        Note note = items.get(position);
        holder.bind(note);
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

        public void bind(final Note note) {
            binding.tvItem.setText(note.getTitle());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getLayoutPosition(), note);
                }
            });
        }
    }
}
