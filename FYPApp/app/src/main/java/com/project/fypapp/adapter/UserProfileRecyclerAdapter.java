package com.project.fypapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;
import com.project.fypapp.model.JobExperience;
import com.project.fypapp.view.ExpandableTextView;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserProfileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<JobExperience> jobExperiences;

    static class JobDescriptionViewHolder extends RecyclerView.ViewHolder {
        private final TextView company;
        private final TextView position;
        private final TextView dates;
        private final ExpandableTextView jobDescription;

        public JobDescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            company = itemView.findViewById(R.id.company_view);
            position = itemView.findViewById(R.id.position_view);
            dates = itemView.findViewById(R.id.date_view);
            jobDescription = itemView.findViewById(R.id.job_description_view);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_experience_long, parent, false);
        return new JobDescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        JobExperience jobExperience = jobExperiences.get(position);
        JobDescriptionViewHolder jobDescriptionViewHolder = (JobDescriptionViewHolder) holder;
        jobDescriptionViewHolder.company.setText(jobExperience.getCompany());
        jobDescriptionViewHolder.position.setText(jobExperience.getPosition());
        jobDescriptionViewHolder.jobDescription.setText(jobExperience.getJobDescription());

        final String dates = jobExperience.getStartingDate() + " - " + jobExperience.getEndingDate();
        jobDescriptionViewHolder.dates.setText(dates);
    }

    @Override
    public int getItemCount() {
        return jobExperiences.size();
    }
}
