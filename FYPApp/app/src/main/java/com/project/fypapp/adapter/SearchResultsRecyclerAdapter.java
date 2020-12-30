package com.project.fypapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;
import com.project.fypapp.model.UserProfile;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class SearchResultsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<UserProfile> users;
    private final SearchResultsOnClickListener onClickListener;

    class SearchResultsViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final TextView bioView;

        SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.name_view);
            bioView = itemView.findViewById(R.id.bio_view);

            itemView.setOnClickListener(view -> onClickListener.onItemClick(view, getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_search_result, parent, false);
        return new SearchResultsViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchResultsViewHolder viewHolder = (SearchResultsViewHolder) holder;
        viewHolder.nameView.setText(users.get(position).getName());
        viewHolder.bioView.setText(users.get(position).getBio());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface SearchResultsOnClickListener {
        void onItemClick(View v, int position);
    }
}
