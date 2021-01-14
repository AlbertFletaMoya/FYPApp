package com.project.fypapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;
import com.project.fypapp.model.JobExperience;
import com.project.fypapp.model.Retiree;
import com.project.fypapp.view.ExpandableTextView;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserProfileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String ADD = "ADD";

    private final Retiree retiree;
    private final List<JobExperience> jobExperiences;
    private final UserProfileRecyclerAdapterListener onClickListener;
    private final boolean profileBelongsToUser;

    class UserInformationViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView email;
        private final TextView location;
        private final TextView bio;
        private final TextView editExperience;

        public UserInformationViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_view);
            email = itemView.findViewById(R.id.email_view);
            location = itemView.findViewById(R.id.location_view);
            bio = itemView.findViewById(R.id.bio_view);

            TextView editInformation = itemView.findViewById(R.id.edit_profile_view);
            editExperience = itemView.findViewById(R.id.edit_experience_view);

            if (!profileBelongsToUser){
                ((ViewGroup) editInformation.getParent()).removeView(editInformation);
                ((ViewGroup) editExperience.getParent()).removeView(editExperience);
            }
            else {
                editInformation.setOnClickListener(view -> onClickListener.editProfileOnClick(view, getAdapterPosition()));
                editExperience.setOnClickListener(view -> onClickListener.editExperienceOnClick(view, getAdapterPosition()));
            }
        }
    }

    class JobDescriptionViewHolder extends RecyclerView.ViewHolder {
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_user_information, parent, false);
            return new UserInformationViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_experience_long, parent, false);
        return new JobDescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0){
            UserInformationViewHolder userInformationViewHolder = (UserInformationViewHolder) holder;
            userInformationViewHolder.name.setText(Retiree.getName(retiree));
            userInformationViewHolder.email.setText(retiree.getEmail());
            userInformationViewHolder.location.setText(Retiree.getLocation(retiree));
            userInformationViewHolder.bio.setText(retiree.getHeadline());

            if (jobExperiences.isEmpty()){
                userInformationViewHolder.editExperience.setText(ADD);
            }
        }

        else {
            JobExperience jobExperience = jobExperiences.get(position-1);
            JobDescriptionViewHolder jobDescriptionViewHolder = (JobDescriptionViewHolder) holder;
            jobDescriptionViewHolder.company.setText(jobExperience.getCompany());
            jobDescriptionViewHolder.position.setText(jobExperience.getPosition());
            jobDescriptionViewHolder.jobDescription.setText(jobExperience.getJobDescription());

            final String dates = jobExperience.getStartingDate() + " - " + jobExperience.getEndingDate();
            jobDescriptionViewHolder.dates.setText(dates);
        }
    }

    @Override
    public int getItemCount() {
        return jobExperiences.size() + 1;
    }

    public interface UserProfileRecyclerAdapterListener {
        void editProfileOnClick(View v, int position);

        void editExperienceOnClick(View v, int position);
    }
}
