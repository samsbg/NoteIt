package com.codepath.noteit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.noteit.databinding.ItemPicturesBinding;

import java.util.List;

public class NoteImagesAdapter extends RecyclerView.Adapter<NoteImagesAdapter.ViewHolder>{

    Context context;
    List<Bitmap> images;

    public NoteImagesAdapter(Context context, List<Bitmap> images) {
        super();
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPicturesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteImagesAdapter.ViewHolder holder, int position) {
        Bitmap image = images.get(position);
        holder.bind(image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemPicturesBinding binding;

        public ViewHolder(ItemPicturesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Bitmap photoFile) {
            Glide.with(context).load(photoFile).into(binding.ivImage);
        }

    }
}
