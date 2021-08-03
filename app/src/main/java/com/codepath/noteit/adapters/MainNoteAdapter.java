package com.codepath.noteit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.databinding.ItemNoteBinding;
import com.codepath.noteit.models.Note;

import java.util.List;

public class MainNoteAdapter extends RecyclerView.Adapter<MainNoteAdapter.ViewHolder>{

    public interface OnClickListener {
        void onItemClicked(Note note);
    }

    public interface OnLongClickListener {
        void onItemClicked(Note note, View v);
    }

    OnClickListener onClickListener;
    OnLongClickListener onLongClickListener;

    Context context;
    List<Note> notes;

    public MainNoteAdapter(Context context, List<Note> notes, OnClickListener onClickListener, OnLongClickListener onLongClickListener) {
        super();
        this.context = context;
        this.notes = notes;
        this.onClickListener = onClickListener;
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
                    onLongClickListener.onItemClicked(note, v);
                    return false;
                }
            });
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClicked(note);
                }
            });
        }
    }
}
