package com.project.fypapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;
import com.project.fypapp.model.JobDescription;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserExperienceRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<JobDescription> jobs;
    private final UserExperienceRecyclerAdapterListener onClickListener;

    class UserExperienceViewHolder extends RecyclerView.ViewHolder {
        private final TextView company;
        private final TextView position;
        private final TextView dates;
        private final TextView edit;

        public UserExperienceViewHolder(@NonNull View itemView) {
            super(itemView);

            company = itemView.findViewById(R.id.company_view);
            position = itemView.findViewById(R.id.position_view);
            dates = itemView.findViewById(R.id.date_view);
            edit = itemView.findViewById(R.id.edit_experience_view);

            edit.setOnClickListener(view -> onClickListener.editExperienceOnClick(view, getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_experience_short, parent, false);
        return new UserExperienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserExperienceViewHolder userExperienceViewHolder = (UserExperienceViewHolder) holder;
        userExperienceViewHolder.company.setText(jobs.get(position).getCompanyName());
        userExperienceViewHolder.position.setText(jobs.get(position).getPosition());

        final String dates = jobs.get(position).getStartingDate() + " - " + jobs.get(position).getEndingDate();
        userExperienceViewHolder.dates.setText(dates);
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public interface UserExperienceRecyclerAdapterListener {
        void editExperienceOnClick(View v, int position);
    }
}
