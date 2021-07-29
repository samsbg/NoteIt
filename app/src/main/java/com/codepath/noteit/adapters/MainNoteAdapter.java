package com.codepath.noteit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.noteit.databinding.ItemNoteBinding;
import com.codepath.noteit.databinding.ItemPicturesBinding;
import com.codepath.noteit.models.Note;

import java.util.Date;
import java.util.List;

public class MainNoteAdapter extends RecyclerView.Adapter<MainNoteAdapter.ViewHolder>{

    public interface OnLongClickListener {
        void onItemClicked(int position, Note note, View v);
    }

    OnLongClickListener onLongClickListener;
    Context context;
    List<Note> notes;

    public MainNoteAdapter(Context context, List<Note> notes, OnLongClickListener onLongClickListener) {
        super();
        this.context = context;
        this.notes = notes;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public MainNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainNoteAdapter.ViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainNoteAdapter.ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemNoteBinding binding;

        public ViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Note note) {
            binding.tvItemTitle.setText(note.getTitle());
            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickListener.onItemClicked(getAdapterPosition(), note, v);
                    return false;
                }
            });
        }

    }
}
