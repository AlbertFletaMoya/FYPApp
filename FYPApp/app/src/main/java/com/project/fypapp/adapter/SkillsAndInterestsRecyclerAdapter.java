package com.project.fypapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.project.fypapp.R;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class SkillsAndInterestsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<String> list;
    private final List<String> userList;
    private final SkillsAndInterestsOnClickListener onClickListener;
    private final boolean isRetiree;
    Context context;

    public class SkillsAndInterestsViewHolder extends RecyclerView.ViewHolder {
        private final TextView labelView;
        private final MaterialCheckBox checkboxView;

        SkillsAndInterestsViewHolder(@NonNull View itemView) {
            super(itemView);

            labelView = itemView.findViewById(R.id.label);
            checkboxView = itemView.findViewById(R.id.checkbox);

            checkboxView.setOnCheckedChangeListener((compoundButton, b) ->
                    onClickListener.onItemClick(getAdapterPosition(), b));
        }

        public void setCheckboxView() {
            this.checkboxView.setChecked(true);
        }

        public void unsetCheckboxView() {
            this.checkboxView.setChecked(false);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isRetiree) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_skill_and_interest, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_search_skill_and_interest, parent, false);
        }
        return new SkillsAndInterestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SkillsAndInterestsViewHolder viewHolder = (SkillsAndInterestsViewHolder) holder;
        viewHolder.labelView.setText(list.get(position));
        viewHolder.checkboxView.setChecked(userList.contains(list.get(position)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface SkillsAndInterestsOnClickListener {
        void onItemClick(int position, boolean checked);
    }
}
