package com.codepath.noteit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.noteit.databinding.ItemSearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    public interface OnClickListener {
        void onItemClicked(int position);
    }

    private Context mContext;
    OnClickListener clickListener;

    private List<String> items;
    private List<String> filter;

    public SearchAdapter(Context context, List<String> filter, OnClickListener clickListener) {
        this.mContext = context;
        this.filter = filter;
        this.clickListener = clickListener;

        this.items = new ArrayList<>();
        this.items.addAll(filter);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchAdapter.ViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        String str = filter.get(position);
        holder.bind(str);
    }

    @Override
    public int getItemCount() {
        return filter.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        filter.clear();
        if (charText.length() == 0) {
            filter.addAll(items);
        } else {
            for (String str : items) {
                if (str.toLowerCase(Locale.getDefault()).contains(charText)) {
                    filter.add(str);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemSearchBinding binding;

        public ViewHolder(ItemSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final String str) {
            binding.tvItem.setText(str);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getLayoutPosition());
                }
            });
        }
    }
}
