package com.project.fypapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;
import com.project.fypapp.model.JobDescription;
import com.project.fypapp.model.UserProfile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserProfileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final UserProfile userProfile;
    private final  UserProfileRecyclerAdapterListener onClickListener;

    class UserInformationViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView email;
        private final TextView location;
        private final TextView bio;

        public UserInformationViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_view);
            email = itemView.findViewById(R.id.email_view);
            location = itemView.findViewById(R.id.location_view);
            bio = itemView.findViewById(R.id.bio_view);

            TextView editInformation = itemView.findViewById(R.id.edit_profile_view);
            editInformation.setOnClickListener(view -> onClickListener.editProfileOnClick(view, getAdapterPosition()));

            TextView editExperience = itemView.findViewById(R.id.edit_experience_view);
            editExperience.setOnClickListener(view -> onClickListener.editExperienceOnClick(view, getAdapterPosition()));
        }
    }

    class JobDescriptionViewHolder extends RecyclerView.ViewHolder {
        private final TextView company;
        private final TextView position;
        private final TextView dates;
        private final TextView jobDescription;

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_information, parent, false);
            return new UserInformationViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_description, parent, false);
        return new JobDescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0){
            UserInformationViewHolder userInformationViewHolder = (UserInformationViewHolder) holder;
            userInformationViewHolder.name.setText(userProfile.getName());
            userInformationViewHolder.email.setText(userProfile.getEmail());
            userInformationViewHolder.location.setText(userProfile.getRegion());
            userInformationViewHolder.bio.setText(userProfile.getBio());
        }

        else {
            JobDescription jobDescription = userProfile.getJobs().get(position-1);
            JobDescriptionViewHolder jobDescriptionViewHolder = (JobDescriptionViewHolder) holder;
            jobDescriptionViewHolder.company.setText(jobDescription.getCompanyName());
            jobDescriptionViewHolder.position.setText(jobDescription.getPosition());
            jobDescriptionViewHolder.jobDescription.setText(jobDescription.getJobDescription());

            final String dates = jobDescription.getStartingDate() + " - " + jobDescription.getEndingDate();
            jobDescriptionViewHolder.dates.setText(dates);
        }
    }

    @Override
    public int getItemCount() {
        return userProfile.getJobs().size() + 1;
    }

    public interface UserProfileRecyclerAdapterListener {
        void editProfileOnClick(View v, int position);

        void editExperienceOnClick(View v, int position);
    }
}
