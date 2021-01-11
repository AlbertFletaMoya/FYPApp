package com.project.fypapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fypapp.R;
import com.project.fypapp.adapter.UserExperienceRecyclerAdapter;
import com.project.fypapp.model.UserProfile;

import static com.project.fypapp.util.Constants.ADD_NEW_EXPERIENCE;

public class ExperienceIndexActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_index);
        initRecyclerView();

        final ConstraintLayout addExperienceView = findViewById(R.id.add_job_layout);
        addExperienceView.setOnClickListener(view -> addExperience());

        final TextView cancelView = findViewById(R.id.cancel_view);
        cancelView.setOnClickListener(view -> cancel());
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        final UserProfile userProfile = new UserProfile();

        UserExperienceRecyclerAdapter userExperienceRecyclerAdapter =
                new UserExperienceRecyclerAdapter(userProfile.getJobs(), (v, position) -> {
                    Intent i = new Intent(ExperienceIndexActivity.this, EditJobExperienceActivity.class);
                    i.putExtra("experienceId", 123);
                    startActivity(i);
                });
        recyclerView.setAdapter(userExperienceRecyclerAdapter);
    }

    private void addExperience() {
        Intent i = new Intent(ExperienceIndexActivity.this, EditJobExperienceActivity.class);
        i.putExtra("experienceId", ADD_NEW_EXPERIENCE);
        startActivity(i);
    }

    private void cancel() {
        Intent i = new Intent(ExperienceIndexActivity.this, MainActivity.class);
        i.putExtra("profileBelongsToUser", true);
        i.putExtra("firstLogIn", false);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
